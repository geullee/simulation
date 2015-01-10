package kr.geul.simulation;

import kr.geul.bkm.BKMEstimator;
import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.DuplicateOptionsException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InconsistentOptionException;
import kr.geul.options.exception.InvalidArgumentException;
import kr.geul.options.option.OTMOption;
import kr.geul.options.structure.OTMOptionCurve;

public class Figure10 {

	/* Parameter name arrays */
	final static String[] svjParameterNames = {"S", "K", "R", "T", "D", "C", 
			"kappaV", "thetaV", "v0", "sigmaV", "muJ", "sigmaJ", "rho", "lambda"};

	/* Parameter values */
	final static double underlyingPrice = 1161.5, riskFreeRate = 0.0355, dividendRate = 0.0031,
			timeToMaturity = 0.1151, defaultOptionPrice = 0.0,
			svKappa = 3.5587, svTheta = 0.1609, svV0 = 0.0500, 
			svSigma = 0.3942, svRho = -0.9826, 
			svjKappaV = 4.2430, svjThetaV = 0.1836, svjV0 = 0.0494,
			svjSigmaV = 0.7199, svjMuJ = -0.0036, svjSigmaJ = 0.0492, svjRho = -0.7283, 
			svjLambda = 1.2607;

	/* Simulation settings */
	final static double minimumLeftEnd = 900.0, maximumLeftEnd = 1160.5, 
			minimumRightEnd = 1170.0, maximumRightEnd = 1370.0,
			gapBetweenXAxisPoints = 10.0, gapBetweenStrikePrices = 0.5,
			fullSampleLeftEnd = 387.0, fullSampleRightEnd = 1402.5;
	final static int numberOfLines = 5;

	final static double	gapBetweenLines = (maximumRightEnd - minimumRightEnd) / (double) (numberOfLines - 1);
	final static int numberOfXAxisPoints = (int) ((maximumLeftEnd - minimumLeftEnd) / gapBetweenXAxisPoints + 1.0),
			numberOfFullObservations = 
			(int) Math.round((fullSampleRightEnd - fullSampleLeftEnd) / gapBetweenStrikePrices) + 1; 

	/* Strike price and option arrays */
	static double[] strikePrices = new double[numberOfFullObservations];
	static OTMOption[] svOptions  = new OTMOption[numberOfFullObservations],
			svjOptions = new OTMOption[numberOfFullObservations];

	/* Option curves and estimation result arrays */
	static OTMOptionCurve svOptionCurve, svjOptionCurve;
	static double svTrueVol, svTrueSkew, svTrueKurt, svjTrueVol, svjTrueSkew, svjTrueKurt;
	static double[][] svVols = new double[numberOfLines][numberOfXAxisPoints], 
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

		int rightIndex = 0;
		generateFullPrices();
		getTrueBKMEstimates();

		for (double right = minimumRightEnd; right <= maximumRightEnd; right += gapBetweenLines) {

			int leftIndex = 0;
			
			for (double left = minimumLeftEnd; left <= maximumLeftEnd; left += gapBetweenXAxisPoints) {

				System.out.println(left + " : " + right);
				setOptionCurves(left, right);
				getBKMEstimates(rightIndex, leftIndex);
				leftIndex++;

			}

			rightIndex++;

		}

