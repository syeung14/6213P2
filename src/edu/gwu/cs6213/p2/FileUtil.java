package edu.gwu.cs6213.p2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	
	public static void main(String[] args) {
		System.out.println(FileUtil.getFileNameNoExt("this porar.txt","r.txt"));
	}
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
}
