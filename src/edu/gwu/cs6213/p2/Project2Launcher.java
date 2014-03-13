package edu.gwu.cs6213.p2;

public class Project2Launcher {

	
	private void loadFile(String srcFile,String tmpOutFile) {
		DataFileSorter fReader = new DataFileSorter();
		
		fReader.initRun(srcFile, tmpOutFile);
		
	}
	
	public static void main(String[] args) {
		String fileName = "largedata2.txt";
		String tmpOutFile = "tmp01.tmp.txt";
		
		Project2Launcher launcher = new Project2Launcher();
		launcher.loadFile(fileName, tmpOutFile);
		
		
	}

}
