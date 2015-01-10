package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Exam_queuefiles {

	static String queueFileName = "D:/Users/z3384108/Desktop/katana/queue_emp1.txt", 
			date, line;
	static File queueFile = new File(queueFileName), 
			pbsFile, txtFile;
	static PrintWriter queueWriter,	pbsWriter, txtWriter;
	static String[] dates = {"101105", "000929", "051110", "050810", "101223",
		"060501", "070413", "071012", "041104", "090821",
		"040608", "051014", "060427", "091222", "090323",
		"000926", "091211", "070615", "001004"}; 
	
	public static void run() throws IOException {

		queueWriter = new PrintWriter(new FileOutputStream(queueFile, true));

		for (int day = 0; day < dates.length; day++) {

			date = dates[day];

			queueWriter.println("qsub " + date + "_exam.pbs");

			pbsFile = 
					new File("D:/Users/z3384108/Desktop/katana/" + date + "_exam.pbs");
			txtFile = 
					new File("D:/Users/z3384108/Desktop/katana/" + date + "_exam.txt");
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
		pbsWriter.println("java -jar db.jar < " + date + "_exam.txt");

	}

	private static void writeTxtFile(String date) {
		txtWriter.println("bkms " + date);
	}

}
