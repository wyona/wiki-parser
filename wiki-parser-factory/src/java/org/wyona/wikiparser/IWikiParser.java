package org.wyona.wikiparser;

import java.io.InputStream;

public interface IWikiParser {
    public void parse(InputStream inputStream);
    public InputStream getInputStream();
}
