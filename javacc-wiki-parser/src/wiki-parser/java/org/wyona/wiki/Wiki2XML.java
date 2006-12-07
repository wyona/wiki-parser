package org.wyona.wiki;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Category;
import org.wyona.wikiparser.IWikiParser;

public class Wiki2XML implements IWikiParser {

    private static Category log = Category.getInstance(Wiki2XML.class);
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
                WikiParser wikiin = new WikiParser();
                //wikiin.init(fstream);
                wikiin.init(new WikiParserTokenManager(new SimpleCharStream(
                new java.io.InputStreamReader(new FileInputStream(args[0]), "UTF-8"))));
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
    
    StringBuffer xmlAsStringBuffer = new StringBuffer();
    /**
     * Traverse tree and output XML
     */
    public void traverse(SimpleNode node, int depth) {

        SimpleNode n = node;        
        // display once header!
        if (show) {
            System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xmlAsStringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            show = false;
        }
                
        for (int i=0; i<depth; i++) {
            System.out.print(" ");
            xmlAsStringBuffer.append(" ");
        }
            
        
        System.out.print("<" + n.toString());
        xmlAsStringBuffer.append("<" + n.toString());
        if (!node.optionMap.isEmpty()) {
            Set keySet = node.optionMap.keySet();
            Iterator kit = keySet.iterator();
            while (kit.hasNext()) {
                Object option = kit.next();
                Object value = node.optionMap.get(option);
                System.out.print(" " + option.toString() + "=" + "\"" + value.toString() + "\"");
                xmlAsStringBuffer.append(" " + option.toString() + "=" + "\"" + value.toString() + "\"");
            }
        }       
        if (n.jjtGetNumChildren() > 0) {
            System.out.println(">"); 
            xmlAsStringBuffer.append(">");
            for (int i = 0; i < n.jjtGetNumChildren(); i++) 
                traverse((SimpleNode)node.jjtGetChild(i), depth + 1);                        
            for (int i = 0; i < depth; i++) {
                System.out.print(" ");    
                xmlAsStringBuffer.append(" ");
            }
            System.out.println("</" + n.toString() + ">");
            xmlAsStringBuffer.append("</" + n.toString() + ">");
        } else {
            System.out.println("/>");
            xmlAsStringBuffer.append("/>");
        }
    }
    
    public void parse(InputStream inputStream) {
        try {
            WikiParser wikiParser = new WikiParser(inputStream);
            traverse(wikiParser.WikiBody(),0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    public InputStream getInputStream() {
        String output = xmlAsStringBuffer.toString();
        output = output.replaceAll("<WikiBody>", "<wiki xmlns:wiki=\"http://www.wyona.org/yanel/1.0\" xmlns=\"http://www.wyona.org/yanel/1.0\"><WikiBody>");
        output = output.replaceAll("</WikiBody>", "</WikiBody></wiki>");
        //log.error("xmlfile is : " + output);
        return new ByteArrayInputStream(output.getBytes());
    }
}
