package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
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

public class Figure3 {

	/* ATM option setting */
	final static ATMSetting atmSetting = ATMSetting.callAndPutATM;

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
	final static double minimumLeftEnd = 960.0, maximumLeftEnd = 1160.0, 
			minimumRightEnd = 1175.0, maximumRightEnd = 1375.0,
			gapBetweenXAxisPoints = 5, gapBetweenStrikePrices = 0.1,
			fullSampleLeftEnd = 389.4, fullSampleRightEnd = 3504.6;
	final static int numberOfLines = 5;

	final static double	gapBetweenLines = (maximumLeftEnd - minimumLeftEnd) / (double) (numberOfLines - 1);
	final static int numberOfXAxisPoints = (int) ((maximumRightEnd - minimumRightEnd) / gapBetweenXAxisPoints) + 1; 

	/* Strike price and option arrays */
	final static double[] strikePrices = getStrikePrices();
	final static int numberOfFullObservations = strikePrices.length;
	static Option[] bsOptions = new Option[numberOfFullObservations],
			svOptions  = new Option[numberOfFullObservations],
			svjOptions = new Option[numberOfFullObservations];

	/* Option curves and estimation result arrays */
	static OptionCurve bsOptionCurve, svOptionCurve, svjOptionCurve;
	static double bsTrueVol, bsTrueSkew, bsTrueKurt, svTrueVol, svTrueSkew, svTrueKurt,
	svjTrueVol, svjTrueSkew, svjTrueKurt;
	static double[][] bsVols = new double[numberOfLines][numberOfXAxisPoints], 
			bsSkews  = new double[numberOfLines][numberOfXAxisPoints],
			bsKurts  = new double[numberOfLines][numberOfXAxisPoints], 
			svVols   = new double[numberOfLines][numberOfXAxisPoints], 
			svSkews  = new double[numberOfLines][numberOfXAxisPoints], 
			svKurts  = new double[numberOfLines][numberOfXAxisPoints], 
			svjVols  = new double[numberOfLines][numberOfXAxisPoints], 
			svjSkews = new double[numberOfLines][numberOfXAxisPoints], 
			svjKurts = new double[numberOfLines][numberOfXAxisPoints];

	/* BKM Estimator */
	static BKMEstimator bkmEstimator = new BKMEstimator();	

	public static void run() throws InvalidArgumentException, 
	InconsistentArgumentLengthException, DuplicateOptionsException, 
	InconsistentOptionException, AtTheMoneyException, FileNotFoundException {
		
		int leftIndex = 0;
		generateFullPrices();
		getTrueBKMEstimates();

		for (double left = maximumLeftEnd; left >= minimumLeftEnd; left -= gapBetweenLines) {

			int rightIndex = 0;

			for (double right = minimumRightEnd; right <= maximumRightEnd; right += gapBetweenXAxisPoints) {

				System.out.println(left + ":" + right);

				setOptionCurves(left, right);
				getBKMEstimates(leftIndex, rightIndex);
				rightIndex++;

			}

			leftIndex++;

		}

		printResults();
		
	}

