package ru.ifmo.ctddev.FSSARecSys.db.internal;

import ru.ifmo.ctddev.FSSARecSys.calculators.ClassifierEvaluator;
import ru.ifmo.ctddev.FSSARecSys.db.ClassifierManager;

import java.io.File;
import java.util.List;

/**
 * Created by Сергей on 14.07.2015.
 */
public class ClusterManager implements ClassifierManager {
    @Override
    public List<String> getAvailableClassifierNames() {
        return null;
    }

    @Override
    public ClassifierEvaluator get(String name) {
        return null;
    }

    @Override
    public boolean register(String name, String className, String[] options) {
        return false;
    }

    @Override
    public boolean register(String name, String className, String[] options, File jarFile) {
        return false;
    }
}
