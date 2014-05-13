package edu.gwu.cs6213.p2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import edu.gwu.cs6213.p2.util.ExternalSort;
import edu.gwu.cs6213.p2.util.PropertiesLoader;
import edu.gwu.cs6213.p2.util.PropertiesParser;

public class DataFileProcesser {

	private static final Logger logger = Logger.getLogger(DataFileProcesser.class);
	
	private int default_memSize = 4000;
	private int buildingBuffer = 100_000;
	private int externalSortBuffer = 1_000_000;
	private String srcFileName;
	private String sortedFileName;
	private String indexFileName;
	private String inputFolder;
	private String processedFolder;
	private String inputFilePrefix;
	private String inputFileExt;
	
	private PropertiesParser prop;
	private static String folderSep = System.lineSeparator();
	
	private File[] inputFiles;
	private File[] sortedFiles;
	
	@Deprecated
	public DataFileProcesser(String srcFileName,
			String sortedFileName, String indexFileName) {
		this();
		
		this.srcFileName = srcFileName;
		this.sortedFileName = sortedFileName;
		this.indexFileName = indexFileName;
	}
	
	public DataFileProcesser() {
		this.prop = PropertiesLoader.getPropertyInstance();
		this.default_memSize = prop.getIntProperty("memory.size", 4000);
		this.externalSortBuffer = prop.getIntProperty("memory.externalsort.buffer");
		this.buildingBuffer = prop.getIntProperty("memory.indexbuild.buffer");
		
		this.inputFolder = prop.getStringProperty("file.sourcefolder");
		this.processedFolder = prop.getStringProperty("file.processedfolder");
		this.inputFilePrefix =  prop.getStringProperty("file.input.prefix");	
		this.inputFileExt =  prop.getStringProperty("file.input.ext");
		
	}

