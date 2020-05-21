# Starfish-Network

The Starfish Network is a Service Team that operates using Discord. In order to work correctly it needs to use a bot that maganages members, freelancers and tickets.

## Documentation

For all the people that bought the bot, the [Wiki](https://github.com/xChevy/Starfish-Network/wiki) contains everything realted to setting it up and using the commands.

### Dependencies

The bot requires **Java 8+**.

We use [Maven](https://maven.apache.org/) to manage the dependencies.

* [JDA](https://github.com/DV8FromTheWorld/JDA)
  * Version: **4.1.1_137**
  * Documentation: [Javadoc](https://ci.dv8tion.net/job/JDA/javadoc/)
* [JDA-Command-Framework](https://github.com/xChevy/JDA-Command-Framework) 
  * Version: **1.0-Snapshot**
* [Gson](https://github.com/google/gson)  
  * Version: **2.8.5**
  * Documentation: [Javadoc](https://www.javadoc.io/doc/com.google.code.gson/gson)
* [mongo-java-driver](https://github.com/mongodb/mongo-java-driver)
  * Version: **3.12.3**
  * Documentation: [Javadoc](https://mongodb.github.io/mongo-java-driver/3.12/javadoc/)
* [spring-boot](https://github.com/spring-projects/spring-boot)
  * Version: **2.2.6**
  * Documentation: [Website](https://spring.io/projects/spring-boot)
  * In case that the user doesn't want to use automatic payments it is not required
  
### Hosting requirements

* Java 8
* Mongo database
* Discord application token

**Optional (for automatic payments)**

* Domain name
*SSL Certificate in PKCS12 format
