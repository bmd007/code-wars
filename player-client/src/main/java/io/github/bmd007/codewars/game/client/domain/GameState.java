package io.github.bmd007.codewars.game.client.domain;

import io.github.bmd007.codewars.game.client.dto.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.text.Position;
import java.util.List;
import java.util.stream.Collectors;

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
        if (canShootEnemy()) {
            return Action.FIRE;
        } else if (isInDanger()) {
            return findSafeMove();
        } else {
            return moveStrategically();
        }
    }

    private Tank myTank() {
        return tankA.getId().equals(myTankId) ? tankA : tankB;
    }

    private Tank enemyTank() {
        return tankA.getId().equals(myTankId) ? tankB : tankA;
    }

    private boolean canShootEnemy() {
        return isInLineOfSight(myTank(), enemyTank(), myTank().getSightDirection());
    }

    private boolean isInLineOfSight(Tank viewer, Tank viewee, Direction sightDirection) {
        return false;
    }

    private boolean isInDanger() {
        // Check if we're in any potential line of fire from the enemy
        for (Direction dir : Direction.values()) {
            if (isInLineOfSight(enemyTank, myTank, dir)) {
                return true;
            }
        }
        return false;
    }

    private Action findSafeMove() {
        List<Position> safeMoves = getPossibleMoves(myTank).stream()
                .filter(move -> !isInDanger(move))
                .collect(Collectors.toList());

        return safeMoves.isEmpty() ? moveRandomly() : moveTowards(safeMoves.get(0));
    }

    private Action moveStrategically() {
        List<Position> goodPositions = findGoodShootingPositions();
        return goodPositions.isEmpty() ? moveTowards(enemyTank) : moveTowards(goodPositions.get(0));
    }

    private List<Position> findGoodShootingPositions() {
        return getAllAdjacentPositions(myTank).stream()
                .filter(this::canShootEnemyFrom)
                .filter(pos -> !isInDanger(pos))
                .collect(Collectors.toList());
    }

    private boolean canShootEnemyFrom(Position pos) {
        for (Direction dir : Direction.values()) {
            if (isInLineOfSight(pos, enemyTank, dir)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInDanger(Position pos) {
        for (Direction dir : Direction.values()) {
            if (isInLineOfSight(enemyTank, pos, dir)) {
                return true;
            }
        }
        return false;
    }

    private Action moveTowards(Position target) {
        // Use BFS to find the best move towards the target
        // Return the appropriate MOVE_X action
    }

    private Action moveRandomly() {
        // Choose a random safe direction to move
    }

}
