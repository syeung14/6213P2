package edu.gwu.cs6213.p2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.ObjectInputStream.GetField;
import java.nio.charset.Charset;

/**
 * 
 * Idea is from 
 * Nick Zhang
 * Java Tip 26_ How to improve Java's I_O performance _ JavaWorld.pdf
 * 
 * 
 * Parse Byte Array and return line by line
 * 
 * @author marcoyeung
 *
 */
public class BufferedRandomAccessFile extends RandomAccessFile {
	private static final int DEFAULT_SIZE = 4000;
	
	private byte buffer[]; 
	private int bufferEnd = 0;
	private int bufferPos = 0;
	private Charset charset = Charset.defaultCharset();
	private long filePosPointer = 0;
	
	private int bufferSize;
	
	public BufferedRandomAccessFile(String file, String mode, int bufferSize)
			throws FileNotFoundException {
		super(file, mode);
		this.bufferSize = bufferSize;
		this.buffer = new byte[bufferSize];
	
	}
	public BufferedRandomAccessFile(String name, String mode)
			throws FileNotFoundException {
		this(name, mode, DEFAULT_SIZE);
	}
	
	public final String getNextLine() throws IOException {
		String str = null;

		if (bufferEnd == bufferPos) {
			fillBuffer();
		}

		if (bufferEnd - bufferPos <= 0) {
			return null;
		}

		// Find line terminator from buffer
		int lineEnd = -1;
		for (int i = bufferPos; i < bufferEnd; i++) {
			if (buffer[i] == '\n') {
				lineEnd = i;
				break;
			}
		}

		// Line terminator not found from buffer
		if (lineEnd < 0) {
			StringBuilder sb = new StringBuilder(256);

			int c;
			while (((c = read()) != -1) && (c != '\n')) {
				if ((char) c != '\r') {
					sb.append((char) c);
				}
			}
			if ((c == -1) && (sb.length() == 0)) {
				return null;
			}

			return sb.toString();
		}

		if (lineEnd > 0 && buffer[lineEnd - 1] == '\r') {
			str = new String(buffer, bufferPos, lineEnd - bufferPos - 1,
					charset);
		} else {
			str = new String(buffer, bufferPos, lineEnd - bufferPos, charset);
		}

		bufferPos = lineEnd + 1; //may reach to end of the buffer
		return str;
	}
	
	@Override
	public long getFilePointer() throws IOException{
		return filePosPointer - bufferEnd + bufferPos;
	}
	
	private void fillBuffer() throws IOException {
		int n = -1;
		n = super.read(buffer);
		if (n > 0) {
			bufferEnd = n;
			bufferPos = 0;
			filePosPointer += n;
		}
	}

	public int read() throws IOException {

		if (bufferEnd - bufferPos == 0) {
			fillBuffer();
		} 
		if (bufferEnd == -1 || bufferPos >= bufferEnd) {
			return -1;
		} else {
			return buffer[bufferPos++];
		}
	}

	
	public static void main(String[] args) {
		String file = "input/test.1.txt";
//		file = "input/inputfile.1.txt";
		
		try (BufferedRandomAccessFile raf = new BufferedRandomAccessFile(file, "r",400);){

			String tmp;
			
			long pos = raf.getFilePointer();
			while ( (tmp=raf.getNextLine())  != null) {
				
				System.out.println(pos +":'" + tmp);
				pos = raf.getFilePointer();
			} //
			
//			String tmp = raf.getNextLine();
//			System.out.println(tmp);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
