/**
 *
 */
package org.wyona.wiki;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceSelector;
import org.apache.cocoon.environment.Request;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationException;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.publication.URLInformation;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.apache.lenya.cms.site.SiteManager;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author greg
 * 
 */
public class Tree2XML {

    /** The URI of the namespace of this generator. */
    protected final String URI = "http://apache.org/cocoon/wiki/1.0";

    /** The namespace prefix for this namespace. */
    protected final String PREFIX = "wiki";

    /** The content handler */
    ContentHandler contentHandler;

    /** The service manager */
    ServiceManager manager;

    Map objectModel;

    Request request;

    Logger logger;

    /** Actions which will be grouped together into text() nodes */
    final static String[] textActions = { "Text", "PlainText" };

    /** Actions where nested Actions should be ignored */
    final static String[] ignoreNestedActions = { "Link" };

    final static String[] linkNodeName = { "Link" };

    StringBuffer textBuf = null;

    protected AttributesImpl attributes = new AttributesImpl();

    public Tree2XML(ContentHandler ch, ServiceManager manager, Map objectModel,
            Request request, Logger logger) {
        contentHandler = ch;
        this.manager = manager;
        this.objectModel = objectModel;
        this.request = request;
        this.logger = logger;
    }

    /**
     * 
     * @param start
     * @return
     */
    public void setContentHandler(ContentHandler ch) {
        contentHandler = ch;
    }

    /**
     * Traverses the JJTree and generates SAX-Events
     * 
     * @param root
     * @throws SAXException
     * @throws PublicationException
     */
    public void traverseJJTree(SimpleNode node) throws SAXException,
            PublicationException {
        attributes.clear();
        if (!node.optionMap.isEmpty()) {
            Set keySet = node.optionMap.keySet();
            Iterator kit = keySet.iterator();
            while (kit.hasNext()) {
                Object option = kit.next();
                Object value = node.optionMap.get(option);
                attributes.addAttribute("", option.toString(), option
                        .toString(), "CDATA", value.toString());
            }
        }
        contentHandler.startElement(URI, node.toString(), PREFIX + ':'
                + node.toString(), attributes);
        if (node.jjtGetNumChildren() > 0) {
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                SimpleNode cn = (SimpleNode) node.jjtGetChild(i);
                checkInternal(cn);
                if (!ignoreNode(cn)) {
                    if (isTextAction(cn)) {
                        appendText(cn.optionMap.get("value").toString());
                    } else {
                        finalizeText();
                        traverseJJTree(cn);
                    }
                }
            }
            finalizeText();
        }
        this.contentHandler.endElement(URI, node.toString(), PREFIX + ':'
                + node.toString());

    }

    public void checkInternal(SimpleNode node) throws PublicationException {

        if (node.toString().equals(linkNodeName[0])) {

            String href = (String) node.optionMap.get("href");

            Publication publication = PublicationUtil.getPublication(
                    this.manager, this.objectModel);
            Session session = RepositoryUtil.getSession(this.request,
                    this.logger);
            DocumentIdentityMap identityMap = new DocumentIdentityMap(session,
                    manager, logger);
            Document[] documents;

            ServiceSelector selector = null;
            SiteManager siteManager = null;
            try {
                selector = (ServiceSelector) this.manager
                        .lookup(SiteManager.ROLE + "Selector");
                siteManager = (SiteManager) selector.select(publication
                        .getSiteManagerHint());

                URLInformation urlInfo = new URLInformation(request
                        .getRequestURI());

                documents = siteManager.getDocuments(identityMap, publication,
                        urlInfo.getArea());


                int i = 0;
                boolean found = false;

                while (!found && i < documents.length) {

                    if (documents[i].getId().equals(href)) {
                        node.setOption("type", "internal");
                        node.setOption("exists", "true");
                        found = true;
                    }
                    i++;
                }

                if (!found) {
                    if (href.startsWith("/")) {
                        node.setOption("type", "internal");
                        node.setOption("exists", "false");
                    } else {
                        node.setOption("type", "external");
                    }
                }

            } catch (ServiceException e) {
                throw new RuntimeException(e);
            } finally {
                if (selector != null) {
                    if (siteManager != null) {
                        selector.release(siteManager);
                    }
                    this.manager.release(selector);
                }
            }

        }

    }

    public boolean ignoreNode(Node node) {
        if (node.jjtGetParent() != null) {
            for (int i = 0; i < ignoreNestedActions.length; i++) {
                if (node.jjtGetParent().toString().compareTo(
                        ignoreNestedActions[i]) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTextAction(Node node) {
        for (int i = 0; i < textActions.length; i++) {
            if (node.toString().compareTo(textActions[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    private void appendText(String text) {
        if (textBuf == null) {
            textBuf = new StringBuffer();
        }
        textBuf.append(text);
    }

    private void resetText() {
        textBuf = null;
    }

    private void finalizeText() throws SAXException {
        if (textBuf != null) {
            char[] text = textBuf.toString().toCharArray();
            contentHandler.characters(text, 0, text.length);
            resetText();
        }
    }

    public void startDocument() throws SAXException {
        contentHandler.startDocument();
        contentHandler.startPrefixMapping(PREFIX, URI);
    }

    public void endDocument() throws SAXException {
        contentHandler.endPrefixMapping(PREFIX);
        contentHandler.endDocument();
    }
}
