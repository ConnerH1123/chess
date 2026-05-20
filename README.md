# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmyyKsRpHfL8-y7CRqFLFcoEVEB-ojMpRHzNpMHrPofw7DRqHQo8cIVG+XYwAgQnihignCQSRJgKSzkVMO-IMkyU4qVyN40vuFTLjAYoSm6MpymW7xKpgKrBhqbpGhwEBqDAaAQMwVpopFvJ3kmvrOi5PZ9tuhiVR2NSutMl7QEgABeKAcFGMZxoUoGYcgqYwOmACMBE5qoebzNBRYlvUPitXq7VdbsdFNkFi7iVQSIbu6W4ZeqMAAJJoCA0AouAMAaTs9UgdU-rMst8Srd1vUoLGCnofCybDdho1OBNoxjFNM0FmM83QItL1vetjYMc5gpbQeE4oM+8Tnpe14o-ewqPgG2NbkjjWGaWXnihkqgAZgZMPRJYGEeWqk6bBl5UfW5loYNjWiTheEEeFtnfBRHPIVzDb0Yx3h+P4XgoOgMRxIk8uK15vhYKJgqgfUDTSBG-ERu0EbdD0cmqApwyi4h6A-YKZP1BRq15AU9QADzW0h5S0w5GE7Xtbn2BrnlCRrPlqH5927lFI4hWAGNY-BNtoHOMcOkuwpxeKT5E-Isryp7ttHRqZ1UCaSDroXhS441zndr2-YkztT2w1AnXvdGn39XbvP-WAaZA5N-Lg3NxbQwqrft-DUs1-7Lr7a+1XI7ewUcCg3DHpeieUcnqflenlTLtI69MoYGOL527YOwJoenlTNN07Xj1gTAenP0NJT9zAuH4aMr8I9LZi-gUTrn8NgcUGp+JohgAAcSVBoLWzdSwNBgUbU29glRW3ZsnHulRr5Ozbh1F2aB3ZV29o-KqnZ6jIByHAnMnk0R0LUOHEkUcqQr3pDARk8ct5Vz3guAUGd6jxRzi+bQ+djTYKQulVUJc0Bl2QJXKRttZ6UN2vPWqjcl5PwZi1AhU8PpfXjDzP6n8B7A2zMPfMo8FoT30WtSWm0OGCLni5c+xNtHsLTpwmhYAmGqAxPw-ceN6gwKZK5aBEAABmsDZjAAvuohqH5fbk0YfA++CBAIpPps1GAxkxgYJzChBo4xCkoBOtIAsbsykVPKCCTY8RdQoGypBCWMAQTJFAGqFpLN9jtOWGUgAchWNSr8YCdDfgzD+I0f5KQKfA4ppSlQVKqTU6QdTlgNKaT0syoyOkIC6Ts4W-T5lzGGXMNpFxxkbQYp4GWAQOAAHY3BOBQE4GIEZghwC4gANngGjWJFyih921s-XWrQOjoMwS9MWWYhlKkmYmPBKTHZtUIcQ0hyi0DlDmfC3pdkfawj9movah45AoCYRiOAaMmEsMjiTLx+9OHcITnwsqAiQlZwlO4vOyUq4yMyqdeR5clFJ2kaopqe1NFRyQXotFBjO5GIGu-XuZjAYWNBlY2ahYx6liWvY7qjiGISrroTMR8g2EyGcfUMl6JKV4oijXQ+mcCZ0PiR4y+pMUX-KPBSpUGSslEt+mooyAyFnaRKac8plT3ZrI2WMLZk5TLHP2Yc5NbSQQOsudcnupiZkCz-lG1Qiyo0rNjcs9Z9TGlJuZrsnSJzOkgG6emvZYazkjPrVciZAC7lAMsOvNymwlZIASGAftfYIBDoAFIQHFICww-hG1qmBZ-UFujGhNGZDJHoZSsFivQFmbABz+1QDgBANyUA1hrMRcSh4QbUUrXRf1TF+7sVzKPcAE9Z6L00QAOosBOsbHoAAhfiCg4AAGlvhrJQtUitlR7JBrXYk+oAArWdaBKXofFLSlAhII7+U8Va7xo4uFMlZVioJFVnXCOzmaq84i+VYoFcdUuIrJGvvZcEnRKGCoNxlSG0sz0DU9UVd3ExlQ+bqqHrmaxOrbH6vlQ4m5XGKquPqDy4AlqUZx3tRWqjB8hS0YlEwpK8o1ksbkQoiuXCK2qfTupvjdUm6CfqMBkMdLDHiZVXmgG6Zf6WNk9qyGur6h6HXCifDOQjX2e2iS+epnDqyPgOe6AlYzQ1nDMqz1sqAxVlDFlrz30JPwD7gPTMIMwZyZC7Yk0GWYC1nrCplzd6vz1BnTh-1-5MmEq-Dk0NN7g2lbVbMv+Kne2yy8J+4do6pvykQMGWAwBsBHsIMQmAK7zDIckvrQ2xtTbGFwa1uELx-4UMlfPEA3A8BUuu1AOlhHPWMoEbUNeG9mkDACQZxcNHj7vcrAgBeHrEnLxI2OO7GMvuxcEb9k+6IAf0YSYOZ7+5weLcS-IQJ0OQl-dPgjjHWmWvXwW3gANvXgI8YG7myTZXv4FuMipoAA)

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
