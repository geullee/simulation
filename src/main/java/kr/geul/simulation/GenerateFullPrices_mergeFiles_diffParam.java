package kr.geul.simulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GenerateFullPrices_mergeFiles_diffParam {

	final static int startPoint = 392, gap = 500;
	final static double endPoint = 3534.9;
	
	public static void run() throws IOException {
	
		for (double sigma = 0.05; sigma < 0.51; sigma += 0.01) {
			
			String resultFileName = "D:/Users/z3384108/Desktop/katana/fullPrices_" + Math.round(sigma * 100.0) + ".csv";
			File resultFile = new File(resultFileName);
			PrintWriter writer = 
					new PrintWriter(new FileOutputStream(resultFile, true));
			
			for (int i = startPoint; i < endPoint; i += gap) {
			
				String line, originalFileName = 
						"D:/Users/z3384108/Desktop/katana/_fullPrices_" + i + "_" + Math.round(sigma * 100.0) + ".csv";
				File originalFile = new File(originalFileName);
				BufferedReader reader = 
						new BufferedReader(new FileReader(originalFile));
				
				double previousStrike = 0.0;
				
				while ((line = reader.readLine()) != null) {
					String[] entries = line.split(",", -1);
					double strike = Math.round(Double.parseDouble(entries[0]) * 10.0) / 10.0;
					
					if (strike != previousStrike) {
						writer.println(strike + "," + entries[1] + "," + entries[2] + "," + entries[3] + "," + entries[4]);
						previousStrike = strike;
					}
					
				}
				
				reader.close();
				
			}
		
			writer.close();
			
		}		
		
	}
	
}
