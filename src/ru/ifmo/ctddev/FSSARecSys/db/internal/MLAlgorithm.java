package ru.ifmo.ctddev.FSSARecSys.db.internal;

import java.io.File;

/**
 * Created by sergey on 27.03.15.
 */
public class MLAlgorithm {
    private String name;
    private String classPath;
    private String options;
    private String taskType;

    public MLAlgorithm(String name, String classPath, String options, String taskType){
        this.name = name;
        this.classPath = classPath;
        this.taskType = taskType;
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getOptions() {
        return options;
    }

    public String getTaskType() {
        return taskType;
    }
}
