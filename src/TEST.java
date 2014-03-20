import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.gwu.cs6213.p2.MemoryByteBuffer;

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


	/** Sort original file into sorted segments */
	private static int initializeSegments(int segmentSize, String originalFile,
			String f1) throws Exception {

		int[] list = new int[segmentSize];
		DataInputStream input = new DataInputStream(new BufferedInputStream(
				new FileInputStream(originalFile)));
		DataOutputStream output = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(f1)));


		int numberOfSegments = 0;
		while (input.available() > 0) {
			numberOfSegments++;
			int i = 0;
			for (; input.available() > 0 && i < segmentSize; i++) {
				list[i] = input.readInt();
			}

			// Sort an array list[0..i-1]
			java.util.Arrays.sort(list, 0, i);

			// Write the array to f1.dat
			for (int j = 0; j < i; j++) {
				output.writeInt(list[j]);
			}
		}

		input.close();
		output.close();

		return numberOfSegments;
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

	private void method() {
		try (BufferedReader inputReader = Files.newBufferedReader(
				Paths.get(new URI ("file:///C:/home/docs/users.txt")), Charset.defaultCharset());
				BufferedWriter outputWriter = Files.newBufferedWriter(
						Paths.get(new URI("file:///C:/home/docs/users.bak")), Charset.defaultCharset())
				) {

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("")));


			String inputLine;
			while ((inputLine = inputReader.readLine()) != null) {
				outputWriter.write(inputLine);
				outputWriter.newLine();
			}
			System.out.println("Copy complete!");

		} catch (URISyntaxException | IOException ex) {
			ex.printStackTrace();
		}

	}

	private static void loadRandom(String originalFile, int line) {

		try (RandomAccessFile raf = new RandomAccessFile(originalFile, "r");
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream("ind.400k.txt"))  )
				) {

			double startTime = System.currentTimeMillis();
			if (line >=0) {
				raf.seek(line);
			
				startTime = System.currentTimeMillis();
				for (int i = 0; i < 1; i++) {
					String tmp = raf.readLine();
					if (tmp.startsWith("Kliol Iesh")) {
						System.out.println("found: " + tmp);
						break;
					}
					System.out.println(tmp);
				}
				double endTime = System.currentTimeMillis();
				System.out.println("search created took:" + (endTime-startTime)/1000);
				
			}/* else
				for (int i = 0; i < 1000; i++) {
					System.out.println(raf.getFilePointer());
					System.out.println(raf.readLine());
				}*/

/*			startTime = System.currentTimeMillis();
			System.out.println("Started to get the anchor values.");
			String tmp,name[];
			long pter = raf.getFilePointer();;
			int cnt=0;
			while (  (tmp = raf.readLine()) != null)  {
				
				if (cnt++ % 4000 == 0) {
					name = tmp.split(",");
					bw.write(name[0]+ "," +pter);
					bw.newLine();
				}
				pter = raf.getFilePointer();
			}
			double endTime = System.currentTimeMillis();
			System.out.println("Index created took:" + (endTime-startTime)/1000);*/
			
		} catch (IOException e) {
			System.out.println("here");
			e.printStackTrace();
		}
	}


	
	private static class Entry{
		private String name;
		private long byteLocation;
		private Entry(String name, long byteLocation) {
			this.name = name;
			this.byteLocation = byteLocation;
		}
		@Override
		public String toString() {
			return name +"-"+ byteLocation;
		}
	}
	
	private static List<Entry>idxData = new ArrayList<>();
	private static void loadIndex(String indexFile) {
		try (RandomAccessFile idxReader = new RandomAccessFile(indexFile, "r");
				) {
			
			String tmp;
			String[]data;
			while (  (tmp = idxReader.readLine()) != null)  {
				data = tmp.split(",");
				idxData.add(new Entry(data[0],Long.parseLong(data[1]) ));
			}

			System.out.println(idxData.size());
		
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private static void searchFile(String dataFile, String name, long byteLocation,int blockSize) {
		double startTime;
		double endTime;
		try (RandomAccessFile raf = new RandomAccessFile(dataFile, "r");) {
			startTime = System.currentTimeMillis();
			
			if (byteLocation >= 0) {
				raf.seek(byteLocation);
				String tmp;
				String data[] ={"",""};
				boolean found = false;
				int cnt = 0;
				while (  (tmp = raf.readLine()) != null && cnt++ < blockSize)  {
					data = tmp.split(",");
					if (name.compareTo(data[0]) == 0) {
						found = true;
						break;
					}
				}

				if (found) {
					System.out.println("Phone entry found; "+data[1]);
				} else{
					System.out.println("Phone entry for "+name+" not found " );	
				}
			}
			endTime = System.currentTimeMillis();
			System.out.println("search took:" + (endTime-startTime)/1000);			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	enum Result{SMALLER,BIGGER,FOUND,NOTFOUND};
	private static void search(String dataFile, String key) {

		int idxsize = idxData.size()-1;
		if (idxsize <=0) {
			System.out.println("index file is not loaded.");
			return;
		}
		// binary search for the anchor index
		double startTime = System.currentTimeMillis();
		Entry anchor= null;
		Result result = null; 
		int low = 0, high = idxsize;
		int mid = 0;
		while (high >= low) {
			mid = (low + high) / 2;
			anchor = idxData.get(mid);
			
			if (key.compareTo(anchor.name) > 0) {
				low = mid + 1;
				result = Result.BIGGER;
			} else if (key.compareTo(anchor.name) == 0) {
				result = Result.FOUND;
				break;
			} else {
				high = mid - 1;
				if (mid == 0) {
					result = Result.NOTFOUND;
				} else 
					result = Result.SMALLER;
			}
		} 
		double endTime = System.currentTimeMillis();
		System.out.println("search anchor took:" + (endTime-startTime)/1000 +":" + anchor);
		System.out.println("record in:" + (result==Result.BIGGER? "bigger than "+ mid: "smaller than "+mid) );
		
		Entry endAnchor=null;
		long startPos = 0, endPos = 0;
		if (result == Result.BIGGER || result == Result.FOUND) {
			if (mid < idxsize) {
				mid++;
				endAnchor = idxData.get(mid);
				endPos = endAnchor.byteLocation;
			} else {
				endPos = new File(dataFile).length();
			}
			
			startPos = anchor.byteLocation;
			
		} else if (result == Result.SMALLER) {
			int b4Mid = mid;
			if (b4Mid > 0) {
				b4Mid--;
			}
			anchor = idxData.get(b4Mid);
			endAnchor = idxData.get(mid);
			
			startPos = anchor.byteLocation;
			endPos = endAnchor.byteLocation;
		} else if (result== Result.NOTFOUND) {
			System.out.println("name is not in the phone book");
		}
		if (endPos>0) {
			searchInBlock(dataFile, startPos, endPos, key);
		} else {
			System.out.println("search is not performed.");
		}
	}

	public static void main(String[] args) {
		try {
			//TEST.initializeSegments(10, "largedata.dat", "sorting.txt");
//			TEST.loadRandom("400K_sorted.txt", 0);
			
//			TEST.loadIndex("ind.400k.idx1.txt");
//			TEST.search("400K_sorted.txt" ,"@Ab Ab");

//			TEST.bufferedArray("400K_sorted.txt");
			
//			TEST.buildIndex("4m_datafile.sorted.txt", "4m_datafile.idx.txt", 4000);
			
			TEST.loadIndex("4m_datafile.idx.txt");
			TEST.search("4m_datafile.sorted.txt" ,"Autev Fauzynt");
			
//			TEST.searchInBlock("400K_sorted.txt",2759734,2867194);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	
	
	private static void searchInBlock(String dataFile, long startPos, long endPos, String key) {
		
		try (RandomAccessFile raf = new RandomAccessFile(dataFile, "r");){
			byte []block = new byte[ (int)(endPos-startPos) ];

			raf.seek(startPos);
			raf.read(block);
			
			MemoryByteBuffer bb = new MemoryByteBuffer(block);
			
			String tmp= "";
			String []data={"",""};
			int cnt=0;
			boolean found =false;
			while ((tmp = bb.getNextLine()) != null) {
				data = tmp.split(",");
				cnt++;
				
				if (key.compareTo(data[0]) == 0) {
					found = true;
					break;
				}
			}
			tmp = found?" record found " + data[1]:" record not found";
			System.out.println("total line:"+cnt+" :"+tmp);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}