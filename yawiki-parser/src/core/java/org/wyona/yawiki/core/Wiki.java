package org.wyona.yawiki.core;

import org.apache.log4j.Category;

import java.io.InputStream;
import java.util.Vector;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 *
 */
public class Wiki extends Element {
    private Category log = Category.getInstance(Wiki.class);

    /**
     *
     */
    public Document parse(InputStream in) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder parser = dbf.newDocumentBuilder();
        Document document = parser.parse(new java.io.StringBufferInputStream("<wiki:wiki xmlns:wiki=\""+NAME_SPACE+"\" xmlns=\""+NAME_SPACE+"\"></wiki:wiki>"));
        parse(null, in, document.getDocumentElement());
        return document;
    }

    /**
     *
     */
    public Vector getChildren() {
        Vector children = new Vector();
        children.addElement(new UList());
        children.addElement(new Table());
        children.addElement(new Link());
        children.addElement(new Emphasized());
        children.addElement(new HeadlineOne());
        return children;
    }

    /**
     *
     */
    boolean startsWith(char ch, int position) {
        return false;
    }

    /**
     *
     */
    boolean endsWith(char ch, int position) {
        return false;
    }

    /**
     *
     */
    String getName() {
        return "wiki";
    }
}
