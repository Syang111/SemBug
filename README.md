# SemBug
SemBug is a tool to automatically test Database Management Systems (DBMSs) in order to find logical bugs in their implementation.

# Getting Started

Requirements:
* Java 11 or above
* [Maven](https://maven.apache.org/) (`sudo apt install maven` on Ubuntu)
* The DBMS that you want to test (currently support MySQL, TiDB, MariaDB, SQLite, PostgreSQL)
* SQLite is an embedded database, which does not need extra setup and does not require connection parameters
* Other databases like MySQL, which requires connection parameters and needs to create a database named test

The following commands create a JAR, and start Sonar to test DBMS:

```
git clone https://github.com/Syang111/Sonar.git
cd Sonar
mvn package -DskipTests
cd target
java -jar Sonar-*.jar --num-threads 4 sqlite3
java -jar Sonar-*.jar --num-threads threadNumber --host hostString --port portNumber --username usernameString --password passwordString databaseName
java -jar Sonar-*.jar --num-threads 4 --username root --password '' --host 127.0.0.1 --port 3306 mysql
java -jar Sonar-*.jar --num-threads 4 --username root --password '' --host 127.0.0.1 --port 3306 tidb
```

# Bugs
You can check bugs found by SemBug in [Bug List](https://github.com/Syang111/SemBug/blob/master/Bugs.md).
