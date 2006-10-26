package org.wyona.wikiparser;

import java.io.InputStream;

public abstract class WikiParser implements IWikiParser {

    private InputStream inputStream;
    
    public void parse(InputStream inputStream) {
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
}
