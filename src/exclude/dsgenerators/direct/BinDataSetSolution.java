package exclude.dsgenerators.direct;

import java.util.HashMap;
import java.util.Map;

import org.uma.jmetal.solution.Solution;

import exclude.features_inversion.classification.dataset.BinDataset;

public class BinDataSetSolution implements Solution<BinDataset> {

    private BinDataset dataset;
    private double ef;

    public BinDataSetSolution(BinDataset dataset) {
        this.dataset = dataset;
    }

    public BinDataSetSolution(BinDataset dataset, double ef) {
        this.dataset = dataset;
        this.ef = ef;
    }

    public BinDataset getDataset() {
        return dataset;
    }

    @Override
    public void setObjective(int index, double value) {
        ef = value;
    }

    @Override
    public double getObjective(int index) {
        return ef;
    }

    @Override
    public BinDataset getVariableValue(int index) {
        return dataset;
    }

    @Override
    public void setVariableValue(int index, BinDataset value) {
        dataset = value;
    }

    @Override
    public String getVariableValueString(int index) {
        return dataset.hashCode() + "";
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }

    @Override
    public Solution<BinDataset> copy() {
        return new BinDataSetSolution(dataset, ef);
    }

    @Override
    public void setAttribute(Object id, Object value) {
        map.put(id, value);
    }

    final Map<Object, Object> map = new HashMap<>();

    @Override
    public Object getAttribute(Object id) {
        return map.get(id);
    }

}
