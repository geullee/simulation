package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.DuplicateOptionsException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InconsistentOptionException;
import kr.geul.options.exception.InvalidArgumentException;
import kr.geul.options.option.OTMOption;
import kr.geul.options.option.Option;

public class Figure1 {

	/* Parameter name arrays */
	final static String[] bsParameterNames = {"S", "K", "R", "T", "D", "C", "sigma"},
		svjParameterNames = {"S", "K", "R", "T", "D", "C", 
		"kappaV", "thetaV", "v0", "sigmaV", "muJ", "sigmaJ", "rho", "lambda"};

	/* Parameter values */
	final static double underlyingPrice = 1168.2, riskFreeRate = 0.0285, dividendRate = 0.0163,
			timeToMaturity = 0.25, defaultOptionPrice = 0.0, bsSigma = 0.2183, 
			svKappa = 4.4691, svTheta = 0.2693, svV0 = 0.0689, 
			svSigma = 0.9963, svRho = -0.6854, 
			svjKappaV = 5.4499, svjThetaV = 0.2611, svjV0 = 0.0684,
			svjSigmaV = 0.9479, svjMuJ = -0.1162, svjSigmaJ = 0.1520, svjRho = -0.6778, 
			svjLambda = 0.1539;
	
	/* Simulation settings */
	static double gapBetweenStrikePrices = 0.1,
			fullSampleLeftEnd = 700.0, fullSampleRightEnd = 1500.0;
	final static int numberOfFullObservations = 
			(int) Math.round((fullSampleRightEnd - fullSampleLeftEnd) / gapBetweenStrikePrices) + 1;
	
	/* Strike price and option arrays */
	static double[] strikePrices = new double[numberOfFullObservations];
	static Option[] bsOptions = new Option[numberOfFullObservations],
			svOptions  = new Option[numberOfFullObservations],
			svjOptions = new Option[numberOfFullObservations];
	
	public static void run(String[] args) throws InvalidArgumentException, 
	InconsistentArgumentLengthException, DuplicateOptionsException, 
	InconsistentOptionException, AtTheMoneyException, FileNotFoundException {
		
		String fileName = "d:/f1_" + args[0] + ".csv";
		File file = new File(fileName);
		
		fullSampleLeftEnd = Double.parseDouble(args[0]);
		fullSampleRightEnd = Double.parseDouble(args[1]);
		
		generateFullPrices();
		printResults(file);
		
	}
	
	private static void printResults(File file) throws FileNotFoundException, InvalidArgumentException {
		
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
		
		printWriter.println("K,bs,sv,svj,bsiv,sviv,svjiv"); 

		for (int i = 0; i < numberOfFullObservations; i++) {
			
			printWriter = new PrintWriter(new FileOutputStream(file, true));
			printWriter.println(strikePrices[i] + ","
					+ bsOptions[i].getOptionPrice() + ","
					+ svOptions[i].getOptionPrice() + ","
					+ svjOptions[i].getOptionPrice() + ","
					+ bsOptions[i].getBSImpVol() + ","
					+ svOptions[i].getBSImpVol() + ","
					+ svjOptions[i].getBSImpVol());

			System.out.println(strikePrices[i] + ","
					+ bsOptions[i].getOptionPrice() + ","
					+ svOptions[i].getOptionPrice() + ","
					+ svjOptions[i].getOptionPrice() + ","
					+ bsOptions[i].getBSImpVol() + ","
					+ svOptions[i].getBSImpVol() + ","
					+ svjOptions[i].getBSImpVol());
			
			printWriter.close();
			
		}
		
	}

	private static void generateFullPrices() throws InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException {

		int arrayIndex = 0;
		for (double strikePrice = fullSampleLeftEnd; strikePrice <= fullSampleRightEnd; 
				strikePrice += gapBetweenStrikePrices) {

			System.out.println(strikePrice);

			if (strikePrice != underlyingPrice) {
					
				double[] bsValues = {underlyingPrice, strikePrice, riskFreeRate, 
						timeToMaturity, dividendRate, defaultOptionPrice, bsSigma};

				double[] svValues = {underlyingPrice, strikePrice, riskFreeRate, 
						timeToMaturity, dividendRate, defaultOptionPrice, svKappa, svTheta, 
						svV0, svSigma, 0.0, 0.0, svRho, 0.0};

				double[] svjValues = {underlyingPrice, strikePrice, riskFreeRate, 
						timeToMaturity, dividendRate, defaultOptionPrice, svjKappaV, svjThetaV, 
						svjV0, svjSigmaV, svjMuJ, svjSigmaJ, svjRho, svjLambda}; 

				OTMOption bsOption = new OTMOption("BS");
				OTMOption svOption = new OTMOption("SVJ");
				OTMOption svjOption = new OTMOption("SVJ");				

				bsOption.set(bsParameterNames, bsValues);
				svOption.set(svjParameterNames, svValues);
				svjOption.set(svjParameterNames, svjValues);

				bsOption.evaluate();
				svOption.evaluate();
				svjOption.evaluate();

				strikePrices[arrayIndex] = bsOption.getVariableArray()[1];
				bsOptions[arrayIndex] = bsOption;
				svOptions[arrayIndex] = svOption;
				svjOptions[arrayIndex] = svjOption;

				arrayIndex++;
				
			}

		}

	}
	
}
