package kr.geul.simulation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.DuplicateOptionsException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InconsistentOptionException;
import kr.geul.options.exception.InvalidArgumentException;

public class Figure16_new extends Simulation {

	public static void run(String[] args) throws IOException, InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException, DuplicateOptionsException, 
	InconsistentOptionException {
		
		double halfWidth = Double.parseDouble(args[0]);
		
		String fileName = "./_f16_" + (int) halfWidth + ".csv";
		
		File file = new File(fileName);
		
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
		
		readFullPrices();
		
		double[][] endPoints = getEndPoints(halfWidth);
		double[] bkmEstimates = new double[18];
		
		for (int symmetryType = nominalSymmetry; 
				symmetryType <= rightExtremeNominalAsymmetry; symmetryType++) {
		
			setOptionCurves(endPoints[symmetryType][0], endPoints[symmetryType][1]);
			double[] estimates = getBKMEstimates_extrapolated();
			
			for (int i = 0; i < 3; i++) {
				bkmEstimates[symmetryType * 3 + i] = estimates[i];	
			}
			
		}
		
		printWriter.print(halfWidth + ",");
		
		for (int i = 0; i < bkmEstimates.length; i++) {
			printWriter.print(bkmEstimates[i]);
			if (i < bkmEstimates.length - 1)
				printWriter.print(",");
		}
		
		printWriter.println("");
		printWriter.close();
		
	}

}
