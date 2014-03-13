package edu.gwu.cs6213.p2;

import java.io.IOException;

public class Project2Launcher {

	
	private void loadFile(String srcFile,String tmpOutFile) throws IOException {
		DataFileSorter fReader = new DataFileSorter();
		fReader.initRun(srcFile, tmpOutFile);

//		IndexBuilder build = new IndexBuilder();
//		build.buildIndex();
		
	}
	
	public static void main(String[] args) {
		String fileName = "largedata0.txt";
		String tmpOutFile = "tmp01.tmp.txt";
		
		Project2Launcher launcher = new Project2Launcher();
		try {
			launcher.loadFile(fileName, tmpOutFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
