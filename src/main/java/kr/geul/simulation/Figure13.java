package kr.geul.simulation;

import kr.geul.bkm.BKMEstimator;
import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.DuplicateOptionsException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InconsistentOptionException;
import kr.geul.options.exception.InvalidArgumentException;
import kr.geul.options.option.OTMOption;
import kr.geul.options.structure.OTMOptionCurve;

public class Figure13 {

	/* Parameter name arrays */
	final static String[] bsParameterNames = {"S", "K", "R", "T", "D", "C", "sigma"},
		svjParameterNames = {"S", "K", "R", "T", "D", "C", 
		"kappaV", "thetaV", "v0", "sigmaV", "muJ", "sigmaJ", "rho", "lambda"};
	
	/* location of simulation results for different symmetry and asymmetry settings */ 
	final static int nominalSymmetry = 0;
	final static int logSymmetry = 1;
	final static int leftNominalAsymmetry = 2;
	final static int rightNominalAsymmetry = 3;
	
	/* Parameter values */
	final static double underlyingPrice = 1161.5, riskFreeRate = 0.0355, dividendRate = 0.0031,
			timeToMaturity = 0.1151, defaultOptionPrice = 0.0, bsSigma = 0.2049, 
			svKappa = 3.5587, svTheta = 0.1609, svV0 = 0.0500, 
			svSigma = 0.3942, svRho = -0.9826, 
			svjKappaV = 4.2430, svjThetaV = 0.1836, svjV0 = 0.0494,
			svjSigmaV = 0.7199, svjMuJ = -0.0036, svjSigmaJ = 0.0492, svjRho = -0.7283, 
			svjLambda = 1.2607;

	/* Simulation settings */
	final static double minimumHalfWidth = 10.0, maximumHalfWidth = 300.0, 
			gapBetweenXAxisPoints = 10.0, gapBetweenStrikePrices = 0.5,
			fullSampleLeftEnd = 387.0, fullSampleRightEnd = 3484.5;
	final static int numberOfLines = 4;

	final static int numberOfXAxisPoints = 
			(int) ((maximumHalfWidth - minimumHalfWidth) / gapBetweenXAxisPoints) + 1,
			numberOfFullObservations = 
			(int) Math.round((fullSampleRightEnd - fullSampleLeftEnd) / gapBetweenStrikePrices) + 1; 

	/* Strike price and option arrays */
	static double[] strikePrices = new double[numberOfFullObservations];
	static OTMOption[] bsOptions  = new OTMOption[numberOfFullObservations], 
			svOptions  = new OTMOption[numberOfFullObservations],
			svjOptions = new OTMOption[numberOfFullObservations];

	/* Option curves and estimation result arrays */
	static OTMOptionCurve bsOptionCurve, svOptionCurve, svjOptionCurve;
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
	InconsistentOptionException, AtTheMoneyException {
		
		int halfWidthIndex = 0;
		
		generateFullPrices();
		getTrueBKMEstimates();
		
		for (double halfWidth = minimumHalfWidth; halfWidth <= maximumHalfWidth; 
				halfWidth += gapBetweenXAxisPoints) {
			
			System.out.println("halfWidth: " + halfWidth);
			double[][] endPoints = getEndPoints(halfWidth);
			
			for (int symmetryType = nominalSymmetry; 
					symmetryType <= rightNominalAsymmetry; symmetryType++) {
			
				setOptionCurves(endPoints[symmetryType][0], endPoints[symmetryType][1]);
				getBKMEstimates(symmetryType, halfWidthIndex);
								
			}
			
			halfWidthIndex++;
			
		}

		printResults();
		
	}

