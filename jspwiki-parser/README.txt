
   README
   ------

   1) Build Patched JSPWiki library
      - cd src/jspwiki-original-and-patches
      - copy build.properties to local.build.properties
      - Edit your maven2 repository path within local.build.properties
      - Run ant

   2) Build Html2WikiXML library
      - ./build.sh clean (OPTIONAL)
      - ./build.sh jar
      - ./build.sh run-examples
