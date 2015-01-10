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

public class Figure4_new extends Simulation {

	public static void run(String[] args) throws IOException, InvalidArgumentException, 
	InconsistentArgumentLengthException, AtTheMoneyException, DuplicateOptionsException, 
	InconsistentOptionException {
		
		double left = Double.parseDouble(args[0]);
		double right = Double.parseDouble(args[1]);
		
		String fileName = "./_f4_" + (int) left + "_" + (int) right + ".csv";
		
		File file = new File(fileName);
		
		PrintWriter printWriter = new PrintWriter(new FileOutputStream(file, true));
		
		readFullPrices();
		setOptionCurves(left, right);
		double[] bkmEstimates = getBKMEstimates();
		
		printWriter.print(left + "," + right + ",");
		
		for (int i = 0; i < bkmEstimates.length; i++) {
			printWriter.print(bkmEstimates[i]);
			if (i < bkmEstimates.length - 1)
				printWriter.print(",");
		}
		
		printWriter.println("");
		printWriter.close();
		
	}

}
