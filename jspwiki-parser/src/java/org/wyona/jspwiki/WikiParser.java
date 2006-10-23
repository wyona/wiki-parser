package org.wyona.jspwiki;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Category;

import com.ecyrd.jspwiki.TestEngine;

/**
 *
 */
public class WikiParser {
    private static Category log = Category.getInstance(WikiParser.class);
    
    private String path2wikiFile = null;
    private InputStream inputStream = null;
    private Properties props = null;
    
    public WikiParser(String path2wikiFile) {
        init(path2wikiFile);
    }
    
    /**
     *
     */
    public static void main(String[] args) {
        new WikiParser(args[0]);
    }
    
    private void init(String path2WikiFile) {
    
        try {
            this.path2wikiFile = path2WikiFile;
            loadProperties();
            transformHtml2Xml();
        } catch(Exception e) {
            log.error(e);
        }
    }
    
    /**
     * this method does mainly the job it reads the file from the repository transform it to 
     * html and calls the Html2WikiXmlTransformer to create a wiki xml file which can be edited with yulup
     */
    private void transformHtml2Xml() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path2wikiFile)));
            TestEngine engine = new TestEngine(props);
            String line = null;
            StringBuffer createdHtml = new StringBuffer();
            createdHtml.append("<html><body>");
            do {
                line = bufferedReader.readLine();
                if(line != null) {
                    line += "\\\\";
                    engine.saveText("NAME", line);
                    createdHtml.append(engine.getHTML("NAME"));                   
                }
            } while(line != null);
            createdHtml.append("</body></html>");
            log.debug(createdHtml.toString());
            Html2WikiXmlTransformer html2WikiXml = new Html2WikiXmlTransformer();
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(new java.io.StringBufferInputStream(createdHtml.toString()), html2WikiXml);
            log.debug(html2WikiXml.showTransformedXmlAsString());
            
            setResultAsInputStream(html2WikiXml.getInputStream());
        } catch(Exception e) {
            log.error(e);
        }
    }
    
    private String getPathToProperties() {
        File actualPath = new File("ABSOLUTE_PATH");
        return actualPath.getAbsolutePath().substring(0, actualPath.getAbsolutePath().indexOf("/ABSOLUTE_PATH")); 
    }
    
    /**
     * this method loads the properties for the jspWikiParser
     */
    private void loadProperties() {
        try {
            props = new Properties();
            props.load(new FileInputStream(new File(getPathToProperties() + "/properties/parser.properties")));    
        } catch(IOException e) {
            log.error("Could not load PropertyFile: ", e);
        }
    }
    
    /**
     * this method loads the InputStream which is needed for the WikiResource
     * @param inputStream
     */
    private void setResultAsInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
    
    /**
     * this method returns the Result of the Page as serialized Xml in an InputStream
     * @return WikiXml of Page as InputStream 
     */
    public InputStream getResultAsInputStream() {
        return this.inputStream;
    }
}
