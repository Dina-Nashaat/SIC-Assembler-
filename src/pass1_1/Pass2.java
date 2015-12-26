/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pass1_1;

import java.io.File;
import java.util.ArrayList;
import static pass1_1.Pass1.startAddress;
import static pass1_1.Pass1.symtab;
//import static pass1_1.Pass2.tempObjCode;
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
                    }
                    else if (Utility.readStm(current, "operandLiteral").startsWith("="))
                    {
                        String op = Utility.checkLiterals(current,"operand");
                        opAdd = Pass1.littab.get(op);
                        
                    } 
                    else if (Utility.readStm(current, "operand") != "") {
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

                }else if (Utility.readStm(current, "label").startsWith("*"))
                    {
                        String op = Utility.checkLiterals(current,"literal");
                        symAdd = "";
                        opAdd = Utility.asciiToHex(op);   
                    } 
                else if (Utility.readStm(current, "opcode").equalsIgnoreCase("equ")) {
                    String res = null;
                    int result = 0;

                    if (Utility.readStm(current, "operand").equals("*")) {
                        res = counter;
                    } else if (Utility.readStm(current, "operand").matches(".*[0-9].*")) {
                        result = Integer.parseInt(Utility.readStm(current, "operand"));
                        res = Integer.toHexString(result);
                    } else if (Utility.readStm(current, "operand").contains("+") || Utility.readStm(current, "operand").contains("-") ) {
                        String[] token = Utility.readStm(current, "operand").split("-|\\+");
                        int a = Integer.parseInt((String) symtab.get(token[0]),16);
                        int b = Integer.parseInt((String) symtab.get(token[1]),16);
                        if (Utility.readStm(current, "operand").contains("+")) {
                            result = a + b;
                        } else if (Utility.readStm(current, "operand").contains("-")) {
                            result = Math.abs(a - b);
                        }
                        res = Integer.toHexString(result);
                    }else{
                        printError("Undefined Operand");
                    }
                    String y = "000000";
                    int h = res.length();
                    opAdd = y.substring(0, 6 - h) + res;
                    symAdd = "";

                } else if (Utility.readStm(current, "opcode").equalsIgnoreCase("byte") || Utility.readStm(current, "opcode").equalsIgnoreCase("word")) {
                    opAdd = "000000";
                    String a = Utility.readStm(current, "operand");
                    int b = Integer.parseInt(a);
                    String c = Integer.toHexString(b);
                    int h = c.length();
                    opAdd = opAdd.substring(0, 6 - h) + c;
                    symAdd = "";
                } else {
                    opAdd = "";
                    symAdd = "      ";
                }

                Rec = symAdd.concat(opAdd);
                record = record.concat(Rec);

                if (record.length() == 60) {
                    int current_address = Integer.parseInt(counter, 16);
                    int record_start = Integer.parseInt(recStart, 16);
                    int record_length = current_address - record_start + 3;
                    String record_length_hex = Integer.toHexString(record_length);
                    Utility.writeTxt(objFile, recStart, record_length_hex, record);            //write T record in object program
                    record = "";
                    //recStart = counter;
                    String n = "000000";
                    String hexa = addHex(counter, 3);
                    int m = hexa.length();
                    recStart = n.substring(0, 6 - m) + hexa;
                }

                if (error == true) {
                    Utility.writeToLST(current, lstFile, counter, errorstr);
                } else {
                    Utility.writeToLST(current, lstFile, counter, null);
                }

                j++;
                if (j== lines.size()) break;
                current = lines.get(j);
                counter = current.substring(68, 74);
            }

            if (Utility.readStm(current, "opcode").equals("end")) {
                String s = "00";
                /*
                 recLength = Integer.toHexString(record.length());
                 int g = recLength.length();
                 recLength = s.substring(0, 2 - g) + recLength;
                 */
                int current_address = Integer.parseInt(counter, 16);
                int record_start = Integer.parseInt(recStart, 16);
                int record_length = (current_address - record_start +3) / 2;
                String record_length_hex = Integer.toHexString(record_length);
                int g = record_length_hex.length();
                recLength = s.substring(0, 2 - g) + record_length_hex;
                
                Utility.writeToLST(current, lstFile, counter, null);
                Utility.writeTxt(objFile, recStart, recLength, record);
                Utility.writeEnd(objFile, startAddress);
            }
        }

    }
}
