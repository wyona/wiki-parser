package org.wyona.yawiki.test;

import org.wyona.yawiki.core.Wiki;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 */
public class HelloWorld {

    /**
     *
     */
    public static void main(String[] args) {
        Wiki wiki = new Wiki();
        try {
            org.w3c.dom.Document document = wiki.parse(new java.io.FileInputStream(args[0]));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new javax.xml.transform.dom.DOMSource(document), new StreamResult(new java.io.FileOutputStream(args[1])));
            //transformer.transform(new javax.xml.transform.dom.DOMSource(document), new StreamResult(System.out));
        } catch(Exception e) {
            System.err.println("" + e);
        }
    }
}
