#! /bin/bash

usage() {
   echo "usage: ""$0"" dbtype host port database username [password]"
   echo "example: mysql 127.0.0.1 3306 mydatabase myusername mypassword"
   echo "supported dbtype: mysql,oracle"
}

DBTYPE="mysql"
HOST="localhost"
PORT="3306"
DATABASE="mysql"
USERNAME="root"
PASSWORD=""

if [ $# -eq 5 ]; then
	DBTYPE="$1"
    HOST="$2"
    PORT="$3"
    DATABASE="$4"
    USERNAME="$5"
    printf "password: "
    read -s PASSWORD
    echo ""
elif [ $# -eq 6 ]; then
    DBTYPE="$1"
    HOST="$2"
    PORT="$3"
    DATABASE="$4"
    USERNAME="$5"
    PASSWORD="$6"
else
    usage
    exit 1
fi

MAIN_CLASS="github.hfdiao.sqlclient.SQLClientConsole"
CLASSPATH="."
for f in `ls *.jar`
do
CLASSPATH="$CLASSPATH"":""$f"
done

java -cp "$CLASSPATH" "$MAIN_CLASS" "$DBTYPE" "$HOST" "$PORT" "$DATABASE" "$USERNAME" "$PASSWORD"
