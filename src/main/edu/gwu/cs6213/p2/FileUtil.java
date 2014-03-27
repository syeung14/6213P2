package edu.gwu.cs6213.p2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.gwu.cs6213.p2.util.ExternalSort;
import edu.gwu.cs6213.p2.util.NameGenerator;

public class FileUtil {

	
	public static String getFileNameNoExt(String fileName, String suffix){
		
		if (fileName==null ||"".equals(fileName) || !fileName.endsWith(suffix) ) {
			return fileName;
		}
		
		int dotIndex = fileName.lastIndexOf(suffix);
		return fileName.substring(0, dotIndex);
		
	}
	public static File[] getAllFiles(String path, String prefix, String ext)
			throws IOException {

		List<File> validFiles = new ArrayList<File>();
		File rootDir = new File(path);
		File[] fileList = rootDir.listFiles();
		if (fileList != null) {
			for (File file : fileList) {
				if (file.isFile()) {
					if (isValidFile(file.getName(), prefix, ext)) {
						validFiles.add(file);
					} else {
//						System.out.println("file is not recognized:" + file.getName());
					}
				}
			}
		}
		return validFiles.toArray(new File[] {});
	}

	private static boolean isValidFile(String sFile, String prefix, String ext) {
		if (!sFile.startsWith(prefix) || !sFile.endsWith(ext)) {
			return false;
		}
		return true;
	}
	
	
	
	public static void otherExternalSort() {
		ExternalSort sort = new ExternalSort();
		try {
			sort.sort(new File(""),new File(""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
//		9 input/inputfile.107.txt#35000000
		
		String fileName = "input/inputfile.";

		for(int i = 107; i< 121; i++) {
			phoneBookGen(fileName + i+".txt", 35000000);
		}
	}
	public static void phoneBookGen(String fileName, int size) {
		
		try (BufferedWriter osw= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));) {

			String entry = "";
			NameGenerator ngen = new NameGenerator();
			for (int i = 0; i< size; i++) {
				entry = ngen.getName()+ " " + ngen.getName()  + "," +
						Integer.toString(100+(int)(new Random().nextInt(400)))+ "-"+
						Integer.toString(100+(int)(new Random().nextInt(900)) )+ "-"+  
						Integer.toString(1000+(int)(new Random().nextInt(9000)));

				osw.write(entry);
				osw.newLine(); //cost 2 bytes
			}
			osw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
