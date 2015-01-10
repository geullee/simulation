package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class GenerateFullPrices_queueFiles_diffParam {

	final static int startPoint = 392, gap = 500;
	final static double endPoint = 3534.9;
	
	public static void run() throws FileNotFoundException {

		String queueFileName = "D:/Users/z3384108/Desktop/katana/queue_fullPrices_diffParam.txt";
		File queueFile = new File(queueFileName);
		PrintWriter queuePrintWriter = new PrintWriter(new FileOutputStream(queueFile, true));
		
		for (double sigma = 0.05; sigma < 0.51; sigma += 0.01) {
		
			File pbsFile;
			String pbsFileName;	
			PrintWriter pbsPrintWriter;
			
			for (int i = startPoint; i < endPoint; i += gap) {
				
				String queue = "qsub fullPrices_" + Math.round(sigma * 100.0) + "_" + i + ".pbs";
				
				pbsFileName = "D:/Users/z3384108/Desktop/katana/fullPrices_" + Math.round(sigma * 100.0) + "_" + i + ".pbs";
				pbsFile = new File(pbsFileName);	
				pbsPrintWriter = new PrintWriter(new FileOutputStream(pbsFile, true));
				
				writePbsFile(pbsPrintWriter, i, sigma);

				queuePrintWriter.println(queue);

				pbsPrintWriter.close();	
				
			}
			
		}
		
		queuePrintWriter.close();
		
	}

	private static void writePbsFile(PrintWriter writer, double startPoint, double sigma) {

		writer.println("#!/bin/bash");
		writer.println("");
		writer.println("#PBS -l nodes=1:ppn=1");
		writer.println("#PBS -l vmem=4gb");
		writer.println("#PBS -l walltime=12:00:00");
		writer.println("");
		writer.println("cd $HOME");
		writer.println("");
		writer.println("java -jar sim.jar " + (int) startPoint + " " + Math.min((startPoint + gap - 0.1), endPoint) + " " + sigma);

	}
	
}
