package com.dreamworks.musicwanted.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.internal.Util;

/**
 * Created by zhang on 2016/4/21.
 */
public class ExecutorHelper {

    private static ExecutorHelper instance;
    private ExecutorService mExecutorService;
    private ExecutorService bgExecutorService;
    private Map<String, LogicTask> taskMap;

    private ExecutorHelper() {
        mExecutorService = Executors.newCachedThreadPool(Util.threadFactory("Logic Task", false));
        bgExecutorService = Executors.newCachedThreadPool(Util.threadFactory("Background Task", false));
        taskMap = new ConcurrentHashMap<>();
    }

    public static ExecutorHelper getInstance() {
        if (instance == null) {
            synchronized (ExecutorHelper.class) {
                if (instance == null) {
                    instance = new ExecutorHelper();
                }
            }
        }
        return instance;
    }

    public void execute(LogicTask task) {
        if (!taskMap.containsKey(task.taskId)) {
            taskMap.put(task.taskId, task);
            mExecutorService.execute(task);
        }
    }

    public void finish (String taskId) {
        if (taskMap.containsKey(taskId)) {
            taskMap.remove(taskId);
        }
    }

    public void bgExecute (Runnable runnable) {
        bgExecutorService.execute(runnable);
    }
}
