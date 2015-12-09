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
import java.util.Hashtable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pass1_1.Utility.*;
import static pass1_1.Utility.addHex;
import static pass1_1.Utility.isComment;
import static pass1_1.Utility.printError;

/**
 *
 * @author Heba
 */
public class Pass1 {

    public static String locctr = "0";
    public static String startAddress = "000000";
    public static int programLength;
    public static String address = "000000";
    public static Hashtable symtab = new Hashtable();
    public static boolean isCommentflag = false;

    public static void main(String[] args) {

        //Create new File
        File intFile = Utility.checkFile("INTFILE");
        OPTAB optab = new OPTAB();
        ArrayList<String> lines = new ArrayList<String>();
        lines = Utility.readAllLines("SRCFILE");
        String line;
        int i = 0;
        line = lines.get(i);                                              //Read next input line

        while (isComment(line)) {
            i++;
            line = lines.get(i);
            isCommentflag = true;
        }

        if (Utility.readStm(line, "opcode").equals("start")) //if OPCODE = 'START' then
        {
            int h = Utility.readStm(line, "operand").length();
            startAddress = startAddress.substring(0, 6 - h) + Utility.readStm(line, "operand");
            locctr = startAddress;
            address = locctr;
            Utility.writeToINT(line, intFile, null, isCommentflag);
            //i++;
            //line = lines.get(i);
            isCommentflag = false;
            //writeLine(line, file);                                                                  //Write line to intermediate file
            //System.out.println(startAddress);
        } else {
            locctr = "0";                                                                                    //else initialize locctr to zero
        }
        i++;
        line = lines.get(i);

        while (!Utility.readStm(line, "opcode").equals("end")) //while OPCODE != END
        {
            Utility.writeToINT(line, intFile, address.toUpperCase(), isCommentflag);

            if (!isComment(line)) {                                                                       //If line is not a comment
                if (symtab.containsKey(Utility.readStm(line, "label"))) {                       //If there is a label in the LABEL field, Search symtab for Label
                    printError("duplicate label");                                                 //Print out Error
                } else //If not
                if (!(Utility.readStm(line, "label").equals(""))) {
                    symtab.put(Utility.readStm(line, "label"), locctr);                        //Insert Label into symtable
                }
                if (OPTAB.optab.containsKey(Utility.readStm(line, "opcode"))) {                   //Search optab for opcode
                    {
                        locctr = addHex(locctr, 3);
                        int h = locctr.length();
                        isCommentflag = false;
                        address = "000000";
                        address = address.substring(0, 6 - h) + locctr;
                    }                                                //Found; Add 3 to locctr
                } else if (Utility.readStm(line, "opcode").equals("word")) {               //opcode not found but equal to WORD
                    locctr = addHex(locctr, 3);                                                 //Founnd; Add 3 to locctr
                    int h = locctr.length();
                    isCommentflag = false;
                    address = "000000";
                    address = address.substring(0, 6 - h) + locctr;
                } else if (Utility.readStm(line, "opcode").equals("resw")) {                //opcode not found but equal to RESW
                    try {
                        int operandOp = 3 * Integer.parseInt(Utility.readStm(line, "operand"));
                        locctr = addHex(locctr, operandOp);                               //Found; 3*operand+locctr
                        int h = locctr.length();
                        isCommentflag = false;
                        address = "000000";
                        address = address.substring(0, 6 - h) + locctr;
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else if (Utility.readStm(line, "opcode").equals("resb")) {                  //opcode not found but equal to BYTE
                    try {
                        locctr = addHex(locctr, Integer.parseInt(Utility.readStm(line, "operand")));  //Found; add #operand to locctr
                        int h = locctr.length();
                        isCommentflag = false;
                        address = "000000";
                        address = address.substring(0, 6 - h) + locctr;
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else if (Utility.readStm(line, "opcode").equals("byte")) {
                    int byteLength = Utility.readStm(line, "operand").length();                //find length in bytes
                    try {
                        locctr = addHex(locctr, byteLength);                                //add length to LOCCTR
                        int h = locctr.length();
                        isCommentflag = false;
                        address = "000000";
                        address = address.substring(0, 6 - h) + locctr;
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else {
                    printError("Invalid Operation Code");
                }

            }
            i++;
            line = lines.get(i);
            while (isComment(line)) {
                i++;
                line = lines.get(i);
                isCommentflag = true;
            }
        }
        if (Utility.readStm(line, "opcode").equals("end")) {
            address = "000000";
            int h = locctr.length();
            address = address.substring(0, 6 - h) + locctr;
            Utility.writeToINT(line, intFile, address.toUpperCase(), isCommentflag);
        }
        try {
            programLength = Integer.parseInt(locctr, 16) - Integer.parseInt(startAddress, 16);
        } catch (NumberFormatException ex) {
        }

        System.out.println("Start Address = " + startAddress);
        System.out.println("LCCTR = " + locctr.toUpperCase());
        System.out.println("Program Length = " + programLength);

        Pass2.pass_2();
    }
}
