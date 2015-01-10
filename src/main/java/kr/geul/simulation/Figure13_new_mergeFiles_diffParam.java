package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Figure13_new_mergeFiles_diffParam {

	final static double minimumHalfWidth = 5.0, maximumHalfWidth = 800.0, halfWidthGap = 5.0;
	
	public static void run() throws IOException {
	
		for (double sigma = 0.1; sigma < 0.51; sigma += 0.1) {
		
			String resultFileName = "D:/Users/z3384108/Desktop/katana/_f13_" + Math.round(sigma * 100.0) + ".csv";	
			File resultFile = new File(resultFileName);
		
			PrintWriter writer = 
					new PrintWriter(new FileOutputStream(resultFile, true));
			
			for (double halfWidth = minimumHalfWidth; halfWidth <= maximumHalfWidth; 
					halfWidth += halfWidthGap) {
					
					String originalFileName = 
							"D:/Users/z3384108/Desktop/katana/_f13_" + (int) halfWidth + "_" + Math.round(sigma * 100.0) + ".csv";
					File originalFile = new File(originalFileName);
					BufferedReader reader = 
							new BufferedReader(new FileReader(originalFile));
					
					String line = reader.readLine();
					writer.println(line);
					reader.close();
					
			}
			
			writer.close();
			
		}
		
	}
	
}
