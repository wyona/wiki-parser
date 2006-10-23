package org.wyona.jspwiki;

import java.io.ByteArrayInputStream;
import java.util.Vector;
import org.apache.log4j.Category;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Html2WikiXmlTransformer extends DefaultHandler {
    
    private final String NAME_SPACE = "http://www.wyona.org/yanel/1.0";
    
    private static Category log = Category.getInstance(Html2WikiXmlTransformer.class);
    private ByteArrayInputStream byteArrayInputStream = null;
    private StringBuffer html2xml = null;
    private Vector htmlElements = new Vector();
    
    private String listType = "";
    private String tagName = null;
    private String prevElement = null;
    private String nextElement = null;
    private String twoAhead = null;
    private String twoBack = null;
    private boolean startTag = false;
    private String plain = "";
    
    /**
     * this method is called at the begging of the document
     */
    public void startDocument() throws SAXException {
        html2xml = new StringBuffer();
        html2xml.append("<wiki xmlns:wiki=\""+NAME_SPACE+"\" xmlns=\""+NAME_SPACE+"\"><Paragraph>");
    }

    /**
     * this method is called when the document end has been reached
     */
    public void endDocument() throws SAXException {
        iterateVector();
        html2xml.append("</Paragraph></wiki>");
        log.debug("\n\n--------------\n" + html2xml.toString());
        setResultInputStream();
        //showVectorElements();
        //System.out.println(html2xml.toString());
    }
    
    /**
     * this method is for debugging only 
     * to show which elements have been added
     *
     */
    private void showVectorElements() {
        for(int i=0; i<htmlElements.size(); i++) {
            System.out.println((String) htmlElements.elementAt(i));
        }
    }
    
    /**
     * this method walks through all the Vector elements and calls 
     * iterateVectorElements
     *
     */
    private void iterateVector() {
        for(int i=0; i<htmlElements.size(); i++) {
            iterateVectorElements(i, (String) htmlElements.elementAt(i));
        }
    }
    
    /**
     * this method iterated through the Vectorelements
     * and appends the according WikiTags so that the page can be 
     * edited with YULUP   
     * @param position of Vector htmlElements
     * @param elementName the tag name of the returning jspWikiPage 
     */
    private void iterateVectorElements(int position, String elementName) {
        
        if(elementName.startsWith("START_")) {
            tagName = elementName.substring(6);
            startTag = true;
        } else 
        if(elementName.startsWith("END_")) {
            tagName = elementName.substring(4);
            startTag = false;
        } else {
            tagName = elementName;
            handleText(tagName);  
        }

        prevElement = getPreviousElement(position);
        nextElement = getNextElement(position);
        twoAhead = get2Ahead(position);
        twoBack = get2Back(position);
        
        if(tagName.equals("ol")) handleOl();
        if(tagName.equals("ul")) handleUl();
        if(tagName.equals("li")) handleLi(position);
        if(tagName.equals("br")) ;//do nothing
        if(tagName.equals("hr")) handleHr();
        if(tagName.equals("a")) handleA(position);
        if(tagName.equals("span")) handleSpan();
        if(tagName.equals("h4")) handleH4();
        if(tagName.equals("h3")) handleH3();
        if(tagName.equals("h2")) handleH2();
        if(tagName.equals("b")) handleB();
        if(tagName.equals("i")) handleI();
        if(tagName.equals("tt")) handleTt();
        if(tagName.equals("dl")) handleDl();
        if(tagName.equals("dt")) handleDt();
        if(tagName.equals("dd")) handleDd();
        if(tagName.equals("table")) handleTable();
        //if(tagName.equals("th")) handleTh();
        if(tagName.equals("tr")) handleTr();
        if(tagName.equals("td")) handleTd();
    }
    
    /**
     * this method handles the tag OL
     *
     */
    private void handleOl() {
        if(!tagName.equals(prevElement) && 
           !tagName.equals(nextElement) && 
           !prevElement.equals("li") &&
           !tagName.equals(twoAhead)) {
             listType = "N";
             String tag = (startTag)? "<" + listType + "List>": "</" + listType + "List>";
             html2xml.append(tag);    
         }
         if(!startTag && !nextElement.equals("ol") && !nextElement.equals("li")) {
             html2xml.append("</" + listType + "List>");
         }
    }
    
    /**
     * this method handles the tag Ul
     *
     */
    private void handleUl() {
        if(!tagName.equals(prevElement) && 
           !tagName.equals(nextElement) && 
           !prevElement.equals("li") &&
           !tagName.equals(twoAhead)) {
            
           listType = "B";
           String tag = (startTag)? "<" + listType + "List>": "</" + listType + "List>";
           html2xml.append(tag);
        }
        if(!startTag && !nextElement.equals("ul") && !nextElement.equals("li")) {
            html2xml.append("</" + listType + "List>");
        }    
    }
    
    /**
     * this method handles the tag LI
     * @param position of the Vector htmlElements
     */
    private void handleLi(int position) {
        if(!tagName.equals(twoBack)) {
            int depth = getListDepth(position);
            String tag = (startTag)? "<" + listType + "ListItem depth=\"" + depth + "\">": "</" + listType + "ListItem>";
            html2xml.append(tag);    
        }
    }
    
    /**
     * this method handles the tag HR
     *
     */
    private void handleHr() {
        if(startTag) {
            html2xml.append("<HRule/>");
        }
    }
    
    /**
     * this method handles the tag a 
     * @param position of Vector htmlElements
     */
    private void handleA(int position) {
        if(startTag) {
            String href = getHrefAttribute(position + 1);
            html2xml.append("<Link href=\"" + href + "\">");
        } else {
            html2xml.append("</Link>");
        }
    }
    
    /**
     * this method will return the href of an link 
     *
     */
    private String getHrefAttribute(int position) {
        return (String) htmlElements.elementAt(position);
    }
    
    /**
     * this method handles the tag SPAN which is indication 
     * something went wrong
     *
     */
    private void handleSpan() {
        if(startTag) {
            html2xml.append("<Error>");
        } else {
            html2xml.append("</Error>");
        }
    }
    
    /**
     * this method handles the tag H4
     *
     */
    private void handleH4() {
        if(startTag) {
            html2xml.append("<MainMainTitle>");
        } else {
            html2xml.append("</MainMainTitle>");
        }
    }
    
    /**
     * this method handles the tag H3
     *
     */
    private void handleH3() {
        if(startTag) {
            html2xml.append("<MainTitle>");
        } else {
            html2xml.append("</MainTitle>");
        }
    }
    
    /**
     * this method handles the tag H2
     *
     */
    private void handleH2() {
        if(startTag) {
            html2xml.append("<Title>");
        } else {
            html2xml.append("</Title>");
        }
    }
    
    /**
     * this method handles the tag B
     *
     */
    private void handleB() {
        if(startTag) {
            html2xml.append("<Bold>");
        } else {
            html2xml.append("</Bold>");
        }
    }
    
    /**
     * this method handles the tag I
     *
     */
    private void handleI() {
        if(startTag) {
            html2xml.append("<Italic>");
        } else {
            html2xml.append("</Italic>");
        }
    }
    
    /**
     * this method handles the tag TT
     *
     */
    private void handleTt() {
        if(startTag) {
            html2xml.append("<Plain>");
            plain = "Plain";
        } else {
            html2xml.append("</Plain>");
            plain = "";
        }
    }
    
    /**
     * this method handles the tag DL
     *
     */
    private void handleDl() {
        if(startTag) {
            html2xml.append("<DefinitionList>");
        } else {
            html2xml.append("</DefinitionList>");
        }
    }
    
    /**
     * this method handles the tag DT
     *
     */
    private void handleDt() {
        if(startTag) {
            html2xml.append("<Term>");
        } else {
            html2xml.append("</Term>");
        }
    }
    
    /**
     * this method handles the tag DD
     *
     */
    private void handleDd() {
        if(startTag) {
            html2xml.append("<Definition>");
        } else {
            html2xml.append("</Definition>");
        }
    }
    
    /**
     * this method handles the tag TABLE
     *
     */
    private void handleTable() {
        if(startTag) {
            html2xml.append("<Table>");
        } else {
            html2xml.append("</Table>");
        }
    }
    
    /**
     * this method handles the tag TR
     *
     */
    private void handleTr() {
        if(startTag) {
            html2xml.append("<TableRow>");
        } else {
            html2xml.append("</TableRow>");
        }
    }
    
    /**
     * this method handles the tag TD
     *
     */
    private void handleTd() {
        if(startTag) {
            html2xml.append("<TableCol>");
        } else {
            html2xml.append("</TableCol>");
        }
    }
    
    /**
     * this method is called whenever the value of a tag is being processed
     * @param elementName 
     */
    private void handleText(String elementName) {
        for(int i = 0; i < elementName.length(); i++) {
            if(elementName.charAt(i) == '\n') {} else
            if(elementName.charAt(i) == '"') { html2xml.append("<" + elementName + "Text value=\"&#34;\"/>"); } else
            if(elementName.charAt(i) == '<') { html2xml.append("<" + elementName + "Text value=\"&#60;\"/>"); } else
            if(elementName.charAt(i) == '>') { html2xml.append("<" + elementName + "Text value=\"&#62;\"/>"); }
            else html2xml.append("<" + plain + "Text value=\"" + elementName.charAt(i) + "\"/>");
        }
    }
    
    /**
     * this method will look what element was called before this one
     * @param position of Vector htmlElements
     * @return the tag name
     */
    private String getPreviousElement(int position) {
        try {
            for(int i=position-1; i>=0; i--) {
                String element = (String) htmlElements.elementAt(i);
                if(element.startsWith("START_")) {
                    element = element.substring(6);
                }
                if(element.startsWith("END_")) {
                    element = element.substring(4);
                }
                return element;
            }    
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * this method will look what tag is next
     * @param position of Vector htmlElements
     * @return the tag name
     */
    private String getNextElement(int position) {
        int start = ((position + 1) > htmlElements.size()) ? htmlElements.size(): position + 1;
        for(int i=start; i<htmlElements.size(); i++) {
            String element = (String) htmlElements.elementAt(i);
            if(element.startsWith("START_")) {
                element = element.substring(6);
            }
            if(element.startsWith("END_")) {
                element = element.substring(4);
            }
            return element;
        }
        return "";
    }
    
    /**
     * this method will look 2 Elements ahead in Vector to decide what to do
     * @param position of Vector htmlElements
     * @return tag name
     */
    private String get2Ahead(int position) {
        int start = ((position + 1) > htmlElements.size()) ? htmlElements.size(): position + 1;
        int index = 0;
        for(int i=start; i<htmlElements.size(); i++) {
            index++;
            String element = (String) htmlElements.elementAt(i);
            if(element.startsWith("START_")) {
                element = element.substring(6);
            }
            if(element.startsWith("END_")) {
                element = element.substring(4);
            }
            if(index == 2)return element;
        }
        return "";
    }
    
    /**
     * this method will look back 2 Elements in the vector to be
     * able to decide what to do
     * @param position in Vector htmlElements
     * @return the tag name
     */
    private String get2Back(int position) {
        int index = 0;
        for(int i=position-1; i>=0; i--) {
            index++;
            String element = (String) htmlElements.elementAt(i);
            if(element.startsWith("START_")) {
                element = element.substring(6);
            }
            if(element.startsWith("END_")) {
                element = element.substring(4);
            }
            if(index == 2)return element;
        }
        return "";
    }
    
    /**
     * this method will iterate the ul and ol tags and count their occurences 
     * and set the attribute depth which will show the list depth
     * @param position of Vector htmlElements
     * @return the list depth
     */
    private int getListDepth(int position) {
        int depth = 1;
        for(int i=position+1; i<htmlElements.size(); i++) {
            String element = (String) htmlElements.elementAt(i);
            if(element.startsWith("START_li")) depth++;
            if(!element.startsWith("START_") && !element.startsWith("END_")) 
            return depth;
        }
        return 0;
    }
    
    /**
     * this method will be called whenever a start tag is processed
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        if(eName.equals("html") || eName.equals("body")) {//ignore html and body
        } else {
            htmlElements.add("START_" + eName);
            for(int i=0; i<attrs.getLength(); i++) {
                if(attrs.getQName(i).equals("href")) {
                    String href = attrs.getValue(i).substring(14);
                    htmlElements.add(href);
                }
            }
        }
    }
    
    /**
     * this method will be called whenever a Tag is closed
     */
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        String eName = ("".equals(localName)) ? qName : localName;
        if(eName.equals("html") || eName.equals("body")) {//ignore html and body
        } else {
            htmlElements.add("END_" + eName);
        }
    }

    /**
     * this methods handles all the tag values and converts them to TextTags
     * where every character will be wrapped in a TextTag e.g. Text will be
     * transformed to: 
     * <Text value="T"/> 
     * <Text value="e"/> 
     * <Text value="x"/> 
     * <Text value="t"/> 
     */
    public void characters(char[] buf, int offset, int len) throws SAXException {
        String value = new String(buf, offset, len);
        htmlElements.add(value);
    }

    /**
     * this method set the result InputStream 
     *
     */
    private void setResultInputStream() {
        this.byteArrayInputStream = new ByteArrayInputStream(html2xml.toString().getBytes());
    }
 
    /**
     * this method returns the result as InputStream
     * @return InputStream
     */
    public ByteArrayInputStream getInputStream() {
        return this.byteArrayInputStream;
    }

    /**
     * this method shows the transformed xml as String
     * @return transformed xml as String 
     */
    public String showTransformedXmlAsString() {
        return html2xml.toString();
    }
    
    /**
     * this method replaces all occurences of '&' but not '&amp;' with '&amp;'
     * @param inputString with or without '&'
     * @return replaced ampersands as string
     */
    private String replaceAmpersand(String inputString) {
        String [] tokens = inputString.split("&amp;");
        String replacedAmpersand = null;
        if(inputString.indexOf("&amp;") == -1) {
            replacedAmpersand = inputString.replaceAll("&", "&amp;");
        } else {
            replacedAmpersand = "";
            for(int i = 0; i < tokens.length; i++) {
                replacedAmpersand += tokens[i].replaceAll("&", "&amp;") + "&amp;";
            }
        }
        log.debug("[" + inputString + "] replaced with [" + replacedAmpersand + "]");
        return replacedAmpersand;
    }
}
