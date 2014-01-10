#! /bin/bash

MAIN_CLASS="github.hfdiao.sqlclient.SQLClientConsole"
CLASSPATH="."
for f in `ls *.jar`
do
CLASSPATH="$CLASSPATH"":""$f"
done

java -cp "$CLASSPATH" "$MAIN_CLASS"
