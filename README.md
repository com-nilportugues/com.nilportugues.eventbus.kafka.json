# EventBus Kafka+JSON Implementation

Sends messages using Kafka and JSON (serialized with the Jackson JSON library).
 
Both Consumer and Producers provided.  For usage, check the tests.

## Installation

Add in your `pom.xml` file the jitpack.io repositories:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```
  
Now add the package as a dependency: 

```xml
<dependencies>		
  <dependency>
    <groupId>com.nilportugues.eventbus</groupId>
    <artifactId>kafka-json</artifactId>
    <version>${eventbus_kafkajson.version}</version>
  </dependency>
</dependencies>  
```
