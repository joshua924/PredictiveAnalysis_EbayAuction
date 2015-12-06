package edu.nyu.sz1288;

import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

public class Test {
	public static void main(String args[]) throws Exception {
		QueryHandler qh = new QueryHandler();
		Instances instances = qh.getInstances();
		LinearRegression model = qh.getLinearModel();
		for(int i=0; i<100; i++) {
			System.out.print(instances.instance(i).value(0));
			System.out.println(" and " + model.classifyInstance(instances.instance(i)));
		}
	}
}
