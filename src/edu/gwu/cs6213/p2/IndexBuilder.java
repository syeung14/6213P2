package edu.gwu.cs6213.p2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class IndexBuilder {
	
	private String fileNamePrefix= "largedata";
	private String fileExt= ".txt";
	
	private static final int BLOCK_SIZE = 400;
	
	public void buildIndex() throws IOException {
		
		File[] files = getAllFiles(".");
		
		BufferedWriter bw =null;
		bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("tmp01.idx.txt")));
		
		for (File file : files) {
			System.out.println("file:"+ file);
			
			readFile(file.getName(), bw);
			
		}
		bw.close();
		
	}
	
	
	private void readFile(String srcFile, BufferedWriter bw) {
		
		BufferedReader br =null;
		try {
			System.out.println("started");
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(srcFile)));
			
			boolean hasMore =false;
			String tmp = "";
			
			long hash=0;
			String[] parts;
			while ((tmp = br.readLine()) != null) {

				int len = tmp.length();
//				for (int i = 0; i < len; i++) {
//					hash= hash*31 + tmp.charAt(i);
//				}
				parts = tmp.split(",");
				hash = hashCode(parts[0]);
				
				bw.write(Long.toString(hash));  
				bw.newLine();
				hash=0;
			}			
			bw.flush();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {br.close();} catch (IOException e) {}
			br = null;
		}
	}
	
	public long hashCode(String name) {
		int hashVal = 0;
		for (int j = 0; j < name.length(); j++) // left to right
		{
			int letter = name.charAt(j) - 0; // get char code
			hashVal = (hashVal * 31 + letter) % 100000; // mod
		}
		return hashVal; // n
	}
	
	private File[] getAllFiles(String path) throws IOException {

		List<File> validFiles = new ArrayList<File>(); 
		File rootDir = new File(path);
		File[] fileList = rootDir.listFiles();
		if (fileList!=null) {

			for (File file : fileList) {
				if (file.isFile()) {
					if (isValidFile(file.getName())) {
						validFiles.add(file);
					} else{
						System.out.println("file is not recognized:" + file.getName());
					}
				}
			}
		}
		return validFiles.toArray(new File[]{});
	}	
	
	private boolean isValidFile (String sFile) {
		if (!sFile.startsWith(fileNamePrefix) || !sFile.endsWith(fileExt) ) {
			return false;
		}
		
		return true;
	}	

	
}