	private static void printResults() throws FileNotFoundException {
		
		String fileName = "d:/f3.csv";
		File file = new File(fileName);
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
		
		printWriter.println("maxK,bsVol50,bsVol48,bsVol46,bsVol44,bsVol42,"
				+ "svVol50,svVol48,svVol46,svVol44,svVol42,"
				+ "svjVol50,svjVol48,svjVol46,svjVol44,svjVol42,"
				+ "bsSkew50,bsSkew48,bsSkew46,bsSkew44,bsSkew42,"
				+ "svSkew50,svSkew48,svSkew46,svSkew44,svSkew42,"
				+ "svjSkew50,svjSkew48,svjSkew46,svjSkew44,svjSkew42,"
				+ "bsKurt50,bsKurt48,bsKurt46,bsKurt44,bsKurt42,"
				+ "svKurt50,svKurt48,svKurt46,svKurt44,svKurt42,"
				+ "svjKurt50,svjKurt48,svjKurt46,svjKurt44,svjKurt42," 
				+ "bsTrueVol,bsTrueSkew,bsTrueKurt,svTrueVol,svTrueSkew,svTrueKurt,"
				+ "svjTrueVol,svjTrueSkew,svjTrueKurt"); 

		System.out.println("maxK,bsVol50,bsVol48,bsVol46,bsVol44,bsVol42,"
				+ "svVol50,svVol48,svVol46,svVol44,svVol42,"
				+ "svjVol50,svjVol48,svjVol46,svjVol44,svjVol42,"
				+ "bsSkew50,bsSkew48,bsSkew46,bsSkew44,bsSkew42,"
				+ "svSkew50,svSkew48,svSkew46,svSkew44,svSkew42,"
				+ "svjSkew50,svjSkew48,svjSkew46,svjSkew44,svjSkew42,"
				+ "bsKurt50,bsKurt48,bsKurt46,bsKurt44,bsKurt42,"
				+ "svKurt50,svKurt48,svKurt46,svKurt44,svKurt42,"
				+ "svjKurt50,svjKurt48,svjKurt46,svjKurt44,svjKurt42," 
				+ "bsTrueVol,bsTrueSkew,bsTrueKurt,svTrueVol,svTrueSkew,svTrueKurt,"
				+ "svjTrueVol,svjTrueSkew,svjTrueKurt"); 

		printWriter.close();
		
		for (int i = 0; i < numberOfXAxisPoints; i++) {

			printWriter = new PrintWriter(new FileOutputStream(file, true));
			
			printWriter.print((minimumRightEnd + i * gapBetweenXAxisPoints) + ",");
			System.out.print((minimumRightEnd + i * gapBetweenXAxisPoints) + ",");

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(bsVols[j][i] + ",");
				System.out.print(bsVols[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(svVols[j][i] + ",");
				System.out.print(svVols[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(svjVols[j][i] + ",");
				System.out.print(svjVols[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(bsSkews[j][i] + ","); 
				System.out.print(bsSkews[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(svSkews[j][i] + ",");
				System.out.print(svSkews[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(svjSkews[j][i] + ","); 
				System.out.print(svjSkews[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(bsKurts[j][i] + ",");
				System.out.print(bsKurts[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(svKurts[j][i] + ",");
				System.out.print(svKurts[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				printWriter.print(svjKurts[j][i] + ",");
				System.out.print(svjKurts[j][i] + ","); 
			}

			printWriter.println(bsTrueVol + "," + bsTrueSkew + "," + bsTrueKurt + "," +
					svTrueVol + "," + svTrueSkew + "," + svTrueKurt + "," +
					svjTrueVol + "," + svjTrueSkew + "," + svjTrueKurt);
			System.out.println(bsTrueVol + "," + bsTrueSkew + "," + bsTrueKurt + "," +
					svTrueVol + "," + svTrueSkew + "," + svTrueKurt + "," +
					svjTrueVol + "," + svjTrueSkew + "," + svjTrueKurt);

			printWriter.close();
			
		}
		
	}

	private static void generateFullPrices() throws InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException {

		for (int index = 0; index < strikePrices.length; index++) {
			
			double strikePrice = strikePrices[index];
			System.out.println(strikePrice);
			Option bsOption, svOption, svjOption;
			
			double[] bsValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, bsSigma};

			double[] svValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, svKappa, svTheta, 
					svV0, svSigma, 0.0, 0.0, svRho, 0.0}; 

			double[] svjValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, svjKappaV, svjThetaV, 
					svjV0, svjSigmaV, svjMuJ, svjSigmaJ, svjRho, svjLambda}; 

			if (strikePrice < underlyingPrice || 
					(strikePrice == underlyingPrice && 
					(atmSetting == ATMSetting.putATMOnly || 
					(atmSetting == ATMSetting.callAndPutATM && strikePrices[index - 1] < underlyingPrice)))) {

				bsOption = new PutOption("BS");
				svOption = new PutOption("SVJ");
				svjOption = new PutOption("SVJ");				

			}	
			
			else {
				
				bsOption = new CallOption("BS");
				svOption = new CallOption("SVJ");
				svjOption = new CallOption("SVJ");
				
			}
			
			bsOption.set(bsParameterNames, bsValues);
			svOption.set(svjParameterNames, svValues);
			svjOption.set(svjParameterNames, svjValues);

			bsOption.evaluate();
			svOption.evaluate();
			svjOption.evaluate();

			bsOptions[index] = bsOption;
			svOptions[index] = svOption;
			svjOptions[index] = svjOption;	
			
		}
		
	}

	private static void getBKMEstimates(int leftIndex, int rightIndex) 
			throws DuplicateOptionsException, InconsistentOptionException {

		double[] bsBKMEstimates, svBKMEstimates, svjBKMEstimates;

		bkmEstimator.setOptions(bsOptionCurve);
		bsBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svOptionCurve);
		svBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svjOptionCurve);
		svjBKMEstimates = bkmEstimator.getEstimates();

		bsVols[leftIndex][rightIndex] = bsBKMEstimates[0];
		bsSkews[leftIndex][rightIndex] = bsBKMEstimates[1];
		bsKurts[leftIndex][rightIndex] = bsBKMEstimates[2];
		svVols[leftIndex][rightIndex] = svBKMEstimates[0];
		svSkews[leftIndex][rightIndex] = svBKMEstimates[1];
		svKurts[leftIndex][rightIndex] = svBKMEstimates[2];
		svjVols[leftIndex][rightIndex] = svjBKMEstimates[0];
		svjSkews[leftIndex][rightIndex] = svjBKMEstimates[1];
		svjKurts[leftIndex][rightIndex] = svjBKMEstimates[2];

	}

	private static double[] getStrikePrices() {
		
		ArrayList<Double> prices = new ArrayList<Double>();
		
		switch (atmSetting) {

		case noATM:
			break;

		case callATMOnly:
			prices.add(underlyingPrice);
			break;

		case putATMOnly:
			prices.add(underlyingPrice);
			break;

		default:
			prices.add(underlyingPrice);
			prices.add(underlyingPrice);

		}
		
		for (double strikePrice = underlyingPrice - gapBetweenStrikePrices; 
				strikePrice > fullSampleLeftEnd - gapBetweenStrikePrices; 
				strikePrice -= gapBetweenStrikePrices) {	
			
			if (strikePrice >= fullSampleLeftEnd) {
				prices.add(0, strikePrice);
			}
			
			else
				prices.add(0, fullSampleLeftEnd);
			
		}
		
		for (double strikePrice = underlyingPrice + gapBetweenStrikePrices; 
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
	
	private static void getTrueBKMEstimates() throws DuplicateOptionsException, 
	InconsistentOptionException {

		double[] bsBKMEstimates, svBKMEstimates, svjBKMEstimates;

		setOptionCurves(fullSampleLeftEnd, fullSampleRightEnd);

		bkmEstimator.setOptions(bsOptionCurve);
		bsBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svOptionCurve);
		svBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svjOptionCurve);
		svjBKMEstimates = bkmEstimator.getEstimates();

		bsTrueVol = bsBKMEstimates[0];
		bsTrueSkew = bsBKMEstimates[1];
		bsTrueKurt = bsBKMEstimates[2];
		svTrueVol = svBKMEstimates[0];
		svTrueSkew = svBKMEstimates[1];
		svTrueKurt = svBKMEstimates[2];
		svjTrueVol = svjBKMEstimates[0];
		svjTrueSkew = svjBKMEstimates[1];
		svjTrueKurt = svjBKMEstimates[2];

	}

	private static void setOptionCurves(double left, double right) throws DuplicateOptionsException, InconsistentOptionException {

		int leftEndStrikeIndex = 0, rightEndStrikeIndex = 0;

		for (int i = 0; i < strikePrices.length; i++) {

			double strike = strikePrices[i];

			if (Math.round(strike * 100.0) == Math.round(left * 100.0))  
				leftEndStrikeIndex = i;

			else if (Math.round(strike * 100.0) == Math.round(right * 100.0))
				rightEndStrikeIndex = i;

		}

		bsOptionCurve = new OptionCurve();
		svOptionCurve = new OptionCurve();
		svjOptionCurve = new OptionCurve();

		for (int i = leftEndStrikeIndex; i <= rightEndStrikeIndex; i++) {

			bsOptionCurve.add(bsOptions[i]);
			svOptionCurve.add(svOptions[i]);
			svjOptionCurve.add(svjOptions[i]);

		}

	}
	
}
