package edu.gwu.cs6213.p2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class DataFileSorter {

	private static int MEM_SIZE = 40000;
	public DataFileSorter() {
	}	
	
	public void sort() {
		
		
	}
	
	public void initRun(String srcFile,String tmpOutFile) {
		if (srcFile ==null || "".equals(srcFile)  ||
				tmpOutFile ==null || "".equals(tmpOutFile)) {
			throw new IllegalArgumentException("file name is not set.");
		}
		
		BufferedReader br =null;
		BufferedWriter bw = null;
		try {
			System.out.println("started");
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(srcFile)));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(tmpOutFile)));

			boolean hasMore =false;
			int runCnt = 0;
			do {
				hasMore = readAndSort(br, bw); 
				if (hasMore) runCnt++;
			} while (hasMore);
				
			System.out.println("done. Run: " + runCnt);
			bw.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {br.close();bw.close();} catch (IOException e) {}
			br = null;
		}
	}
	
	private boolean readAndSort(BufferedReader br , BufferedWriter bw ) throws IOException {
		double startTime = System.currentTimeMillis();
		String[] entries = new String[MEM_SIZE];
		String tmp = "";
		int i = 0;
		while (i < MEM_SIZE && (tmp = br.readLine()) != null) {
			entries[i] = tmp;
			i++;
		} //

		Arrays.sort(entries, 0, i);
		
		System.out.println("found:"+Arrays.binarySearch(entries,0,i, "Air Assuz,135-437-5730") );
		
		for (String e : entries) {
			if (e != null) {

				bw.write(e);
				bw.newLine();
			}
		}
		double endTime = System.currentTimeMillis();
		System.out.println("Sort took:" + (endTime-startTime)/1000);
		if (entries[0]!=null) {
			return true; //may have more data
		}
		return false;  //read nothing, eof
	}
	
}
