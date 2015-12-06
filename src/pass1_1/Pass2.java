/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pass1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import static pass1_1.Pass1.programLength;
import static pass1_1.Pass1.startAddress;
//import static pass1_1.Pass2.tempObjCode;
import pass1_1.Utility;
import static pass1_1.Utility.addHex;
import static pass1_1.Utility.isComment;
import static pass1_1.Utility.printError;

/**
 *
 * @author Heba
 */
public class Pass2 {

    public static String progName;
    public static String opAdd;
    public static String objcode;

    public static String record = "";
    public static String recStart;
    public static String recLength;
    public static String Rec;
    public static String errorstr;
    public static boolean error = false;
    public static String SA;

    public static void pass_2() {

        //Create LISTFILE and OBJFILE
        File lstFile = Utility.checkFile("LISTFILE");
        File objFile = Utility.checkFile("OBJFILE");
        String locctr = "0";
        ArrayList<String> lines = new ArrayList<String>();
        lines = Utility.readAllLines("INTFILE");
        int x = 0;
        int j = 0;      //Lines counter
        String current = lines.get(j);
        int codeAdd = 0;

        if (Utility.readStm(current, "opcode").equals("start")) {
            progName = Utility.readStm(current, "label");
            locctr = startAddress;
            SA = locctr;
//            j++;
            //          current = lines.get(j);
        } else {
            errorstr = Utility.printError("No Starting Address");
            error = true;
        }
        if (error == true) {
            Utility.writeToLST(current, lstFile, locctr, errorstr);
        } else {
            Utility.writeToLST(current, lstFile, locctr, null);
        }

        Utility.writeLine(Utility.writeObjectProg("H", null, null, null), objFile);
        while (!Utility.readStm(current, "opcode").equals("end")) {                                     //While this is not end of program
            if (!isComment(current)) {                                                                  //If this is not a comment
                if (OPTAB.optab.containsKey(Utility.readStm(current, "opcode"))) {                      //Search optab for opcode
                    if (Utility.readStm(current, "operand").startsWith("0")) {                          //found, but contains address in HEX (starts with 0)
                        opAdd = Utility.readStm(current, "operand").substring(1);
                    } else if (Utility.readStm(current, "operand") != "") {
                        if (Utility.readStm(current, "operand").contains(",")) {                                //Indexing
                            int cut = Utility.readStm(current, "operand").indexOf(",");
                            String operand = Utility.readStm(current, "operand").substring(0, cut - 1);
                            if (Pass1.symtab.containsKey(operand)) {
                                opAdd = (String) Pass1.symtab.get(operand);
                                x = Integer.parseInt(Utility.readStm(current, "operand").substring(cut + 1));// law el x be 1, incremenet OPAdd, nezawed 3al PC el x
                                opAdd = addHex(opAdd, x);
                            } else {
                                opAdd = "0";
                                Utility.printError("Undefined Symbol");
                            }
                        } else if (Pass1.symtab.containsKey(Utility.readStm(current, "operand"))) {
                            opAdd = (String) Pass1.symtab.get(Utility.readStm(current, "operand"));
                            codeAdd = Integer.parseInt(OPTAB.optab.get(Utility.readStm(current, "opcode")).toString());
                        } else {
                            opAdd = "0";
                            errorstr = Utility.printError("Undefined Symbol");
                            error = true;
                        }
                    }
                } else {
                    opAdd = "0";
                }
            } else if (Utility.readStm(current, "opcode").equals("byte") || Utility.readStm(current, "opcode").equals("word")) {
                opAdd = "000000";
                int h = Utility.readStm(current, "operand").length();
                opAdd = opAdd.substring(0, 6 - h) + Utility.readStm(current, "operand");
                
            }

            String symAdd = Integer.toHexString(codeAdd);
            switch (symAdd) {
                case "0":
                    symAdd = "00";
                    break;
                case "8":
                    symAdd = "08";
                    break;
                case "4":
                    symAdd = "04";
                    break;
                case "c":
                    symAdd = "0c";
                default:
                    break;

            }

            Rec = symAdd.concat(opAdd);
            record = record.concat(Rec);

            if (record.length() == 60) {
                Integer inputDec = Integer.parseInt(locctr, 16);
                inputDec = (inputDec-1) - (Integer.parseInt(SA));
                String outputHex = Integer.toHexString(inputDec);
                Utility.writeTxt(objFile, SA, outputHex, record);                              //write H record in object program
                record = "";
                SA = locctr;
            }
            if (error == true) {
                Utility.writeToLST(current, lstFile, locctr, errorstr);
            } else {
                Utility.writeToLST(current, lstFile, locctr, null);
            }

            j++;
            current = lines.get(j);
            if (!(Utility.readStm(current, "opcode").equals("end"))) {
                locctr = current.substring(68, 74);
            }
            if (Utility.readStm(current, "opcode").equals("end")) {
                recLength = Integer.toHexString(record.length());
                Utility.writeTxt(objFile, locctr, recLength, record);
                Utility.writeEnd(objFile, startAddress);
            }
        }
    }
}
