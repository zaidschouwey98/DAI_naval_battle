# Naval battle
## Overview
The Naval Battle Game is a simple network-based game implemented using Java. It allows two players to compete in a classic naval battle game over a TCP connection. The project demonstrates the implementation of network communication, multithreading, and the use of Picocli for command-line interfaces.

Players can connect to the server and take turns to attack each other's grids, aiming to sink all opponent ships to win the game. The game supports rematches and ensures smooth communication between players through a well-defined protocol.

## Features
- Multiplayer Gameplay: Two players can join and play simultaneously. More than one game can be played simultaneously.
- Rematch Support: Players can request a rematch after a game ends.

## Usage
### Server
The server hosts the game, allowing players to connect.

*Go to the repository folder first*

``` java -jar target/naval_battle-1.0-SNAPSHOT.jar server --port <PORT>```

### Exemple

```java -jar target/naval_battle-1.0-SNAPSHOT.jar server --port 6433```

### Output 
```
Server started !
Waiting for players to connect...
```

### Client
The client connects to the server to play the game.

``` java -jar target/naval_battle-1.0-SNAPSHOT.jar client --host <SERVER_IP> --port <PORT>```

### Exemple

```java -jar target/naval_battle-1.0-SNAPSHOT.jar client --host 127.0.0.1 --port 6433```

### Output
```
Waiting for the game to start...
```
When another player connects, the game start.
```bash
Game is starting !
```
Then comes the boat placement phase.
```
-----------------------------------
Place your 3 boats [1 - 16]
Enter boat n°1:
```
When all the boat are placed from boths sides, the game starts.

```
You've placed your boats. Wait for opponent.
-----------------------------------
Your board:
          1   2   3   4  
         +---+---+---+---+
       1 | O | O | O | ~ |
         +---+---+---+---+
       2 | ~ | ~ | ~ | ~ |
         +---+---+---+---+
       3 | ~ | ~ | ~ | ~ |
         +---+---+---+---+
       4 | ~ | ~ | ~ | ~ |
         +---+---+---+---+
-----------------------------------
Enter the cell to attack:
```


- ' ~ ' : empty space
- ' O ' : Boat
- ' X ' : Hit 
- ' * ' : Miss



When all of a player's ships are sunk, the game stops and ask each player for a rematch.
```
YOU WIN!
You can type 'Rematch' to ask for a rematch and 'Exit' to quit.
```
You can now write rematch to have a battle again against the same opponent or exit to leave.


## Build and Development
### 1. Clone the repository

```bash
git clone https://github.com/zaidschouwey98/DAI_naval_battle.git
cd DAI_naval_battle
```

### 2. Run the project
   
Open the repository on Intellij IDEA or your favorite IDE.
Run the project with the "Run the server" launch configuration or use the "Package the app" configuration to create a .jar.

## Docker
The docker package can be found at [this url](https://github.com/users/zaidschouwey98/packages/container/package/naval_battle).
### Build Docker Image
To containerize the application, run the following command:
```docker build -t naval_battle .```

### Run with Docker
#### Server
```docker run -t naval_battle server```

#### Client
```docker run -t naval_battle client --host=<HOSTIP>```

Exemple for localhost :

```docker run -t naval_battle client --host=127.0.0.1```
