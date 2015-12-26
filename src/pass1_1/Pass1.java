/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pass1_1;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import static pass1_1.Utility.addHex;
import static pass1_1.Utility.hexToAscii;
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
    public static Hashtable<String,ArrayList> littab = new Hashtable();
    public static List done = new ArrayList();
    public static boolean isCommentflag = false;

    public static void main(String[] args) throws UnsupportedEncodingException {

        //Create new File
        File intFile = Utility.checkFile("INTFILE");
        OPTAB optab = new OPTAB();
        ArrayList<String> lines = new ArrayList<String>();
        lines = Utility.readAllLines("SRCFILE");
        String line;
        int i = 0;
        line = lines.get(i);                  //Read next input line

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
            Utility.writeToINT(line, intFile, startAddress, isCommentflag);
            isCommentflag = false;

        } else {
            locctr = "0";              //else initialize locctr to zero
        }

        i++;
        line = lines.get(i);

        while (!Utility.readStm(line, "opcode").equals("end")) //while OPCODE != END
        {
            String op = Utility.checkLiterals(line,"operand");
            Boolean isHex = Utility.checkHex(line, "operand");
            if (op != null) {
                if(isHex) {op = Utility.lhexToAscii(op);}
                ArrayList litVal = new ArrayList();
                litVal.add(0, op);
                litVal.add(1,isHex);
                if(!(littab.containsKey(op)))
                {
                    littab.put(op, litVal);
                }
            }
            if (Utility.readStm(line, "opcode").equals("LTORG")) {
                Enumeration<String> enumKey = littab.keys();
                while (enumKey.hasMoreElements()) {
                    String key = enumKey.nextElement();
                    ArrayList val = littab.get(key);
                    if(done.contains(key)) break;
                    boolean isHexD =(boolean) val.get(1);
                    if(isHexD) line = "*       " + "=" + "X'"+hexToAscii(key)+"'";
                    else line = "*       " + "=" + "C'"+key+"'";
                    int h = locctr.length();
                    address = address.substring(0, 6 - h) + locctr;
                    Utility.writeToINT(line, intFile, address.toUpperCase(), isCommentflag);
                    done.add(key);
                    val.add(0, locctr);
                    
                    littab.put(key, val);
                    locctr = locctr + key.length();
                    }
                }
            

            Utility.writeToINT(line, intFile, address.toUpperCase(), isCommentflag);

            if (!isComment(line)) {                               //If line is not a comment
                String label = Utility.readStm(line, "label");
                String code = Utility.readStm(line, "opcode");
                String operand = Utility.readStm(line, "operand");

                //check on labels: 
                if (symtab.containsKey(label)) {       //If there is a label in the LABEL field, search symtab for Label
                    if (code.equals("equ")) {                //If opcode is EQU, replace old value with new operand value 
                        symtab.replace(label, operand);
                    } else {
                        printError("Duplicate Label");
                    }

                } else if (!label.equals("")) {                            //If LABEL exists but not in SYMTAB
                    /*if (code.equals("word") || code.equals("byte")) {
                        symtab.put(label, operand);
                    } else {*/
                    symtab.put(label, locctr);                           //Insert Label into symtable
                }
                /*else {
                     printError("Illegal Operand Expression");
                     }*/

                //check on opcodes:
                if (OPTAB.optab.containsKey(code)) {                   //Search optab for opcode
                    {
                        locctr = addHex(locctr, 3);                  //Found; Add 3 to locctr
                        int h = locctr.length();
                        isCommentflag = false;
                        address = "000000";
                        address = address.substring(0, 6 - h) + locctr;
                    }
                } else if (code.equals("word")) {               //opcode not found but equal to WORD
                    locctr = addHex(locctr, 3);                     //Founnd; Add 3 to locctr
                    int h = locctr.length();
                    isCommentflag = false;
                    address = "000000";
                    address = address.substring(0, 6 - h) + locctr;
                } else if (code.equals("resw")) {                //opcode not found but equal to RESW
                    try {
                        int operandOp = 3 * Integer.parseInt(operand);
                        locctr = addHex(locctr, operandOp);                               //Found; 3*operand+locctr
                        int h = locctr.length();
                        isCommentflag = false;
                        address = "000000";
                        address = address.substring(0, 6 - h) + locctr;
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else if (code.equals("resb")) {                  //opcode not found but equal to BYTE
                    try {
                        locctr = addHex(locctr, Integer.parseInt(operand));  //Found; add #operand to locctr
                        int h = locctr.length();
                        isCommentflag = false;
                        address = "000000";
                        address = address.substring(0, 6 - h) + locctr;
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else if (code.equals("byte")) {
                    int byteLength = operand.length();          //find length in bytes
                    try {
                        locctr = addHex(locctr, byteLength);               //add length to LOCCTR
                        int h = locctr.length();
                        isCommentflag = false;
                        address = "000000";
                        address = address.substring(0, 6 - h) + locctr;
                    } catch (Exception ex) {
                        printError("Invalid Operator");
                    }
                } else if (code.equals("equ")) {
                    //do nothing
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
        Enumeration<String> enumKey = littab.keys();
        while (enumKey.hasMoreElements()) {
                    String key = enumKey.nextElement();
                    if(done.contains(key)) break;
                    ArrayList val = littab.get(key);
                    boolean isHexD =(boolean) val.get(1);
                    if(isHexD) line = "*       " + "=" + "X'"+Utility.asciiToHex(key)+"'";
                    else line = "*       " + "=" + "C'"+key+"'";
                    int h = locctr.length();
                    address = address.substring(0, 6 - h) + locctr;
                    Utility.writeToINT(line, intFile, address.toUpperCase(), isCommentflag);
                    done.add(key);
                    littab.put(key, val);
                    locctr = locctr + key.length();
                    }


        try {
            programLength = Integer.parseInt(locctr, 16) - Integer.parseInt(startAddress, 16);
        } catch (NumberFormatException ex) {
        }

        System.out.println(
                "Start Address = " + startAddress);
        System.out.println(
                "LCCTR = " + locctr.toUpperCase());
        System.out.println(
                "Program Length = " + programLength);

        Pass2.pass_2();
    }
}
