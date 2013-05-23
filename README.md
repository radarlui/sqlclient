sqlclient
=========

A simple command line sql client written in Java

build
=========
mvn package

usage
=========

* connect mysql database:

      java -cp .:jline.jar:mysql-connector.jar:sqlclient.jar github.hfdiao.sqlclient.SQLClientConsole
 
* connect oracle database:

      java -cp .:jline.jar:ojdbc.jar:sqlclient.jar github.hfdiao.sqlclient.SQLClientConsole