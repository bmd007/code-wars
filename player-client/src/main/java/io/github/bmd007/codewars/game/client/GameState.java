package io.github.bmd007.codewars.game.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class GameState {

    private String gameId;
    private BattleField battleField;
    private Tank tankA;
    private Tank tankB;
    private boolean isOver = false;

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

    @Data
    public static class BattleField {
        private int width;
        private int height;
        private GameObject[][] gameObjects;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GameObject {
        private int x;
        private int y;
        private String type;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Ground extends GameObject {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Stone extends GameObject {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Bullet extends GameObject {
        private Direction trajectoryDirection;
        private String tankId;
        private boolean hit = false;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Tank extends GameObject {
        private Direction sightDirection = Direction.RIGHT;
        private boolean isAlive = true;
        private String id;
    }
}
