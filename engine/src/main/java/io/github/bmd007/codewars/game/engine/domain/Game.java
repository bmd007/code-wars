package io.github.bmd007.codewars.game.engine.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.bmd007.codewars.game.engine.dto.GameEvent;
import io.github.bmd007.codewars.game.engine.dto.GameStartedEvent;
import io.github.bmd007.codewars.game.engine.dto.TankScored;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Slf4j
@Data
public class Game {

    @JsonIgnore
    private final Random random = new Random();
    @JsonIgnore
    private final ConcurrentLinkedQueue<GameEvent> gameEvents = new ConcurrentLinkedQueue<>();

    private String gameId;
    private BattleField battleField;
    private Tank tankA;
    private Tank tankB;
    private boolean isOver = false;

    public Game(String gameId, String tankAId, String tankBId) {
        this.gameId = gameId;
        this.battleField = new BattleField();
        tankA = new Tank(0, 0, tankAId);
        tankB = new Tank(battleField.width - 1, battleField.height - 1, tankBId);
        battleField.addGameObject(tankA);
        battleField.addGameObject(tankB);
        addRandomStones();
        gameEvents.add(new GameStartedEvent(gameId, tankAId, tankBId));
    }

    public void applyActionToTankA(Action action) {
        applyAction(tankA, action);
    }

    public void applyActionToTankB(Action action) {
        applyAction(tankB, action);
    }

    private void applyAction(Tank tank, Action action) {
        if (!tank.isAlive()) {
            log.warn("Player {} is dead", tank.getId());
            return;
        }
        if (tank == tankA) {
            switch (action) {
                case MOVE_UP -> moveTankA(Direction.UP);
                case MOVE_DOWN -> moveTankA(Direction.DOWN);
                case MOVE_LEFT -> moveTankA(Direction.LEFT);
                case MOVE_RIGHT -> moveTankA(Direction.RIGHT);
                case FIRE -> fireByTankA();
                case LOOK_UP -> changeDirectionByTankA(Direction.UP);
                case LOOK_DOWN -> changeDirectionByTankA(Direction.DOWN);
                case LOOK_LEFT -> changeDirectionByTankA(Direction.LEFT);
                case LOOK_RIGHT -> changeDirectionByTankA(Direction.RIGHT);
            }
        } else if (tank == tankB) {
            switch (action) {
                case MOVE_UP -> moveTankB(Direction.UP);
                case MOVE_DOWN -> moveTankB(Direction.DOWN);
                case MOVE_LEFT -> moveTankB(Direction.LEFT);
                case MOVE_RIGHT -> moveTankB(Direction.RIGHT);
                case FIRE -> fireByTankB();
                case LOOK_UP -> changeDirectionByTankB(Direction.UP);
                case LOOK_DOWN -> changeDirectionByTankB(Direction.DOWN);
                case LOOK_LEFT -> changeDirectionByTankB(Direction.LEFT);
                case LOOK_RIGHT -> changeDirectionByTankB(Direction.RIGHT);
            }
        }
    }

    public void changeDirectionByTankA(Direction direction) {
        tankA.changeDirection(direction);
    }

    public void changeDirectionByTankB(Direction direction) {
        tankB.changeDirection(direction);
    }

    public void fireByTankA() {
        fireBy(tankA);
    }

    public void fireByTankB() {
        fireBy(tankB);
    }

    private void fireBy(Tank tank) {
        if (!tank.fire()) {
            return;
        }
        int x = tank.getX();
        int y = tank.getY();
        Direction sightDirection = tank.getSightDirection();
        switch (sightDirection) {
            case UP -> {
                for (int i = y - 1; i >= 0; i--) {
                    if (hitIfAnyAt(x, i)) {
                        return;
                    }
                }
            }
            case DOWN -> {
                for (int i = y + 1; i < battleField.height; i++) {
                    if (hitIfAnyAt(x, i)) {
                        return;
                    }
                }
            }
            case LEFT -> {
                for (int i = x - 1; i >= 0; i--) {
                    if (hitIfAnyAt(i, y)) {
                        return;
                    }
                }
            }
            case RIGHT -> {
                for (int i = x + 1; i < battleField.width; i++) {
                    if (hitIfAnyAt(i, y)) {
                        return;
                    }
                }
            }
        }
    }

    private void destroyTank(Tank tank) {
        battleField.convertToGround(tank);
        tank.kill();
        if (tank == tankA) {
            tankA = null;
            gameEvents.add(new TankScored(gameId, tankB.getId()));
        } else if (tank == tankB) {
            tankB = null;
            gameEvents.add(new TankScored(gameId, tankA.getId()));
        }
        isOver = true;
    }

