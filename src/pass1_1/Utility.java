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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static pass1_1.Pass1.programLength;
import static pass1_1.Pass1.startAddress;
import static pass1_1.Pass2.objcode;
import static pass1_1.Pass2.progName;
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
     public static String buffer=" ";
     public static File checkFile (String Filename)
     {
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
            newline.append("                                                                                                ");
            newline.insert(67, locctr);
            if(commentFlag) newline.insert(66, "t");
            else newline.insert(66, "f");
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
            newline.insert(7,Pass2.Rec);
            newline.insert(13, line);
            
            writer.println(newline.toString());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Pass1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
     public static void writeTxt(File file,String stAdd,String length, String objCode)
     {
          
         
         buffer = "T"+stAdd+length+objCode;
         writeLine(buffer, file);
     }
     
     
     public static String readStm(String stmt, String type) {
        type = type.toLowerCase();
        int start, end=stmt.length();
        
        switch (type) {
            case "label":
                if (stmt.substring(0,7).contains("      "))
                    return "";
                else if(stmt.substring(0, 7).startsWith(" "))
                    return printError("Invalid Label String");
                start = 0;
                end = 7;
                break;
            case "opcode":
                if (stmt.substring(9,stmt.length()).startsWith(" "))
                   return printError("Invalid opcode String");
                start = 9;
                if(stmt.length()<14)
                    end = stmt.length();
                else end = 14;
                break;
            case "operand":
                if (stmt.substring(17,stmt.length()).startsWith(" "))
                    return printError("Invalid operand String");
                start = 17;
                if(stmt.length()<=34)
                    end = stmt.length();
                else 
                { 
                    end = 34;
                }

                break;
            case "comment":
                start = 35;
                end = stmt.length();
            default:
                return printError("unidentified operation");
        }
        String trgStm = stmt.substring(start, end).trim().toLowerCase();
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
        switch(errormessage){
            case "Undefined Symbol":
                return "Undefined Symbol";
            case "No Starting Address":
                return "No Starting Address";
            case "Invalid Operator":
                return "Invalid Operator";
            case "Invalid Operation Code":
                return "Invalid Operation Code";
            case "duplicate label":
                return "duplicate label";
            case "Undefined Record":
                return "Undefined Record";
            default:
                return null;
        }
    }
    
    public static String writeObjectProg(String type, String order, String oprAdd,String codeAdd) {
        String line = null;            
        String progLength = "000000";
        int h =  Integer.toHexString(programLength).toString().length();
        progLength = progLength.substring(0,6-h)+ Integer.toHexString(programLength);
        switch (type) {
            case "H":
                line = "H" + Pass2.progName + startAddress +progLength;
                break;
            case "T":
                switch (order) {
                    case "initialize":
                        line = "T" + recStart + recLength + objcode;
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
    
    public static void writeEnd(File file, String stAdd)
    {
         String buffer;  
         buffer = "E"+stAdd;
         writeLine(buffer, file);   
    
    }
}