	public void buildIndex() {
		try {
			loadInputFiles();
			if (inputFiles==null) {
				System.out.println("Input files is not loaded.");
			}
			
			String sortFsuffix = "." + Constants.SORTED + "." + inputFileExt;
			String indFileSuffix = "." + Constants.INDEX + "." + inputFileExt;
			/*String sFileN, s1stPartN, sortedF, indexF;

			for (File f : inputFiles) {
				sFileN = f.getName();
				s1stPartN = FileUtil.getFileNameNoExt(sFileN,"."+ inputFileExt);
				sortedF = processedFolder +"/" + s1stPartN + sortFsuffix;
				indexF = processedFolder +"/" + s1stPartN + indFileSuffix;
				
				if (! new File(sortedF).isFile()) {
					System.out.println("Sorted file not found, index is not created. " + sortedF );
				}else {
					System.out.println("Building index for " + sortedF);
					doBuildIndex2(sortedF, indexF);
					System.out.println("=====================================");
				}
			}*/
			
			
			final List<Callable<Void>> partitions = new ArrayList<Callable<Void>>();
			
			String sFileN, s1stPartN;
			for (File f : inputFiles) {
				sFileN = f.getName();
				s1stPartN = FileUtil.getFileNameNoExt(sFileN,"."+ inputFileExt);
				final String sortedF = processedFolder +"/" + s1stPartN + sortFsuffix;
				final String indexF = processedFolder +"/" + s1stPartN + indFileSuffix;
				partitions.add(new Callable<Void>() {
					
					@Override public Void call() throws Exception {
						if (! new File(sortedF).isFile()) {
							System.out.println("Sorted file not found, index is not created. " + sortedF );
						}else {
							System.out.println("Building index for " + sortedF);
							doBuildIndex2(sortedF, indexF);
							System.out.println("=====================================");
						}
						return null;
					}
				});
			}
			
			int processors = Runtime.getRuntime().availableProcessors();
			ExecutorService exec = Executors.newFixedThreadPool(processors-1);
			try {
				final List<Future<Void>> sortList = exec.invokeAll(partitions);
				
				int cnt =0;
				for (Future<Void> jobs : sortList) {
					jobs.get();
					cnt++;
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void doBuildIndex2(String sortedFileName, String indexFileName){
		
		try (BufferedRandomAccessFile mbb = new BufferedRandomAccessFile(sortedFileName,"r",buildingBuffer);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(indexFileName)));
				){
			
			long pter =  mbb.getFilePointer();
			
			double startTime = System.currentTimeMillis();
			String tmp,name[];
			int cnt = 0;
			while (  (tmp = mbb.getNextLine()) != null)  {

				if ( cnt++ % default_memSize == 0) {
					name = tmp.split(",");
					bw.write(name[0]+ "," +pter);
					bw.newLine();
				}
				pter =  mbb.getFilePointer();;
			}
			double endTime = System.currentTimeMillis();
			System.out.println("Index "+indexFileName+" is created took:" + (endTime-startTime)/1000 +" s");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Deprecated
	private void doBuildIndex(String sortedFileName, String indexFileName){
			
		try (RandomAccessFile raf = new RandomAccessFile(sortedFileName, "r");
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(indexFileName))  )
				) {

			double startTime = System.currentTimeMillis();
			System.out.println("Started to create index for " + sortedFileName);
			String tmp,name[];
			long pter = raf.getFilePointer();;
			int cnt = 0;
			while (  (tmp = raf.readLine()) != null)  {

				if ( cnt++ % default_memSize == 0) {
					name = tmp.split(",");
					bw.write(name[0]+ "," +pter);
					bw.newLine();
				}
				pter = raf.getFilePointer();
			}
			double endTime = System.currentTimeMillis();
			System.out.println("Index "+indexFileName+" is created took:" + (endTime-startTime)/1000 +" s");
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}	

	/*****************************************************************************************************/
	/*****************************************************************************************************/
	/*****************************************************************************************************/

	/**
	 * @deprecated
	 * External sort from 3rd file, reference only
	 */
	public void sortFileContentSeq() {
		loadInputFiles();
		
		for (File f : inputFiles) {
			final String srcName = f.getName();
			String firstPart = FileUtil.getFileNameNoExt(srcName, "."+inputFileExt);

			final String sortedFName = processedFolder+"/"+ firstPart + "."+Constants.SORTED+"."+inputFileExt;

			try {
				double startTime = System.currentTimeMillis();
				System.out.println("sorting:"+ srcName);
				
				ExternalSort.sort(new File(inputFolder +"/"+srcName), new File(sortedFName), new File(processedFolder));
				
				double endTime = System.currentTimeMillis();
				System.out.println("External Merge Sort took:" + (endTime - startTime) / 1000 +" s");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	/**
	 * @deprecated
	 * Sorted file with external sort in a thread pool
	 * The external sort with lots of I/O and only 3 temp files is too slow
	 */
	public void sortFileContentExec() {
		loadInputFiles();
		
		final List<Callable<Void>> partitions = new ArrayList<Callable<Void>>();
		
		for (File f : inputFiles) {
			final String srcName = f.getName();
			String firstPart = FileUtil.getFileNameNoExt(srcName, "."+inputFileExt);
			
			final String sortedFName = processedFolder+"/"+ firstPart + "."+Constants.SORTED+"."+inputFileExt;
			
			partitions.add(new Callable<Void>() {

				@Override public Void call() throws Exception {
					doSortFileContent(srcName, sortedFName);
					return null;
				}
			}  );
		}
		
		int processors = Runtime.getRuntime().availableProcessors();
		ExecutorService exec = Executors.newFixedThreadPool(processors-1);
		try {
			final List<Future<Void>> sortList = exec.invokeAll(partitions);
			
			System.out.println("waiting...");
			int cnt =0;
			for (Future<Void> jobs : sortList) {
				jobs.get();
				cnt++;
				System.out.println(cnt + " is back.");
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
				
	}

	private void loadInputFiles() {
		try {
			inputFiles = FileUtil.getAllFiles(inputFolder, inputFilePrefix, inputFileExt);
			System.out.println("Number of input files: "+ inputFiles.length);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void doSortFileContent(String srcFileName, String sortedFileName){
		
		if (srcFileName ==null || "".equals(srcFileName)  ||
				sortedFileName ==null || "".equals(sortedFileName)) {

			throw new IllegalArgumentException("file name is not set.");
		}
		String tmp01 = processedFolder +"/"+ srcFileName +".tmp.1";//  prop.getStringProperty("externalsort.tmp.1");
		String tmp02 = processedFolder +"/"+ srcFileName +".tmp.2";//  prop.getStringProperty("externalsort.tmp.2");
		String tmp03 = processedFolder +"/"+ srcFileName +".tmp.3";//  prop.getStringProperty("externalsort.tmp.3");
		
		srcFileName = inputFolder +"/"+srcFileName;
		
		int runCnt = 0;
		try (BufferedRandomAccessFile br = new BufferedRandomAccessFile(srcFileName,"r", externalSortBuffer);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tmp01)));   ) {
			System.out.println("Start to count the number of runs " + srcFileName );

			boolean hasMore =false;
			do {
				hasMore = initialSort(br, bw); 
				if (hasMore) runCnt++;
			} while (hasMore);
				
			System.out.println("Number of runs: " + runCnt);
			bw.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		double startTime = System.currentTimeMillis();
		System.out.println("Started External Merge Sort.");
		mergeSort(runCnt, default_memSize, tmp01, tmp02, tmp03, sortedFileName);
		
		try {
			new File(tmp01).delete();
			new File(tmp02).delete();
			new File(tmp03).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		double endTime = System.currentTimeMillis();
		System.out.println("External Merge Sort took:" + (endTime - startTime) / 1000 +" s");
		System.out.println("=====================================");
		
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
	
	private void mergeSort(int run, int memSize, String tmpSort, String tmpHalf, String tmp03
			, String sorted) {
		if (run > 1) {
			mergeFile(run, memSize, tmpSort, tmpHalf, tmp03);
			mergeSort((run + 1) / 2, memSize * 2, tmp03, tmpSort, tmpHalf, sorted);
		} else {
			File sortedFile = new File(sorted);
			if (sortedFile.exists()) sortedFile.delete();
			new File(tmpSort).renameTo(sortedFile);
			System.out.println(sortedFile.getName() +" is created.");
		}
	}
	
	private void mergeFile(int run,int memSize, String partialSort, String tmpHalf, String moreSorted) {
		
		try (BufferedRandomAccessFile src1 = new BufferedRandomAccessFile(partialSort,"r", externalSortBuffer);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(tmpHalf))) ) {
			
			//
			for (int i = 0; i < (run / 2) * memSize; i++) {
				bw.write(src1.readLine());
				bw.newLine();
			}
			bw.flush();
			
			try (BufferedRandomAccessFile src2 = new BufferedRandomAccessFile(tmpHalf,"r", externalSortBuffer);
					BufferedWriter target = new BufferedWriter(new OutputStreamWriter(
							new FileOutputStream(moreSorted))) ) {
				
				mergeRuns(run / 2, memSize, src1, src2, target);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private void mergeRuns(int run,int memSize, BufferedRandomAccessFile partialSort,
			BufferedRandomAccessFile tmpHaf, BufferedWriter moreSorted)
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
	private void mergeSortRuns(int memSize,BufferedRandomAccessFile partialSort,
			BufferedRandomAccessFile tmpHalf, BufferedWriter moreSorted)
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