    private boolean hitIfAnyAt(int x, int i) {
        GameObject gameObject = battleField.getGameObject(x, i);
        return switch (gameObject) {
            case Stone stone when stone.isDestructible() -> {
                battleField.convertToGround(gameObject);
                yield true;
            }
            case Tank tank -> {
                destroyTank(tank);
                yield true;
            }
            case null, default -> {
                log.debug("No object at position ({}, {})", x, i);
                yield false;
            }
        };
    }

    public void addRandomStones() {
        for (int i = 0; i < battleField.height; i++) {
            for (int j = 0; j < battleField.width; j++) {
                if (random.nextBoolean()) {
                    addStone(j, i);
                }
            }
        }
    }

    public void addStone(int x, int y) {
        battleField.addGameObject(new Stone(x, y));
    }

    public void moveTankA(Direction direction) {
        battleField.moveTank(tankA, direction);
    }

    public void moveTankB(Direction direction) {
        battleField.moveTank(tankB, direction);
    }

    @JsonIgnore
    public Set<GameEvent> getLatestGameEvents() {
        var latestGameEvents = gameEvents.stream().collect(Collectors.toUnmodifiableSet());
        gameEvents.clear();
        return latestGameEvents;
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public enum Action {
        MOVE_UP,
        MOVE_DOWN,
        MOVE_LEFT,
        MOVE_RIGHT,
        LOOK_UP,
        LOOK_DOWN,
        LOOK_LEFT,
        LOOK_RIGHT,
        FIRE
    }

    public record BattleField(int width, int height, GameObject[][] gameObjects) {
        public BattleField {
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException("Width and height must be non-negative");
            }
        }

        private BattleField() {
            this(10, 10, new GameObject[10][10]);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    gameObjects[i][j] = new Ground(i, j);
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    if (gameObjects[i][j] == null) {
                        log.error("({}, {}) was null", i, j);
                    }
                }
            }
        }

        void addGameObject(final GameObject gameObject) {
            if (!(gameObjects[gameObject.getX()][gameObject.getY()] instanceof Ground)) {
                log.warn("({}, {}) Already occupied", gameObject.getX(), gameObject.getY());
                return;
            }
            gameObjects[gameObject.getX()][gameObject.getY()] = gameObject;
        }

        void convertToGround(final GameObject gameObject) {
            gameObjects[gameObject.getX()][gameObject.getY()] = new Ground(gameObject.getX(), gameObject.getY());
        }

        GameObject getGameObject(int x, int y) {
            return gameObjects[x][y];
        }

        private void moveTank(Tank tank, Direction direction) {
            tank.changeDirection(direction);
            int x = tank.getX();
            int y = tank.getY();
            int newX = x;
            int newY = y;
            switch (direction) {
                case UP -> newY = y - 1;
                case DOWN -> newY = y + 1;
                case LEFT -> newX = x - 1;
                case RIGHT -> newX = x + 1;
            }
            if (newX < 0 || newX >= width || newY < 0 || newY >= height) {
                return;
            }
            if (!(gameObjects[newX][newY] instanceof Ground)) {
                log.warn("Can't move to ({}, {}), already occupied", newX, newY);
                return;
            }
            gameObjects[x][y] = new Ground(x, y);
            tank.move(newX, newY);
            gameObjects[newX][newY] = tank;
        }
    }

    @NoArgsConstructor
    @Data
    public static class GameObject {
        private int x;
        private int y;

        private GameObject(int x, int y) {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Invalid position: (" + x + ", " + y + ")");
            }
            this.x = x;
            this.y = y;
        }

        public String getType() {
            return this.getClass().getSimpleName();
        }

        void move(int newX, int newY) {
            if (newX < 0 || newY < 0) {
                throw new IllegalArgumentException("Invalid position: (" + newX + ", " + newY + ")");
            }
            setX(newX);
            setY(newY);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Ground extends GameObject {
        private Ground(int x, int y) {
            super(x, y);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Stone extends GameObject {
        private boolean isDestructible = true;
        private Stone(int x, int y) {
            super(x, y);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Tank extends GameObject {
        private int numberOfBombs;
        private Direction sightDirection;
        private boolean isAlive = true;
        private String id;

        private Tank(int x, int y, String id) {
            super(x, y);
            this.id = id;
            this.numberOfBombs = 25;
            this.sightDirection = Direction.RIGHT;
        }

        private void kill() {
            isAlive = false;
        }

        private void changeDirection(Direction direction) {
            sightDirection = direction;
        }

        private boolean fire() {
            if (numberOfBombs <= 0) {
                log.warn("Player {} has no ammunition", id);
                return false;
            }
            numberOfBombs--;
            return true;
        }
    }
}
