package edu.gwu.cs6213.p2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.gwu.cs6213.p2.util.PropertiesLoader;
import edu.gwu.cs6213.p2.util.PropertiesParser;

public class DataFileSorter {

	private int default_memSize = 4000;
	private PropertiesParser prop;
	
	public DataFileSorter() {
		this.prop = PropertiesLoader.getPropertyInstance();
		this.default_memSize = prop.getIntProperty("memory.size", 4000);

	}
	
	private boolean initialSort(BufferedRandomAccessFile br , BufferedWriter bw ) throws IOException {
		String[] entries = new String[default_memSize];
		String tmp = "";
		int i = 0;
		while (i < default_memSize && (tmp = br.getNextLine()) != null) {
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

	
	public void sort(String srcFile, String outFile, File tmpFolder) throws IOException {
		
		File tmp = File.createTempFile("proj2", null, tmpFolder );
		tmp.deleteOnExit();
		
		try (BufferedRandomAccessFile raf = new BufferedRandomAccessFile(srcFile, "r");) {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		System.out.println(tmp.getName());
		System.out.println(tmp.getAbsolutePath());
		long freemem = Runtime.getRuntime().freeMemory();
		System.out.println(freemem/(1024*1024));
		
		if (tmp.exists()) tmp.delete();
	}
	
	public static void main(String[] args) {
		
		String src = "input/inputfile.118.txt";
		String outFile = "processed/inputfile.118.sorted.txt";
		
		try {
			new DataFileSorter().sort(src, outFile, new File("processed"));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
