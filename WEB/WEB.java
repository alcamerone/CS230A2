/*
* WEB Parallel Processing Demonstration Class
* Author: cekb635
* ID: 2624052
* Date: 18/5/2012
* This class serves as a demonstration of the efficiency of parallel processing, by requesting
* information from a number of web-pages, then performing computationally intensive tasks with
* this information, then displaying the results.
*/

import java.lang.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;

public class WEB{
	
	public static void main(String[] args){
		
		Scanner fileScanner = null;
		InputStream currPageStream = null;
		int urlIndex = 0;
		int nUrls = 0;
		int nLines = 0;
		double startTimeMillis;
		double endTimeMillis;
		double timeTakenSecs;
		
		try{
		/*------------------------------------------------------------------------------------------------------------------------------------------
		SEQUENTIAL APPROACH
		------------------------------------------------------------------------------------------------------------------------------------------*/
			//sets up file scanner
			fileScanner = new Scanner(new FileReader(args[0]));
			//shows the number of lines in the file
			nLines = getNumberOfLines(fileScanner);
			System.out.println("[" + Thread.currentThread().getId() + "] SeqFetch urls=" + nLines + "\n");
			fileScanner = new Scanner(new FileReader(args[0]));
			//starts the timer
			startTimeMillis = System.currentTimeMillis();
			
			while(fileScanner.hasNext()){
				currPageStream = establishConnection(fileScanner.next(), urlIndex); //establishes the connection to the page and creates
																					//a workable input-stream from this
				getOutput(currPageStream, fileScanner.next(), urlIndex);//retrieves specified information from the web-page information
				urlIndex++;
			}
			
			endTimeMillis = System.currentTimeMillis();//gets the final time
			timeTakenSecs = (endTimeMillis - startTimeMillis) / 1000;//computes time taken
			//prints results
			System.out.println("\n[" + Thread.currentThread().getId() + "] SeqFetch secs=" + timeTakenSecs);
			
		/*------------------------------------------------------------------------------------------------------------------------------------------
		PARALLEL APPROACH
		------------------------------------------------------------------------------------------------------------------------------------------*/
			
			System.out.println("\n[" + Thread.currentThread().getId() + "] ParFetch urls=" + nLines + " procs=" + Runtime.getRuntime().availableProcessors() + "\n");
			fileScanner = new Scanner(new FileReader(args[0]));
			//as above, but here, iterates through the data in the file and, rather than processing it immediately, sets up a number
			//of tasks to be completed by a ForkJoinThreadPool
			ArrayList<URLProcessingThread> newTasks = new ArrayList<URLProcessingThread>();
			ForkJoinPool fjThreadPool = new ForkJoinPool(nLines); //this allocates one thread to each line
			urlIndex = 0;
			
			startTimeMillis = System.currentTimeMillis();
			while(fileScanner.hasNext()){
				newTasks.add(new URLProcessingThread(fileScanner.next(), urlIndex, fileScanner.next()));
				urlIndex++;
			}
			fjThreadPool.invokeAll(newTasks); //starts all tasks, and waits for them to be completed
			
			endTimeMillis = System.currentTimeMillis();
			timeTakenSecs = (endTimeMillis - startTimeMillis) / 1000;
			System.out.println("\n[" + Thread.currentThread().getId() + "] ParFetch secs=" + timeTakenSecs);
			
		}catch(FileNotFoundException e){
			System.out.println("File not found!");
		}
	}
	
	static class URLProcessingThread implements Callable<Void>{
		
		String url;
		int urlIndex;
		String pageNumberIndex;
		InputStream parCurrPageStream;
		
		public URLProcessingThread(String url, int urlIndex, String pageNumberIndex){
			this.url = url;
			this.urlIndex = urlIndex;
			this.pageNumberIndex = pageNumberIndex;
		}
		
		public Void call(){
			parCurrPageStream = establishConnection(url, urlIndex);
			getOutput(parCurrPageStream, pageNumberIndex, urlIndex);
			return null;
		}
	}
	
	public static InputStream establishConnection(String url, int urlIndex){
		try{
			URLConnection urlC = new URL(url).openConnection();
			System.out.println("[" + Thread.currentThread().getId() + "] " + urlIndex + "	:" + url);
			return urlC.getInputStream();
		}catch(MalformedURLException e){
			System.out.println("Invalid URL");
			return null;
		}catch(IOException ioe){
			System.out.println("An IOException occurred: " + ioe.getMessage());
			return null;
		}
	}
	
	public static void getOutput(InputStream input, String reqdIndex, int urlIndex){
		Stack<String> lastLines = new Stack<String>();
		int index = Integer.parseInt(reqdIndex);
		int lastLineIndex = index + 1;
		Scanner pageScanner = null;

		pageScanner = new Scanner(input);
		for(int i = 0; i < index && pageScanner.hasNext(); i++){
			pageScanner.nextLine();
		}
		System.out.println("[" + Thread.currentThread().getId() + "] " + urlIndex+ "." + index + "	:" + pageScanner.nextLine());
		
		while(pageScanner.hasNextLine()){
			lastLines.push(pageScanner.nextLine());
			lastLineIndex++;
		}
		
		System.out.println("[" + Thread.currentThread().getId() + "] " + urlIndex + "." + lastLineIndex + "	:" + lastLines.pop());
	}
	
	public static int getNumberOfLines(Scanner fileScanner){
		int nLines = 0;
		while(fileScanner.hasNext()){
			fileScanner.nextLine();
			nLines++;
		}
		return nLines;
	}
}