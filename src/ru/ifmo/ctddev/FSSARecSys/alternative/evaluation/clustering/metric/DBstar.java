package ru.ifmo.ctddev.FSSARecSys.alternative.evaluation.clustering.metric;

import ru.ifmo.ctddev.FSSARecSys.db.internal.Metrics;
import weka.clusterers.Clusterer;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Created by sergey on 06.03.16.
 */
public class DBstar extends ClusterMetric {
    public DBstar(ArrayList<Instances> clusters, Clusterer clusterer) {
        super(clusters, clusterer);
    }

    @Override
    public String getName() {
        return "DB*";
    }

    @Override
    public Double evaluate() throws Exception {
        //count S_i = 1 / |C_i| sum_all_xi(dist(x_i, centr_i))

        ArrayList<Double> sTemp = new ArrayList<>(numOfClusters);
        ArrayList<EuclideanDistance> euclideanDistances = new ArrayList<>(numOfClusters);

        for (int i = 0; i < numOfClusters; i++){
            sTemp.add(Double.NEGATIVE_INFINITY);
            euclideanDistances.add(new EuclideanDistance());
        }

        for (int i = 0; i < numOfClusters; i++) {
            Double sumTmp = 0.0;
            Instances currentCluster = clusters.get(i);
            euclideanDistances.set(i, new EuclideanDistance(currentCluster));

            Instance centroid = getClusterCentroid(i);
            centroids.add(centroid);

            for (int j = 0; j < currentCluster.numInstances(); j++){
                Instance instance = currentCluster.instance(j);
                sumTmp += euclideanDistances.get(i).distance(instance, centroid);
            }
            sTemp.set(i, sumTmp / currentCluster.numInstances());
        }

        //count D_i = max_j (i!=j) {(S_i + S_j)} / min(i!=j) {dist(centr_i, centr_j)}

        ArrayList<Double> dTemp = new ArrayList<>(numOfClusters);
        for (int i = 0; i < numOfClusters; i++){
            dTemp.add(Double.NEGATIVE_INFINITY);
        }

        EuclideanDistance centroidDist = new EuclideanDistance(centroids);

        Double maxVal = Double.NEGATIVE_INFINITY;
        Double minVal = Double.POSITIVE_INFINITY;
        for (int i = 0; i < clusters.size(); i++) {
            maxVal = Double.NEGATIVE_INFINITY;
            minVal = Double.POSITIVE_INFINITY;
            for (int j = 0; j < clusters.size(); j++)
                if (i != j) {
                    maxVal = Double.max(maxVal, (sTemp.get(i) + sTemp.get(j)));
                    minVal = Double.min(minVal, centroidDist.distance(centroids.instance(i), centroids.instance(j)));
                }
            dTemp.set(i, maxVal / minVal);
        }
        Double result = 0.0;

        for (Double i: dTemp) {
            result += i;
        }

        return result / numOfClusters;
    }
}