	private static void getBKMEstimates(int symmetryType, int halfWidthIndex) 
			throws InvalidArgumentException, InconsistentArgumentLengthException, 
			DuplicateOptionsException, InconsistentOptionException {

		double[] bsBKMEstimates, svBKMEstimates, svjBKMEstimates;

		bkmEstimator.setOptions(bsOptionCurve);
		bsBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svOptionCurve);
		svBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svjOptionCurve);
		svjBKMEstimates = bkmEstimator.getEstimates();

		bsVols[symmetryType][halfWidthIndex] = bsBKMEstimates[0];
		bsSkews[symmetryType][halfWidthIndex] = bsBKMEstimates[1];
		bsKurts[symmetryType][halfWidthIndex] = bsBKMEstimates[2];
		svVols[symmetryType][halfWidthIndex] = svBKMEstimates[0];
		svSkews[symmetryType][halfWidthIndex] = svBKMEstimates[1];
		svKurts[symmetryType][halfWidthIndex] = svBKMEstimates[2];
		svjVols[symmetryType][halfWidthIndex] = svjBKMEstimates[0];
		svjSkews[symmetryType][halfWidthIndex] = svjBKMEstimates[1];
		svjKurts[symmetryType][halfWidthIndex] = svjBKMEstimates[2];

	}
	
	private static double[][] getEndPoints(double halfWidth) {
		
		double[][] endPoints = new double[4][2];
		
		double multiplier = 1.0 / gapBetweenStrikePrices;
	    double fullWidth = halfWidth * 2.0;
	    double logSymmetryLeftWidth 
	    	= (-fullWidth + Math.sqrt(Math.pow(fullWidth, 2) + 4 * Math.pow(underlyingPrice, 2))) 
	    		/ 2.0;
	    double logSymmetryLeftEnd = 
	    		Math.round(logSymmetryLeftWidth * multiplier) / multiplier;
	    double logSymmetryRightEnd = logSymmetryLeftEnd + fullWidth;
	    
		endPoints[nominalSymmetry][0] = underlyingPrice - halfWidth;
		endPoints[nominalSymmetry][1] = underlyingPrice + halfWidth;
		endPoints[logSymmetry][0] = logSymmetryLeftEnd;
		endPoints[logSymmetry][1] = logSymmetryRightEnd;
		endPoints[leftNominalAsymmetry][0] 
				= Math.round((underlyingPrice - (halfWidth * 1.2)) * multiplier) / multiplier;
		endPoints[leftNominalAsymmetry][1] 
				= Math.round((underlyingPrice + (halfWidth * 0.8)) * multiplier) / multiplier;
		endPoints[rightNominalAsymmetry][0] 
				= Math.round((underlyingPrice - (halfWidth * 0.8)) * multiplier) / multiplier;
		endPoints[rightNominalAsymmetry][1] 
				= Math.round((underlyingPrice + (halfWidth * 1.2)) * multiplier) / multiplier;
		
		System.out.println(endPoints[0][0] + "," + endPoints[0][1] + ","
				+ endPoints[1][0] + "," + endPoints[1][1] + ","
				+ endPoints[2][0] + "," + endPoints[2][1] + ","
				+ endPoints[3][0] + "," + endPoints[3][1]); 
		
		return endPoints;
		
	}

	private static void generateFullPrices() throws InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException {

		int arrayIndex = 0;
		for (double strikePrice = fullSampleLeftEnd; strikePrice <= fullSampleRightEnd; 
				strikePrice += gapBetweenStrikePrices) {

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
	
	private static void printResults() {
		
		System.out.println("halfWidth,bsVolNom,bsVolLog,bsVolLeft,bsVolRight," 
				+ "svVolNom,svVolLog,svVolLeft,svVolRight,"
				+ "svjVolNom,svjVolLog,svjVolLeft,svjVolRight,"
				+ "bsSkewNom,bsSkewLog,bsSkewLeft,bsSkewRight,"
				+ "svSkewNom,svSkewLog,svSkewLeft,svSkewRight,"
				+ "svjSkewNom,svjSkewLog,svjSkewLeft,svjSkewRight,"
				+ "bsKurtNom,bsKurtLog,bsKurtLeft,bsKurtRight,"
				+ "svKurtNom,svKurtLog,svKurtLeft,svKurtRight,"
				+ "svjKurtNom,svjKurtLog,svjKurtLeft,svjKurtRight,"
				+ "bsTrueVol,bsTrueSkew,bsTrueKurt,svTrueVol,svTrueSkew,svTrueKurt,"
				+ "svjTrueVol,svjTrueSkew,svjTrueKurt"); 
		
		for (int i = 0; i < numberOfXAxisPoints; i++) {

			System.out.print((minimumHalfWidth + i * gapBetweenXAxisPoints) + ",");

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(bsVols[j][i] + ","); 
			}
			
			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svVols[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svjVols[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(bsSkews[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svSkews[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svjSkews[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(bsKurts[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svKurts[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svjKurts[j][i] + ","); 
			}

			System.out.println(bsTrueVol + "," + bsTrueSkew + "," + bsTrueKurt + "," +
					svTrueVol + "," + svTrueSkew + "," + svTrueKurt + "," +
					svjTrueVol + "," + svjTrueSkew + "," + svjTrueKurt);

		}
		
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

		bsOptionCurve = new OTMOptionCurve();
		svOptionCurve = new OTMOptionCurve();
		svjOptionCurve = new OTMOptionCurve();

		for (int i = leftEndStrikeIndex; i <= rightEndStrikeIndex; i++) {

			bsOptionCurve.add(bsOptions[i]);
			svOptionCurve.add(svOptions[i]);
			svjOptionCurve.add(svjOptions[i]);

		}

	}
	
}
