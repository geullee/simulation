package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Figure16_new_queueFiles {

	final static double minimumHalfWidth = 5.0, maximumHalfWidth = 800.0, halfWidthGap = 5.0;
	
	public static void run() throws FileNotFoundException {
	
		String queueFileName = "D:/Users/z3384108/Desktop/katana/queue_f16.txt";
		File queueFile = new File(queueFileName);
		File pbsFile;
		String pbsFileName;
		PrintWriter queuePrintWriter = new PrintWriter(new FileOutputStream(queueFile, true));
		PrintWriter pbsPrintWriter;
		
		for (double halfWidth = minimumHalfWidth; halfWidth <= maximumHalfWidth; 
				halfWidth += halfWidthGap) {

				String queue = "qsub f16_" + (int) halfWidth + ".pbs";
				
				pbsFileName = "D:/Users/z3384108/Desktop/katana/f16_" + (int) halfWidth + ".pbs";
				pbsFile = new File(pbsFileName);	
				pbsPrintWriter = new PrintWriter(new FileOutputStream(pbsFile, true));
				
				writePbsFile(pbsPrintWriter, halfWidth);

				queuePrintWriter.println(queue);

				pbsPrintWriter.close();	
		
		}
		
		queuePrintWriter.close();
		
	}

	private static void writePbsFile(PrintWriter writer, double halfWidth) {

		writer.println("#!/bin/bash");
		writer.println("");
		writer.println("#PBS -l nodes=1:ppn=1");
		writer.println("#PBS -l vmem=4gb");
		writer.println("#PBS -l walltime=02:00:00");
		writer.println("");
		writer.println("cd $HOME");
		writer.println("");
		writer.println("java -jar sim.jar " + halfWidth);

	}
	
}
