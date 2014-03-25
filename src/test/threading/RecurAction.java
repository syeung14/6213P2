package threading;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.RecursiveAction;

import org.apache.log4j.Logger;


public class RecurAction extends RecursiveAction {
	private static final Logger logger = Logger.getLogger(RecurAction.class);	
	private long MEMSIZE= 4000;
	
	private long from,to;
	private String file;
	public RecurAction(String file, long from, long to) {
		this.from = from;
		this.to = to;
		this.file= file;
	}

	PriorityQueue<String> tmpList;
	private long readFile(long pointer) {
		tmpList = new PriorityQueue<>(100);/*, new Comparator<String>() {
			
			@Override public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
			
		});*/
        
		int cnt=0;
		long pnt=-1;
		try (RandomAccessFile raf = new RandomAccessFile(file, "r") ){
			
			raf.seek(pointer);
			String tmp;
			while (  (tmp = raf.readLine()) !=null && cnt++ < MEMSIZE ) {
				tmpList.offer(tmp);
				
			}

			pnt = raf.getFilePointer();
			
			for(int i =0;i<1000; i++)
				logger.debug(tmpList.poll());
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pnt;
	}

	@Override
	protected void compute() {
		if ((to - from) <= 4000) {
			readFile(from);
		} else {
			long endPos = from + MEMSIZE;
			RecurAction ta = new RecurAction(file, from, endPos);
			ta.fork();

			RecurAction ta1 = new RecurAction(file, endPos, to);
			ta1.compute();
			
			ta.join();
			System.out.println("done");
			
		}
		return;
	}

}
