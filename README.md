sqlclient
=========

A simple command line sql client written in Java

build
=========
mvn package

usage
=========

* connect mysql database:

      java -cp .:libreadline-java.jar:sqlclient.jar:mysql-connector-$version.jar -jar sqlclient.jar
 
* connect oracle database:

      java -cp .:libreadline-java.jar:sqlclient.jar:ojdbc-$version.jar -jar sqlclient.jar