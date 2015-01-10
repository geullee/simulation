package kr.geul.simulation;

import java.io.IOException;

import kr.geul.options.exception.AtTheMoneyException;
import kr.geul.options.exception.DuplicateOptionsException;
import kr.geul.options.exception.InconsistentArgumentLengthException;
import kr.geul.options.exception.InconsistentOptionException;
import kr.geul.options.exception.InvalidArgumentException;

public class Main {
	
	static long startTime, endTime;
	
	public static void main(String[] args) throws InvalidArgumentException, 
	InconsistentArgumentLengthException, DuplicateOptionsException, 
	InconsistentOptionException, AtTheMoneyException, IOException {
		
		tic();
		SymmetryMeasure.run();
		toc();
		
	}

	static void tic() {	
		startTime = System.currentTimeMillis();		
	}

	static void toc() {

		endTime = System.currentTimeMillis();
		System.out.println(" DONE, Elapsed time: "
				+ (double) (((double) endTime - startTime) / 1000.00)
				+ " seconds");

	}
	
}
