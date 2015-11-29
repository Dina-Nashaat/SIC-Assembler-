/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pass1_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    public static String locctr;
    
    public static ArrayList readAllLines ()
    {
        ArrayList<String> lines= new ArrayList<String>();
        try {
            Scanner s = new Scanner(new File ("SRCFILE"));
            while(s.hasNextLine())
            {
                lines.add(s.nextLine());
            }
            s.close();
        } catch (FileNotFoundException ex) {
        }
          return lines;
    }

    static String addHex(String inputHex) {
        Integer inputDec = Integer.parseInt(inputHex, 16);
        inputDec += 3;
        String outputHex = Integer.toHexString(inputDec);
        return outputHex;
    }

    static String decToHex(int deci) {
        String hexa = Integer.toHexString(deci);
        return hexa;
    }

    static boolean testHexOperand(String x) {
        return x.startsWith("0");
    }

    public static String readStm(String stmt, String type)
    {
        type = type.toLowerCase();
        int start=0, end=0;
        switch(type)
        {
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
            default:
                return "unidentified operation";
        }
        String trgStm = stmt.substring(start, end);
        return trgStm;
    }
    
    public static boolean isComment (String stmt)
    {        
        if(stmt.startsWith("."))
            return true;
        return false;
    }
    public static void main(String[] args) {
        // TODO code application logic here 
        Hashtable optab = new Hashtable();
        optab.put("add", 24);
        optab.put("and", 64);
        optab.put("comp", 40);
        optab.put("div", 36);
        optab.put("j", 60);
        optab.put("jeq", 48);
        optab.put("jgt", 52);
        optab.put("jlt", 56);
        optab.put("jsub", 72);
        optab.put("lda", 0);
        optab.put("ldch", 80);
        optab.put("ldl", 8);
        optab.put("ldx", 4);
        optab.put("mul", 32);
        optab.put("or", 68);
        optab.put("rd", 216);
        optab.put("rsub", 76);
        optab.put("sta", 12);
        optab.put("stch", 84);
        optab.put("stl", 20);
        optab.put("stx", 16);
        optab.put("sub", 28);
        optab.put("td", 224);
        optab.put("tix", 44);
        optab.put("wd", 220);
        
        Hashtable symtab = new Hashtable();
        
        ArrayList<String> line = new ArrayList<String>();
        line = readAllLines();
        
    }

    
}
