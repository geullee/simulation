package kr.geul.simulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SharedWriter {
	
	private static boolean isNameDefined = false;
	private static int numberOfClients = 0;
	
	private static ConcurrentLinkedQueue<String> lines = 
		new ConcurrentLinkedQueue<String>();
	private static String fileName;
	private static File file;
	private static PrintWriter printWriter;
	
	public synchronized void addClient() {
		numberOfClients++;
		System.out.println("Number of clients: " + numberOfClients);
	}
	
	public synchronized void removeClient() throws FileNotFoundException {
		
		numberOfClients--;
		System.out.println("Number of clients: " + numberOfClients);
		
		flush(lines.size());
		printWriter.close();
		printWriter = new PrintWriter(new FileOutputStream(file, true));
		
	}
	
	private synchronized void flush(int size) {
		
		for (int i = 0; i < size; i++) {
			printWriter.println(lines.poll());
		}
		
	}

	public synchronized static boolean isDefined() {
		return isNameDefined;
	}
	
	public void addLine(String line) {
		
		lines.offer(line);
		System.out.println("line " + lines.size() + ": " + line);
		
	}
	
	public synchronized static void set(String name) throws FileNotFoundException {
		
		isNameDefined = true;
		fileName = name;
		file = new File(fileName);
		printWriter = new PrintWriter(new FileOutputStream(file, true));
		
	}
	
}
