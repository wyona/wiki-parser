#!/bin/bash

cd $(dirname $0)

JAR=../build/lib/WikiParser.jar

FAILS=0
for i in *.txt; do
	echo "running test: $i"
	
	echo -n "   generate: "
	java -jar $JAR $i > $i.xml.test
	if [ $? -ne 0 ]; then
		echo "  failed"
		let FAILS=FAILS+1
	else
		echo "  done"
	fi

	echo -n "   compare: "
	diff $i.xml $i.xml.test > $i.xml.test.diff 
	if [ $? -ne 0 ]; then
		echo "  failed"
		let FAILS=FAILS+1
	else
		echo "  done"
	fi
done	

if [ $FAILS -eq 0 ]; then
	rm *.xml.test
	rm *.xml.test.diff
	echo "ALL TESTS PASSED"
	exit 0
else
	echo "$FAILS tests failed"
	exit 1
fi

