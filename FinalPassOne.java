package finalassembler;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.Set;

public class FinalPassOne {

    public static void main(String[] args) {
        HashMap<String, String> symTable = new HashMap<String, String>();
        HashMap<String, String> mnemonicOperand = new HashMap<String, String>();

        // The name of the file to open.
        String nameRead = "prog5-indexed_v.2.txt";
        String nameWrite = "symtable.txt";
        String interWrite = "inter.txt";
        int LOCCTR = 0;
        int address = 0;
        int startingAddress = 0;
        String line;


        try {
            
            FileReader fileReader = new FileReader(nameRead);
            FileWriter fileWriter = new FileWriter(nameWrite);
            FileWriter interWriter = new FileWriter(interWrite);

          
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            BufferedWriter bufferedInterW = new BufferedWriter(interWriter);

            while ((line = bufferedReader.readLine()) != null) {
                
                if (line.trim().length() == 0 || line.trim().charAt(0) == '.') {
                    continue;
                }
          //      String[] columns = line.split("      ");
                String [] columns = line.split("\\s+");
                String label;
                String Menomonic;
                
//                if(columns.length ==3)
//                	 label = columns[0]; 
//                else label = "";
//                System.out.println(label);
//                
//                if(columns.length ==3)
//                	Menomonic = columns[1];
//                else Menomonic = columns[0];
//                System.out.println(Menomonic);


                 
//                mnemonicOperand.put(columns[1], columns[2]);
               
                if ("START".equals(columns[1])) {
                    address = Integer.parseInt(columns[2]);
                    startingAddress = address;
                    

                } else if ("RESW".equals(columns[1])) {
                    address += (3 * Integer.parseInt(columns[2]));
                   

                } else if ("WORD".equals(columns[1])) {
                    address += 3;
                 

                } else if ("RESB".equals(columns[1])) {
                    address += Integer.parseInt(columns[2]);
                    bufferedReader.readLine();
              

                } else if ("BYTE".equals(columns[1])) {
                    if (columns[2].charAt(0) == 'C') {
                        address += (columns[2].length() - 3);
                

                    } else {
                        address += (columns[2].length() - 3) / 2;
                        

                    }

                } 
                else {
                    address += 3;
                }

                if (!((line.charAt(0) == ' ')||line.charAt(0) == '*')) { //to avoid lines without labels
                    if ("START".equals(columns[1])) {
                        bufferedWriter.write(columns[0]);
                        bufferedWriter.write("      ");
                        bufferedWriter.write(columns[2]);
                        bufferedWriter.newLine();
                        symTable.put(columns[0], String.format("%04x",LOCCTR));
                    } else {
                        bufferedWriter.write(columns[0]);
                        bufferedWriter.write("      ");
                        bufferedWriter.write(String.format("%04x",LOCCTR));
                        bufferedWriter.newLine();
                        symTable.put(columns[0], String.format("%04x",LOCCTR));

                    }
                }
                if ("START".equals(columns[1])) {
                    bufferedInterW.write(columns[0]);
                    bufferedInterW.write("      ");
                    bufferedInterW.write(columns[1]);
                    bufferedInterW.write("      ");
                    bufferedInterW.write(columns[2]);
                    bufferedInterW.newLine();

                } else {
                    bufferedInterW.write(String.format("%04x",LOCCTR));
                    bufferedInterW.write("      ");
                   bufferedInterW.write(columns[1]);
                    bufferedInterW.write("      ");
                    
                  bufferedInterW.write(columns[2]);

                    bufferedInterW.newLine();
                }

                LOCCTR = address;
            }
            System.out.println(symTable);
            System.out.println(mnemonicOperand);

            int programLength = LOCCTR - startingAddress;
            System.out.println(Integer.toHexString(programLength));
            System.out.println(Integer.toHexString(LOCCTR));
            System.out.println(Integer.toHexString(startingAddress));

            bufferedReader.close();
            bufferedWriter.close();
            bufferedInterW.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '"
                    + nameRead + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                    + nameRead + "'");
        }

    }
}
