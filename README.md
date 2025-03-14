# Convirgance (JDBC)

<a href="https://central.sonatype.com/artifact/com.invirgance/convirgance-jdbc/versions">![Version](https://img.shields.io/badge/Version-pre&dash;release-blue)</a> <a href="https://github.com/InvirganceOpenSource/convirgance-jdbc?tab=MIT-1-ov-file">![License](https://img.shields.io/badge/License-MIT-green)</a> <a href="#">![Repository](https://img.shields.io/badge/Platform-Java-gold)</a> <a href="https://central.sonatype.com/artifact/com.invirgance/convirgance-jdbc">![Repository](https://img.shields.io/badge/Repository-Maven_Central-red)</a>

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
    <version>0.2.1</version>
</dependency>
```

## Documentation

- [JavaDocs](https://docs.invirgance.com/javadocs/convirgance-jdbc/) (Work in Progress)


## License

Convirgance is available under the MIT License. See [License](LICENSE.md) for more details.
