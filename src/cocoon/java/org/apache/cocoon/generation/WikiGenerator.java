/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cocoon.generation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.ProcessingException;
import org.apache.cocoon.components.source.SourceUtil;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.SourceResolver;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceException;
import org.apache.lenya.cms.publication.Document;
import org.apache.lenya.cms.publication.DocumentIdentityMap;
import org.apache.lenya.cms.publication.Publication;
import org.apache.lenya.cms.publication.PublicationUtil;
import org.apache.lenya.cms.repository.RepositoryUtil;
import org.apache.lenya.cms.repository.Session;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.wyona.wiki.ParseException;
import org.wyona.wiki.SimpleNode;
import org.wyona.wiki.WikiParser;

/**
 * Blog entry generator
 */
public class WikiGenerator extends ServiceableGenerator {

    /** The URI of the namespace of this generator. */
    protected static final String URI = "http://apache.org/cocoon/wiki/1.0";

    /** The namespace prefix for this namespace. */
    protected static final String PREFIX = "wiki";

    protected static final String WIKI_NODE_NAME = "wiki";

    /** The static parser slave */
    WikiParser wikiParser = null;
    
    protected Source inputSource = null;
    
    /**
     * Convenience object, so we don't need to create an AttributesImpl for every element.
     */
    protected AttributesImpl attributes;

    /**
     * Set the request parameters. Must be called before the generate method.
     * 
     * @param resolver
     *            the SourceResolver object
     * @param objectModel
     *            a <code>Map</code> containing model object
     * @param src
     *            the source URI (ignored)
     * @param par
     *            configuration parameters
     */
    public void setup(SourceResolver resolver, Map objectModel, String src, Parameters par) throws ProcessingException,
            SAXException, IOException {
        super.setup(resolver, objectModel, src, par);
        try {
            this.inputSource = super.resolver.resolveURI(src);
        } catch (SourceException se) {
            throw SourceUtil.handle("Error during resolving of '" + src + "'.", se);
        }
        this.attributes = new AttributesImpl();
    }

    /**
     * Generate XML data.
     * 
     * @throws SAXException
     *             if an error occurs while outputting the document
     */
    public void generate() throws SAXException, ProcessingException {
        attributes.clear();
        this.contentHandler.startDocument();
        this.contentHandler.startPrefixMapping(PREFIX, URI);        
        this.contentHandler.startElement(URI, WIKI_NODE_NAME, PREFIX + ':' + WIKI_NODE_NAME, attributes);

        try {                        
            InputStream wikiIs = inputSource.getInputStream();
           
            if (wikiParser == null) {
                wikiParser = new WikiParser(wikiIs);
            } else {
                wikiParser.ReInit(wikiIs);
            }
            
            try {
                SimpleNode root = wikiParser.WikiBody();
                handleNode(root, 0);
            } catch (ParseException pe) {
                throw new ProcessingException(pe.getMessage());
            }
        } catch (Exception e) {
            throw new ProcessingException(e);
        }

        this.contentHandler.endElement(URI, WIKI_NODE_NAME, PREFIX + ':' + WIKI_NODE_NAME);
        this.contentHandler.endPrefixMapping(PREFIX);
        this.contentHandler.endDocument();
    }

    private void handleNode(SimpleNode node, int depth) throws SAXException {
        attributes.clear();
        if (!node.optionMap.isEmpty()) {
            Set keySet = node.optionMap.keySet();
            Iterator kit = keySet.iterator();
            while (kit.hasNext()) {
                Object option = kit.next();
                Object value = node.optionMap.get(option);
                attributes.addAttribute("", option.toString(), option.toString(), "CDATA", value.toString());
            }
        }
        
        this.contentHandler.startElement(URI, node.toString(), PREFIX + ':' + node.toString(), attributes);        
        if (node.jjtGetNumChildren() > 0) {            
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                handleNode((SimpleNode) node.jjtGetChild(i), depth + 1);
            }            
        }        
        this.contentHandler.endElement(URI, node.toString(), PREFIX + ':' + node.toString());
    }
}
