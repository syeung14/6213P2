package threading;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;

import edu.gwu.cs6213.p2.DataFileProcesser;

public class ExecutorTest {
	private static final Logger logger = Logger.getLogger(ExecutorTest.class);
	
	private void launcher() {
		
		
		ExecutorService es = Executors.newSingleThreadExecutor();

		Future<Long> result = es.submit(new LoadAndCalc(30));

		try {
			System.out.printf("resutl: %10d ",result.get());
			System.out.println(Runtime.getRuntime().availableProcessors());
			
			System.out.println("");

			for (int i = 0; i < 10; i++) {
				 int a = ThreadLocalRandom.current().nextInt(100,1000);
				 
				 System.out.println(a);
				
			}

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		es.shutdown();
	}
	
	void doForkJoin(){
		ForkJoinPool pool = new ForkJoinPool(2);
		
		RecurAction ra = new RecurAction("input/inputfile.1.txt", 0, 50000);
		pool.invoke(ra);
		
	}

	public static void main(String[] args) {
		new ExecutorTest().doForkJoin();
	}
}
