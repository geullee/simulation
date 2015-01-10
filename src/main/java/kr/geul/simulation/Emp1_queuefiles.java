package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Emp1_queuefiles {

	static String queueFileName = "D:/Users/z3384108/Desktop/katana/queue_emp1.txt", 
			csvFileName = "D:/Users/z3384108/Desktop/katana/final.csv", date, line;
	static File queueFile = new File(queueFileName), 
			csvFile = new File(csvFileName), pbsFile, txtFile;
	static PrintWriter queueWriter,	pbsWriter, txtWriter;
	static BufferedReader bufferedReader;

	public static void run() throws IOException {

		queueWriter = new PrintWriter(new FileOutputStream(queueFile, true));
		bufferedReader = new BufferedReader(new FileReader(csvFile));

		int totalDays = 0;

		while ((line = bufferedReader.readLine()) != null) {
			totalDays++;
		}

		totalDays--;
		bufferedReader.close();

		bufferedReader = 
				new BufferedReader(new FileReader(csvFile));
		bufferedReader.readLine();

		for (int day = 0; day < totalDays; day++) {

			line = bufferedReader.readLine();
			String[] values = line.split(",", -1);
			date = values[0].substring(2, 8);

			queueWriter.println("qsub " + date + "_e1.pbs");

			pbsFile = 
					new File("D:/Users/z3384108/Desktop/katana/" + date + "_e1.pbs");
			txtFile = 
					new File("D:/Users/z3384108/Desktop/katana/" + date + "_e1.txt");
			pbsWriter = new PrintWriter(new FileOutputStream(pbsFile, true));
			txtWriter = new PrintWriter(new FileOutputStream(txtFile, true)); 

			writePbsFile(date);
			writeTxtFile(date);

			pbsWriter.close();
			txtWriter.close();

		}	

		queueWriter.close();

	}

	private static void writePbsFile(String date) {

		pbsWriter.println("#!/bin/bash");
		pbsWriter.println("");
		pbsWriter.println("#PBS -l nodes=1:ppn=1");
		pbsWriter.println("#PBS -l vmem=4gb");
		pbsWriter.print("#PBS -l walltime=12:00:00");
		pbsWriter.println("");
		pbsWriter.println("cd $HOME");
		pbsWriter.println("");
		pbsWriter.println("java -jar db.jar < " + date + "_e1.txt");

	}

	private static void writeTxtFile(String date) {
		txtWriter.println("bkms " + date);
	}

}
