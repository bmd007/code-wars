package io.github.bmd007.codewars.game.leaderboard.dto;

public record ScoreDto(String team, int score) implements Comparable<ScoreDto> {
    @Override
    public int compareTo(ScoreDto o) {
        return Integer.compare(o.score(), this.score());
    }
}
