package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import kr.geul.bkm.BKMEstimator;
import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.DuplicateOptionsException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InconsistentOptionException;
import kr.geul.options.exception.InvalidArgumentException;
import kr.geul.options.option.CallOption;
import kr.geul.options.option.Option;
import kr.geul.options.option.PutOption;
import kr.geul.options.structure.OptionCurve;

public class Simulation {

	/* location of simulation results for different symmetry and asymmetry settings */ 
	final static int nominalSymmetry = 0;
	final static int logSymmetry = 1;
	final static int leftNominalAsymmetry = 2;
	final static int leftExtremeNominalAsymmetry = 3;
	final static int rightNominalAsymmetry = 4;
	final static int rightExtremeNominalAsymmetry = 5;
	
	/* ATM option setting */
	final static ATMSetting atmSetting = ATMSetting.callAndPutATM;

	/* Parameter name arrays */
	final static String[] bsParameterNames = {"S", "K", "R", "T", "D", "C", "sigma"},
			svjParameterNames = {"S", "K", "R", "T", "D", "C", 
			"kappaV", "thetaV", "v0", "sigmaV", "muJ", "sigmaJ", "rho", "lambda"};
	
	/* Parameter values */
	final static double underlyingPrice = 1183.2, riskFreeRate = 0.0301, dividendRate = 0.0167,
			timeToMaturity = 0.25, defaultOptionPrice = 0.0;
	static double bsSigma = 0.2033;
	static final double svjKappaV = 4.4592;
	static final double svjThetaV = 0.2108;
	static double svjV0 = 0.0494;
	static final double svjSigmaV = 0.8116;
	static final double svjMuJ = -0.1000;
	static final double svjSigmaJ = 0.1546;
	static final double svjRho = -0.6812;
	static final double svjLambda = 0.1583;
	static final double discountedUnderlyingPrice = Math.round((underlyingPrice * 
			Math.exp(-dividendRate * timeToMaturity)) * 10.0) / 10.0;

	/* Simulation settings */
	final static double minimumLeftEnd = 960.0, maximumLeftEnd = 1160.0, 
			minimumRightEnd = 1175.0, maximumRightEnd = 1375.0,
			gapBetweenXAxisPoints = 5, gapBetweenStrikePrices = 0.1;

	static double fullSampleLeftEnd = 392.8, fullSampleRightEnd = 3534.9;
	final static int numberOfLines = 5;

	final static double	gapBetweenLines = (maximumLeftEnd - minimumLeftEnd) / (double) (numberOfLines - 1);
	final static int numberOfXAxisPoints = (int) ((maximumRightEnd - minimumRightEnd) / gapBetweenXAxisPoints + 1.0); 

	/* Strike price and option arrays */
	static double[] strikePrices = getStrikePrices();
	static int numberOfFullObservations = strikePrices.length;
	static Option[] bsOptions  = new Option[numberOfFullObservations],
			svjOptions = new Option[numberOfFullObservations];
	
	/* Option curves and estimation result arrays */
	static OptionCurve bsOptionCurve, svjOptionCurve;
	static double bsTrueVol, bsTrueSkew, bsTrueKurt, svjTrueVol, svjTrueSkew, svjTrueKurt;
	static double[][] bsVols = new double[numberOfLines][numberOfXAxisPoints], 
			bsSkews  = new double[numberOfLines][numberOfXAxisPoints], 
			bsKurts  = new double[numberOfLines][numberOfXAxisPoints], 
			svjVols  = new double[numberOfLines][numberOfXAxisPoints], 
			svjSkews = new double[numberOfLines][numberOfXAxisPoints], 
			svjKurts = new double[numberOfLines][numberOfXAxisPoints];

	/* BKM Estimator */
	static BKMEstimator bkmEstimator = new BKMEstimator();

	protected static double[] getBKMEstimates() 
			throws DuplicateOptionsException, InconsistentOptionException {

		double[] bsBKMEstimates, svjBKMEstimates, bkmEstimates = new double[6];

		bkmEstimator.setOptions(bsOptionCurve);
		bsBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svjOptionCurve);
		svjBKMEstimates = bkmEstimator.getEstimates();

		bkmEstimates[0] = bsBKMEstimates[0];
		bkmEstimates[1] = bsBKMEstimates[1];
		bkmEstimates[2] = bsBKMEstimates[2];
		bkmEstimates[3] = svjBKMEstimates[0];
		bkmEstimates[4] = svjBKMEstimates[1];
		bkmEstimates[5] = svjBKMEstimates[2];
		
