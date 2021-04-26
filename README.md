![Java CI with Maven](https://github.com/xChevy/Starfish/workflows/Java%20CI%20with%20Maven/badge.svg?event=push)

# Starfish-Network

The Starfish Network is a Service Team that operates using Discord. In order to work correctly it needs to use a bot that maganages members, freelancers and tickets.

## Contents

* [Features](#features)
* [Usage](#usage)
  * [Dependencies](#dependencies)
  * [Hosting Requirements](#hosting-requirements)
  * [Running](#running)

## Features

- [X] Commands for adding and removing people in channels: `-add` and `-remove`, both commands do also work to add and remove freelancers
- [X] Ticket panel for easy ticket creation
- [X] Custom freelancing system with `-promote` and `-demote` commands. Check the freelancer with `-freelancerInfo|fi @tag`
- [X] Custom quotes system in which freelancers can send offers to `quote` ticket with the command: `-quote <ticket id> <offer>`
- [X] Freelancers can have reviews from customers
- [X] Generate invoices with `paypal.me` with the command `-invoice <subtotal> <service>`
- [X] `-clear <amount>` command which helps keeping a channel clear. Note: It cannot be executed inside ticket.
- [X] Custom permission system which improves Discord's by adding nodes to the bot's commands
- [X] Freelancers have portfolio commands
- [X] Fantastic ticket system with commands to announce, see ticket information, close and check previous offers.
- [X] Highly configurable and customizable, change the questions for every ticket type, every message sent by the bot, the command output and each channel purpose.
- [X] Mongo database is required to run the bot
- [X] Ticket transcript system to save the conversation of a ticket in files.

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

