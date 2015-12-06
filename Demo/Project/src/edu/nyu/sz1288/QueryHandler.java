package edu.nyu.sz1288;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.core.Instance;
import weka.core.Instances;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class QueryHandler implements HttpHandler {
	private Logistic logistic;
	private LinearRegression linear;
	private Instances instances, copyInstances;
	private static final double MIN = 0.1, MAX = 10.0;

	public Instances getInstances() {
		return copyInstances;
	}
	
	public LinearRegression getLinearModel() {
		return linear;
	}
	
	public QueryHandler() throws Exception {
		System.out.println("Reading in instances ..");
		instances = IOHandler.readData("data/SelectedFeatureData.csv");
		Filtering.logAttributes(instances);
		copyInstances = new Instances(instances);
		
		System.out.println("Building Logistic classifier ..");
		instances.setClassIndex(1);
		instances = Filtering.discretize(instances);
		instances.setClassIndex(0);
		instances.deleteAttributeAt(1);
		logistic = new Logistic();
		logistic.buildClassifier(instances);
		
		System.out.println("Building Linear Regression classifier ..");
		copyInstances.setClassIndex(1);
		copyInstances.deleteAttributeAt(0);
		linear = new LinearRegression();
		linear.buildClassifier(copyInstances);
		
		System.out.println("Classifiers built.");
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		Headers responseHeaders = exchange.getResponseHeaders();
		responseHeaders.set("Content-Type", "text/plain");
	    responseHeaders.set("Access-Control-Allow-Origin", "*");
	    exchange.sendResponseHeaders(200, 0);
		try {
			Instance instance = constructInstance(exchange, instances);
			double[] p = getBestStartingPrice(instance);
			String response = p[0] + "\t" + p[1] + "\t" + p[2] + "\t" + p[3];
			System.out.println(response);
			OutputStream responseBody = exchange.getResponseBody();
			responseBody.write(response.getBytes());
			responseBody.flush();
			responseBody.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private double[] getBestStartingPrice(Instance instance) throws Exception {
		double bestStartingPrice = -1, bestExpectedFinalPrice = Double.MIN_VALUE,
				bestSaleProb = -1, bestPricePercent = -1;
		for(double i = MIN; i <= MAX; i += 0.01) {
			instance.setValue(1, i);
			double tmpSaleProb = logistic.distributionForInstance(instance)[1];
			double tmpPricePercent = linear.classifyInstance(instance);
			double expe = tmpSaleProb * tmpPricePercent;
			if(expe > bestExpectedFinalPrice) {
				bestStartingPrice = i;
				bestExpectedFinalPrice = expe;
				bestSaleProb = tmpSaleProb;
				bestPricePercent = tmpPricePercent;
			}
		}
		return new double[] {bestStartingPrice, bestExpectedFinalPrice, bestSaleProb, bestPricePercent};
	}

	private Instance constructInstance(HttpExchange exchange, Instances instances) throws Exception {
		Map<String, String> args = parseQuery(exchange.getRequestURI().getQuery());
		Instance inst = new Instance(8);
		inst.setDataset(instances);
		inst.setValue(2, Double.valueOf(args.get("sellerclosepercent")));
		inst.setValue(3, Math.log(Double.valueOf(args.get("averageprice"))));
		inst.setValue(4, Integer.valueOf(args.get("auctioncount")));
		inst.setValue(5, Integer.valueOf(args.get("auctionsalecount")));
		inst.setValue(6, Integer.valueOf(args.get("sellerauctioncount")));
		inst.setValue(7, Math.log(Double.valueOf(args.get("medianprice"))));
		return inst;
	}
	
	private Map<String, String> parseQuery(String query) {
		Map<String, String> args = new HashMap<>();
		for(String each : query.split("&")) {
			String[] qs = each.split("=");
			args.put(qs[0], qs[1]);
		}
		return args;
	}

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(9999), 0);
		server.createContext("/", new QueryHandler());
		server.setExecutor(null);
		server.start();
	}
}
