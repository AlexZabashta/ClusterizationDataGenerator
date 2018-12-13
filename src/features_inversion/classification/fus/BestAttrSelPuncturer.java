package features_inversion.classification.fus;

import java.util.Random;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class BestAttrSelPuncturer extends AttributePuncturer {

	final ASEvaluation evaluation;

	public BestAttrSelPuncturer(ASEvaluation evaluation, Random random) {
		super(random);
		this.evaluation = evaluation;
	}

	@Override
	public String toString() {
		return "BestAttrSelPuncturer " + evaluation.getClass().getSimpleName();
	}

	@Override
	public Instances select(Instances instances, int n) {

		Ranker ranker = new Ranker();
		ranker.setNumToSelect(n + 1);

		AttributeSelection filter = new AttributeSelection();
		filter.setSearch(ranker);
		filter.setEvaluator(evaluation);

		try {
			filter.setInputFormat(instances);
			return Filter.useFilter(instances, filter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
