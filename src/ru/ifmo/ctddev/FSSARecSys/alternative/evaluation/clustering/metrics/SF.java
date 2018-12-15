package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metrics;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class SF extends ClusterMetric {

    public SF(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);

    }
    @Override
    public String getName() {
        return "SF";
    }


    private Double bcd(){
        Instance datasetCentroid = getDatasetCentroid();
        Instances centroidsCpy = new Instances(centroids);
        centroidsCpy.add(datasetCentroid);

        EuclideanDistance e = new EuclideanDistance(centroidsCpy);

        Double sum = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            sum += clusters.get(i).numInstances() * e.distance(centroidsCpy.instance(i), centroidsCpy.lastInstance());
        }

        return sum / (numOfClusters * unitedClusters.numInstances());

    }

    private Double wcd(){
        Double result = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            Double sum = 0.0;
            Instances currCluster = clusters.get(i);
            currCluster.add(centroids.instance(i));
            EuclideanDistance ecl = new EuclideanDistance(currCluster);
            for (int j = 0; j < currCluster.numInstances() - 1; j++) {
                sum += ecl.distance(currCluster.instance(j), currCluster.lastInstance());
            }
            result += (1 / clusters.get(i).numInstances()) * sum;
        }
        return result;
    }

    @Override
    public Double evaluate() throws Exception {
        return Math.exp(Math.exp(bcd() + wcd()));
    }
}
