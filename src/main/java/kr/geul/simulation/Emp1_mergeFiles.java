package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Emp1_mergeFiles {

	public static void run() throws IOException {
	
		String dayListFileName = "D:/Users/z3384108/Desktop/katana/final.csv", 
			   resultFileName = "D:/Users/z3384108/Desktop/katana/BKM_log.csv",
			   date, dayListLine, dayLine;	
		File dayListFile = new File(dayListFileName),
			 resultFile = new File(resultFileName), dayFile;
		BufferedReader dayListBufferedReader = new BufferedReader(new FileReader(dayListFile)),
				dayBufferedReader;
		PrintWriter writer = 
				new PrintWriter(new FileOutputStream(resultFile, true));
	
		int totalDays = 0;

		while ((dayListLine = dayListBufferedReader.readLine()) != null) {
			totalDays++;
		}

		totalDays--;
		dayListBufferedReader.close();
		
		dayListBufferedReader = 
				new BufferedReader(new FileReader(dayListFile));
		dayListBufferedReader.readLine();
		
		for (int day = 0; day < totalDays; day++) {
			
			dayListLine = dayListBufferedReader.readLine();
			String[] values = dayListLine.split(",", -1);
			date = values[0].substring(2, 8);
			
			dayFile = 
					new File("D:/Users/z3384108/Desktop/katana/BKM_log_spi_" + date + ".csv");
			dayBufferedReader = new BufferedReader(new FileReader(dayFile));
					
			dayLine = dayBufferedReader.readLine();
			while ((dayLine = dayBufferedReader.readLine()) != null) {
				writer.println(dayLine);
			}
			
			dayBufferedReader.close();
			
		}
		
		writer.close();
		
	}
	
}
