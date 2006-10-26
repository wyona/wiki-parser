package org.wyona.wikiparser;

import java.io.InputStream;

public class WikiParserFactory implements IWikiParserFactory, IWikiParserType {
    
    public IWikiParser create(int wikiParserType, InputStream inputStream) {
        switch (wikiParserType) {
        //case WYONA_WIKI_PARSER:
            //return new org.wyona.wiki.WikiParser(inputStream);
        case JSP_WIKI_PARSER:
            return (IWikiParser) new org.wyona.jspwiki.WikiParser(inputStream);
        default:
            return (IWikiParser) new org.wyona.jspwiki.WikiParser(inputStream);
        }
    }
}
