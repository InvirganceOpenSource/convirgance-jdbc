# Convirgance (JDBC)

![Version](https://img.shields.io/badge/Version-pre&dash;release-blue) ![License](https://img.shields.io/badge/License-MIT-green) ![Repository](https://img.shields.io/badge/Platform-Java-gold) ![Repository](https://img.shields.io/badge/Repository-n/a-red)

Library to automatically manage JDBC connections and drivers. Most common databases are supported with drivers pulled from Maven Central automatically.

## Quick Start

```java
String url = "jdbc:postgres://my_server/my_database";
String username = "user";
String password = "password";

DataSource source = DriverDataSource.getDataSource(url, username, password);
DBMS dbms = new DBMS(source);
```

## Documentation

- [JavaDocs](https://docs.invirgance.com/javadocs/convirgance-jdbc/0.1.0-SNAPSHOT/) (Work in Progress)
