/*
* SOD Parallel Processing Demonstration Class
* Author: cekb635
* ID: 2624052
* Date: 12/5/2012
* This class serves as a demonstration of the efficiency of parallel processing, by performing
* a number of computationally intensive tasks first sequentially, and then in parallel,
* and displaying the results.
*/

import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.*;

public class SOD{
	
	public static void main(String args[]){
		
		Scanner fileScanner = null;
		Scanner lineScanner = null;
		long currInt;
		long currLineTotal;
		double startTimeMillis;
		double endTimeMillis;
		double timeTakenSeconds;
		ArrayList<Long> lineTotals = new ArrayList<Long>();
		
		try{
		/*-----------------------------------------------------------------------------------------------------------------------------------------------
		SEQUENTIAL APPROACH
		-----------------------------------------------------------------------------------------------------------------------------------------------*/	
		
			//sets up file reader
			startTimeMillis = System.currentTimeMillis();
			fileScanner = new Scanner(new FileReader(args[0]));
			while(fileScanner.hasNextLine()){ //iterates through the lines in the input file
				currLineTotal = 0;
				lineScanner = new Scanner(fileScanner.nextLine());
				while(lineScanner.hasNext()){ //iterates through a single line, returning the SOD for each value
					currInt = lineScanner.nextInt();
					currLineTotal += returnSOD(currInt);
				}
				lineTotals.add(currLineTotal);
			}
			
			//computes the sum of the sum of all divisors
			for(int i = 0; i < lineTotals.size(); i++){
				lineTotals.set(i, returnSOD(lineTotals.get(i)));
			}
			
			//finally, computes the sum of the sum of the sum of all divisors
			int sum = 0;
			for(int i = 0; i < lineTotals.size(); i++){
				sum += lineTotals.get(i);
			}
			sum = Math.abs(sum); //this line avoids integer wrap-around issues
			endTimeMillis = System.currentTimeMillis();
			timeTakenSeconds = (endTimeMillis - startTimeMillis) / 1000;
			
			//prints output
			System.out.println("[" + Thread.currentThread().getId() + "] Seqsum lines=" + lineTotals.size());
			System.out.println("[" + Thread.currentThread().getId() + "] Seqsum res=" + sum + " secs= " + timeTakenSeconds);
			
			lineScanner.close();
			fileScanner.close();
			
		
		/*------------------------------------------------------------------------------------------------------------------------------------------
		PARALLEL APPROACH
		------------------------------------------------------------------------------------------------------------------------------------------*/
		
		currInt = 0;
		List<Future<Long>> parLineTotals = new ArrayList<Future<Long>>();
		//as above, but here, iterates through the data in the file and, rather than processing it immediately, sets up a number
		//of tasks to be completed by a ForkJoinThreadPool. Also takes a record of each thread's ID as it is used.
		ArrayList<LineProcessorThread> newTasks = new ArrayList<LineProcessorThread>();
		HashSet<Long> threadIds = new HashSet<Long>();
		ForkJoinPool fjThreadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

		startTimeMillis = System.currentTimeMillis();
		fileScanner = new Scanner(new FileReader(args[0]));
		while(fileScanner.hasNextLine()){ //iterates through the lines in the input file
			newTasks.add(new LineProcessorThread(fileScanner.nextLine(), threadIds));
		}
		parLineTotals = fjThreadPool.invokeAll(newTasks);
		
		for(int i = 0; i < parLineTotals.size(); i++){
			lineTotals.set(i, parLineTotals.get(i).get());
		}			
		
		//computes the sum of the sum of all divisors
		for(int i = 0; i < lineTotals.size(); i++){
			lineTotals.set(i, returnSOD(lineTotals.get(i)));
		}
		
		//finally, computes the sum of the sum of the sum of all divisors
		sum = 0;
		for(int i = 0; i < lineTotals.size(); i++){
			sum += lineTotals.get(i);
		}
		sum = Math.abs(sum);//this line avoids integer wrap-around issues
		endTimeMillis = System.currentTimeMillis();
		timeTakenSeconds = (endTimeMillis - startTimeMillis) / 1000;
		
		//returns output
		System.out.println("\n[" + Thread.currentThread().getId() + "] ParSum lines=" + lineTotals.size() + " procs=" + Runtime.getRuntime().availableProcessors());
		System.out.println("[" + Thread.currentThread().getId() + "] ParSum threads=" + threadIds.toString());
		System.out.println("[" + Thread.currentThread().getId() + "] ParSum res=" + sum + " secs= " + timeTakenSeconds);
		
		lineScanner.close();
		fileScanner.close();
	
		}catch(FileNotFoundException e){
			System.out.println("File not found!");
		}catch(InterruptedException f){
			System.out.println("Thread(s) interrupted during execution.");
		}catch(ExecutionException g){
			System.out.println("Error occurred during execution.");
		}
	}
	
	static class LineProcessorThread implements Callable<Long>{
		
		String line;
		HashSet<Long> threadIds;
		
		public LineProcessorThread(String line, HashSet<Long> threadIds){
			this.line = line;
			this.threadIds = threadIds;
		}
		
		public Long call(){
			threadIds.add(Thread.currentThread().getId()); //retains a record of the ID of the thread used for this task
			int currInt = 0;
			int currLineTotal = 0;
			Scanner lineScanner = new Scanner(line);
				while(lineScanner.hasNext()){ //iterates through a single line, returning the SOD for each value
					currInt = lineScanner.nextInt();
					currLineTotal += returnSOD(currInt);
				}
			return new Long(currLineTotal);
		}
		
	}
	
	public static long returnSOD(long input){
		int sum = 1;
		for(int i = 2; i <= (input/2); i++){
			if(input % i == 0){
				sum += i;
			}
		}
		return sum;
	}
}