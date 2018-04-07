package finalassembler;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

public class FinalPassTwo {
	
	public static void Print(String a, String s, char f,int l) throws IOException
	{

		System.out.println(f+" " + a+ " " +Integer.toHexString(l) +" " + s);
	}

	
    static String octalToBinary(int octal) {
        String octalResult = "";
        int remainder;      
        String binary = "";
        while (octal != 0) {
            //pick each digits of this number and convert it to binary
            remainder = octal % 10;
            //convert it to binary

            while (remainder > 0) {
                binary = (remainder % 2) + binary;
                remainder = remainder / 2;
            }

            octalResult = binary + octalResult;
           
            octal = octal / 10;
        }
        return octalResult;

    }
	
	public static void main(String[] args) throws IOException {
		
		 char indexedModeChar;
	     int indexedModeInt;
         String indexedModeString;
		int prolength;
         int ends=0;
         int starts=0;
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		HashMap<String, String> symmap= new HashMap<String, String>();
		
		hm.put("ADD", 0x18);
        hm.put("AND", 0x40);
        hm.put("DIV", 0x24);
        hm.put("J", 0x3C);
        hm.put("COMP", 0x28);
        hm.put("JEQ", 0x30);
        hm.put("JGT", 0x34);
        hm.put("JLT", 0x38);
        hm.put("JSUB", 0x48);
        hm.put("LDA", 0x00);
        hm.put("LDCH", 0x50);
        hm.put("LDL", 0x08);
        hm.put("LDX", 0x04);
        hm.put("MUL", 0x20);
        hm.put("OR", 0x44);
        hm.put("RD", 0xD8);
        hm.put("RSUB", 0x4C);
        hm.put("STA", 0x0C);
        hm.put("STCH", 0x54);
        hm.put("STL", 0x14);
        hm.put("STX", 0x10);
        hm.put("SUB", 0x1C);
        hm.put("TD", 0xE0);
        hm.put("TIX", 0x2C);
        hm.put("WD", 0xDC);
        
        
        FileReader input = new FileReader("inter.txt");
        BufferedReader source = new BufferedReader(input);
        
        FileReader rowan = new FileReader("inter.txt");
        BufferedReader nevine = new BufferedReader(rowan);
        
        FileReader sym = new FileReader("symtable.txt");
        BufferedReader table = new BufferedReader(sym);
        
        FileWriter inter = new FileWriter("list.txt");
        BufferedWriter list = new BufferedWriter(inter);
        
		 FileWriter code = new FileWriter("objectprogram.txt");
	     BufferedWriter object = new BufferedWriter(code);

	     /* Read the symtable from file and put it in symmap*/
	     
	     String thread=null;
	     while ((thread = table.readLine())!=null)
			{
			
				if(thread.trim().length() == 0)
				{
					continue;
				}
				String[] label=thread.split("      ");
				if(symmap.containsKey(label[0]))
				{
					System.out.println("ERROR VALUE AREADY EXISTS!!");
					break;
				}
				else
				symmap.put(label[0],label[1]);
			}
        /* End the reading */
	     
	    String  sick=null;
	     while ((sick = nevine.readLine())!=null)
			{
			
				if(sick.trim().length() == 0)
				{
					continue;
				}
				String[] label=sick.split("      ");
				if("START".equals(label[1]))
				{
					starts=Integer.parseInt(label[2],16);
				}
				else if("END".equals(label[1]))
				{
					ends=Integer.parseInt(label[0],16);
				}
			}
			prolength= ends - starts;
			//System.out.println(ends);
			System.out.println(String.format("%06X", prolength));

	     
        String line = null;
        String print = "";
        String LOCCTR="0000";
    	String c="";
        int length=0;

        /* Read input file and get from Symtable and Optable the value*/
        

			while ((line = source.readLine())!=null)
				{
				
					if(line.trim().length() == 0)
					{
						continue;
					}
					
					String[] record = line.split("      ");
					String Adress= record[0];
					String Menemonic = record[1];
					String Operand = record[2];
					
					if("START".equals(record[1]))
					{
						System.out.println("H"+ " "+ record[0]+" "+ String.format("%06X", starts) +" "+ String.format("%06X", prolength));
						String b = "H"+ " "+ record[0]+" "+ String.format("%06X", starts) +" "+ String.format("%06X", prolength);
						object.write("H"+ " "+ record[0]+" "+ String.format("%06X", starts) +"  "+ String.format("%06X", prolength));
	            		object.newLine();
	//					Write(b);
					}
		            if(!("RESW".equals(record[1])||"RESB".equals(record[1])||"START".equals(record[1])||"END".equals(record[1])||"WORD".equals(record[1])||"BYTE".equals(record[1])))
		            {
		            /*indexing part*/
		            	if(length ==0)
		            		LOCCTR=Adress;
		            	
		            	if (record[2].contains(",")) {
		            		String extract = record[2].substring(0, record[2].length()-2); //extracted string without ",X"
		                    indexedModeChar = symmap.get(extract).charAt(0);
		                    indexedModeInt = indexedModeChar - '0';
		                    if (indexedModeInt == 0) {
		                        indexedModeString = "0";
		                    } else {
		                        indexedModeString = octalToBinary(indexedModeInt);
		                    }

		                    indexedModeInt = Integer.parseInt(indexedModeString);

		                    indexedModeString = Integer.toHexString(Integer.parseInt("1".concat(String.format("%03d", indexedModeInt)), 2));
		                    String newvalue = symmap.get(extract).replaceFirst("[a-za-z0-9_]", indexedModeString);
		                    symmap.replace(extract, newvalue);
		              /*Indexing end */ 
		              /*we print in this if using "extract" not "record[2]" because otherwise it would give null */
		     
		            	list.write(Menemonic+"    "+Operand+"    "+String.format("%02X",hm.get(Menemonic))+symmap.get(extract));
		            	String x =String.format("%02X",hm.get(Menemonic))+symmap.get(extract);
		            	if(x.length()/2 +length >30) {
		            		Print(LOCCTR,print ,'T',length);
		            		object.write("T "+LOCCTR+ " " +Integer.toHexString(length) +" " + print);
		            		object.newLine();
		            		print="";
		            		length=0;
		            		LOCCTR=Adress;
		            	}
		            	else {
		            		print = print.concat(x);
		            		print = print.concat(" ");
		            		length += (x.length()/2);
		            	}
		            	
		            	list.newLine();
		            	}
		            	
		            	else  /* If there is no ",X" we use record[2] as we print normally*/
		            	{
		            		list.write(Menemonic+"    "+Operand+"    "+String.format("%02X",hm.get(Menemonic))+symmap.get(Operand));
			            	String x =String.format("%02X",hm.get(Menemonic))+symmap.get(Operand);
			            	if(x.length()/2 +length >30) {
			            		Print(LOCCTR,print ,'T',length);
			            		object.write("T "+LOCCTR+ " " +Integer.toHexString(length) +" " + print);
			            		object.newLine();
			            		print="";
			            		length=0;
			            		LOCCTR=Adress;
			            	}
			            	else {
			            		print = print.concat(x);
			            		print = print.concat(" ");
			            		length += (x.length()/2);
			            	}
			            	
			            	list.newLine();
		            	}
		            }
		            else if("WORD".equals(Menemonic))
		            {
		            	list.write(Menemonic+"    "+Operand+"    "+String.format("%04X",Integer.parseInt(Operand)));
		            	list.newLine();
		            	if(length ==0)
		            		LOCCTR=Adress;
		            	String x= String.format("%06X",Integer.parseInt(Operand));
		            	if(x.length()/2 +length >30) {
		            		Print(LOCCTR,print ,'T',length);
		            		object.write("T "+LOCCTR+ " " +Integer.toHexString(length) +" " + print);
		            		object.newLine();
		            		print="";
		            		length=0;
		            		LOCCTR=Adress;

		            	}
		            	else {
		            		print = print.concat(x);
		            		print = print.concat(" ");
		            		length += (x.length()/2);
		            	}

		            }
		            else if("BYTE".equals(Menemonic))
		            {
		            	if(length ==0)
		            		LOCCTR=Adress;
		            	list.write(Menemonic+"    "+Operand+ "    ");

		            	if('X'==Operand.charAt(0))
		            	{		            		
		            		String x= Operand.substring(2, Operand.length()-1);
		            		list.write(x);


			            	if(x.length()/2 +length >30) {
			            		Print(LOCCTR,print ,'T',length);
			            		object.write("T "+LOCCTR+ " " +Integer.toHexString(length) +" " + print);
			            		object.newLine();
			            		print="";
			            		length=0;
			            		LOCCTR=Adress;
			            	}
			            	else {
			            		print = print.concat(x);
			            		print = print.concat(" ");
			            		length += (x.length()/2);
			            	}

		            	}
		            	else if('C'==Operand.charAt(0))
		            	{
		            	String str2 = Operand.substring(2, record[2].length()-1);
		            	String x = "";
		            	System.out.println(str2);
		            	for(int i=0;i<Menemonic.length()+1;i++)
		            	{
		            		char character= str2.charAt(i);
		            		int ascii[] = new int[1000];
		            		ascii[i]= (int) character;
		            		list.write(Integer.toHexString(ascii[i]));
		            		x=x.concat(Integer.toHexString(ascii[i]));
		            	}
		            	if(x.length()/2 +length >30) 
		            	{
		            		Print(LOCCTR,print ,'T',length);
		            		object.write("T "+LOCCTR+ " " +Integer.toHexString(length) +" " + print);
		            		object.newLine();
		            		print="";
		            		length=0;
		            		LOCCTR=Adress;
		            	}
		            	else 
		            	{
		            		print = print.concat(x);
		            		print = print.concat(" ");
		            		length += (x.length()/2);
		            	}
		            	
	            		list.newLine();	
		            	}
	            		
		            }
		            else if("RESW".equals(record[1])||"RESB".equals(record[1]))
		            {
		            	list.write(Menemonic+"    "+Operand+ "    "+ " ");
	            		list.newLine();
	            	
	            		if(length!=0)
	            		{
			            		Print(LOCCTR,print,'T',length);
			            		object.write("T "+LOCCTR+ " " +Integer.toHexString(length) +" " + print);
			            		object.newLine();
			            		print="";
			            		length=0;
	            		}
		            }
		            else if ("END".equals(record[1]))
		            {		            	

		            	if(length!=0)
		            	{
		            		Print(LOCCTR,print,'T',length);
		            		object.write("T "+LOCCTR+ " " +Integer.toHexString(length) +" " + print);
		            		object.newLine();
		            		System.out.print("E "+ String.format("%06X", starts));
		            		object.write("E "+ String.format("%06X", starts));
		            		
		            	}
		            }
				}

      list.close();
      inter.close();
      object.close();
      code.close();
      
	}
	
}
