package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Thesis1_mergeFiles {

	public static void run() throws IOException {
	
		String resultFileName = "D:/Users/z3384108/Desktop/katana/GetBasicProperties.csv";	
		File resultFile = new File(resultFileName);
		PrintWriter writer = 
				new PrintWriter(new FileOutputStream(resultFile, true));
	
		for (int year = 0; year < 11; year++) {
			
			for (int month = 1; month < 13; month++) {
				
				String date = getDateString(year, month);
				String originalFileName = 
						"D:/Users/z3384108/Desktop/katana/GetBasicProperties_" + date + ".csv";
				File originalFile = new File(originalFileName);
				BufferedReader reader = 
						new BufferedReader(new FileReader(originalFile));
				
				String line = reader.readLine();
				
				while ((line = reader.readLine()) != null) {
					writer.println(line);
				}
				
				reader.close();
				
			}
			
		}
		
		writer.close();
		
	}

	private static String getDateString(int year, int month) {

		String result = "";
		result += (year < 10 ? "0" + year : year);
		result += (month < 10 ? "0" + month : month);		
		return result;
		
	}
	
}
