package io.github.bmd007.codewars.game.engine.dto;


import io.github.bmd007.codewars.game.engine.domain.Game;

public record GameStateDto(
        String gameId,
        BattleField battleField,
        Tank tankA,
        Tank tankB) {

    public record BattleField(int width, int height, GameObject[][] gameObjects) {
    }

    public record GameObject(int x, int y, String type) {
    }

    public record Tank(int x, int y, int numberOfBombs, Game.Direction sightDirection, boolean isAlive, String id) {
    }

    public static GameStateDto fromGame(Game game) {
        GameObject[][] gameObjects = new GameObject[game.getBattleField().width()][game.getBattleField().height()];
        for (int x = 0; x < game.getBattleField().width(); x++) {
            for (int y = 0; y < game.getBattleField().height(); y++) {
                gameObjects[x][y] = new GameObject(x, y, game.getBattleField().gameObjects()[x][y].getType());
            }
        }
        return new GameStateDto(
                game.getGameId(),
                new BattleField(game.getBattleField().width(), game.getBattleField().height(),gameObjects ),
                new Tank(game.getTankA().getX(), game.getTankA().getY(), game.getTankA().getNumberOfBombs(), game.getTankA().getSightDirection(), game.getTankA().isAlive(), game.getTankA().getId()),
                new Tank(game.getTankB().getX(), game.getTankB().getY(), game.getTankB().getNumberOfBombs(), game.getTankB().getSightDirection(), game.getTankB().isAlive(), game.getTankB().getId())
        );
    }
}
