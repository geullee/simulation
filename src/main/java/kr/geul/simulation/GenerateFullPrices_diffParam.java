package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InvalidArgumentException;
import kr.geul.options.option.CallOption;
import kr.geul.options.option.Option;
import kr.geul.options.option.PutOption;

public class GenerateFullPrices_diffParam extends Simulation {

	public static void run(String[] args) throws InvalidArgumentException, InconsistentArgumentLengthException, 
		AtTheMoneyException, FileNotFoundException {
		
		fullSampleLeftEnd = Double.parseDouble(args[0]);
		fullSampleRightEnd = Double.parseDouble(args[1]);
		bsSigma = Double.parseDouble(args[2]);
		svjV0 = Math.pow(Double.parseDouble(args[2]), 2.0);
		strikePrices = getStrikePrices();
		numberOfFullObservations = strikePrices.length;
		bsOptions  = new Option[numberOfFullObservations];
		svjOptions = new Option[numberOfFullObservations];
		
		generateFullPrices();
		printResults(args);
		
	}
	
	private static void generateFullPrices() throws InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException {

		for (int index = 0; index < strikePrices.length; index++) {
			
			double strikePrice = strikePrices[index];
			System.out.println(strikePrice);
			Option bsOption, svjOption;
			
			double[] bsValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, bsSigma};

			double[] svjValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, svjKappaV, svjThetaV, 
					svjV0, svjSigmaV, svjMuJ, svjSigmaJ, svjRho, svjLambda}; 

			if (strikePrices[0] >= discountedUnderlyingPrice) {
				
				bsOption = new CallOption("BS");
				svjOption = new CallOption("SVJ");
				
			}
			
			else if (strikePrice < discountedUnderlyingPrice ||  
					(strikePrice == discountedUnderlyingPrice && 
					(atmSetting == ATMSetting.putATMOnly || 
					(atmSetting == ATMSetting.callAndPutATM && (strikePrices[index - 1] < underlyingPrice))
					))) {

				bsOption = new PutOption("BS");
				svjOption = new PutOption("SVJ");				

			}	
			
			else {
				
				bsOption = new CallOption("BS");
				svjOption = new CallOption("SVJ");
				
			}
			
			bsOption.set(bsParameterNames, bsValues);
			svjOption.set(svjParameterNames, svjValues);

			bsOption.evaluate();
			svjOption.evaluate();

			bsOptions[index] = bsOption;
			svjOptions[index] = svjOption;	
			
		}
		
	}
	
	private static void printResults(String[] args) throws FileNotFoundException, InvalidArgumentException {
		
		String fileName = "./_fullPrices_" + args[0] + "_" + Math.round(bsSigma * 100.0) + ".csv";				
		File file = new File(fileName);
		
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
		
		/* K, bsPrice, svjPrice, bsIV, svjIV */ 

		for (int i = 0; i < numberOfFullObservations; i++) {
			
			printWriter = new PrintWriter(new FileOutputStream(file, true));
			printWriter.println(strikePrices[i] + ","
					+ bsOptions[i].getOptionPrice() + ","
					+ svjOptions[i].getOptionPrice() + ","
					+ bsOptions[i].getBSImpVol() + ","
					+ svjOptions[i].getBSImpVol());

			System.out.println(strikePrices[i] + ","
					+ bsOptions[i].getOptionPrice() + ","
					+ svjOptions[i].getOptionPrice() + ","
					+ bsOptions[i].getBSImpVol() + ","
					+ svjOptions[i].getBSImpVol());
			
			printWriter.close();
			
		}
		
	}
	
}
