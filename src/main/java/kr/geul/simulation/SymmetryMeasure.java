package kr.geul.simulation;

import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InvalidArgumentException;
import kr.geul.options.option.CallOption;
import kr.geul.options.option.PutOption;

public class SymmetryMeasure {

	static double underlyingPrice = 1000.0, strikePriceGap = 10.0, riskFreeRate = 0.07,
			dividendRate = 0.0, timeToMaturity = 1.0 / 12.0, defaultOptionPrice = 0.0,
			bsSigma = 0.5;
	static String[] bsParameterNames = {"S", "K", "R", "T", "D", "C", "sigma"};
	
	public static void run() throws InvalidArgumentException, InconsistentArgumentLengthException, AtTheMoneyException {
				
		for (double maximumStrikePrice = underlyingPrice + strikePriceGap; 
				maximumStrikePrice < underlyingPrice * 2.0; maximumStrikePrice += strikePriceGap) {
			
			double minimumStrikePrice_nom = 
					underlyingPrice - (maximumStrikePrice - underlyingPrice), 
				   minimumStrikePrice_log =
				   	underlyingPrice * (underlyingPrice / maximumStrikePrice);
			
			double[] bsValues_max = {underlyingPrice, maximumStrikePrice, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, bsSigma};
			double[] bsValues_min_nom = {underlyingPrice, minimumStrikePrice_nom, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, bsSigma};
			double[] bsValues_min_log = {underlyingPrice, minimumStrikePrice_log, riskFreeRate, 
					timeToMaturity, dividendRate, defaultOptionPrice, bsSigma};
			
			CallOption option_max = new CallOption("BS");
			PutOption option_min_nom = new PutOption("BS"), option_min_log = new PutOption("BS");
			
			option_max.set(bsParameterNames, bsValues_max);
			option_min_nom.set(bsParameterNames, bsValues_min_nom);
			option_min_log.set(bsParameterNames, bsValues_min_log);
			
			option_max.evaluate();
			option_min_nom.evaluate();
			option_min_log.evaluate();
			
			double price_max = option_max.getOptionPrice(),
				   price_min_nom = option_min_nom.getOptionPrice(),
				   price_min_log = option_min_log.getOptionPrice();
			
			double wPrice_max = getW(maximumStrikePrice, price_max),
				   wPrice_min_nom = getW(minimumStrikePrice_nom, price_min_nom),
				   wPrice_min_log = getW(minimumStrikePrice_log, price_min_log);
			
			System.out.println(maximumStrikePrice + "," + minimumStrikePrice_nom + ","
					+ minimumStrikePrice_log + "," + (wPrice_max + wPrice_min_nom) + ","
					+ (wPrice_max + wPrice_min_log)); 
			
		}
		
	}

	private static double getW(double strikePrice, double optionPrice) {

		return ((6.0 * Math.log(strikePrice / (underlyingPrice * 
				Math.exp(-dividendRate * timeToMaturity)))) - 
				(3.0 * Math.pow(Math.log(strikePrice / (underlyingPrice * 
				Math.exp(-dividendRate * timeToMaturity))), 2.0))) / 
				(Math.pow(strikePrice, 2.0)) * optionPrice;
		
	}
	
}
