package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Figure3_new_queueFiles {

	final static double minimumLeftEnd = 770.0, maximumLeftEnd = 1170.0, 
			minimumRightEnd = 1190.0, maximumRightEnd = 1690.0,
			rightGap = 5.0, leftGap = (maximumLeftEnd - minimumLeftEnd) / 4.0;
	
	public static void run() throws FileNotFoundException {
	
		String queueFileName = "D:/Users/z3384108/Desktop/katana/queue_f3.txt";
		File queueFile = new File(queueFileName);
		File pbsFile;
		String pbsFileName;
		PrintWriter queuePrintWriter = new PrintWriter(new FileOutputStream(queueFile, true));
		PrintWriter pbsPrintWriter;
		
		for (double rightEnd = minimumRightEnd; rightEnd <= maximumRightEnd; rightEnd += rightGap) {
			
			for (double leftEnd = minimumLeftEnd; leftEnd <= maximumLeftEnd; leftEnd += leftGap) {
			
				String queue = "qsub f3_" + (int) leftEnd + "_" + (int) rightEnd + ".pbs";
				
				pbsFileName = "D:/Users/z3384108/Desktop/katana/f3_" + (int) leftEnd + "_" + 
						(int) rightEnd + ".pbs";
				pbsFile = new File(pbsFileName);	
				pbsPrintWriter = new PrintWriter(new FileOutputStream(pbsFile, true));
				
				writePbsFile(pbsPrintWriter, leftEnd, rightEnd);

				queuePrintWriter.println(queue);

				pbsPrintWriter.close();	
				
			}
			
		}
		
		queuePrintWriter.close();
		
	}

	private static void writePbsFile(PrintWriter writer, double leftEnd, double rightEnd) {

		writer.println("#!/bin/bash");
		writer.println("");
		writer.println("#PBS -l nodes=1:ppn=1");
		writer.println("#PBS -l vmem=4gb");
		writer.println("#PBS -l walltime=00:30:00");
		writer.println("");
		writer.println("cd $HOME");
		writer.println("");
		writer.println("java -jar sim.jar " + leftEnd + " " + rightEnd);

	}
	
}
