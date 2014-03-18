package edu.gwu.cs6213.p2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class DataFileSorter {

	private static int MEM_SIZE = 4000;
	public DataFileSorter() {
	}	
	
	public void sort() {
		
	}
	
	public void sortFileContent(String srcFile,String tmpOutFile) {
		if (srcFile ==null || "".equals(srcFile)  ||
				tmpOutFile ==null || "".equals(tmpOutFile)) {

			throw new IllegalArgumentException("file name is not set.");
		}
		
		BufferedReader br =null;
		BufferedWriter bw = null;
		int runCnt = 0;
		try {
			System.out.println("started");
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(srcFile)));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(tmpOutFile)));

			boolean hasMore =false;
			do {
				hasMore = readAndSort(br, bw); 
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
		mergeSort(runCnt, MEM_SIZE, tmpOutFile, "tmpHalf.txt", "sorting.txt");
		double endTime = System.currentTimeMillis();
		
		System.out.println("Merge Sort took:" + (endTime-startTime)/1000);

		
	}
	

	
	private boolean readAndSort(BufferedReader br , BufferedWriter bw ) throws IOException {
		String[] entries = new String[MEM_SIZE];
		String tmp = "";
		int i = 0;
		while (i < MEM_SIZE && (tmp = br.readLine()) != null) {
			entries[i] = tmp;
			i++;
		} //

		Arrays.sort(entries, 0, i);  //TODO internal sort could be implemented 
		
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
	
	private void mergeSort(int run, int memSize, String partialSort, String tmpHalf, String moreSorted) {
		if (run > 1) {
			mergeFile(run, memSize, partialSort, tmpHalf, moreSorted);
			mergeSort((run + 1) / 2, memSize * 2, moreSorted, partialSort, tmpHalf);
		} else {
			
			System.out.println("done");
		}
		
	}
	
	private void mergeFile(int run,int memSize, String partialSort, String tmpHalf, String moreSorted) {
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(partialSort)));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tmpHalf))) ) {
			
			//
			for (int i = 0; i < (run / 2) * memSize; i++) {
				bw.write(br.readLine());
				bw.newLine();
			}
			bw.flush();
			
			try (BufferedReader src2 = new BufferedReader(new InputStreamReader(
					new FileInputStream(tmpHalf)));
					BufferedWriter tar3 = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(moreSorted))) ) {
				
				mergeRuns(run / 2, memSize, br, src2, tar3);
				
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
}
