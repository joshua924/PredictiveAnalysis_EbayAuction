package edu.nyu.sz1288;

import java.io.File;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class IOHandler {
	public static Instances readData(String path) throws Exception {
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File("data/SelectedFeatureData.csv"));
		Instances data = loader.getDataSet();
		return data;
	}
}
