package features_inversion.classification.fus;

import java.util.Random;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.BadRanker;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class WorstAttrSelPuncturer extends AttributePuncturer {

	final ASEvaluation evaluation;

	public WorstAttrSelPuncturer(ASEvaluation evaluation, Random random) {
		super(random);
		this.evaluation = evaluation;
	}

	@Override
	public String toString() {
		return "WorstAttrSelPuncturer " + evaluation.getClass().getSimpleName();
	}

	@Override
	public Instances select(Instances instances, int n) {

		BadRanker ranker = new BadRanker();
		ranker.setNumToSelect(n + 1);

		AttributeSelection filter = new AttributeSelection();
		filter.setSearch(ranker);
		filter.setEvaluator(evaluation);

		try {
			filter.setInputFormat(instances);
			return Filter.useFilter(instances, filter);
		} catch (Exception e) {
			return null;
		}
	}
}
