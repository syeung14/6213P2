import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

import javax.xml.ws.Action;

import edu.gwu.cs6213.p2.BufferedRandomAccessFile;

public class TEST {
	private static void readLine(String fileName){
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));

			LineNumberReader lreader = new LineNumberReader(new FileReader(fileName));
			//			lreader.mark(5000);
			lreader.setLineNumber(5000);//not setting physical pointer

			System.out.println(lreader.readLine());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void byteArray(){
		byte[] salt = {
				(byte) 0x35, (byte)0x46,
				(byte) 0x25, (byte)0xc6,
				(byte) 0x85, (byte)0xa6,
				(byte) 0xe5, (byte)0x16 };
	}

	private static void randomChannel() {
		String s = "I was here!\n";
		byte data[] = s.getBytes();
		ByteBuffer out = ByteBuffer.wrap(data);

		ByteBuffer copy = ByteBuffer.allocate(12);

		try (FileChannel fc = (FileChannel.open(null,null,null))) {
			// Read the first 12
			// bytes of the file.
			int nread;
			do {
				nread = fc.read(copy);
			} while (nread != -1 && copy.hasRemaining());

			// Write "I was here!" at the beginning of the file.
			fc.position(0);
			while (out.hasRemaining())
				fc.write(out);
			out.rewind();

			// Move to the end of the file.  Copy the first 12 bytes to
			// the end of the file.  Then write "I was here!" again.
			long length = fc.size();
			fc.position(length-1);
			copy.flip();
			while (copy.hasRemaining())
				fc.write(copy);
			while (out.hasRemaining())
				fc.write(out);
		} catch (IOException x) {
			System.out.println("I/O Exception: " + x);
		}		
	}

	private static void testParse(){
		String[] data = "".split(" ");

		System.out.println(data.length);
		for (String st : data) {
			System.out.println(st);
		}
	}

	private static void testQueue() {
		PriorityQueue<String> tmpList;
		tmpList = new PriorityQueue<>(1, 

				new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}

		});

		tmpList.offer("Ach Issauth,127-718-3234");
		tmpList.offer("Beib Jttitt,234-166-4994");
		tmpList.offer("Bech Oig,482-651-2141");
		tmpList.offer("25-9240");
		tmpList.offer("Aw Iowoh,329-171-2334");

		for (String string : tmpList) {
			System.out.println(string);
		}
		System.out.println("");
		while(tmpList.size()>0)
			System.out.println(tmpList.poll());

		String names[] = {"Ach","Beib","Bech","25-9240","Aw"};

		Collections.sort(Arrays.asList(names),defaultcomparator);
		System.out.println("");
		/*for (String n : names) {
			System.out.println(n);
		}*/

	}
	public static Comparator<String> defaultcomparator = new Comparator<String>() {
		@Override
		public int compare(String r1, String r2) {
			return r1.compareTo(r2);
		}
	};

	private static void testArray() {
		Object da[][] = new Object[6][8]; 
		
		String data[] = {"Ach","Beib","Bech","25-9240","Aw"};

		String sub[] = new String[3];
		System.arraycopy(data, 1, sub, 0, 3);

		for (String s : sub) {
			System.out.println(s);
		}

	}
	private static void testCollection() {

		try (BufferedRandomAccessFile raf = new BufferedRandomAccessFile(
				"input/inputfile.01.txt", "r");) {

			List<String>data =new ArrayList<>(5);

			String tmp;
			int cnt =0;
			while ( (tmp=raf.getNextLine())!=null  )  {
				if (++cnt >20) {
					break;
				}
				data.add(tmp);
			}

			System.out.println(data);

			cnt=0;
			Stack<String> st = new Stack<>();
			while ( (tmp=raf.getNextLine())!=null  )  {
				if (++cnt >20) {
					break;
				}
				st.push(tmp);
			}

			cnt=0;
			Queue<String> qu = new PriorityBlockingQueue<>(1, defaultcomparator);
			while ( (tmp=raf.getNextLine())!=null  )  {
				if (++cnt >20) {
					break;
				}
				qu.offer(tmp);
			}

			int v =5;
			for (int i =0; i< v;i++) {

				String a = qu.peek();
				System.out.println(a);
				qu.remove();

			}
			while(qu.size()>0)
				System.out.println(qu.poll());


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static PriorityBlockingQueue<String> qu = new PriorityBlockingQueue<>(1, defaultcomparator);
	static ArrayBlockingQueue<String> ioData = new ArrayBlockingQueue<>(1);
	static LinkedBlockingDeque<String> lq = new LinkedBlockingDeque<>(1);
	private static void doTestBlockingColl() {


		Runnable a =new Runnable() {

			@Override
			public void run() {
				try (BufferedRandomAccessFile raf = new BufferedRandomAccessFile(
						"input/inputfile.01.txt", "r");) {

					while (true) {

						Thread.sleep(2000);
						String t = raf.getNextLine();

						qu.put(t);
						System.out.println(Thread.currentThread().getName() + " offered " + t);
					}

				} catch (Exception e) {

					e.printStackTrace();
				}


			}
		};
		Runnable b =new Runnable() {

			@Override
			public void run() {
				while(true){
					try {
						System.out.println("going to take");
						String tm = qu.take();

						System.out.println(Thread.currentThread().getName() + "taken   " + tm);
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};

		new Thread(a).start();
		new Thread(b).start();

	}

	@Action
	static void testCharacter() {
		char ch = 'a';

		char uniChar = '\u039A';

		Character cha = new Character('a');
		System.out.println(ch);
		System.out.println(uniChar);

	}

	
	private static double[][]matrix = {{1},{4},
										{3},{6}};
	private static double[][] square = {
            {8, 1, 6},
            {3, 5, 7},
            {4, 9, 2}
        };
	
	/**
	 * J:\development_kit_2013\Portable Offline Browser\Download\www.seas.gwu.edu\~simhaweb\cs133\lectures\module2\module2.html
	 */
	private static double[][] A = {
	            {1},
	            {2, 1},
	            {3, 2, 1},
	            {4, 3, 2, 1},
	            {5, 4, 3, 2, 1}
	        };

	/** Return the sum of the elements of matrix. */
	public static double testSum(double[][] matrix) {       // once
		double result = 0;                                // once
		for (int i = 0; i < matrix.length; i++) {         // n + 1 times
			for (int j = 0; j < matrix[i].length; j++) {    // n(n + 1) times
				result += matrix[i][j];                       // n * n times
				System.out.print(matrix[i][j] +" ");
			}
			System.out.println("");
		}
		return result;                                    // once
	}


	public static void main(String[] args) {
		try {
			//TEST.initializeSegments(10, "largedata.dat", "sorting.txt");
			TEST.testArray();

			//			TEST.searchInBlock("400K_sorted.txt",2759734,2867194);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}