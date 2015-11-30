
import java.util.Hashtable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Heba
 */
public class OPTAB {
    
    Hashtable optab = new Hashtable();

    public OPTAB() {
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
    }
    
        
}
