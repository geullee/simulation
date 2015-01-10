package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Thesis1_queueFiles {

	static String fileName = "D:/Users/z3384108/Desktop/katana/queue_thesis1.txt", date;
	static File queueFile = new File(fileName), pbsFile, txtFile;
	static PrintWriter queueWriter, pbsWriter, txtWriter;
	
	public static void run() throws FileNotFoundException {

		queueWriter = new PrintWriter(new FileOutputStream(queueFile, true));
		
		for (int year = 0; year < 11; year++) {

			for (int month = 1; month < 13; month++) {

				date = "qsub " + getFileName(year, month) + ".pbs";
				queueWriter.println(date);

				pbsFile = 
						new File("D:/Users/z3384108/Desktop/katana/" + getFileName(year, month) + ".pbs");
				txtFile = 
					new File("D:/Users/z3384108/Desktop/katana/" + getFileName(year, month) + ".txt");
				pbsWriter = new PrintWriter(new FileOutputStream(pbsFile, true));
				txtWriter = new PrintWriter(new FileOutputStream(txtFile, true)); 
				
				writePbsFile(year, month);
				writeTxtFile(year, month);
				
				pbsWriter.close();
				txtWriter.close();
				
			}

		}

		queueWriter.close();
		
	}

	private static void writePbsFile(int year, int month) {

		pbsWriter.println("#!/bin/bash");
		pbsWriter.println("");
		pbsWriter.println("#PBS -l nodes=1:ppn=1");
		pbsWriter.println("#PBS -l vmem=4gb");
		pbsWriter.print("#PBS -l walltime=12:00:00");
		pbsWriter.println("");
		pbsWriter.println("cd $HOME");
		pbsWriter.println("");
		pbsWriter.println("java -jar t1.jar < " + getFileName(year, month) + ".txt");
		
	}
	
	private static void writeTxtFile(int year, int month) {
		txtWriter.println("getb " + getFileName(year, month).subSequence(0, 4));
	}

	private static String getFileName(int year, int month) {

		String fileName;

		if (year < 10)
			fileName = "0" + year;
		else
			fileName = "" + year;

		if (month < 10)
			fileName += "0";

		fileName += month;

		return fileName + "_t1";

	}
	
}
