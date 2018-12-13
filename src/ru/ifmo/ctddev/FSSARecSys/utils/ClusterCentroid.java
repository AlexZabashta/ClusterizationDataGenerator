package ru.ifmo.ctddev.FSSARecSys.utils;

import weka.core.*;

/**
 * Created by Администратор on 20.08.2015.
 */
public class ClusterCentroid {

    private DistanceFunction m_DistanceFunction = new EuclideanDistance();
    private boolean m_PreserveOrder = false;

    public void setDistanceFucntion(DistanceFunction df) throws Exception {
        if (!(df instanceof EuclideanDistance)
                && !(df instanceof ManhattanDistance)) {
            throw new Exception(
                    "SimpleKMeans currently only supports the Euclidean and Manhattan distances.");
        }
        m_DistanceFunction = df;
    }

    public Instance findCentroid(int centroidIndex, Instances members) {
        double[] vals = new double[members.numAttributes()];

        // used only for Manhattan Distance
        Instances sortedMembers = null;
        int middle = 0;
        boolean dataIsEven = false;

        if (m_DistanceFunction instanceof ManhattanDistance) {
            middle = (members.numInstances() - 1) / 2;
            dataIsEven = ((members.numInstances() % 2) == 0);
            if (m_PreserveOrder) {
                sortedMembers = members;
            } else {
                sortedMembers = new Instances(members);
            }
        }

        for (int j = 0; j < members.numAttributes(); j++) {

            // in case of Euclidian distance the centroid is the mean point
            // in case of Manhattan distance the centroid is the median point
            // in both cases, if the attribute is nominal, the centroid is the mode
            if (m_DistanceFunction instanceof EuclideanDistance
                    || members.attribute(j).isNominal()) {
                vals[j] = members.meanOrMode(j);
            } else if (m_DistanceFunction instanceof ManhattanDistance) {
                // singleton special case
                if (members.numInstances() == 1) {
                    vals[j] = members.instance(0).value(j);
                } else {
                    vals[j] = sortedMembers.kthSmallestValue(j, middle + 1);
                    if (dataIsEven) {
                        vals[j] = (vals[j] + sortedMembers.kthSmallestValue(j, middle + 2)) / 2;
                    }
                }
            }
        }
        return new Instance(1.0, vals);
    }
}

