package features_inversion.classification.fus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;

public class RandomPuncturer extends AttributePuncturer {

	public RandomPuncturer(Random random) {
		super(random);
	}

	public boolean[] array(int n) {
		return array(n, random.nextInt(n) + 1);
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

	@Override
	public String toString() {
		return "RandomPuncturer";
	}

	@Override
	public Instances select(Instances oldInstances, int newAttributes) {
		int oldAttributes = oldInstances.numAttributes() - 1;
		int classIndex = oldInstances.classIndex();

		if (classIndex == -1) {
			return null;
		}

		boolean[] useAttribute = array(oldAttributes, newAttributes);

		ArrayList<Attribute> attributes = new ArrayList<Attribute>(newAttributes + 1);
		for (int i = 0; i < newAttributes; i++) {
			attributes.add(new Attribute("atr" + i));
		}
		List<String> classValues = new ArrayList<>();
		classValues.add("Negative");
		classValues.add("Positive");

		attributes.add(new Attribute("class", classValues));

		Instances newInstances = new Instances("dataset", attributes, oldInstances.numInstances());
		newInstances.setClassIndex(newAttributes);

		for (Instance oldInst : oldInstances) {
			Instance newInst = new DenseInstance(newAttributes + 1);
			newInst.setDataset(newInstances);

			for (int i = 0, j = 0, a = 0; i <= oldAttributes; i++) {
				if (i != classIndex && useAttribute[j++]) {
					newInst.setValue(a++, oldInst.value(i));
				}
			}

			newInst.setClassValue(oldInst.classValue());

			newInstances.add(newInst);

		}

		return newInstances;
	}

}
