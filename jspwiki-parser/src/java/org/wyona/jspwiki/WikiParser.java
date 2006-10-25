package org.wyona.jspwiki;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Category;

import com.ecyrd.jspwiki.WikiContext;
import com.ecyrd.jspwiki.WikiEngine;
import com.ecyrd.jspwiki.WikiPage;

/**
 *
 */
public class WikiParser extends org.wyona.wikiparser.WikiParser {
    private static Category log = Category.getInstance(WikiParser.class);
    
    private InputStream inputStream = null;
    private Properties properties = null;
    
    public WikiParser(InputStream is) {
        init(is);
    }
    
    /**
     *
     */
    public static void main(String[] args) {
        FileInputStream fstream;
        try {
            fstream = new FileInputStream(args[0]);
            new WikiParser(fstream);
        } catch (FileNotFoundException e) {
            log.error(e);
        }
    }
    
    private void init(InputStream is) {
        inputStream = is;
        loadProperties();
    }    
    
    public void parse(InputStream inputStream) {
        transformHtml2Xml();
        getInputStream();
    }
    
    /**
     * this method does mainly the job it reads the file from the repository transform it to 
     * html and calls the Html2WikiXmlTransformer to create a wiki xml file which can be edited with yulup
     */
    private void transformHtml2Xml() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            
            WikiEngine engine = new WikiEngine(properties);
            WikiPage page = new WikiPage(engine, "PAGE");
            WikiContext context = new WikiContext(engine, page);
            
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");                
            }
            StringBuffer createdHtml = new StringBuffer();
            createdHtml.append("<html><body>");
            createdHtml.append(engine.textToHTML(context, stringBuffer.toString()));
            createdHtml.append("<html><body>");

            Html2WikiXmlTransformer html2WikiXml = new Html2WikiXmlTransformer();
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(new java.io.ByteArrayInputStream(createdHtml.toString().getBytes()), html2WikiXml);
            setResultAsInputStream(html2WikiXml.getInputStream());
        } catch(Exception e) {
            log.error(e);
        }
    }
    
    /**
     * this method loads the properties for the jspWikiParser
     */
    private void loadProperties() {
        try {
            properties = new Properties();
            InputStream is = getClass().getResourceAsStream( "/parser.properties" );
            properties.load(is);    
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
    public InputStream getInputStream() {
        return this.inputStream;
    }
    
    /**
     * this method simply prints data from the inputStream into the logfile
     *
     */
    public void debugInputStream() {
        try {
            InputStreamReader inR = new InputStreamReader(inputStream);
            BufferedReader buf = new BufferedReader(inR);
            String line;
            while ((line = buf.readLine()) != null) {
                log.debug(line);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }
}
