package com.eXiua.tasksi.dto;

public class TaskKpiDTO {
    public long totalTasks;
    public long todo;
    public long inProgress;
    public long done;
    public double avgProgress;
    public long overdue;


    public TaskKpiDTO() {
    }

    public TaskKpiDTO(long totalTasks, long todo, long inProgress, long done, double avgProgress, long overdue) {
        this.totalTasks = totalTasks;
        this.todo = todo;
        this.inProgress = inProgress;
        this.done = done;
        this.avgProgress = avgProgress;
        this.overdue = overdue;
    }

    @Override
    public String toString() {
        return "TaskKpiDTO{" +
                "totalTasks=" + totalTasks +
                ", todo=" + todo +
                ", inProgress=" + inProgress +
                ", done=" + done +
                ", avgProgress=" + avgProgress +
                ", overdue=" + overdue +
                '}';
    }

    // Getters and Setters (or use Lombok for brevity)
    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getTodo() {
        return todo;
    }

    public void setTodo(long todo) {
        this.todo = todo;
    }

    public long getInProgress() {
        return inProgress;
    }

    public void setInProgress(long inProgress) {
        this.inProgress = inProgress;
    }

    public long getDone() {
        return done;
    }

    public void setDone(long done) {
        this.done = done;
    }

    public double getAvgProgress() {
        return avgProgress;
    }

    public void setAvgProgress(double avgProgress) {
        this.avgProgress = avgProgress;
    }

    public long getOverdue() {
        return overdue;
    }

    public void setOverdue(long overdue) {
        this.overdue = overdue;
    }

}
