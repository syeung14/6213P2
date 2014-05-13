package edu.gwu.cs6213.p2;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

public class Project2Launcher {
	private static final Logger logger = Logger.getLogger(Project2Launcher.class);

	private String[] availableTask = {
			"\t Project 2 menu",
			"\t ===================================================================",
			"\t Please type '1' to pre-process input files (sort input file and build indexes)",
			"\t Please type '2 <name>' to perform search (full name case sensitive)",
			"\t Please type '3' to rebuild index files",
			"\t Please type '4' to reload index files to memory",
			"\t Please type 'exit' to quit the console" 
			};

	
	private DataFileProcesser fProcessor;
	private FileSearcher fSearcher;
	
	private Project2Launcher() {
		
		fProcessor = new DataFileProcesser();
		fSearcher = new FileSearcher();
		
	}
	
	private void doRequest() {
		InputStream inStream = new  BufferedInputStream(System.in);
		PrintStream printStream = new PrintStream(System.out);;
		try {
			userOptions(printStream);
			displayPrompt(printStream);
			String request = getRawRequest(inStream);
			while ( request != null && !request.equals("exit")) {
				doRequest(request, printStream);
				displayPrompt(printStream);
				request = getRawRequest(inStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doRequest(String cmd, PrintStream printStream) throws IOException {
		if (cmd==null || "".equals(cmd.trim())) {
			return;
		}
		cmd=cmd.trim();
		int ispace = cmd.indexOf(" ");

		String request="", name="";
		int numRecords =0;
		if (ispace != -1) {
			request = cmd.substring(0,ispace);
			name = cmd.substring(ispace).trim();
			
			if (name.indexOf("#") != -1) {
				int pos = name.indexOf("#");
				numRecords = Integer.parseInt(name.substring(pos+1));
				
				name = name.substring(0, pos);
			}
			
		} else {
			request = cmd;
		}
		
		String[] data = { request, name };
		
		if ("1".equals(data[0])) {
			try {
				printStream.println(" ");
				loadFile();
				printStream.println(" ");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (data.length > 1 && "2".equals(data[0])) {
			printStream.println(" ");
			printStream.println("Search phone number for '" + data[1] + "'");
			search(data[1]);
			printStream.println(" ");
		} else if ("3".equals(data[0])) {
			printStream.println(" ");
			buildIndex();
			printStream.println(" ");
		} else if ("4".equals(data[0])) {
			printStream.println(" ");
			fSearcher = new FileSearcher();
			printStream.println("\t Index files are re-loaded.");
			printStream.println(" ");
		} else if (data.length >1 && "9".equals(data[0]) && numRecords>0) {
			//9 input/input01.txt#10000
			printStream.println(" ");
			FileUtil.phoneBookGen(name, numRecords);
			printStream.println(" ");
		} else if (data[0].equalsIgnoreCase("h")) {
			userOptions(printStream);
		} else {
			printStream.println("\t Invalid command ");
		}
			
	}
	
	protected void userOptions(PrintStream printStream) {
		for (String cmd : availableTask) {
			printStream.println(cmd);
		}
	}
	private void displayPrompt(PrintStream printStream){
		printStream.print("Project2 ('h' - menu)> ");
		printStream.flush();
	}
	private String getRawRequest(InputStream in) throws IOException {
		byte buf[] = new byte[1024];
		int pos = 0;
		int c;
	
		while ((c = in.read()) != -1) {
			switch(c){
			case '\r':
				break;
			case '\n':
				return new String(buf, 0 , pos);
			default:
				buf[pos++] = (byte)c;
			}
			try {
				Thread.sleep(1); //so that others get enough time slice
			}catch(InterruptedException ie){
				System.out.println("Received exception: " + ie + " in doRequest. Exception ignored.");
			}
		}
		return null;
	}
	
	private void loadFile() throws IOException {
		sortFile();
		buildIndex();
	}
	
	private void sortFile() {
		fProcessor.sortFileContentSeq();
	}
	private void buildIndex() throws IOException {
		fProcessor.buildIndex();
		fSearcher.loadInputandIndexFile();
	}
	
	private void search(String key){
		double startTime = System.currentTimeMillis();
		SearchResult result = fSearcher.search(key);
		double endTime = System.currentTimeMillis();
		System.out.println("Search result for "+key +" took "+ (endTime-startTime) +" ms : " + result);
	}
	
	public static void main(String[] args) {
		String fileName = "largedata0.txt";
		String tmpOutFile = "largedata0.sorted.txt";
		
		Project2Launcher launcher = new Project2Launcher();
		try {
//			launcher.buildIndex();
//			launcher.search("Ab Aus");
			logger.debug("Program started.");
			
			launcher.doRequest();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} //
}
