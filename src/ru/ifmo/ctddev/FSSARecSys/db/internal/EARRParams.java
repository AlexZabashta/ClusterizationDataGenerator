package ru.ifmo.ctddev.FSSARecSys.db.internal;

import java.util.DoubleSummaryStatistics;

/**
 * Created by Администратор on 11.08.2015.
 */
public class EARRParams {
    private Double accuracy;
    private Double numberOfSelectedFeatures;
    private Double selectionTime;

    public EARRParams (Double accuracy, Double numberOfSelectedFeatures, Double selectionTime) {
        this.accuracy = accuracy;
        this.numberOfSelectedFeatures = numberOfSelectedFeatures;
        this.selectionTime = selectionTime;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public Double getNumberOfSelectedFeatures() {
        return numberOfSelectedFeatures;
    }

    public Double getSelectionTime() {
        return selectionTime;
    }
}
