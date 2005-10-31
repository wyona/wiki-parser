#!/bin/bash

cd $(dirname $0)

JAR=../build/lib/WikiParser.jar

FAILS=0
for i in *.txt; do
	echo "running test: $i"
	
	echo -n "   generate: "
	java -jar $JAR $i > $i.tree.test
	if [ $? -ne 0 ]; then
		echo "  failed"
		let FAILS=FAILS+1
	else
		echo "  done"
	fi

	echo -n "   compare: "
	diff $i.tree $i.tree.test > $i.tree.test.diff 
	if [ $? -ne 0 ]; then
		echo "  failed"
		let FAILS=FAILS+1
	else
		echo "  done"
	fi
done	

if [ $FAILS -eq 0 ]; then
	rm *.tree.test
	rm *.tree.test.diff
	echo "ALL TESTS PASSED"
	exit 0
else
	echo "$FAILS tests failed"
	exit 1
fi

