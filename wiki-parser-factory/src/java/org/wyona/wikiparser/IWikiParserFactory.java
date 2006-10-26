package org.wyona.wikiparser;

import java.io.InputStream;

public interface IWikiParserFactory {
    IWikiParser create (int type, InputStream inputStream); 
}
