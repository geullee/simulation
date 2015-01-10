package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class GenerateFullPrices_queueFiles {

	final static int startPoint = 392, gap = 3535-392;
	final static double endPoint = 3534.9;
	
	public static void run() throws FileNotFoundException {
	
		String queueFileName = "D:/Users/z3384108/Desktop/katana/queue_fullPrices.txt";
		File queueFile = new File(queueFileName);
		File pbsFile;
		String pbsFileName;
		PrintWriter queuePrintWriter = new PrintWriter(new FileOutputStream(queueFile, true));
		PrintWriter pbsPrintWriter;
		
		for (int i = startPoint; i < endPoint; i += gap) {
			
			String queue = "qsub fullPrices_" + i + ".pbs";
			
			pbsFileName = "D:/Users/z3384108/Desktop/katana/fullPrices_" + i + ".pbs";
			pbsFile = new File(pbsFileName);	
			pbsPrintWriter = new PrintWriter(new FileOutputStream(pbsFile, true));
			
			writePbsFile(pbsPrintWriter, i);

			queuePrintWriter.println(queue);

			pbsPrintWriter.close();	
			
		}
		
		queuePrintWriter.close();
		
	}

	private static void writePbsFile(PrintWriter writer, double startPoint) {

		writer.println("#!/bin/bash");
		writer.println("");
		writer.println("#PBS -l nodes=1:ppn=1");
		writer.println("#PBS -l vmem=4gb");
		writer.println("#PBS -l walltime=12:00:00");
		writer.println("");
		writer.println("cd $HOME");
		writer.println("");
		writer.println("java -jar sim.jar " + (int) startPoint + " " + (startPoint + gap - 0.1));

	}
	
}
