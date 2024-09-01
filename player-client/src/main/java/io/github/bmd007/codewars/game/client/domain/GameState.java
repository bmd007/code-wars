package io.github.bmd007.codewars.game.client.domain;

import io.github.bmd007.codewars.game.client.dto.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Data
public class GameState {

    private String gameId;
    private BattleField battleField;
    private Tank tankA;
    private Tank tankB;
    private boolean isOver = false;
    private String myTankId;

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
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

//    @EqualsAndHashCode(callSuper = true)
//    @Data
//    public static class Bullet extends GameObject {
//        private Direction trajectoryDirection;
//        private String tankId;
//        private boolean hit = false;
//    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Tank extends GameObject {
        private Direction sightDirection;
        private boolean isAlive;
        private String id;
    }

    public Action decideNextAction() {
        if (isEnemyInLineOfLight()) {
            return Action.FIRE;
        }
        return isInEnemiesLineOfSight()
                .map(this::flyAwayOrShoot)
                .orElse(Action.FIRE);
    }

    private Tank getMyTank() {
        return tankA.getId().equals(myTankId) ? tankA : tankB;
    }

    private Tank getEnemyTank() {
        return tankA.getId().equals(myTankId) ? tankB : tankA;
    }

    private boolean isEnemyInLineOfLight() {
        return isInLineOfSight(getMyTank(), getEnemyTank(), getMyTank().getSightDirection());
    }

    private boolean isInLineOfSight(Tank viewer, Tank viewee, Direction sightDirection) {
        return switch (sightDirection) {
            case UP -> viewer.getY() > viewee.getY() && viewer.getX() == viewee.getX();
            case DOWN -> viewer.getY() < viewee.getY() && viewer.getX() == viewee.getX();
            case LEFT -> viewer.getX() > viewee.getX() && viewer.getY() == viewee.getY();
            case RIGHT -> viewer.getX() < viewee.getX() && viewer.getY() == viewee.getY();
        };
    }

    private Optional<Direction> isInEnemiesLineOfSight() {
        return Arrays.stream(Direction.values())
                .filter(dir -> isInLineOfSight(getEnemyTank(), getMyTank(), dir))
                .findFirst();
    }

    private Action flyAwayOrShoot(Direction enemyDirection) {
        if (getMyTank().getY() > 0) {
            var gameObject = getBattleField().getGameObjects()[getMyTank().getX()][getMyTank().getY() - 1];
            if (gameObject instanceof Ground) {
                return Action.MOVE_UP;
            }
        }
        if (getMyTank().getY() < getBattleField().getHeight() - 1) {
            var gameObject = getBattleField().getGameObjects()[getMyTank().getX()][getMyTank().getY() + 1];
            if (gameObject instanceof Ground) {
                return Action.MOVE_DOWN;
            }
        }
        if (getMyTank().getX() > 0) {
            var gameObject = getBattleField().getGameObjects()[getMyTank().getX() - 1][getMyTank().getY()];
            if (gameObject instanceof Ground) {
                return Action.MOVE_LEFT;
            }
        }
        if (getMyTank().getX() < getBattleField().getWidth() - 1) {
            var gameObject = getBattleField().getGameObjects()[getMyTank().getX() + 1][getMyTank().getY()];
            if (gameObject instanceof Ground) {
                return Action.MOVE_RIGHT;
            }
        }
        // can't go anywhere, but at least face the enemy
        return switch (enemyDirection) {
            case UP -> Action.MOVE_DOWN;
            case DOWN -> Action.MOVE_UP;
            case LEFT -> Action.MOVE_RIGHT;
            case RIGHT -> Action.MOVE_LEFT;
        };
    }
}
