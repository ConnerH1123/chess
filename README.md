# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmyyKsRpHfL8-y7CRqFLFcoEVEB-ojMpRHzNpMHrPofw7DRqHQo8cIVG+XYwAgQnihignCQSRJgKSzkVMO-IMkyU4qVyN40vuFTLjAYoSm6MpymW7xKpgKrBhqbpGhwEBqDAaAQMwVpopFvJ3kmvrOi5PZ9tuhiVR2NT1AAQiGPlqGAUYxnGhSgZhyCpjA6b4aMYw5qoebzNBRYlvUejriihKdQ29FlQuAqNc5rozluGXqjAACSaAgNAKLgDAGk7PVIHVP6zLTJe0BIAAXigHDdSgsYKeh8LJoN2HDU4ACMBETVNBZjLN0D1D4j16s9b27HRTbOYKQX0oecgoM+8Tnpe14Y5tlQPmuAYE1uaONYZpZeeKGSqABmA07dElgYR5aqTpsGXlR9bmWh-WNaJOF4QR4W2d8FF88hAurU2njeH4-heCg6AxHEiSq+rXm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDNLiHoL9go0-UFGI3kBT1AAPI7SHlMzDkYeJVBIq5Ql655kenh1JI3buUUjiFYC4-j8FO2gc5Jw6S7CnF4pPhT8iyvK-vO-tGrHVQJpIOu5eFETrNhy6BW9v2VOh-d8PxIj72fd98ZC-9JRgGmINg-yEMzcWMMKj3ffI42DFN1t1U7Zu8gJ1St7BRwKDcMel7p5RmfZ+Vuck8K0gH0yhi46+1Xtm7Akx2ADNMyza9sy8MB6Xdf1KgixgLhUaxkUYMUVsxfwKJ1z+GwOKDU-E0QwAAOJKg0AbLupYGioIttbewSoHa80zi7D8wdSweygK9L2aBfYN0Dl-KqnZ6jIByOgnMnk0QcLUHHPy28ZC73pDARkqdj4N3Phte8+d4pFxfNoUuxoSFIXSqqKuaAa7IHrso52q9Q7h1qh3J+39mqSgXtQpGA9epkJHkNdMoMxrg3zDPOa88qGvXevLFeQjNr6Nbg-Smxid452EWwsAPDVAYkkfuaR9RUFMlcigiAAAzNBsxgCP07M-Ch9RkHsIwR-BAgEKHNyMssQhOYUINHGBUlAh1pAFh9rU+p5QQSbHiLqFA2VIJyxgCCZIoA1TdK5vsPp5SlQADkKxqT-jATo-82YDVHqLMB4zKnaWqWMZpDTfbbNacsdpnThlmRmf0hAgzjmSzGVsyZ0ydKzPmcvRiSsAgcAAOxuCcCgJwMQIzBDgFxAAbPACchgeEwCKADMSzDTFSQ6AQohPcZZZlqVMuYCzEyVBfu4l6tD6E6LQOUJSNy5hopOfc+ysIQ4wvDljdEPCMRwFBTwvh-kgmCJCaOERTI04SPWjEvO9RZHk3kSXZKDdVGZSOho2u2iM4qL0TS1uhiE7YNdOYjxH1oxfWscPIBULx4OOzFPZxhZZ6ljhjizxED+UVT8S5AJW8qbBIvsIulKAGWoqVNEiqV96iPjSccTJLcGrkKpaWJlR4PVKkKcU8NpSwJrLUFUmpSp6mNL2W0jpk5TJXLORc3NvSQRermL0i4cyMUh1sYDUBxLamqBTSSupOyYBNLTdIfZYxDk5s5uS0Z+aQBDMLacpNZKy0VqeVA5WlgD5uU2BrJACQwAzr7BAedAApCA4pA2GH8AMwdkLR6GwAcbJozIZI9FqcQ+V6AszYHOTOqAcAIBuSgGsbZlb4RYpyUohGFi8WtoYcS+9wBH3PtfTRAA6iwQ6lsegtX4goOAABpb42yUJtrmC0v+Qd432vqAAKy3WgBlRHxQspQMteOzqOWuq5aI3lBKfWXyFEKwuIqrwKPFQSyVB1q6yt-afBVPjm4GPbqqmF3crVap6j9PV8ADVAyNeNE100zWuMtU9Cx1qnmKqauHR1wABFExTp69tzHFx+oLhKHhSV5TbN4+ozRdcRHtttbnfDbc6qd0k6WNqHA+FWLkwApZdixaONU5DaGpYFowCWr5Lx7nFyeds3tNR8AX3QErGaGs4Y+rGLVQGKsoY8tBaHiF4Win0yZgi7mU1UNzXu2K7l6iNqfMPHDfUTd5GY3-iKbhr8Cbf6ftCzW8L4DJ1MWVl4UDC6l0zflIgYMsBgDYHvYQWhEKsG+eNqbc2ltrbGDId+zrMAJtMP063EA3A8CMpu1AVlxmfG1H3ofLpAxIkWeJqxm+b3KwIA3O6QJWTE50bHPd3Gn2kvfeXL9u+-2OPBsHC6ja4PlupfkFE6HsS4fogRxjoz7WX5LbwLGgbwETFlJG5V5ZIDxs4bWkAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
