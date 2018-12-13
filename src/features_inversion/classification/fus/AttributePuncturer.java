package features_inversion.classification.fus;

import java.util.Random;

import weka.core.Instances;

public abstract class AttributePuncturer {

	public final Random random;

	public AttributePuncturer(Random random) {
		this.random = random;
	}

	public Instances select(Instances instances) {
		int n = random.nextInt(instances.numAttributes() - 1) + 2;
		return select(instances, n);
	}

	public abstract Instances select(Instances instances, int n);
}
