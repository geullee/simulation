package kr.geul.simulation;

import java.io.IOException;

import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.DuplicateOptionsException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InconsistentOptionException;
import kr.geul.options.exception.InvalidArgumentException;
import kr.geul.options.option.CallOption;
import kr.geul.options.option.Option;
import kr.geul.options.option.PutOption;

public class PCPTest extends Simulation {

	final static String[] bsParameterNames = {"S", "K", "R", "T", "D", "C", "sigma"},
			svjParameterNames = {"S", "K", "R", "T", "D", "C", 
			"kappaV", "thetaV", "v0", "sigmaV", "muJ", "sigmaJ", "rho", "lambda"};
	
	final static double underlyingPrice = 1183.2, riskFreeRate = 0.0301, dividendRate = 0.0167,
			timeToMaturity = 0.25, defaultOptionPrice = 0.0, bsSigma = 0.2033; 
	
	static Option call, put;
	
	public static void run(String[] args) throws IOException, InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException, DuplicateOptionsException, 
	InconsistentOptionException {
		
		call = new CallOption("BS");
		put = new PutOption("BS");
		
		double[] bsValues = {underlyingPrice, 1183.2, riskFreeRate, 
				timeToMaturity, dividendRate, defaultOptionPrice, bsSigma};
		
		call.set(bsParameterNames, bsValues);
		put.set(bsParameterNames, bsValues);
		call.evaluate();
		put.evaluate();
		
		double callPrice = call.getOptionPrice();
		double putPrice = put.getOptionPrice();
		
		System.out.println("call: " + callPrice + ", put: " + putPrice); 
		System.out.println((callPrice - putPrice) + ", " + (underlyingPrice
				* Math.exp(-dividendRate * timeToMaturity) - 1183.2 * Math.exp(-riskFreeRate * timeToMaturity)));
		
	}

}
