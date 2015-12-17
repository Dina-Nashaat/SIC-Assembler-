/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pass1_1;

import java.io.File;
import java.util.ArrayList;
import static pass1_1.Pass1.startAddress;
//import static pass1_1.Pass2.tempObjCode;
import static pass1_1.Utility.addHex;
import static pass1_1.Utility.isComment;

/**
 *
 * @author Heba
 */
public class Pass2 {

    public static String progName;
    public static String opAdd;
    public static String objcode;
    public static String symAdd;
    public static String current;
    public static String record = "";
    public static String recStart = "0";
    public static String recLength;
    public static String Rec = "000000";
    public static String counter = "0";
    public static String errorstr;
    public static boolean error = false;

    public static void pass_2() {

        //Create LISTFILE and OBJFILE
        File lstFile = Utility.checkFile("LISTFILE");
        File objFile = Utility.checkFile("OBJFILE");

        ArrayList<String> lines = new ArrayList<String>();
        lines = Utility.readAllLines("INTFILE");
        int codeAdd = 0;
        int x = 0;
        int j = 0;      //Lines counter      
        current = lines.get(j);

        if (Utility.readStm(current, "opcode").equals("start")) {
            progName = Utility.readStm(current, "label");
            counter = startAddress;
            recStart = counter;
            //j++;
            //current = lines.get(j);
        } else {
            errorstr = Utility.printError("No Starting Address");
            error = true;
        }
        if (error == true) {
            Utility.writeToLST(current, lstFile, counter, errorstr);
        } else {
            Utility.writeToLST(current, lstFile, counter, null);
        }

        Utility.writeLine(Utility.writeObjectProg("H", null, null, null), objFile);
        j++;
        current = lines.get(j);
        counter = current.substring(68, 74);

        while (!Utility.readStm(current, "opcode").equalsIgnoreCase("end")) {                    //While this is not end of program
            if (!isComment(current)) {                                                                  //If this is not a comment
                if (OPTAB.optab.containsKey(Utility.readStm(current, "opcode"))) {                //Search optab for opcode

                    if (Utility.readStm(current, "operand").startsWith("0")) {                          //found, but contains address in HEX (starts with 0)
                        opAdd = Utility.readStm(current, "operand").substring(1);
                    } else if (Utility.readStm(current, "operand") != "") {
                        if (Utility.readStm(current, "operand").contains(",")) {                             //Indexing
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
                            //codeAdd = Integer.parseInt(OPTAB.optab.get(Utility.readStm(current, "opcode")).toString());
                        } else {
                            opAdd = "0";
                            errorstr = Utility.printError("Undefined Symbol");
                            error = true;
                        }
                    }

                    codeAdd = (int) OPTAB.optab.get(Utility.readStm(current, "opcode"));
                    String y = "00";
                    String w = Integer.toHexString(codeAdd);
                    int h = w.length();
                    symAdd = y.substring(0, 2 - h) + w;

                } else if (Utility.readStm(current, "opcode").equalsIgnoreCase("byte") || Utility.readStm(current, "opcode").equalsIgnoreCase("word")) {
                    opAdd = "000000";
                    String a = Utility.readStm(current, "operand");
                    int b = Integer.parseInt(a);
                    String c = Integer.toHexString(b);
                    int h = c.length();
                    opAdd = opAdd.substring(0, 6 - h) + c;
                    symAdd = "";
                }
                else{
                    opAdd = "";
                    symAdd = "      ";
                }

                Rec = symAdd.concat(opAdd);
                record = record.concat(Rec);

                if (record.length() == 60) {
                    Integer inputDec = Integer.parseInt(counter, 16);
                    inputDec = (inputDec - 1) - (Integer.parseInt(recStart));
                    String outputHex = Integer.toHexString(inputDec);
                    Utility.writeTxt(objFile, recStart.toUpperCase(), outputHex, record.toUpperCase());            //write T record in object program
                    record = "";
                    recStart = counter.toUpperCase();
                }

                if (error == true) {
                    Utility.writeToLST(current, lstFile, counter, errorstr);
                } else {
                    Utility.writeToLST(current, lstFile, counter, null);
                }

                j++;
                current = lines.get(j);
                counter = current.substring(68, 74);

            }
            
            if (Utility.readStm(current, "opcode").equals("end")) {
                recLength = Integer.toHexString(record.length()/2);
                Utility.writeTxt(objFile, counter, recLength, record);
                Utility.writeEnd(objFile, startAddress);
            }
        }
        
    }
}
