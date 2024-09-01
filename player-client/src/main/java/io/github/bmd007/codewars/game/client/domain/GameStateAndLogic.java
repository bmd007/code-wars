package io.github.bmd007.codewars.game.client.domain;

import io.github.bmd007.codewars.game.client.dto.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Data
public class GameStateAndLogic {

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
            log.info("Enemy in line of sight, shooting");
            return Action.FIRE;
        }
        return isInEnemiesLineOfSight()
                .map(this::moveAwayOrShoot)
                .orElseGet(this::getCloserToEnemyOrShoot);
    }

    private Action getCloserToEnemyOrShoot() {
        var currentX = getMyTank().getX();
        var currentY = getMyTank().getY();
        var currentDirection = getMyTank().getSightDirection();
        if (getMyTank().getY() > getEnemyTank().getY()) {
            if (getMyTank().getX() > getEnemyTank().getX()) {
                if (getBattleField().getGameObjects()[currentX][currentY - 1].getType().equals(Ground.class.getSimpleName())) {
                    return Action.MOVE_UP;
                }
                if (getBattleField().getGameObjects()[currentX - 1][currentY].getType().equals(Ground.class.getSimpleName())) {
                    return Action.MOVE_LEFT;
                }
                if (currentDirection == Direction.UP || currentDirection == Direction.LEFT) {
                    return Action.FIRE;
                }
                return Set.of(Action.LOOK_LEFT, Action.LOOK_UP).stream().findAny().get();
            }
            if (getMyTank().getX() < getEnemyTank().getX()) {
                if (getBattleField().getGameObjects()[currentX][currentY - 1].getType().equals(Ground.class.getSimpleName())) {
                    return Action.MOVE_UP;
                }
                if (getBattleField().getGameObjects()[currentX + 1][currentY].getType().equals(Ground.class.getSimpleName())) {
                    return Action.MOVE_RIGHT;
                }
                if (currentDirection == Direction.UP || currentDirection == Direction.RIGHT) {
                    return Action.FIRE;
                }
                return Set.of(Action.LOOK_RIGHT, Action.LOOK_UP).stream().findAny().get();
            }
        }
        if (getMyTank().getY() < getEnemyTank().getY()) {
            if (getMyTank().getX() > getEnemyTank().getX()) {
                if (getBattleField().getGameObjects()[currentX][currentY + 1].getType().equals(Ground.class.getSimpleName())) {
                    return Action.MOVE_DOWN;
                }
                if (getBattleField().getGameObjects()[currentX - 1][currentY].getType().equals(Ground.class.getSimpleName())) {
                    return Action.MOVE_LEFT;
                }
                if (currentDirection == Direction.DOWN || currentDirection == Direction.LEFT) {
                    return Action.FIRE;
                }
                return Set.of(Action.LOOK_LEFT, Action.LOOK_DOWN).stream().findAny().get();
            }
            if (getMyTank().getX() < getEnemyTank().getX()) {
                if (getBattleField().getGameObjects()[currentX][currentY + 1].getType().equals(Ground.class.getSimpleName())) {
                    return Action.MOVE_DOWN;
                }
                if (getBattleField().getGameObjects()[currentX + 1][currentY].getType().equals(Ground.class.getSimpleName())) {
                    return Action.MOVE_RIGHT;
                }
                if (currentDirection == Direction.DOWN || currentDirection == Direction.RIGHT) {
                    return Action.FIRE;
                }
                return Set.of(Action.LOOK_RIGHT, Action.LOOK_DOWN).stream().findAny().get();
            }
        }
        return Action.FIRE;
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

    private Action moveAwayOrShoot(Direction enemiesLineOfSight) {
        if (getMyTank().getY() > 0) {
            var gameObject = getBattleField().getGameObjects()[getMyTank().getX()][getMyTank().getY() - 1];
            if (gameObject.getType().equals(Ground.class.getSimpleName())) {
                return Action.MOVE_UP;
            } else {
                if (isInLineOfSight(getMyTank(), getEnemyTank(), Direction.UP)) {
                    if (getMyTank().getSightDirection() == Direction.UP) {
                        return Action.FIRE;
                    } else {
                        return Action.MOVE_UP;
                    }
                }
            }
        }
        if (getMyTank().getY() < getBattleField().getHeight() - 1) {
            var gameObject = getBattleField().getGameObjects()[getMyTank().getX()][getMyTank().getY() + 1];
            if (gameObject.getType().equals(Ground.class.getSimpleName())) {
                return Action.MOVE_DOWN;
            } else {
                if (isInLineOfSight(getMyTank(), getEnemyTank(), Direction.DOWN)) {
                    if (getMyTank().getSightDirection() == Direction.DOWN) {
                        return Action.FIRE;
                    } else {
                        return Action.MOVE_DOWN;
                    }
                }
            }
        }
        if (getMyTank().getX() > 0) {
            var gameObject = getBattleField().getGameObjects()[getMyTank().getX() - 1][getMyTank().getY()];
            if (gameObject.getType().equals(Ground.class.getSimpleName())) {
                return Action.MOVE_LEFT;
            } else {
                if (isInLineOfSight(getMyTank(), getEnemyTank(), Direction.LEFT)) {
                    if (getMyTank().getSightDirection() == Direction.LEFT) {
                        return Action.FIRE;
                    } else {
                        return Action.MOVE_LEFT;
                    }
                }
            }
        }
        if (getMyTank().getX() < getBattleField().getWidth() - 1) {
            var gameObject = getBattleField().getGameObjects()[getMyTank().getX() + 1][getMyTank().getY()];
            if (gameObject.getType().equals(Ground.class.getSimpleName())) {
                return Action.MOVE_RIGHT;
            } else {
                if (isInLineOfSight(getMyTank(), getEnemyTank(), Direction.RIGHT)) {
                    if (getMyTank().getSightDirection() == Direction.RIGHT) {
                        return Action.FIRE;
                    } else {
                        return Action.MOVE_RIGHT;
                    }
                }
            }
        }
        return switch (enemiesLineOfSight) {
            case UP -> Action.MOVE_DOWN;
            case DOWN -> Action.MOVE_UP;
            case LEFT -> Action.MOVE_RIGHT;
            case RIGHT -> Action.MOVE_LEFT;
        };
    }
}
