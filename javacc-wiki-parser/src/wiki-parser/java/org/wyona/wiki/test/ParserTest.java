package org.wyona.wiki.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import org.wyona.wiki.SimpleCharStream;
import org.wyona.wiki.SimpleNode;
import org.wyona.wiki.WikiParser;
import org.wyona.wiki.WikiParserTokenManager;

public class ParserTest {
        
    private static void runnTest(File testFile) {        
        SimpleNode rootNode = null;        
        try {            
            System.err.println("running test: " + testFile.getAbsolutePath());            
            WikiParser wikiParser = new WikiParser(new WikiParserTokenManager(new SimpleCharStream(
                    new InputStreamReader(new FileInputStream(testFile), "UTF-8"))));            
            rootNode = wikiParser.WikiBody();                                           
            File testFileTree = new File(testFile.getAbsolutePath() + ".new");
            if (testFileTree.exists()) {
                testFileTree.delete();
            }                
            DumpNodeTree(new FileOutputStream(testFileTree), rootNode, 0);                        
            if (!compareFiles(testFileTree, new File(testFile.getAbsolutePath() + ".test"))) {
                throw new Exception("test file does not match");
            }                        
        } catch (Exception e) {
            System.err.println("parser test failed: " + testFile.getAbsolutePath());
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private static void generateTest(File testFile) {        
        SimpleNode rootNode = null;        
        try {
            WikiParser wikiParser = new WikiParser(new WikiParserTokenManager(new SimpleCharStream(
                    new InputStreamReader(new FileInputStream(testFile), "UTF-8"))));            
            rootNode = wikiParser.WikiBody();                                           
            File testFileTree = new File(testFile.getAbsolutePath() + ".test");
            if (testFileTree.exists()) {
                testFileTree.delete();
            }                
            DumpNodeTree(new FileOutputStream(testFileTree), rootNode, 0);                             
        } catch (Exception e) {
            System.err.println("parser test failed: " + testFile.getAbsolutePath());
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private static boolean compareFiles(File file1, File file2) throws IOException {
        
        FileInputStream in1,in2 = null;
        
        in1 = new FileInputStream(file1);
        in2 = new FileInputStream(file2);
        
        byte[] buf1 = new byte[8192];
        byte[] buf2 = new byte[8192];        
        
        int rb1,rb2;
        
        while ((rb1 = in1.read(buf1)) > 0) {
            rb2 = in2.read(buf2, 0, rb1);
            if (rb1 != rb2) {
                return false;
            } else {
                for (int i=0; i<rb1; i++) {
                    if (buf1[i] != buf2[i]) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    private static void DumpNodeTree(OutputStream out, SimpleNode node, int depth) {
        SimpleNode n = node;        
        PrintStream output = new PrintStream(out);        
        for (int i = 0; i < depth; i++)
            output.print(" ");
            output.print(n.toString());
        if (!node.optionMap.isEmpty()) {
            Set keySet = node.optionMap.keySet();
            Iterator kit = keySet.iterator();
            while (kit.hasNext()) {
                Object option = kit.next();
                Object value = node.optionMap.get(option);
                output.print(":" + option.toString() + "=" + value.toString());
            }
        }
        output.println();
        if (n.jjtGetNumChildren() > 0) {
            for (int i = 0; i < n.jjtGetNumChildren(); i++) {
                DumpNodeTree(out, (SimpleNode) node.jjtGetChild(i), depth + 1);
            }
        }
    }
    
    public static void main(String[] args) {  
        if (args.length > 1) {
            System.err.println("generating parser tests..");
            File testDir = new File(args[0]);
            File[] testDirFiles = testDir.listFiles();
            for (int i=0; i<testDirFiles.length; i++) {
                if (testDirFiles[i].getName().endsWith(".txt")) {
                    generateTest(testDirFiles[i]);
                }
            }    
        } else {
            System.err.println("running parser tests..");
            File testDir = new File(args[0]);
            File[] testDirFiles = testDir.listFiles();
            for (int i=0; i<testDirFiles.length; i++) {
                if (testDirFiles[i].getName().endsWith(".txt")) {
                    runnTest(testDirFiles[i]);
                }
            }               
        }
    }
    
}
