package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Figure13_new_queueFiles_diffParam {

	final static double minimumHalfWidth = 5.0, maximumHalfWidth = 800.0, halfWidthGap = 5.0;
	
	public static void run() throws FileNotFoundException {
	
		String queueFileName = "D:/Users/z3384108/Desktop/katana/queue_f13.txt";
		File queueFile = new File(queueFileName);
		File pbsFile;
		String pbsFileName;
		PrintWriter queuePrintWriter = new PrintWriter(new FileOutputStream(queueFile, true));
		PrintWriter pbsPrintWriter;
		
		for (double sigma = 0.1; sigma < 0.51; sigma += 0.1) {
		
			for (double halfWidth = minimumHalfWidth; halfWidth <= maximumHalfWidth; 
					halfWidth += halfWidthGap) {

					String queue = "qsub f13_" + (int) halfWidth + "_" + Math.round(sigma * 100.0) + ".pbs";
					
					pbsFileName = "D:/Users/z3384108/Desktop/katana/f13_" + (int) halfWidth + "_" + Math.round(sigma * 100.0) + ".pbs";
							
					pbsFile = new File(pbsFileName);	
					pbsPrintWriter = new PrintWriter(new FileOutputStream(pbsFile, true));
					
					writePbsFile(pbsPrintWriter, halfWidth, sigma);

					queuePrintWriter.println(queue);

					pbsPrintWriter.close();	
			
			}

		}
				
		queuePrintWriter.close();
		
	}

	private static void writePbsFile(PrintWriter writer, double halfWidth, double sigma) {

		writer.println("#!/bin/bash");
		writer.println("");
		writer.println("#PBS -l nodes=1:ppn=1");
		writer.println("#PBS -l vmem=4gb");
		writer.println("#PBS -l walltime=00:30:00");
		writer.println("");
		writer.println("cd $HOME");
		writer.println("");
		writer.println("java -jar sim.jar " + halfWidth + " " + sigma);

	}
	
}
