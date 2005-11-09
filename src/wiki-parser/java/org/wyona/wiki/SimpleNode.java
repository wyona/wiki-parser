package org.wyona.wiki;

import java.util.HashMap;


public class SimpleNode implements Node {
  protected Node parent;
  protected Node[] children;
  protected int id;
  protected WikiParser parser;
  
  public HashMap optionMap = new HashMap();
  public int type;
  public String image;
  
  public SimpleNode(int i) {
    id = i;
  }

  public SimpleNode(WikiParser p, int i) {
    this(i);
    parser = p;
  }

  public void jjtOpen() {
  }

  public void jjtClose() {
  }
  
  public void jjtSetParent(Node n) { parent = n; }
  public Node jjtGetParent() { return parent; }

  public void jjtAddChild(Node n, int i) {
    if (children == null) {
      children = new Node[i + 1];
    } else if (i >= children.length) {
      Node c[] = new Node[i + 1];
      System.arraycopy(children, 0, c, 0, children.length);
      children = c;
    }
    children[i] = n;
  }

  public Node jjtGetChild(int i) {
    return children[i];
  }

  public int jjtGetNumChildren() {
    return (children == null) ? 0 : children.length;
  }

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */

  public String toString() { return WikiParserTreeConstants.jjtNodeName[id]; }
  public String toString(String prefix) { return prefix + toString(); }

  /* Override this method if you want to customize how the node dumps
     out its children. */

  public void dump(String prefix) {
    System.out.println(toString(prefix));
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
	SimpleNode n = (SimpleNode)children[i];
	if (n != null) {
	  n.dump(prefix + " ");
	}
      }
    }
  }

  public void setToken(int type, String image) {
      this.type = type;
      this.image = image;
  }
  
  public void setOption(String name, int value) {
      optionMap.put(name, new Integer(value));
  }
  
  public void setOption(String name, String value) {
      optionMap.put(name, new String(value));
  }
  
  public void setOption(String name, boolean value) {
      optionMap.put((Object)name, new Boolean(value));
  }
}

