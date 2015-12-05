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
import static pass1_1.Pass2.temp;
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
    public static String temp;
    public static String recStart;
    public static int recLength;
    public static int x = 0;

    public static void main(String[] args) {

       File lstFile = Utility.checkFile("LISTFILE");
       File objFile = Utility.checkFile("OBJFILE");
       
        ArrayList<String> lines = new ArrayList<String>();
        lines = Utility.readAllLines("INTFILE");

        int j = 0;
        String current = lines.get(j);

        if (Utility.readStm(current, "opcode").equals("start")) {
            Utility.writeLine(current, lstFile);
            progName = Utility.readStm(current, "label");
            j++;
            current = lines.get(j);
        }

        /*write H record in object program*/
        temp = Utility.writeObjectProg("H", null);
        Utility.writeLine(temp, objFile);
        /*initialise T record*/
        temp = Utility.writeObjectProg("T", "initialise");
        Utility.writeLine(temp, objFile);

        while (!Utility.readStm(current, "opcode").equals("end")) {
            if (!isComment(current)) {                                                                  //If this is not a comment
                if (OPTAB.optab.containsKey(Utility.readStm(current, "opcode"))) {                              //Search optab for opcode
                    if (Utility.readStm(current, "operand").startsWith("0")) {                                  //found, 
                        opAdd = Utility.readStm(current, "operand").substring(1);
                    } else if (Utility.readStm(current, "operand") != "") {
                        if (Utility.readStm(current, "operand").contains(",")) {                                //Indexing
                            int cut = Utility.readStm(current, "operand").indexOf(",");
                            String operand = Utility.readStm(current, "operand").substring(0, cut - 1);
                            if (Pass1.symtab.containsKey(operand)) {
                                opAdd = (String) Pass1.symtab.get(operand);
                                x = Integer.parseInt(Utility.readStm(current, "operand").substring(cut + 1));// law el x be 1, incremenet OPAdd, nezawed 3al PC el x
                            } else {
                                opAdd = "0";
                                Utility.printError("Undefined Symbol");
                            }
                        } else {
                            if (Pass1.symtab.containsKey(Utility.readStm(current, "operand"))) {
                                opAdd = (String) Pass1.symtab.get(Utility.readStm(current, "operand"));
                            } else {
                                opAdd = "0";
                                Utility.printError("Undefined Symbol");
                            }
                        }
                    }
                } else {
                    opAdd = "0";
                    /*assemble instruction object code*/
                    objcode = (String) OPTAB.optab.get(Utility.readStm(current, "opcode")) + opAdd;
                }
            } else if (Utility.readStm(current, "opcode").equals("BYTE") || Utility.readStm(current, "opcode").equals("WORD")) {
                objcode = Utility.readStm(current, "operand");
            }
            
            //if (/*object code wont fit in current T record*/) {
                /*write T record to object program*/
              //  temp = writeObjectProg("T", "initialise");
               // Pass1.writeLine(temp, file3);
                //*initialise new T record*
            //}
            
            /*add object code to T record*/
            temp = Utility.writeObjectProg("T", "add");
            Utility.writeLine(temp, objFile);
        }

        //*write listing line*
        j++;
        current = lines.get(j);
        /*write last T record to object program*/
        temp = Utility.writeObjectProg("T", "add");
        Utility.writeLine(temp, objFile);
        /*write E record to object program*/
        temp = Utility.writeObjectProg("E", null);
        Utility.writeLine(temp, objFile);
        Utility.writeLine(current, lstFile);
    }
}
