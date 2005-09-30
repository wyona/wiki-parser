
package org.wyona.wiki;

import java.io.FileInputStream;
import org.apache.log4j.Category;

public class Wiki2XML {

    static Category log = Category.getInstance(Wiki2XML.class);
    
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
                log.error(e);
                throw new IllegalStateException(
                        "An unrecoverable error occured, see logfiles for details: " + e);          
            }
        }
        else
            System.out.println("Invalid parameters");
    }
}
    