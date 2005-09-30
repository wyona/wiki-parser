#!/bin/bash

JAR=../build/lib/WikiParser.jar

rm *.xml

for i in *.txt; do
	echo -n "generating test: $i"
	java -jar $JAR $i > $i.xml
	if [ $? -ne 0 ]; then
		echo "  failed"
		exit 1
	else
		echo "  done"
	fi
done
