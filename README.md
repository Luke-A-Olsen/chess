# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

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

## Phase 2 Sequence Diagram

The link below will lead you to my design plan for the Web API that is required for Phase 3 of this chess project.

[Sequence Diagram]([https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwdFNp43h+P4XgoOgMRxIk+mGYJ9i+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDBRVHoI2DHacx-gouu-jYOKGr8WiMAAOJKho1niTUdkxc5bn2Eq3mXr5hT0YxOkBBwADsbhOCgTgxBGwRwFxABs8AToYcVzDARTIOYNnVJJrQdOlmXTNliHoFmGVzAAckqmkBUxumWCgfYQJsRlIAkYBzQtS0AFIQOKsUVv4ySgGqbUlGJvpdaWzTMjJPSjSgWXwUNimjNgCDAHNUBwBACDQGsd0AJLSFN+VBV473LatYPyogwawMA2CvYQeQFK1iXnRJl0OU5LlucY-mYEAA](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2Z0YKAE9VuImgDmMAAwA6AJyZMdqBACu2AMQALADMABwATK4gMP7IdgAWYDoIPoYASih2SKrmckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9TsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6CwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YpmeqBRzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKGmTgeO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+KzzDDh1L4Wr9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEjiOFmEwQZ8MDQcCyxwfECFISh+xXOgHAeF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoARiqfJCIK-P8gK0eh8KVEB-rgeWKkwes+h-DsXzQo8wHiVQSIwAgQnihignCQSRJgKSb6GLuNL7gyTJTspXI3nuI6CsuMBihKboynKZbvEqmAqsGGpujAaAQMwABmvgStAMDqTsMDiiA0AouAIW+WFSa+s6Xbpb2CAwPlMVbl5IHVP6zLTJe0BIAAXigHBRjGcaFKBmHIKmMDpgAjAROaqHm8zQUWJb1D4PV6n1g27HRTaVby1U2XZrXyNu3lUqF-L1IecgoM+8Tnpe163oulQRY+AYvW1dXtnppYueKGSqABmAAx1ElgYRhnzCRqHfBRVH1vDWkTTV8BTdhMC4fhowwwlxGkYjl7I8hqNofRjHeH4-heCg6AxHEiT04zLm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDEjiHoNpgoA-U0tIeDVkYSdLr2UJHPPfBMtoJ5f0+UdN0wIyYCPTrlF63OVV3h9wqReKT4-fIsrykrsvJeq316jAkBIcVArilQJpIAxb0ChjXndo1F2QzUrpbfEO1DSNKCxgpcsY6JaaOPN+OLctBZjGt0AbUnKd7Y2DGHQukfq-Vj2vobV22-SRgoNwx6XpbZM28b71CvUGQzBANA+1ev2dv9qulqD+4q7Cas2fpVzo8mWNgDheFZvtNdMbTKLrv42Dihq-FojAADiSoaFzK+lg0V9C6LdhKlLpN61numz4rn-KxDBu9RkA5BvjmXu1s45Gzrv5c2PcPb61rvucKDsorOxfNoN2xp-6e1VBqR6fs9aB2KmgEOyBw7XUXEAhqfYoEP0ThRSuacM7xnXpUHOM084LX5EXVaxYy4KgrlAAaQ0GxUyQcdWqnZ6hNynrZHcrcB6jnsmiMBagMT9zrig+oaDKxNTUR6CO8c7JPyVDyTodCf5L0Bqo2+IMwaAPoTAcYb8cwFgaC4pUABJaQBYQSbHiLqFAbpOTFzImMZIoA1QhMgsTEEriUAADlYkIwuJ0NenV4QbxKFvHGO98YJNUO4zxcwfF+OWAEoJMSVIIxBJEkA0SiIrVqcsBJySam0XSdXamzF-AcAAOyuEcCgRwMQIzBDgFxAAbPACchg1FFE3tzTJvNWgdFfu-JOZMCJtNiWjTJ8tf7YN1khHZSp2krS0oAqR8j6p3XRGojEcA5lqLcmoDyUDFEwNNkyC2CDNHIPvDop2E9YruxwYUL2+DLyEIDtkEhZCw4SLvNQnstD2pRxWZKIRIjhrRnTmNLO2TppzW4bmfMfD1qCMYcI3aYiDpGMxdI0FcjBxfL8jAe5KBHkJNepQyO9sZFrmvmYrcjLqEJJ8ZYh41j6jPKPNypU9iECAVnvHfSYxJW+PqBkqGk0cnbzxiUlAZSdXdM8DTAIFhO4OU2EzJACQwDWr7BAO1AApCA4oRVzBiPUtUiycnLKhqs5kMkegJI-ic9ABFsAIGANaqAcAIAOSgF8eJ3jtUwF1YmKxX4-5RsUvjWN8bKBJpTWm1pGaCyWWsUG259QABWnq0CPI9eKV5KBCTvINtPaBHKzZ-IhQC6qgrHYSlka7OKCCkp4InrCxm8Lg6hwoW3euNy7Jov7Bipx3UaW4uYYSthmMDWcPztmHhFLCz8NLJtPddK97Nn5cYjWE7gCfJkPy2BPKM3DrtkPMd64DGYLilqmdKVvWGA4BANQ6VMowCtGiZFVD10a03dKnmMAABCIY3k5APZnI9HD0xGrGIXS9Jdr31D0OuFEXacj0pruKlD9UgPnShTAXD6J1ylRTcQk0ZoazhnGobJxgYBNhiQvh1hBzs6b1zpmAuF7mkUapfx4M5o-ZCYYx4DF7KRwbWwFoB5SoMS8u0L+weEVmSGfuhB5uvaPxHPniOReX51VgWzcvdhcm8lGofRa3pXh432sdUF+UiBgywGANgWNhA8jxnvjcyS-NBbC1FsYb+Mq80wAxBlWUEBzSkmuR2etnLuB4A0e+iOt1ytQEq4y0dI8x6GBNE1M6wA1ivrWI8OzrL3y5rhPUZz-JXPWScSMTzWTvMntxrvbpQA))
