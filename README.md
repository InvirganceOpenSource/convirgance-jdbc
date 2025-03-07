# Convirgance (JDBC)

![Version](https://img.shields.io/badge/Version-pre&dash;release-blue) ![License](https://img.shields.io/badge/License-MIT-green) ![Repository](https://img.shields.io/badge/Platform-Java-gold) ![Repository](https://img.shields.io/badge/Repository-Maven_Central-red)

Library to automatically manage JDBC connections and drivers. Most common databases are supported with drivers pulled from Maven Central automatically.

## Quick Start

```java
String url = "jdbc:postgres://my_server/my_database";
String username = "user";
String password = "password";

DataSource source = DriverDataSource.getDataSource(url, username, password);
DBMS dbms = new DBMS(source);
```

## Installation

Add the following dependency to your Maven `pom.xml` file:

```xml
<dependency>
    <groupId>com.invirgance</groupId>
    <artifactId>convirgance-jdbc</artifactId>
    <version>0.1.0</version>
</dependency>
<dependency>
    <groupId>com.invirgance</groupId>
    <artifactId>convirgance-storage</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Documentation

- [JavaDocs](https://docs.invirgance.com/javadocs/convirgance-jdbc/) (Work in Progress)


## License

Convirgance is available under the MIT License. See [License](LICENSE.md) for more details.
