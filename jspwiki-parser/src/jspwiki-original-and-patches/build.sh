#!/bin/sh

echo "INFO: Build ..."

# ----- Parameters

# ----- Check for JAVA_HOME
JAVA_HOME="$JAVA_HOME"
if [ "$JAVA_HOME" = "" ];then
  echo "ERROR: No JAVA_HOME set!"
  echo "       Have you installed JDK (Java Development Kit)? If so, then set JAVA_HOME ..."
  echo "       MacOS X : setenv JAVA_HOME /System/Library/Frameworks/JavaVM.framework/Home"
  echo "       Linux   : export JAVA_HOME=/usr/local/j2sdk-..."
  echo "       Windows : Click Start ..."
  exit 1
fi

# ----- Check Java version
# TODO: ....

# ----- Set Environment Variables
unset ANT_HOME
ANT_HOME=$PWD/../../tools/apache-ant-1.6.5
#echo $ANT_HOME

PATH=$ANT_HOME/bin:$PATH
#echo $PATH

# ----- Build JSPWiki
#mvn --version
ant -version
ant -f build.xml "$@"
