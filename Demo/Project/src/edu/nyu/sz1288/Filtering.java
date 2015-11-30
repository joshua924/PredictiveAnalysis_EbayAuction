package edu.nyu.sz1288;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;

public class Filtering {
	public static void applyFilter(Instances data, String[] options)
			throws Exception {
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(data);
		data = Filter.useFilter(data, remove);
	}

	public static Instances discretize(Instances data) throws Exception {
		Discretize filter = new Discretize("1");
		filter.setInputFormat(data);
		filter.setBins(2);
		for (int i = 0; i < data.numInstances(); i++) {
			filter.input(data.instance(i));
		}
		filter.batchFinished();
		Instances newData = filter.getOutputFormat();
		Instance processed;
		while ((processed = filter.output()) != null) {
			newData.add(processed);
		}
		return newData;
	}
}
