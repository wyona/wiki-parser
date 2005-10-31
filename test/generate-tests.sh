#!/bin/bash

JAR=../build/lib/WikiParser.jar

rm *.tree

for i in *.txt; do
	echo -n "generating test: $i"
	java -jar $JAR $i > $i.tree
	if [ $? -ne 0 ]; then
		echo "  failed"
		exit 1
	else
		echo "  done"
	fi
done
