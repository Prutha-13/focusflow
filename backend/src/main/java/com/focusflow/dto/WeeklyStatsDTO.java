package com.focusflow.dto;

import java.util.Map;

public class WeeklyStatsDTO {

    private Map<String, DayStat> dailyStats;
    private long totalCompleted;
    private long totalPending;
    private long totalInProgress;

    public WeeklyStatsDTO(Map<String, DayStat> dailyStats, long totalCompleted,
                          long totalPending, long totalInProgress) {
        this.dailyStats = dailyStats;
        this.totalCompleted = totalCompleted;
        this.totalPending = totalPending;
        this.totalInProgress = totalInProgress;
    }

    public static class DayStat {
        private String date;
        private int tasksCompleted;
        private int pomodorosCompleted;

        public DayStat(String date, int tasksCompleted, int pomodorosCompleted) {
            this.date = date;
            this.tasksCompleted = tasksCompleted;
            this.pomodorosCompleted = pomodorosCompleted;
        }

        public String getDate() { return date; }
        public int getTasksCompleted() { return tasksCompleted; }
        public int getPomodorosCompleted() { return pomodorosCompleted; }
    }

    public Map<String, DayStat> getDailyStats() { return dailyStats; }
    public long getTotalCompleted() { return totalCompleted; }
    public long getTotalPending() { return totalPending; }
    public long getTotalInProgress() { return totalInProgress; }
}