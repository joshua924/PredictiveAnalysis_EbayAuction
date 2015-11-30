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
	private Logistic log;
	private LinearRegression linear;
	private Instances instances, copyInstances;
	
	public QueryHandler() throws Exception {
		System.out.println("Reading in instances ..");
		instances = IOHandler.readData("data/SelectedFeatureData.csv");
		copyInstances = new Instances(instances);
		
		System.out.println("Building Logistic classifier ..");
		instances.setClassIndex(1);
		instances = Filtering.discretize(instances);
		instances.setClassIndex(0);
		instances.deleteAttributeAt(1);
		log = new Logistic();
		log.buildClassifier(instances);
		
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
			double d = log.classifyInstance(instance);
			double e = linear.classifyInstance(instance);
			System.out.println(d + " and " + e);
			String response = (d == 1.00 ? "Sale" : "No Sale") + "\t" + e;
			OutputStream responseBody = exchange.getResponseBody();
			responseBody.write(response.getBytes());
			responseBody.flush();
			responseBody.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Instance constructInstance(HttpExchange exchange, Instances instances) throws Exception {
		Map<String, String> args = parseQuery(exchange.getRequestURI().getQuery());
		Instance inst = new Instance(8);
		inst.setDataset(instances);
		inst.setValue(1, Double.valueOf(args.get("startingbidpercent")));
		inst.setValue(2, Double.valueOf(args.get("sellerclosepercent")));
		inst.setValue(3, Double.valueOf(args.get("averageprice")));
		inst.setValue(4, Integer.valueOf(args.get("auctioncount")));
		inst.setValue(5, Integer.valueOf(args.get("auctionsalecount")));
		inst.setValue(6, Integer.valueOf(args.get("sellerauctioncount")));
		inst.setValue(7, Double.valueOf(args.get("medianprice")));
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
