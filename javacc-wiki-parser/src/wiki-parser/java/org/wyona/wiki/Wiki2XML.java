package org.wyona.wiki;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Set;

public class Wiki2XML {

    
    /**
     * Class that takes wiki syntax input from a file
     * and outputs the according XML.
     */
    public static void main(String[] args) {
        
        if (args.length > 0)
        {
            try
            {
                FileInputStream fstream = new FileInputStream(args[0]);
                WikiParser wikiin = new WikiParser(fstream);
                new Wiki2XML().traverse(wikiin.WikiBody(),0);
            } 
            catch (Exception e)
            {
                System.err.println("File input error");
                throw new IllegalStateException(
                        "An unrecoverable error occured, see logfiles for details: " + e);          
            }
        }
        else {
            if (args.length == 0) {
                System.out.println("No file specified");
            }
        }
    }

    // needed for XML Header
    static boolean show = true;
    
    /**
     * Traverse tree and output XML
     */
    public void traverse(SimpleNode node, int depth) {

        SimpleNode n = node;        
        // display once header!
        if (show) {
            System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            show = false;
        }
                
        for (int i=0; i<depth; i++)
            System.out.print(" ");      
        System.out.print("<" + n.toString());       
        if (!node.optionMap.isEmpty()) {
            Set keySet = node.optionMap.keySet();
            Iterator kit = keySet.iterator();
            while (kit.hasNext()) {
                Object option = kit.next();
                Object value = node.optionMap.get(option);
                System.out.print(" " + option.toString() + "=" + "\"" + value.toString() + "\"");
            }
        }       
        if (n.jjtGetNumChildren() > 0) {
            System.out.println(">"); 
            for (int i = 0; i < n.jjtGetNumChildren(); i++) 
                traverse((SimpleNode)node.jjtGetChild(i), depth + 1);                        
            for (int i = 0; i < depth; i++)
                System.out.print(" ");      
            System.out.println("</" + n.toString() + ">");
        } else {
            System.out.println("/>"); 
        }
    }
}


    
