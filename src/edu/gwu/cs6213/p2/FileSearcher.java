package edu.gwu.cs6213.p2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.gwu.cs6213.p2.util.PropertiesLoader;
import edu.gwu.cs6213.p2.util.PropertiesParser;


public class FileSearcher {
	private static final Logger logger = Logger.getLogger(FileSearcher.class);
	
	private PropertiesParser prop;
	private String processedFolder;

	private String inputFolder;
	private String inputFilePrefix;
	private String inputFileExt;

	
	private File[] inputFiles;
	
	public FileSearcher() {
		this.prop = PropertiesLoader.getPropertyInstance();
		this.inputFolder = prop.getStringProperty("file.sourcefolder");
		this.processedFolder = prop.getStringProperty("file.processedfolder");
		this.inputFilePrefix =  prop.getStringProperty("file.input.prefix");	
		this.inputFileExt =  prop.getStringProperty("file.input.ext");

		try {
			inputFiles = FileUtil.getAllFiles(inputFolder, inputFilePrefix, inputFileExt);

//			sortedFiles = FileUtil.getAllFiles(processedFolder, inputFilePrefix, sortFileSuffix);
//			for (File f: sortedFiles) {
//				System.out.println(f);
//			}
			
//			indexFiles = FileUtil.getAllFiles(processedFolder, inputFilePrefix, indFileSuffix);
//			for (File f: indexFiles) {
//				System.out.println(f);
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SearchResult search(String key) {
		if (inputFiles== null || inputFiles.length==0) {
			System.out.println("there is no input files.");
			return null;
		}
		String indFileSuffix = "." + Constants.INDEX + "." + inputFileExt;
		String sortFileSuffix = "." + Constants.SORTED + "." + inputFileExt;
		String sFileN, s1stPartN, sortedF, indexF;
		SearchResult result = null;
		logger.debug("============In Search method "+key+"=======================");
		
		for (File sf : inputFiles) {
			sFileN = sf.getName();
			s1stPartN = FileUtil.getFileNameNoExt(sFileN, ".txt");
			sortedF = processedFolder + "/" + s1stPartN + sortFileSuffix;
			indexF = processedFolder + "/" + s1stPartN + indFileSuffix;
			
			if (!(new File(sortedF).isFile()) || !(new File(indexF).isFile())) {
				System.out.println("pre-processed files not found for " + sFileN);
			} else {
				double startTime = System.currentTimeMillis();
				result = search(sortedF, indexF, key);
				logger.debug("=====================================");
				double endTime = System.currentTimeMillis();
//				System.out.println("Search For search method for "+key +" took "+ (endTime-startTime) +" ms : " + result);
			}
			if (result != null) {
				return result;
			}
		}
		

		
		return result;
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
			return name +":"+ byteLocation;
		}
	}
	private List<Entry>idxData;
	private void loadIndex(String indexFile) {
		try (RandomAccessFile idxReader = new RandomAccessFile(indexFile, "r"); ) {
			
			idxData = new ArrayList<>();
			
			String tmp;
			String[] data;
			while (  (tmp = idxReader.readLine()) != null)  {
				data = tmp.split(",");
				idxData.add(new Entry(data[0],Long.parseLong(data[1]) ));
			}

			logger.debug("total records in index " + indexFile +":"+idxData.size());
		
		} catch (IOException e) {
			logger.error("ERROR:index file is missing. \n", e);
		}		
	}
	
	private SearchResult searchInBlock(String dataFile, long startPos, long endPos, String key) {
		
		SearchResult result = null;
		try (RandomAccessFile raf = new RandomAccessFile(dataFile, "r");){
			byte []block = new byte[ (int)(endPos-startPos) ];

			raf.seek(startPos);
			raf.read(block);
			
			MemoryByteBuffer bb = new MemoryByteBuffer(block);
			
			String tmp= "";
			String []data={"",""};
			int cnt=0;
			boolean found = false;
			while ((tmp = bb.getNextLine()) != null) {
				data = tmp.split(",");
				cnt++;
				
				if (key.compareTo(data[0]) == 0) {
					result = new SearchResult(dataFile, tmp);
					found = true;
					break;
				}
			}
			tmp = found?" record found " + data[1]:" record not found";
			logger.debug("total line loaded :"+cnt+" :"+tmp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}	

	enum SearchState{SMALLER,BIGGER,FOUND,NOTFOUND};
	private SearchResult search(String dataFileName, String indexFileName, String key) {
		SearchResult result =null;
		
		loadIndex(indexFileName);
		if (idxData == null || (idxData.size() - 1) <= 0) {
			System.out.println("index file is not loaded: " + indexFileName);
			return result;
		}

		int idxsize = idxData.size() - 1;
		// binary search for the anchor index
		double startTime = System.currentTimeMillis();
		Entry anchor= null;
		SearchState state = null; 
		int low = 0, high = idxsize;
		int mid = 0;
		while (high >= low) {
			mid = (low + high) / 2;
			anchor = idxData.get(mid);
			
			if (key.compareTo(anchor.name) > 0) {
				low = mid + 1;
				state = SearchState.BIGGER;
			} else if (key.compareTo(anchor.name) == 0) {
				state = SearchState.FOUND;
				break;
			} else {
				high = mid - 1;
				if (mid == 0) {
					state = SearchState.NOTFOUND;
				} else 
					state = SearchState.SMALLER;
			}
		} 
		double endTime = System.currentTimeMillis();
		logger.debug("binary search index took:" + (endTime-startTime) +" ms");
		
		Entry endAnchor=null;
		long startPos = 0, endPos = 0;
		if (state == SearchState.BIGGER || state == SearchState.FOUND) {
			if (mid < idxsize) {
				mid++;
				endAnchor = idxData.get(mid);
				endPos = endAnchor.byteLocation;
			} else {
				endPos = new File(dataFileName).length();
			}
			
			startPos = anchor.byteLocation;
			
		} else if (state == SearchState.SMALLER) {
			int b4Mid = mid;
			if (b4Mid > 0) {
				b4Mid--;
			}
			anchor = idxData.get(b4Mid);
			endAnchor = idxData.get(mid);
			
			startPos = anchor.byteLocation;
			endPos = endAnchor.byteLocation;
		} else if (state== SearchState.NOTFOUND) {
			System.out.println("name is not in index");
		}
		if (endPos>0) {
			logger.debug("Searching " + dataFileName + " with anchor.");
			startTime = System.currentTimeMillis();
			result = searchInBlock(dataFileName, startPos, endPos, key);
			endTime = System.currentTimeMillis();
			logger.debug("Search in data file took:" + (endTime-startTime) +" ms");
		} else {
			System.out.println("search is not performed.");
		}
		return result;
	}
	

}
