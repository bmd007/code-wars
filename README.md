# CODE WARS
A game platform for learning programming, datastructures and algorithms.
You can code your own player and compete with others.

### Playing the game manually locally:
    1- Clone the repository
    2- Make sure you have Java 22 installed + docker
    3- Run `docker-compose -f kafka-compose.yml up`
    4- cd engine
    5- Run `./gradlew bootRun --args='--spring.profiles.active=local'`
    6- open localhost:8080 in your browser

## Writing code to play the game:
You need to make changes to the [player-client](player-client) project to write your own player.
What your player code needs to do:

    1- Get the latest state of the game from the engine by calling the REST API. `GET http://${gameEngineHost}:8080/games/current`.
    2- Decide on the next move.
    3- Send a GameCommand.java to the game engine as a kafka message. PlayerClient.java has an example of how to send a message to the game engine. 

As long as you can send commands to the Kafka topic and read the latest state of the game from the REST API, you can write your player in any language you want.
You just need to provide a Docker image name to the [game-orchestra-compose.yml](game-orchestra-compose.yml) file.
The real tournament will also be played using Docker images/containers.

There is no limitation on what method and technology you use to decide the next move. Just keep in mind that the engine tries it's best to keep the game fair.
For example, the engine will only process one command from each player at a time.
The rest API to fetch the latest state of the game is also rate limited to prevent spamming (TODO!).
The rules about resource (CPU, memory, etc.) usage will be defined in each tournament. But there should be no strange limitations. 
This is not going to be an efficiency contest.

## Game story:
The game is a 2D grid where each cell can be empty, a wall, or a tank (player).
There are only two tanks in the game and destruction of one of the tanks ends the game.
One hit is enough to destroy a tank.
The tanks can move in 4 directions (up, down, left, right) and can shoot in 4 directions.
The walls van be destroyed by shooting at them as well. 
One hit is enough to destroy a wall.

### Running your code against itself:
    1- In the player-client/build.gradle file, change bootBuildImage.imageName to your desirable docker image name
    2- Replace `bmd007/codewars-player` in the [game-orchestra-compose.yml](game-orchestra-compose.yml) file with your docker image name (corresponding to the previous step)
    3- Finish the implementation of player-client
    4- Run `./gradlew bootBuildImage` in the engine directory
    4- Run `./gradlew bootBuildImage` in the player-client directory
    6- Run `docker-compose -f game-orchestra-compose.yml up`
    7- Now your code will be playing against itself
    8- Watch the game on localhost:8080 (do not use the buttons on the UI meaning do not interfere with the game, just watch! :)) )

## Tournament:
In order to register for the tournament, add a comment to https://github.com/bmd007/code-wars/issues/1

#TODO
 - application ([tournament-orchestrator](tournament-orchestrator)) that fills the [tournament-orchestrator-compose.yml.template](tournament-orchestrator%2Fsrc%2Fmain%2Fresources%2Ftournament-orchestrator-compose.yml.template) and starts games
 - [engine](engine)
 - sample player [player-client](player-client)
 - [leaderboard](leaderboard) app
 - game visualizer
 - map maker
