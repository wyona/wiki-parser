Wiki-Parser
==========

 1) Prerequisites
 
 2.1) Installation (Linux)
 2.2) Installation (Windows)



1) Prerequisites

   Linux:
   Maven >= 1.0.2	http://maven.apache.org
   export PATH=/usr/local/maven/bin:$PATH


   Windows:
   Ant >= 1.6.5		http://ant.apache.org

    
2.1) Installation (Linux)

   maven wikiparser:jar

   java -jar build/lib/WikiParser.jar <file>
   (e.g. java -jar build/lib/WikiParser.jar test)

   java -classpath build/lib/WikiParser.jar org.wyona.wiki.Wiki2XML test/complex.txt
   
2.2) Installation (Windows)
   
   maven wikiparser:compile
   Put new classes directory into Classpath
   java org.wyona.wiki.Wiki2XML <file>
