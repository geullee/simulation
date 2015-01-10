package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Figure16_new_mergeFiles {

	final static double minimumHalfWidth = 5.0, maximumHalfWidth = 800.0, halfWidthGap = 5.0;
	
	public static void run() throws IOException {
	
		String resultFileName = "D:/Users/z3384108/Desktop/katana/_f16.csv";	
		File resultFile = new File(resultFileName);
	
		PrintWriter writer = 
				new PrintWriter(new FileOutputStream(resultFile, true));
		
		for (double halfWidth = minimumHalfWidth; halfWidth <= maximumHalfWidth; 
				halfWidth += halfWidthGap) {
			
			
				
				String originalFileName = 
						"D:/Users/z3384108/Desktop/katana/_f16_" + (int) halfWidth + ".csv";
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
