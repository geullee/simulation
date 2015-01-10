package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InvalidArgumentException;

public class GenerateFullWeights extends Simulation {

	public static void run(String[] args) throws InvalidArgumentException, InconsistentArgumentLengthException, 
		AtTheMoneyException, FileNotFoundException {
		
		fullSampleLeftEnd = Double.parseDouble(args[0]);
		fullSampleRightEnd = Double.parseDouble(args[1]);
		strikePrices = getStrikePrices();
		numberOfFullObservations = strikePrices.length;
		
		double[][] weights = generateFullWeights();
		printResults(weights);
		
	}
	
	private static double[][] generateFullWeights() throws InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException {

		double[][] weights = new double[numberOfFullObservations][3];
		
		for (int index = 0; index < strikePrices.length; index++) {
			
			double strikePrice = strikePrices[index];
			
			weights[index][0] = (2.0 * (1.0 - Math.log(strikePrice / discountedUnderlyingPrice))) / 
					(Math.pow(strikePrice, 2.0));
			weights[index][1] = ((6.0 * Math.log(strikePrice / discountedUnderlyingPrice)) - 
					(3.0 * Math.pow(Math.log(strikePrice / discountedUnderlyingPrice), 2.0))) / 
					(Math.pow(strikePrice, 2.0));
			weights[index][2] = ((12.0 * Math.pow(Math.log(strikePrice / discountedUnderlyingPrice), 2.0)) - 
					(4.0 * Math.pow(Math.log(strikePrice / discountedUnderlyingPrice), 3.0))) 
					/ (Math.pow(strikePrice, 2.0));
			
		}
		
		return weights;
		
	}
	
	private static void printResults(double[][] weights) throws FileNotFoundException {
		
		String fileName = "d:/FullWeights.csv";
		File file = new File(fileName);
		
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));

		for (int i = 0; i < numberOfFullObservations; i++) {
			
			printWriter = new PrintWriter(new FileOutputStream(file, true));
			printWriter.println(strikePrices[i] + "," + weights[i][0] + "," + weights[i][1] + 
					"," + weights[i][2]);

			System.out.println(strikePrices[i] + weights[i][0] + "," + weights[i][1] + 
					"," + weights[i][2]);
			
			printWriter.close();
			
		}
		
	}
	
}
