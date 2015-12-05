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
            writer.println(line.toLowerCase());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Pass1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
     public static String readStm(String stmt, String type) {
        type = type.toLowerCase();
        int start, end=stmt.length();
        
        switch (type) {
            case "label":
                if (stmt.substring(0,7).contains("      "))
                    return "";
                else if(stmt.substring(0, 7).startsWith(" "))
                    return "Invalid Label String";
                start = 0;
                end = 7;
                break;
            case "opcode":
                if (stmt.substring(9,stmt.length()).startsWith(" "))
                   return "Invalid Opcode String";
                start = 9;
                if(stmt.length()<14)
                    end = stmt.length();
                else end = 14;
                break;
            case "operand":
                if (stmt.substring(17,stmt.length()).startsWith(" "))
                    return "Invalid Operand String";
                start = 17;
                if(stmt.length()<=34)
                    end = stmt.length();
                else 
                { 
                    end = 34;
                    return "Invalid Operand";
                }

                break;
            case "comment":
                start = 35;
                end = stmt.length();
            default:
                return "Unidentified operation";
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

    public static void printError(String errormessage) {
        System.out.println(errormessage);
    }
    
    public static String writeObjectProg(String type, String order) {
        String line = null;
        switch (type) {
            case "H":
                line = "H" + progName + startAddress + programLength;
                break;
            case "T":
                switch (order) {
                    case "initialize":
                        line = "T" + recStart + recLength + objcode;
                        break;
                    case "add":
                        line = objcode;
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

}