		return bkmEstimates;

	}
	
	protected static double[] getBKMEstimates_extrapolated() 
			throws InvalidArgumentException, InconsistentArgumentLengthException, 
			DuplicateOptionsException, InconsistentOptionException, AtTheMoneyException {

		double[] svjBKMEstimates, bkmEstimates = new double[3];

		svjOptionCurve.setExtrapolationRange(fullSampleLeftEnd, fullSampleRightEnd);
		svjOptionCurve.setStrikePriceGap(gapBetweenStrikePrices);
		svjOptionCurve.extrapolate(1.0 / gapBetweenStrikePrices);

		bkmEstimator.setOptions(svjOptionCurve);
		svjBKMEstimates = bkmEstimator.getEstimates();
		
		bkmEstimates[0] = svjBKMEstimates[0];
		bkmEstimates[1] = svjBKMEstimates[1];
		bkmEstimates[2] = svjBKMEstimates[2];

		return bkmEstimates;
		
	}
	
	/* Get the number of strike prices */
	protected static double[] getStrikePrices() {

		ArrayList<Double> prices = new ArrayList<Double>();

		if (fullSampleLeftEnd <= discountedUnderlyingPrice && 
			fullSampleRightEnd >= discountedUnderlyingPrice) {
		
			switch (atmSetting) {

			case noATM:
				break;

			case callATMOnly:
				prices.add(discountedUnderlyingPrice);
				break;

			case putATMOnly:
				prices.add(discountedUnderlyingPrice);
				break;

			default:
				prices.add(discountedUnderlyingPrice);
				prices.add(discountedUnderlyingPrice);

			}
			
		}
		
		for (double strikePrice = Math.min(discountedUnderlyingPrice - gapBetweenStrikePrices, fullSampleRightEnd); 
				strikePrice > fullSampleLeftEnd - gapBetweenStrikePrices; 
				strikePrice -= gapBetweenStrikePrices) {	

			if (strikePrice >= fullSampleLeftEnd) {
				prices.add(0, strikePrice);
			}

			else
				prices.add(0, fullSampleLeftEnd);

		}
		
		for (double strikePrice = Math.max(discountedUnderlyingPrice + gapBetweenStrikePrices, fullSampleLeftEnd); 
				strikePrice < fullSampleRightEnd + gapBetweenStrikePrices; 
				strikePrice += gapBetweenStrikePrices) {
			
			if (strikePrice <= fullSampleRightEnd) {
				prices.add(strikePrice);
			}

			else
				prices.add(fullSampleRightEnd);

		}

		double[] strikePrices = new double[prices.size()];

		for (int i = 0; i < strikePrices.length; i++) {
			strikePrices[i] = prices.get(i);
		}

		return strikePrices;

	}

	protected static double[][] getEndPoints(double halfWidth) {
		
		double[][] endPoints = new double[6][2];
		
		double multiplier = 1.0 / gapBetweenStrikePrices;
	    double fullWidth = halfWidth * 2.0;
	    double logSymmetryLeftWidth 
	    	= (-fullWidth + Math.sqrt(Math.pow(fullWidth, 2) + 4 * Math.pow(discountedUnderlyingPrice, 2))) 
	    		/ 2.0;
	    double logSymmetryLeftEnd = 
	    		Math.round(logSymmetryLeftWidth * multiplier) / multiplier;
	    double logSymmetryRightEnd = logSymmetryLeftEnd + fullWidth;
	    
		endPoints[nominalSymmetry][0] = discountedUnderlyingPrice - halfWidth;
		endPoints[nominalSymmetry][1] = discountedUnderlyingPrice + halfWidth;
		endPoints[logSymmetry][0] = logSymmetryLeftEnd;
		endPoints[logSymmetry][1] = logSymmetryRightEnd;
		endPoints[leftNominalAsymmetry][0] 
				= Math.round((discountedUnderlyingPrice - (halfWidth * 1.2)) * multiplier) / multiplier;
		endPoints[leftNominalAsymmetry][1] 
				= Math.round((discountedUnderlyingPrice + (halfWidth * 0.8)) * multiplier) / multiplier;
		endPoints[leftExtremeNominalAsymmetry][0] 
				= Math.round((discountedUnderlyingPrice - (halfWidth * 1.8)) * multiplier) / multiplier;
		endPoints[leftExtremeNominalAsymmetry][1] 
				= Math.round((discountedUnderlyingPrice + (halfWidth * 0.2)) * multiplier) / multiplier;
		endPoints[rightNominalAsymmetry][0] 
				= Math.round((discountedUnderlyingPrice - (halfWidth * 0.8)) * multiplier) / multiplier;
		endPoints[rightNominalAsymmetry][1] 
				= Math.round((discountedUnderlyingPrice + (halfWidth * 1.2)) * multiplier) / multiplier; 
		endPoints[rightExtremeNominalAsymmetry][0] 
				= Math.round((discountedUnderlyingPrice - (halfWidth * 0.2)) * multiplier) / multiplier;
		endPoints[rightExtremeNominalAsymmetry][1] 
				= Math.round((discountedUnderlyingPrice + (halfWidth * 1.8)) * multiplier) / multiplier;
		return endPoints;
		
	}
	
	protected static double[][] getPricesAndImpVols() throws IOException {
		
		double[][] result = new double[strikePrices.length][7];
		
		String fileName = "./fullPrices.csv";
		File file = new File(fileName);
		String line;

		BufferedReader fileReader = 
				new BufferedReader(new FileReader(file));
		
		int location = 0;
		
		while ((line = fileReader.readLine()) != null) {
			
			String[] data = line.split(",", -1);
			
			for (int i = 0; i < data.length; i++) {
				if (data[i].length() > 0)
					result[location][i] = Double.parseDouble(data[i]);
			}
			
			location++;
			
		}
		
		fileReader.close();
		
		return result;
				
	}

	protected static double[][] getPricesAndImpVols_diffParam(double sigma) throws IOException {
		
		double[][] result = new double[strikePrices.length][7];
		
		String fileName = "./fullPrices_" + Math.round(sigma * 100.0) + ".csv";
		File file = new File(fileName);
		String line;

		BufferedReader fileReader = 
				new BufferedReader(new FileReader(file));
		
		int location = 0;
		
		while ((line = fileReader.readLine()) != null) {
			
			String[] data = line.split(",", -1);
			
			for (int i = 0; i < data.length; i++) {
				if (data[i].length() > 0)
					result[location][i] = Double.parseDouble(data[i]);
			}
			
			location++;
			
		}
		
		fileReader.close();
		
		return result;
				
	}
	
	protected static void readFullPrices() throws InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException, IOException {

		double[][] pricesAndImpVols = getPricesAndImpVols();
		
		for (int index = 0; index < strikePrices.length; index++) {
			
			double strikePrice = strikePrices[index];
			System.out.println(strikePrice);
			Option bsOption, svjOption;
			
			double[] bsValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, pricesAndImpVols[index][1], pricesAndImpVols[index][3]};
			
			double[] svjValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, pricesAndImpVols[index][2], pricesAndImpVols[index][4]};
			
			if (strikePrices[0] >= discountedUnderlyingPrice) {
				
				bsOption = new CallOption("BS");
				svjOption = new CallOption("SVJ");
				
			}
			
			else if (strikePrice < discountedUnderlyingPrice ||  
					(strikePrice == discountedUnderlyingPrice && 
					(atmSetting == ATMSetting.putATMOnly || 
					(atmSetting == ATMSetting.callAndPutATM && (strikePrices[index - 1] < discountedUnderlyingPrice))
					))) {

				bsOption = new PutOption("BS");
				svjOption = new PutOption("BS");				

			}	
			
			else {
				
				bsOption = new CallOption("BS");
				svjOption = new CallOption("BS");
				
			}
			
			bsOption.set(bsParameterNames, bsValues);
			svjOption.set(bsParameterNames, svjValues);

			bsOptions[index] = bsOption;
			svjOptions[index] = svjOption;	
			
		}
		
	}
	
	protected static void readFullPrices_diffParam(double sigma) throws InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException, IOException {

		double[][] pricesAndImpVols = getPricesAndImpVols_diffParam(sigma);
		
		for (int index = 0; index < strikePrices.length; index++) {
			
			double strikePrice = strikePrices[index];
			System.out.println(strikePrice);
			Option bsOption, svjOption;
			
			double[] bsValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, pricesAndImpVols[index][1], pricesAndImpVols[index][3]};
			
			double[] svjValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, pricesAndImpVols[index][2], pricesAndImpVols[index][4]};
			
			if (strikePrices[0] >= discountedUnderlyingPrice) {
				
				bsOption = new CallOption("BS");
				svjOption = new CallOption("SVJ");
				
			}
			
			else if (strikePrice < discountedUnderlyingPrice ||  
					(strikePrice == discountedUnderlyingPrice && 
					(atmSetting == ATMSetting.putATMOnly || 
					(atmSetting == ATMSetting.callAndPutATM && (strikePrices[index - 1] < discountedUnderlyingPrice))
					))) {

				bsOption = new PutOption("BS");
				svjOption = new PutOption("BS");				

			}	
			
			else {
				
				bsOption = new CallOption("BS");
				svjOption = new CallOption("BS");
				
			}
			
			bsOption.set(bsParameterNames, bsValues);
			svjOption.set(bsParameterNames, svjValues);

			bsOptions[index] = bsOption;
			svjOptions[index] = svjOption;	
			
		}
		
	}
	
	protected static void setOptionCurves(double left, double right) throws DuplicateOptionsException, InconsistentOptionException {

		int leftEndStrikeIndex = 0, rightEndStrikeIndex = 0;

		for (int i = 0; i < strikePrices.length; i++) {

			double strike = strikePrices[i];
			if (Math.round(strike * 100.0) == Math.round(left * 100.0))  
				leftEndStrikeIndex = i;

			else if (Math.round(strike * 100.0) == Math.round(right * 100.0))
				rightEndStrikeIndex = i;

		}
		
		bsOptionCurve = new OptionCurve();
		svjOptionCurve = new OptionCurve();

		for (int i = leftEndStrikeIndex; i <= rightEndStrikeIndex; i++) {
			
			bsOptionCurve.add(bsOptions[i]);
			svjOptionCurve.add(svjOptions[i]);

		}

	}
	
}
