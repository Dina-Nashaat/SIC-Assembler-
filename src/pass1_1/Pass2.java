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
import static pass1_1.Pass1.isComment;
import static pass1_1.Pass1.programLength;
import static pass1_1.Pass1.readStm;
import static pass1_1.Pass1.startAddress;
import static pass1_1.Pass1.writeLine;
import static pass1_1.Pass2.temp;

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

    public static ArrayList readAllLines2() {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            Scanner s = new Scanner(new File("INTFILE"));
            while (s.hasNextLine()) {
                lines.add(s.nextLine());
            }
            s.close();
        } catch (FileNotFoundException ex) {
        }
        return lines;
    }

    public static String writeObjectProg(String type, String order) {
        String line = null;
        switch (type) {
            case "H":
                line = "H" + progName + startAddress + programLength;
                break;
            case "T":
                switch (order) {
                    case "initialise":
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
                Pass1.printError("Undefined Record");
        }
        return line;
    }

    public static void main(String[] args) {

        File file2 = new File("LISTFILE");
        File file3 = new File("OBJFILE");

        ArrayList<String> lines = new ArrayList<String>();
        lines = readAllLines2();
        int j = 0;
        String current = lines.get(j);

        if (readStm(current, "opcode").equals("start")) {
            Pass1.writeLine(current, file2);
            progName = readStm(current, "label");
            j++;
            current = lines.get(j);
        }

        /*write H record in object program*/
        temp = writeObjectProg("H", null);
        Pass1.writeLine(temp, file3);
        /*initialise T record*/
        temp = writeObjectProg("T", "initialise");
        Pass1.writeLine(temp, file3);

        while (!readStm(current, "opcode").equals("end")) {
            if (!isComment(current)) {
                if (OPTAB.optab.containsKey(readStm(current, "opcode"))) {
                    if (readStm(current, "operand").startsWith("0")) {
                        opAdd = readStm(current, "operand").substring(1);
                    } else if (readStm(current, "operand") != "") {
                        if (readStm(current, "operand").contains(",")) {
                            int cut = readStm(current, "operand").indexOf(",");
                            String operand = readStm(current, "operand").substring(0, cut - 1);
                            if (Pass1.symtab.containsKey(operand)) {
                                opAdd = (String) Pass1.symtab.get(operand);
                                x = Integer.parseInt(readStm(current, "operand").substring(cut + 1));
                            } else {
                                opAdd = "0";
                                Pass1.printError("Undefined Symbol");
                            }
                        } else {
                            if (Pass1.symtab.containsKey(readStm(current, "operand"))) {
                                opAdd = (String) Pass1.symtab.get(readStm(current, "operand"));
                            } else {
                                opAdd = "0";
                                Pass1.printError("Undefined Symbol");
                            }
                        }
                    }
                } else {
                    opAdd = "0";
                    /*assemble instruction object code*/
                    objcode = (String) OPTAB.optab.get(readStm(current, "opcode")) + opAdd;
                }
            } else if (readStm(current, "opcode").equals("BYTE") || readStm(current, "opcode").equals("WORD")) {
                objcode = readStm(current, "operand");
            }
            if (/*object code wont fit in current T record*/) {
                /*write T record to object program*/
                temp = writeObjectProg("T", "initialise");
                Pass1.writeLine(temp, file3);
                //*initialise new T record*
            }
            /*add object code to T record*/
            temp = writeObjectProg("T", "add");
            Pass1.writeLine(temp, file3);
        }

        //*write listing line*
        j++;
        current = lines.get(j);
        /*write last T record to object program*/
        temp = writeObjectProg("T", "add");
        Pass1.writeLine(temp, file3);
        /*write E record to object program*/
        temp = writeObjectProg("E", null);
        Pass1.writeLine(temp, file3);
        Pass1.writeLine(current, file2);
    }
}
