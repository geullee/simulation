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

public class Figure13_new_diffParam extends Simulation {

	public static void run(String[] args) throws IOException, InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException, DuplicateOptionsException, 
	InconsistentOptionException {
		
		double halfWidth = Double.parseDouble(args[0]);
		double sigma = Double.parseDouble(args[1]);
		
		String fileName = "./_f13_" + (int) halfWidth + "_" + Math.round(sigma * 100.0) + ".csv";
		
		File file = new File(fileName);
		
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
		
		readFullPrices_diffParam(sigma);
		
		double[][] endPoints = getEndPoints(halfWidth);
		double[] bkmEstimates = new double[36];
		
		for (int symmetryType = nominalSymmetry; 
				symmetryType <= rightExtremeNominalAsymmetry; symmetryType++) {
		
			setOptionCurves(endPoints[symmetryType][0], endPoints[symmetryType][1]);
			double[] estimates = getBKMEstimates();
			
			for (int i = 0; i < 6; i++) {
				bkmEstimates[symmetryType * 6 + i] = estimates[i];	
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
