/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pass1_1;

import static java.awt.JobAttributes.DestinationType.FILE;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.System.exit;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Heba
 */
public class Pass1_1 {

    /**
     */
    public static String locctr = "0";
    public static String startAddress = "0";
    public static int programLength;

    public static ArrayList readAllLines() {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            Scanner s = new Scanner(new File("SRCFILE"));
            while (s.hasNextLine()) {
                lines.add(s.nextLine());
            }
            s.close();
        } catch (FileNotFoundException ex) {
        }
        return lines;
    }

    public static void writeLine(String line, File file) {

        try {
            FileWriter fw = new FileWriter(file, true);
            PrintWriter writer = new PrintWriter(fw);
            writer.println(line.toLowerCase());
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Pass1_1.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public static String readStm(String stmt, String type) {
        type = type.toLowerCase();
        int start, end;
        switch (type) {
            case "label":
                start = 0;
                end = 7;
                break;
            case "opcode":
                start = 9;
                end = 14;
                break;
            case "operand":
                start = 17;
                end = stmt.length();
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

    public static boolean isComment(String stmt) {
        return stmt.startsWith(".");
    }

    public static void main(String[] args) {
        // TODO code application logic here 
        File file = new File("INTFILE");
        if (file.exists()) {
            file.delete();
        } else {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Pass1_1.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        OPTAB optab = new OPTAB();
        Hashtable symtab = new Hashtable();

        ArrayList<String> lines = new ArrayList<String>();
        lines = readAllLines();
        String line;
        int i = 0;
        line = lines.get(i);                                                                                 //Read next input line
        while (isComment(line)) {
            i++;
            line = lines.get(i);
        }
        if (readStm(line, "opcode").equals("start")) //if OPCODE = 'START' then
        {
            startAddress = readStm(line, "operand");                                    //save #[operand] as starting address
            locctr = startAddress;                                                               //initialize LOCCTR to starting address
            writeLine(line, file);
            i++;
            line = lines.get(i);
            //writeLine(line, file);                                                                  //Write line to intermediate file
            //System.out.println(startAddress);
        } else {
            locctr = "0";                                                                                    //else initialize locctr to zero
        }

        while (!readStm(line, "opcode").equals("end")) //while OPCODE != END
        {
            if (!isComment(line)) {                                                                       //If line is not a comment
                if (symtab.containsKey(readStm(line, "label"))) {                       //If there is a label in the LABEL field, Search symtab for Label
                    printError("duplicate error");                                                 //Print out Error
                } else {                                                                                           //If not
                    if (!(readStm(line, "label").equals(""))) {
                        symtab.put(readStm(line, "label"), locctr);                        //Insert Label into symtable
                    }
                }

                if (OPTAB.optab.containsKey(readStm(line, "opcode"))) {                   //Search optab for opcode
                    locctr = addHex(locctr, 3);                                                  //Found; Add 3 to locctr
                } else if (readStm(line, "opcode").equals("word")) {               //opcode not found but equal to WORD
                    locctr = addHex(locctr, 3);                                                 //Founnd; Add 3 to locctr
                } else if (readStm(line, "opcode").equals("resw")) {                //opcode not found but equal to RESW
                    try {
                        int operandOp = 3 * Integer.parseInt(readStm(line, "operand"));
                        locctr = addHex(locctr, operandOp);                               //Found; 3*operand+locctr
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else if (readStm(line, "opcode").equals("resb")) {                  //opcode not found but equal to BYTE
                    try {
                        locctr = addHex(locctr, Integer.parseInt(readStm(line, "operand")));  //Found; add #operand to locctr
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else if (readStm(line, "opcode").equals("byte")) {
                    int byteLength = readStm(line, "operand").length();                //find length in bytes
                    try {
                        locctr = addHex(locctr, byteLength);                                //add length to LOCCTR
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else {
                    printError("Invalid Operation Code");
                }
                writeLine(line, file);
            }
            i++;
            line = lines.get(i);
            while (isComment(line)) {
                i++;
                line = lines.get(i);
            }
        }
        if (readStm(line, "opcode").equals("end")) {
            writeLine(line, file);
        }
        try {
            programLength = Integer.parseInt(locctr, 16) - Integer.parseInt(startAddress, 16);
        } catch (NumberFormatException ex) {
        }
        /*
        System.out.println("Start Address = " + startAddress);
        System.out.println("LCCTR = " + locctr.toUpperCase());
        System.out.println("Program Length = " + programLength);
        */
    }

    private static void printError(String errormessage) {
        System.out.println(errormessage);
    }
}
