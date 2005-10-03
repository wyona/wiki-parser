
package org.wyona.wiki;

import java.io.FileInputStream;

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
                wikiin.traverseXML(wikiin.WikiBody(),0);
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
}

    