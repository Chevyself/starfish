# Starfish-Network

The Starfish Network is a Service Team that operates using Discord. In order to work correctly it needs to use a bot that maganages members, freelancers and tickets.

## Contents

* [Usage](#usage)
  * [Dependencies](#dependencies)
  * [Hosting Requirements](#hosting-requirements)
  * [Running](#running)

## Installation

To know how to use other features of the Bot please head to the [Wiki](https://github.com/xChevy/Starfish-Network/wiki). This guide is
strictly to learn how to use the bot

### Dependencies

The bot requires **Java 8+**.

The bot will download libraries such as `JDA`, `Mongo` and `Gson` to the `Libs` directory
other libraries will be directly injected to the bot

We use [Maven](https://maven.apache.org/) to manage the dependencies.

* [JDA](https://github.com/DV8FromTheWorld/JDA)
  * Version: **4.1.1_137**
  * Documentation: [Javadoc](https://ci.dv8tion.net/job/JDA/javadoc/)
* [JDA-Command-Framework](https://github.com/xChevy/JDA-Command-Framework)
  * Version: **1.12-Snapshot**
* [Gson](https://github.com/google/gson)
  * Version: **2.8.6**
  * Documentation: [Javadoc](https://www.javadoc.io/doc/com.google.code.gson/gson)
* [mongo-java-driver](https://github.com/mongodb/mongo-java-driver)
  * Version: **3.12.3**
  * Documentation: [Javadoc](https://mongodb.github.io/mongo-java-driver/3.12/javadoc/)
* [Addons](https://github.com/xChevy/Addons)
  * Version: **1.0.0_0**
  
### Hosting requirements

* Java 8
* Mongo database
* Discord application token

### Running

To start running you need to initialize the bot using `java -jar` We suggest you using this two scripts.
Those were made for `Unix` machines which is what we recommend to use in your VPS.

#### start.sh

```shell
#!/bin/bash
screen -dmS starfish sh restart.sh
```

#### restart.sh

```shell
#!/bin/bash
while true
do
# You can add your token directly to the parameters of the bot
java -jar Starfish.jar token=<Your discord token>
done
```

