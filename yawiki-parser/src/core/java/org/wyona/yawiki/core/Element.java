package org.wyona.yawiki.core;

import org.apache.log4j.Category;

import java.io.InputStream;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 */
abstract public class Element {
    private Category log = Category.getInstance(Element.class);

    protected final String NAME_SPACE = "http://apache.org/cocoon/wiki/1.0";

    /**
     *
     */
    abstract Vector getChildren();

    /**
     *
     */
    abstract boolean startsWith(char ch, int position);

    /**
     *
     */
    abstract boolean endsWith(char ch, int position);

    /**
     *
     */
    abstract String getName();

    /**
     *
     */
    public StringBuffer parse(StringBuffer pre, InputStream in, Node parent) throws Exception {
        org.w3c.dom.Document document = parent.getOwnerDocument();
        org.w3c.dom.Element thisDOMElement = (org.w3c.dom.Element) parent.appendChild(document.createElementNS(NAME_SPACE, getName()));
        log.error("DEBUG: <" + getName() + ">");
        int c = -1;
        int position = 0;

        StringBuffer sb = new StringBuffer("");
        int numberOfCharsMatched = 0;
        Vector matchingChildren = getChildren();

        if (pre != null) {
            log.error("Pre: " + pre);
            for (int i = 0;i < pre.length();i++) {
            }
        }

        while ((c = in.read()) != -1) {
            position++;
            log.error("DEBUG: Character: " + (char)c + " (position: " + position + ")");

            if (endsWith((char)c, 0)) {
                log.error("DEBUG: </" + getName() + ">");
                StringBuffer post = new StringBuffer((char)c);
                return post;
            }

            Vector clone = (Vector) matchingChildren.clone();
            java.util.Enumeration enum = clone.elements();
            matchingChildren.removeAllElements();
            while (enum.hasMoreElements()) {
                Element element = (Element) enum.nextElement();
                //log.error("DEBUG: " + element.getName());
                if (element.startsWith((char)c, numberOfCharsMatched)) {
                    sb.append((char)c);
                    log.error("DEBUG: Matched: " + element.getName() + " ("+position+")");
                    matchingChildren.addElement(element);
                }
            }

            if (matchingChildren.size() > 0) {
                numberOfCharsMatched++;
                log.error("DEBUG: Number of chars matched: " + numberOfCharsMatched);
                if (matchingChildren.size() == 1) {
                    StringBuffer post = ((Element) matchingChildren.firstElement()).parse(sb, in, thisDOMElement);

                    if (post != null) {
                        log.error("Post: " + post);
                        for (int i = 0;i < post.length();i++) {
                        }
                    }
                    sb = new StringBuffer("");
                    numberOfCharsMatched = 0;
                    matchingChildren = getChildren();
                }
            } else {
                //log.error("DEBUG: Must be a text node: " + (char)c);
                thisDOMElement.appendChild(document.createTextNode("" + (char)c));
                // Reset all variables
                sb = new StringBuffer("");
                numberOfCharsMatched = 0;
                matchingChildren = getChildren();
            }
        }
        return null;
    }









/*
                    String element = (String) children.elementAt(i);
                    if (c == element.charAt(numberOfCharsMatched)) {
                        sb.append((char)c);
                        System.out.println("Element matched: " + element);
                    } else {
  		    }
*/


/*
    public ResultSet parse(String queryString) throws Exception {
        log.debug("Parse: " + queryString);

        ResultSet resultSet = new ResultSet("Google", "http://www.google.com");

        URL searchURL;
        try {
            //searchURL = new URL("http://www.google.com/search?q=" + queryString);
            searchURL = new URL("http://www.google.com/search?hl=en&q=" + queryString + "&btnG=Google+Search");
        } catch (MalformedURLException e) {
            log.error(e.toString());
            return null;
        }

        log.info("Connect to " + searchURL);

        // Connect and parse Input Stream
        java.io.InputStream in = null;
        try {
            URLConnection uc = searchURL.openConnection();
            uc.setRequestProperty("User-Agent", "Mozilla/5.0");
            uc.connect();
            in = uc.getInputStream();
        } catch (UnknownHostException e) {
            File sampleFile = new File(getSamplesDir(), sampleFilename);
            log.warn(e.toString() + " ---  Read sample file: " + sampleFile);
            in = new java.io.FileInputStream(sampleFile);
        }

        try {
            // To debug
            //java.io.FileOutputStream out = new java.io.FileOutputStream(sampleFilename);



            //String marker = "<html>";
            String marker = "<p class=g>";
            //String marker = "<p class=\"g\">";
            int markerLength = marker.length();
            int c = -1;
            int numberOfCharsMatched = 0;
            int position = 0;
            StringBuffer sb = new StringBuffer("");
            while ((c = in.read()) != -1) {
                //out.write(c);
                position++;
                //log.error("Character: " + (char)c);
                if (c == marker.charAt(numberOfCharsMatched)) {
                    numberOfCharsMatched++;
                    sb.append((char)c);
                    if (numberOfCharsMatched == markerLength) {
                        log.debug("Marker " + marker + " found at position: " + (position - markerLength));
                        numberOfCharsMatched = 0;
                        sb = new StringBuffer("");

                        String titleWithLink = searchEnd(in, position, "</a>");
                        //String titleWithLink = searchEnd(in, position, "<br>");
                        position = position + titleWithLink.length();
                        if (titleWithLink != null) {
                            String link = titleWithLink.substring(titleWithLink.indexOf("<a") + 8); // Beginning of Link
                            int endOfLink = link.indexOf(">");
                            String title = link.substring(endOfLink + 1, link.indexOf("</a>"));
                            link = link.substring(0, endOfLink); // End of Link
                            log.debug("Title: " + title);
                            log.debug("Link: " + link);

			    String excerpt = searchEnd(in, position, "color=#008000");
                            position = position + excerpt.length();

			    try {
                            excerpt = excerpt.substring(excerpt.indexOf("<font") + 14, excerpt.length() - 23);
                            log.debug("Excerpt: " + excerpt);
                            } catch (StringIndexOutOfBoundsException e) {
                                excerpt = "No excerpt!";
                                log.warn("No excerpt!");
                            }

                            resultSet.add(new Result(title, excerpt, new URL(link)));
                        } else {
                            log.error("No end of search result found!");
                            //position = position + ???;
                        }
                    }
                } else {
                    if (numberOfCharsMatched > 0) {
                        //log.error("Sorry, no marker " + sb.toString()  + " at position: " + (position - numberOfCharsMatched));
                        numberOfCharsMatched = 0;
                        sb = new StringBuffer("");
                    }
                }
            }
            in.close();

            //out.close();
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }
        return resultSet;
    }
*/

    /**
     * Search for marker representing the end of an element (e.g. title, excerpt, ...)
     */
/*
    private String searchEnd(InputStream in, int position, String marker) throws IOException {
        log.debug("Search marker: " + marker);
        int markerLength = marker.length();
        int numberOfCharsMatched = 0;
        StringBuffer sb = new StringBuffer("");
        int c = -1;
        while ((c = in.read()) != -1) {
            position++;
            sb.append((char)c);
            if (c == marker.charAt(numberOfCharsMatched)) {
                numberOfCharsMatched++;
                if (numberOfCharsMatched == markerLength) {
                    log.debug("Marker " + marker + " found at position: " + (position - markerLength));
                    return sb.toString();
                }
            } else {
                if (numberOfCharsMatched > 0) {
                    //log.debug("Sorry, no marker " + sb.toString()  + " at position: " + (position - numberOfCharsMatched));
                    numberOfCharsMatched = 0;
                }
            }
        }
        return null;
    }
*/
}
