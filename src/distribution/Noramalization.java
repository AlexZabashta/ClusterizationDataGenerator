package distribution;


public abstract class Noramalization {

	public void normalize(double[] distribution) {
		normalize(distribution, 0);
	}

	public abstract void normalize(double[] distribution, double maxTominScale);

}
