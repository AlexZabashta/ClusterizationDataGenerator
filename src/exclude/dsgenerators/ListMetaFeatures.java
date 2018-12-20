package exclude.dsgenerators;

import java.util.ArrayList;
import java.util.List;

import com.ifmo.recommendersystem.metafeatures.MetaFeatureExtractor;
import com.ifmo.recommendersystem.metafeatures.general.DataSetDimensionality;

import exclude.features_inversion.classification.dataset.BinDataset;

public class ListMetaFeatures {

    private static final List<MetaFeatureExtractor> mf = new ArrayList<>();

    public static String getName(int index) {
        return mf.get(index).getName();
    }

    public static double extractValue(int index, BinDataset dataset) {
        try {
            return mf.get(index).extractValue(dataset);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Double.NaN;
        }
    }

    public static int size() {
        return mf.size();
    }

    static {
        mf.add(new com.ifmo.recommendersystem.metafeatures.general.NumberOfClasses());
        mf.add(new com.ifmo.recommendersystem.metafeatures.general.DataSetDimensionality());
        mf.add(new com.ifmo.recommendersystem.metafeatures.statistical.MeanStandardDeviation());
        mf.add(new com.ifmo.recommendersystem.metafeatures.statistical.MeanCoefficientOfVariation());
        mf.add(new com.ifmo.recommendersystem.metafeatures.general.NumberOfInstances());
        mf.add(new com.ifmo.recommendersystem.metafeatures.general.NumberOfFeatures());
        mf.add(new com.ifmo.recommendersystem.metafeatures.statistical.MeanLinearCorrelationCoefficient());
        mf.add(new com.ifmo.recommendersystem.metafeatures.statistical.MeanSkewness());
        mf.add(new com.ifmo.recommendersystem.metafeatures.statistical.MeanKurtosis());
        mf.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.NormalizedClassEntropy());
        mf.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.MeanNormalizedFeatureEntropy());
        mf.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.MeanMutualInformation());
        mf.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.MaxMutualInformation());
        mf.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.EquivalentNumberOfFeatures());
        mf.add(new com.ifmo.recommendersystem.metafeatures.informationtheoretic.NoiseSignalRatio());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeDevAttr());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeDevBranch());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeDevLevel());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeHeight());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeLeavesNumber());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMaxAttr());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMaxBranch());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMaxLevel());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMeanAttr());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMeanBranch());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMeanLevel());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMinAttr());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMinBranch());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeNodeNumber());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeWidth());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeDevClass());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMaxClass());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMinClass());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.pruned.PrunedTreeMeanClass());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeDevAttr());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeDevBranch());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeDevLevel());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeHeight());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeLeavesNumber());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMaxAttr());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMaxBranch());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMaxLevel());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMeanAttr());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMeanBranch());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMeanLevel());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMinAttr());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMinBranch());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeNodeNumber());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeWidth());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeDevClass());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMaxClass());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMinClass());
        mf.add(new com.ifmo.recommendersystem.metafeatures.decisiontree.unpruned.UnprunedTreeMeanClass());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MaxHalfBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MaxSqrtBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MaxOneTenthBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MinHalfBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MinSqrtBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MinOneTenthBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MeanHalfBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MeanSqrtBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.MeanOneTenthBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.StdDevHalfBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.StdDevSqrtBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.StdDevOneTenthBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.knn.FullBestK());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MaxHalfPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MaxSqrtPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MaxOneTenthPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MinHalfPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MinSqrtPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MinOneTenthPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MeanHalfPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MeanSqrtPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.MeanOneTenthPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.StdDevHalfPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.StdDevSqrtPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.StdDevOneTenthPerceptronWeightSum());
        mf.add(new com.ifmo.recommendersystem.metafeatures.classifierbased.neural.FullPerceptronWeightSum());

        mf.add(new SMOF());
        mf.add(new NaiveBayesF());

    }
}
