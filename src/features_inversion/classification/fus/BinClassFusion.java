package features_inversion.classification.fus;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.CorrelationAttributeEval;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class BinClassFusion {

	public static void printer(Object object) {
		System.out.println(object);
	}

	public static void main(String[] args) throws Exception {

		Random random = new Random();

		List list = null;

		printer(list);

		BinClassFusion fusion = new BinClassFusion(random);

		Instances x = new Instances(new FileReader("data/carff/462.arff"));
		x.setClassIndex(x.numAttributes() - 1);

		Instances y = new Instances(new FileReader("data/carff/848.arff"));
		y.setClassIndex(y.numAttributes() - 1);

		Instances z = fusion.fuse(x, y);

		RandomPuncturer puncturer = new RandomPuncturer(random);
		// System.out.println(z);

		ASEvaluation evaluation = new CorrelationAttributeEval();
		AttributePuncturer best = new BestAttrSelPuncturer(evaluation, random);
		AttributePuncturer worst = new WorstAttrSelPuncturer(evaluation, random);

		System.out.println(best.select(z));
		System.out.println(worst.select(z));
	}

	final Random random;

	public BinClassFusion(Random random) {
		this.random = random;
	}

	public boolean[] array(int n) {
		return array(n, random.nextInt(n) + 1);
		// return array(n, n);
	}

	public boolean[] array(int n, int m) {
		boolean[] array = new boolean[n];

		for (int i = 0; i < m; i++) {
			array[i] = true;
		}

		for (int i = m; i < n; i++) {
			int j = random.nextInt(i + 1);

			boolean temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}

		return array;
	}

	public double value(double min, double max) {
		return min + random.nextDouble() * (max - min);
	}

	public int value(int min, int max) {
		return min + random.nextInt(max - min + 1);
	}

	public Instances fuse(Instances x, Instances y) {
		Instances[] values = { x, y };

		int[] numAttributes = new int[3];
		int[] numClasses = new int[2];
		int[] numInstances = new int[2];
		int[] classIndex = new int[2];
		Attribute[] classAtribute = new Attribute[3];

		for (int i = 0; i < 2; i++) {
			Instances instances = values[i];

			classIndex[i] = instances.classIndex();
			if (classIndex[i] == -1) {
				return null;
			}

			numClasses[i] = instances.numClasses();
			if (numClasses[i] != 2) {
				return null;
			}

			numAttributes[i] = instances.numAttributes() - 1;
			numInstances[i] = instances.numInstances();
			classAtribute[i] = instances.classAttribute();
		}

		numAttributes[2] = numAttributes[0] + numAttributes[1];

		ArrayList<Attribute> attributes = new ArrayList<Attribute>(numAttributes[2] + 1);
		for (int i = 0; i < numAttributes[2]; i++) {
			attributes.add(new Attribute("atr" + i));
		}

		List<String> classValues = new ArrayList<>();
		classValues.add("Negative");
		classValues.add("Positive");
		classAtribute[2] = new Attribute("class", classValues);
		attributes.add(classAtribute[2]);

		Instances z = new Instances("dataset", attributes, numInstances[0] + numInstances[1]);
		z.setClassIndex(numAttributes[2]);

		for (int i = 0; i < 2; i++) {
			for (int e = 0; e < numInstances[i]; e++) {

				if (random.nextBoolean()) {
					continue;
				}

				Instance instance = values[i].get(e);

				Instance cur = new DenseInstance(numAttributes[2] + 1);
				cur.setDataset(z);

				// TODO random attribute selection
				for (int a = 0, l = 0; l < 2; l++) {
					if (i == l) {
						for (int j = 0; j <= numAttributes[l]; j++) {
							if (j != classIndex[l]) {
								cur.setValue(a++, instance.value(j));
							}
						}
					} else {
						for (int j = 0; j <= numAttributes[l]; j++) {
							if (j != classIndex[l]) {

								double mean = values[l].attributeStats(j).numericStats.mean;
								double std = values[l].attributeStats(j).numericStats.stdDev;

								cur.setValue(a++, random.nextGaussian() * std + mean);
							}
						}
					}
				}

				cur.setClassValue(classValues.get((int) (instance.classValue())));

				z.add(cur);
			}
		}

		return z;
	}

}