		printResults();

	}

	private static void generateFullPrices() throws InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException {

		int arrayIndex = 0;
		for (double strikePrice = fullSampleLeftEnd; strikePrice <= fullSampleRightEnd; 
				strikePrice += gapBetweenStrikePrices) {
			
			System.out.println(strikePrice);
			
			double[] svValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, svKappa, svTheta, 
					svV0, svSigma, 0.0, 0.0, svRho, 0.0}; 

			double[] svjValues = {underlyingPrice, strikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, svjKappaV, svjThetaV, 
					svjV0, svjSigmaV, svjMuJ, svjSigmaJ, svjRho, svjLambda}; 

			OTMOption svOption = new OTMOption("SVJ");
			OTMOption svjOption = new OTMOption("SVJ");				

			svOption.set(svjParameterNames, svValues);
			svjOption.set(svjParameterNames, svjValues);

			svOption.evaluate();
			svjOption.evaluate();

			strikePrices[arrayIndex] = svOption.getVariableArray()[1];
			svOptions[arrayIndex] = svOption;
			svjOptions[arrayIndex] = svjOption;

			arrayIndex++;

		}

	}
	
	private static void getBKMEstimates(int leftIndex, int rightIndex) 
			throws InvalidArgumentException, InconsistentArgumentLengthException, 
			DuplicateOptionsException, InconsistentOptionException, AtTheMoneyException {

		double[] svBKMEstimates, svjBKMEstimates;

		svOptionCurve.setExtrapolationRange(fullSampleLeftEnd, fullSampleRightEnd);
		svOptionCurve.setStrikePriceGap(gapBetweenStrikePrices);
		svOptionCurve.extrapolate();
		svjOptionCurve.setExtrapolationRange(fullSampleLeftEnd, fullSampleRightEnd);
		svjOptionCurve.setStrikePriceGap(gapBetweenStrikePrices);
		svjOptionCurve.extrapolate();
		
		bkmEstimator.setOptions(svOptionCurve);
		svBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svjOptionCurve);
		svjBKMEstimates = bkmEstimator.getEstimates();

		svVols[leftIndex][rightIndex] = svBKMEstimates[0];
		svSkews[leftIndex][rightIndex] = svBKMEstimates[1];
		svKurts[leftIndex][rightIndex] = svBKMEstimates[2];
		svjVols[leftIndex][rightIndex] = svjBKMEstimates[0];
		svjSkews[leftIndex][rightIndex] = svjBKMEstimates[1];
		svjKurts[leftIndex][rightIndex] = svjBKMEstimates[2];

	}
	
	private static void getTrueBKMEstimates() throws DuplicateOptionsException, 
	InconsistentOptionException {

		double[] svBKMEstimates, svjBKMEstimates;
		
		setOptionCurves(fullSampleLeftEnd, fullSampleRightEnd);
		bkmEstimator.setOptions(svOptionCurve);
		svBKMEstimates = bkmEstimator.getEstimates();
		bkmEstimator.setOptions(svjOptionCurve);
		svjBKMEstimates = bkmEstimator.getEstimates();
		
		svTrueVol = svBKMEstimates[0];
		svTrueSkew = svBKMEstimates[1];
		svTrueKurt = svBKMEstimates[2];
		svjTrueVol = svjBKMEstimates[0];
		svjTrueSkew = svjBKMEstimates[1];
		svjTrueKurt = svjBKMEstimates[2];
		
	}
	
	private static void printResults() {
		
		System.out.println("maxK,svVol50,svVol52,svVol54,svVol56,svVol58,"
				+ "svjVol50,svjVol52,svjVol54,svjVol56,svjVol58,"
				+ "svSkew50,svSkew52,svSkew54,svSkew56,svSkew58,"
				+ "svjSkew50,svjSkew52,svjSkew54,svjSkew56,svjSkew58,"
				+ "svKurt50,svKurt52,svKurt54,svKurt56,svKurt58,"
				+ "svjKurt50,svjKurt52,svjKurt54,svjKurt56,svjKurt58," 
				+ "svTrueVol,svTrueSkew,svTrueKurt,svjTrueVol,svjTrueSkew,svjTrueKurt"); 

		for (int i = 0; i < numberOfXAxisPoints; i++) {

			System.out.print((minimumLeftEnd + i * gapBetweenXAxisPoints) + ",");

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svVols[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svjVols[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svSkews[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svjSkews[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svKurts[j][i] + ","); 
			}

			for (int j = 0; j < numberOfLines; j++) {
				System.out.print(svjKurts[j][i] + ","); 
			}

			System.out.println(svTrueVol + "," + svTrueSkew + "," + svTrueKurt + "," +
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
		
		svOptionCurve = new OTMOptionCurve();
		svjOptionCurve = new OTMOptionCurve();

		for (int i = leftEndStrikeIndex; i <= rightEndStrikeIndex; i++) {

			svOptionCurve.add(svOptions[i]);
			svjOptionCurve.add(svjOptions[i]);

		}
		
	}
	
}
