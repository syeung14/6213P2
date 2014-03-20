package edu.gwu.cs6213.p2;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 
 * Parse Byte Array and return line by line
 * 
 * @author marcoyeung
 *
 */
public class MemoryByteBuffer {
	

	private byte buffer[];
	private int bufferEnd = 0;
	private int bufferPos = 0;
	private Charset charset;
	
	public MemoryByteBuffer(byte[] buffer){
		this(buffer, Charset.defaultCharset());
	}
	public MemoryByteBuffer(byte[] buffer, Charset charset) {
		this.buffer = buffer;
		bufferEnd = buffer.length;
		this.charset = charset;
	}

	public final String getNextLine() throws IOException {
		String str = null;

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

		bufferPos = lineEnd + 1;
		return str;
	}
	
	private int read() {

		if (bufferEnd == 0 || bufferPos >= bufferEnd) {
			return -1;
		} else {
			return buffer[bufferPos++];
		}
	}

}
