package io.github.bmd007.codewars.game.leaderboard.domain;

public record Score(String team, int score) implements Comparable<Score> {
    public Score {
        if (team == null || team.isBlank() || team.isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null, empty or blank");
        }
        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }
    }

    public Score(String team){
        this(team, 0);
    }

    public Score addScore(){
        return new Score(this.team, this.score + 1);
    }

    @Override
    public int compareTo(Score o) {
        return Integer.compare(o.score(), this.score());
    }
}
