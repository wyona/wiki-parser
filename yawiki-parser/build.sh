#!/bin/sh

echo "INFO: Build Yet Another Wiki Parser ..."

# ----- Parameters

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : setenv JAVA_HOME /usr"
  echo "       Linux   : export JAVA_HOME=/usr/local/j2sdk-..."
  echo "       Windows : Click Start ..."
  exit 1
fi

# ----- Check Java version
# TODO: ....

# ----- Set Environment Variables
ORIGINAL_ANT_HOME=$ANT_HOME
unset ANT_HOME
ANT_HOME=$PWD/tools/apache-ant-1.6.5
#echo $ANT_HOME

ORIGINAL_PATH=$PATH
PATH=$PWD/tools/maven-2.0.4/bin:$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Build Yet Another Wiki Parser ...
#mvn --version
ant -version
ant -f build.xml $@

# ----- Reset Environment Variables
ANT_HOME=$ORIGINAL_ANT_HOME
#echo $ANT_HOME
PATH=$ORIGINAL_PATH
#echo $PATH
