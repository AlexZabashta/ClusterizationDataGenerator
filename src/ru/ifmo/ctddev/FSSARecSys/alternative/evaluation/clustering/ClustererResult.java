package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering;

/**
 * Created by Администратор on 12.08.2015.
 */
public class ClustererResult {
    private Double DBIndex;
    private Double DunnIndex;
    private Double SilhouetteIndex;

    public ClustererResult(Double DBIndex, Double DunnIndex, Double SilhouetteIndex){
        this.DBIndex = DBIndex;
        this.DunnIndex = DunnIndex;
        this.SilhouetteIndex = SilhouetteIndex;
    }

    public Double countSquareDistance(){
        return Math.sqrt(Math.pow(DBIndex, 2.0) + Math.pow((1 / DunnIndex), 2.0) + Math.pow((1 / SilhouetteIndex), 2.0));
    }
}
