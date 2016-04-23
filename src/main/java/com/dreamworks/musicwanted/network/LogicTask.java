package com.dreamworks.musicwanted.network;

/**
 * Created by zhang on 2016/4/21.
 */
public abstract class LogicTask implements Runnable {

    public String taskId;

    public LogicTask(String taskId) {
        this.taskId = taskId;
    }

    public abstract void execute();

    @Override
    public void run() {
        execute();
        ExecutorHelper.getInstance().finish(this.taskId);
    }
}
