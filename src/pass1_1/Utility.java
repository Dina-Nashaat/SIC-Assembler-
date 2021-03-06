/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pass1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;
import static pass1_1.Pass1.programLength;
import static pass1_1.Pass1.startAddress;
import static pass1_1.Pass2.Rec;
import static pass1_1.Pass2.recLength;
import static pass1_1.Pass2.recStart;

/**
 *
 * @author dinan
 */
public class Utility {

    public static ArrayList readAllLines(String filename) {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            Scanner s = new Scanner(new File(filename));
            while (s.hasNextLine()) {
                lines.add(s.nextLine());
            }
            s.close();
        } catch (FileNotFoundException ex) {
        }
        return lines;
    }

    public static String buffer = " ";

    public static File checkFile(String Filename) {
        File file = new File(Filename);
        if (file.exists()) {
            file.delete();
        } else {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Pass1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return file;
    }

    public static void writeLine(String line, File file) {

        try {
            FileWriter fw = new FileWriter(file, true);
            PrintWriter writer = new PrintWriter(fw);
            writer.println(line);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Pass1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeToINT(String line, File file, String locctr, boolean commentFlag) {

        StringBuilder newline = new StringBuilder();

        try {
            FileWriter fw = new FileWriter(file, true);
            PrintWriter writer = new PrintWriter(fw);

            newline.append(line);
            newline.append("                                                                                      ");
            newline.insert(67, locctr);
            if (commentFlag) {
                newline.insert(66, "t");
            } else {
                newline.insert(66, "f");
            }
            writer.println(newline.toString());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Pass1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeToLST(String line, File file, String locctr, String error) {

        StringBuilder newline = new StringBuilder();

        try {
            FileWriter fw = new FileWriter(file, true);
            PrintWriter writer = new PrintWriter(fw);
            newline.append("                                                                                                ");
            newline.insert(0, locctr);
            newline.insert(7, Pass2.Rec.toUpperCase());
            newline.insert(14, line);

            writer.println(newline.toString());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Pass1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeTxt(File file, String stAdd, String length, String objCode) {
        if(length.length()==1)
            length = "0"+length;
        buffer = "T" + stAdd + length + objCode;
        writeLine(buffer.toUpperCase(), file);
    }

    public static String readStm(String stmt, String type) {
        type = type.toLowerCase();
        int start, end = stmt.length();
        String trgStm;
        switch (type) {
            case "label":
                if (stmt.substring(0, 7).equals("      ")) {
                    return "";
                } else if (stmt.substring(0, 7).startsWith(" ")) {
                    return printError("Invalid Label String");
                }
                start = 0;
                end = 7;
                trgStm = stmt.substring(start, end).trim().toLowerCase();
                break;
            case "opcode":
                if (stmt.substring(9, stmt.length()).startsWith(" ")) {
                    return printError("Invalid opcode String");
                }
                start = 9;
                if (stmt.length() < 14) {
                    end = stmt.length();
                } else {
                    end = 14;
                }
                trgStm = stmt.substring(start, end).trim().toLowerCase();
                break;
            case "operand":
                if (stmt.substring(17, stmt.length()).startsWith(" ")) {
                    return printError("Invalid operand String");
                }
                start = 17;
                if (stmt.length() < 34) {
                    end = stmt.length();
                } else {
                    end = 34;
                }
                trgStm = stmt.substring(start, end).trim().toLowerCase();
                break;
            case "comment":
                start = 35;
                end = stmt.length();
                trgStm = stmt.substring(start, end).trim().toLowerCase();
                break;
            case "literal":
                start = 8;
                if (stmt.length() < 14) {
                    end = stmt.length();
                } else {
                    end = 20;
                }
                trgStm = stmt.substring(start, end).trim();
                break;
            case "operandliteral":
                if (stmt.substring(17, stmt.length()).startsWith(" ")) {
                    return printError("Invalid operand String");
                }
                start = 17;
                if (stmt.length() < 34) {
                    end = stmt.length();
                } else {
                    end = 34;
                }
                trgStm = stmt.substring(start, end).trim();
                break;
            default:
                return printError("unidentified operation");
        }

        return trgStm;
    }

    public static String addHex(String inputHex, int i) {
        Integer inputDec = Integer.parseInt(inputHex, 16);
        inputDec += i;
        String outputHex = Integer.toHexString(inputDec);
        return outputHex;
    }

    public static String decToHex(int deci) {
        String hexa = Integer.toHexString(deci);
        return hexa;
    }

    public static boolean testHexOperand(String x) {
        return x.startsWith("0");
    }

    public static boolean isComment(String stmt) {
        return stmt.startsWith(".");
    }

    public static String printError(String errormessage) {
        return errormessage;
    }

    public static String writeObjectProg(String type, String order, String oprAdd, String codeAdd) {
        String line = null;
        String progLength = "000000";
        int h = Integer.toHexString(programLength).toString().length();
        progLength = (progLength.substring(0, 6 - h) + Integer.toHexString(programLength)).toUpperCase();
        switch (type) {
            case "H":
                line = "H" + Pass2.progName + " " + startAddress.toUpperCase() + progLength.toUpperCase();
                break;
            case "T":
                switch (order) {
                    case "initialize":
                        line = "T" + recStart + recLength + Rec;
                        break;
                    case "add":
                        line = codeAdd + oprAdd;
                        break;
                }
                break;
            case "E":
                line = "E" + startAddress;
                break;
            default:
                Utility.printError("Undefined Record");
        }
        return line;
    }

    public static void writeEnd(File file, String stAdd) {
        String buffer;
        buffer = "E" + stAdd.toUpperCase();
        writeLine(buffer, file);

    }

    public static String checkLiterals(String line, String type) {
        boolean HexFlag;

        String operand = "", op;
        op = "";
        Pattern p = Pattern.compile("\'([^\']*)\'");
        Matcher m = p.matcher(operand);

        switch (type) {
            case "operand":
                operand = readStm(line, "operandLiteral");
                p = Pattern.compile("\'([^\']*)\'");
                m = p.matcher(operand);
                break;
            case "literal":
                operand = readStm(line, "literal");
                p = Pattern.compile("\\'([^\\']*)\\'");
                m = p.matcher(operand);
                break;
        }
        if (operand.startsWith("=")) {
            while (m.find()) {
                op = m.group(1);
            }
            return op;
        } else {
            return null;
        }
    }

    public static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public static String lhexToAscii(String hexValue) throws UnsupportedEncodingException {
        byte[] bytes = DatatypeConverter.parseHexBinary(hexValue);
        String result = new String(bytes, "UTF-8");
        return result;

    }
    
    public static String hexToAscii(String hex){

	  StringBuilder sb = new StringBuilder();
	  StringBuilder temp = new StringBuilder();
	  
	  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
	  for( int i=0; i<hex.length()-1; i+=2 ){
		  
	      //grab the hex in pairs
	      String output = hex.substring(i, (i + 2));
	      //convert hex to decimal
	      int decimal = Integer.parseInt(output, 16);
	      //convert the decimal to character
	      sb.append((char)decimal);
		  
	      temp.append(decimal);
	  }
	  //System.out.println("Decimal : " + );
	  
	  return sb.toString();
  }
    
    
    public static boolean checkHex (String line, String Type)
    {
        String operand = "";
        operand = readStm(line, "operandLiteral");
        operand = operand.substring(1);
            if (operand.startsWith("X")) {
                return true;
            } else {
                return false;
            }
    }

}
