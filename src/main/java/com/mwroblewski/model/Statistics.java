package com.mwroblewski.model;

import lombok.*;

@Getter
@Setter
public class Statistics {

    private int wins;
    private int losses;
    private double rate;

    public Statistics() {
    }

    public Statistics(int wins, int losses) {
        this.wins = wins;
        this.losses = losses;
        this.rate = ((double) wins) / (wins + losses);
    }

    public Statistics getDeepCopy() {
        return new Statistics(this.wins, this.losses);
    }

    private boolean isCorrectContent(int wins, int losses) {
        return wins >= 0 && losses >= 0;
    }

    public boolean addStatistics(int wins, int losses, boolean reverse) {
        if (isCorrectContent(wins, losses)) {
            if (reverse) {
                this.wins += losses;
                this.losses += wins;
                this.rate = ((double) this.wins) / (this.wins + this.losses);
            } else {
                this.wins += wins;
                this.losses += losses;
                this.rate = ((double) this.wins) / (this.wins + this.losses);
            }

            return true;
        } else
            return false;

    }

    public boolean addStatistics(Statistics statistics, boolean reverse) {
        return this.addStatistics(statistics.getWins(), statistics.getLosses(), reverse);
    }

}