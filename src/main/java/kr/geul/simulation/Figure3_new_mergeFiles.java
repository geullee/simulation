package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Figure3_new_mergeFiles {

	final static double minimumLeftEnd = 770.0, maximumLeftEnd = 1170.0, 
			minimumRightEnd = 1190.0, maximumRightEnd = 1690.0,
			rightGap = 5.0, leftGap = (maximumLeftEnd - minimumLeftEnd) / 4.0;
	
	public static void run() throws IOException {
	
		String resultFileName = "D:/Users/z3384108/Desktop/katana/_f3.csv";	
		File resultFile = new File(resultFileName);
	
		PrintWriter writer = 
				new PrintWriter(new FileOutputStream(resultFile, true));
		
		for (double rightEnd = minimumRightEnd; rightEnd <= maximumRightEnd; rightEnd += rightGap) {
			
			for (double leftEnd = minimumLeftEnd; leftEnd <= maximumLeftEnd; leftEnd += leftGap) {
				
				String originalFileName = 
						"D:/Users/z3384108/Desktop/katana/_f3_" + (int) leftEnd + "_" + 
						(int) rightEnd + ".csv";
				File originalFile = new File(originalFileName);
				BufferedReader reader = 
						new BufferedReader(new FileReader(originalFile));
				
				String line = reader.readLine();
				writer.println(line);
				reader.close();
				
			}
			
		}
		
		writer.close();
		
	}
	
}
