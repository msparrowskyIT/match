package com.mwroblewski.service;

import com.mwroblewski.model.Statistics;
import com.mwroblewski.utils.Utils;

public class StatisticsService {

    public Statistics addStatistics(Statistics statistics, int wins, int losses, boolean reverse) {
        Statistics copy = statistics.getDeepCopy();
            if (copy.addStatistics(losses, wins, reverse))
                return copy;

        return null;
    }

    public Statistics addStatistics(Statistics s1, Statistics s2, boolean reverse) {
        return this.addStatistics(s1, s2.getWins(), s2.getLosses(), reverse);
    }

}
