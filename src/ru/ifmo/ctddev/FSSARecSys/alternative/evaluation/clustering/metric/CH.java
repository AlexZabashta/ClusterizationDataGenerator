package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metric;

import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class CH extends ClusterMetric {

    public CH(ArrayList<Instances> instances, Clusterer clusterer) {
        super(instances, clusterer);
    }

    @Override
    public String getName() {
        return "CH";
    }

    @Override
    public Double evaluate() throws Exception {
        Double numerator = 0.0;

        Instance datasetCentroid = getDatasetCentroid();
        Instances centroidsCpy = new Instances(centroids);
        centroidsCpy.add(datasetCentroid);

        EuclideanDistance e = new EuclideanDistance(centroidsCpy);

        Double sum = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            sum += clusters.get(i).numInstances() * Math.pow(e.distance(centroidsCpy.instance(i), centroidsCpy.lastInstance()), 2.0);
        }
        numerator = (unitedClusters.numInstances() - numOfClusters) * sum;

        Double denominator = 0.0;

        sum = 0.0;
        for (int i = 0; i < numOfClusters; i++) {
            Instances currCluster = clusters.get(i);
            currCluster.add(centroids.instance(i));
            EuclideanDistance ecl = new EuclideanDistance(currCluster);
            for (int j = 0; j < currCluster.numInstances() - 1; j++) {
                sum += Math.pow(ecl.distance(currCluster.instance(j), currCluster.lastInstance()), 2.0);
            }
        }
        denominator = sum * (numOfClusters - 1);
        return numerator / denominator;
    }
}
