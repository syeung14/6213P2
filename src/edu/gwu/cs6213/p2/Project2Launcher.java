package edu.gwu.cs6213.p2;

import java.io.IOException;

import edu.gwu.cs6213.p2.util.PropertiesLoader;
import edu.gwu.cs6213.p2.util.PropertiesParser;

public class Project2Launcher {

	
	DataFileProcesser fProcessor;
	
	private Project2Launcher(String srcFile,String sortFile) {
		
		PropertiesParser prop = PropertiesLoader.getPropertyInstance();
		
		fProcessor = new DataFileProcesser(srcFile, sortFile, sortFile +".idx");
	}

	private void loadFile() throws IOException {
		fProcessor.sortFileContent();
		fProcessor.buildIndex();
		
	}
	
	private void search(String key){
		
		fProcessor.search(key);
		
	}
	
	public static void main(String[] args) {
		String fileName = "largedata0.txt";
		String tmpOutFile = "largedata0.sorted.txt";
		
		Project2Launcher launcher = new Project2Launcher(fileName, tmpOutFile);
		try {
//			launcher.loadFile();
			launcher.search("Cuthech Grjithiar");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} //
}
