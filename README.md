## Compiling

```bash 
mvn compile
```

Or To compile and run:

```bash
mvn exec:java -Dexec.mainClass="com.chatbot.Main"
```

Or compile and package into a JAR first, then run:
``` bash
mvn package
java -cp target/classes com.chatbot.Main
```

The simplest path while developing is mvn compile followed by the exec:java command.