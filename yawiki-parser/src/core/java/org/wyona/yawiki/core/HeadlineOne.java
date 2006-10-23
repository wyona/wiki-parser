package org.wyona.yawiki.core;

import org.apache.log4j.Category;

import java.io.InputStream;
import java.util.Vector;

/**
 *
 */
public class HeadlineOne extends Element {
    private Category log = Category.getInstance(HeadlineOne.class);

    /**
     *
     */
    public Vector getChildren() {
        return new Vector();
        //return null;
    }

    /**
     *
     */
    boolean startsWith(char ch, int position) {
        if (position == 0) {
            if (ch == '!') return true;
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     */
    boolean endsWith(char ch, int position) {
        if (ch == '\n') return true;
        return false;
    }

    /**
     *
     */
    String getName() {
        return "h1";
    }
}
