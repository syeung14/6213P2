package edu.gwu.cs6213.p2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataFileProcesser {

	private int default_memSize = 4000;
	private String srcFileName;
	private String sortedFileName;
	private String indexFileName;

	
	public DataFileProcesser(String srcFileName,
			String sortedFileName, String indexFileName) {
		this.srcFileName = srcFileName;
		this.sortedFileName = sortedFileName;
		this.indexFileName = indexFileName;
	}

	public void buildIndex() {
		try (RandomAccessFile raf = new RandomAccessFile(sortedFileName, "r");
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(indexFileName))  )
				) {

			double startTime = System.currentTimeMillis();
			System.out.println("Started to get the anchor values.");
			String tmp,name[];
			long pter = raf.getFilePointer();;
			int cnt = 0;
			while (  (tmp = raf.readLine()) != null)  {

				if ( cnt++ % default_memSize == 0) {
					name = tmp.split(",");
					bw.write(name[0]+ "," +pter);
					bw.newLine();
				}
				pter = raf.getFilePointer();
			}
			double endTime = System.currentTimeMillis();
			System.out.println("Index created took:" + (endTime-startTime)/1000);
			
		} catch (IOException e) {
			System.out.println("here");
			e.printStackTrace();
		}		
	}	

	public void sortFileContent() {
		
		if (srcFileName ==null || "".equals(srcFileName)  ||
				sortedFileName ==null || "".equals(sortedFileName)) {

			throw new IllegalArgumentException("file name is not set.");
		}
		String tmp01 ="tmp01.tmp",tmp02 ="tmp02.tmp", tmp03="tmp03.tmp";
		
		BufferedReader br =null;
		BufferedWriter bw = null;
		int runCnt = 0;
		try {
			System.out.println("started");
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(srcFileName)));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(tmp01)));

			boolean hasMore =false;
			do {
				hasMore = initialSort(br, bw); 
				if (hasMore) runCnt++;
			} while (hasMore);
				
			System.out.println("initial done. Run: " + runCnt);
			bw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {br.close();bw.close();} catch (IOException e) {}
			br = null;
		}
		
		double startTime = System.currentTimeMillis();

		System.out.println("Started external merge sort.");
		mergeSort(runCnt, default_memSize, tmp01, tmp02, tmp03, sortedFileName);
		
		try {
			new File(tmp01).delete();
			new File(tmp02).delete();
			new File(tmp03).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double endTime = System.currentTimeMillis();
		System.out.println("Merge Sort took:" + (endTime - startTime) / 1000);
		
	}
	
	private boolean initialSort(BufferedReader br , BufferedWriter bw ) throws IOException {
		String[] entries = new String[default_memSize];
		String tmp = "";
		int i = 0;
		while (i < default_memSize && (tmp = br.readLine()) != null) {
			entries[i] = tmp;
			i++;
		} //

		Arrays.sort(entries, 0, i);  //TODO internal sort can be implemented 
		
		for (String e : entries) {
			if (e != null) {

				bw.write(e);
				bw.newLine();
			}
		}
		if (entries[0]!=null) {
			return true; //may have more data
		}
		return false;  //read nothing, eof
	}
	
	private void mergeSort(int run, int memSize, String tmpSort, String tmpHalf, String tmp03
			, String sorted) {
		if (run > 1) {
			mergeFile(run, memSize, tmpSort, tmpHalf, tmp03);
			mergeSort((run + 1) / 2, memSize * 2, tmp03, tmpSort, tmpHalf, sorted);
		} else {
			File sortedFile = new File(sorted);
			if (sortedFile.exists()) sortedFile.delete();
			new File(tmpSort).renameTo(sortedFile);
			System.out.println("done");
		}
	}
	
	private void mergeFile(int run,int memSize, String partialSort, String tmpHalf, String moreSorted) {
		
		try (BufferedReader src1 = new BufferedReader(new InputStreamReader(
				new FileInputStream(partialSort)));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tmpHalf))) ) {
			
			//
			for (int i = 0; i < (run / 2) * memSize; i++) {
				bw.write(src1.readLine());
				bw.newLine();
			}
			bw.flush();
			
			try (BufferedReader src2 = new BufferedReader(new InputStreamReader(
					new FileInputStream(tmpHalf)));
					BufferedWriter target = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(moreSorted))) ) {
				
				mergeRuns(run / 2, memSize, src1, src2, target);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void mergeRuns(int run,int memSize, BufferedReader partialSort,
			BufferedReader tmpHaf, BufferedWriter moreSorted)
			throws IOException {
		
		for (int i = 0; i < run; i++) {
			mergeSortRuns(memSize, partialSort, tmpHaf, moreSorted);
		}
		String tmp="";
		while ((tmp = partialSort.readLine()) != null) {
			moreSorted.write(tmp);
			moreSorted.newLine();
		}
		moreSorted.flush();
	}


	/**
	 * Merge particalSort and tmpHalf to moreSorted
	 * 
	 * @param partialSort
	 * @param tmpHalf
	 * @param moreSorted
	 * @throws IOException
	 */
	private void mergeSortRuns(int memSize,BufferedReader partialSort,
			BufferedReader tmpHalf, BufferedWriter moreSorted)
			throws IOException {
		String tmpStr1 = partialSort.readLine();  
		String tmpStr2 = tmpHalf.readLine();
		int cnt1 = 1;
		int cnt2 = 1;

		while (true) {
			if (tmpStr1.compareTo(tmpStr2) < 0) {
				moreSorted.write(tmpStr1);
				moreSorted.newLine();
				
				if (cnt1++ >= memSize) {
					if (tmpStr2 != null) {
						moreSorted.write(tmpStr2);
						moreSorted.newLine();
					} 
					break;
				} else {
					tmpStr1 = partialSort.readLine(); //could be null
					if (tmpStr1 == null) {
						if (tmpStr2 != null) {
							moreSorted.write(tmpStr2);
							moreSorted.newLine();
						}
						break;
					} //
				} //
			} else {
				moreSorted.write(tmpStr2);
				moreSorted.newLine();

				if (cnt2++ >= memSize) {
					if (tmpStr1 != null) {
						moreSorted.write(tmpStr1);
						moreSorted.newLine();
					}
					break;
				} else {
					tmpStr2 = tmpHalf.readLine();
					if (tmpStr2 == null) {
						if (tmpStr1 != null) {
							moreSorted.write(tmpStr1);
							moreSorted.newLine();
						}
						break;
					} //
				} //
			} //
		}//
		moreSorted.flush();

		// write the rest into target sorted file directly
		while (cnt1++ < memSize && tmpStr1 != null) {
			tmpStr1 = partialSort.readLine();
			if (tmpStr1 != null) {
				moreSorted.write(tmpStr1);
				moreSorted.newLine();
			}
		}
		moreSorted.flush();

		while (cnt2++ < memSize && tmpStr2 != null) {
			tmpStr2 = tmpHalf.readLine();
			if (tmpStr2 != null) {
				moreSorted.write(tmpStr2);
				moreSorted.newLine();
			}
		}
		moreSorted.flush();
	} 
	
	
	
	/*****************************************************************************/
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
	private List<Entry>idxData;
	private void loadIndex(String indexFile) {
		try (RandomAccessFile idxReader = new RandomAccessFile(indexFile, "r"); ) {
			
			idxData = new ArrayList<>();
			
			String tmp;
			String[]data;
			while (  (tmp = idxReader.readLine()) != null)  {
				data = tmp.split(",");
				idxData.add(new Entry(data[0],Long.parseLong(data[1]) ));
			}

			System.out.println(idxData.size());
		
		} catch (IOException e) {
			System.out.println("ERROR:index file is missing. \n" + e.getMessage());
		}		
	}
	
	private void searchInBlock(String dataFile, long startPos, long endPos, String key) {
		
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
	

	enum Result{SMALLER,BIGGER,FOUND,NOTFOUND};
	public void search(String key) {
		
		loadIndex(indexFileName);
		if (idxData==null) {
			return;
		}

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
				endPos = new File(sortedFileName).length();
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
			searchInBlock(sortedFileName, startPos, endPos, key);
		} else {
			System.out.println("search is not performed.");
		}
	}
	
	
	
	
	
}
