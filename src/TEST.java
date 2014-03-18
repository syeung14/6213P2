import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TEST {
	private static void readLine(String fileName){
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));

			LineNumberReader lreader = new LineNumberReader(new FileReader(fileName));
//			lreader.mark(5000);
			lreader.setLineNumber(5000);//not setting physical pointer

			System.out.println(lreader.readLine());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/** Sort original file into sorted segments */
	private static int initializeSegments(int segmentSize, String originalFile,
			String f1) throws Exception {

		int[] list = new int[segmentSize];
		DataInputStream input = new DataInputStream(new BufferedInputStream(
				new FileInputStream(originalFile)));
		DataOutputStream output = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(f1)));


		int numberOfSegments = 0;
		while (input.available() > 0) {
			numberOfSegments++;
			int i = 0;
			for (; input.available() > 0 && i < segmentSize; i++) {
				list[i] = input.readInt();
			}

			// Sort an array list[0..i-1]
			java.util.Arrays.sort(list, 0, i);

			// Write the array to f1.dat
			for (int j = 0; j < i; j++) {
				output.writeInt(list[j]);
			}
		}

		input.close();
		output.close();

		return numberOfSegments;
	}

	private static void randomChannel() {
		String s = "I was here!\n";
		byte data[] = s.getBytes();
		ByteBuffer out = ByteBuffer.wrap(data);

		ByteBuffer copy = ByteBuffer.allocate(12);

		try (FileChannel fc = (FileChannel.open(null,null,null))) {
			// Read the first 12
			// bytes of the file.
			int nread;
			do {
				nread = fc.read(copy);
			} while (nread != -1 && copy.hasRemaining());

			// Write "I was here!" at the beginning of the file.
			fc.position(0);
			while (out.hasRemaining())
				fc.write(out);
			out.rewind();

			// Move to the end of the file.  Copy the first 12 bytes to
			// the end of the file.  Then write "I was here!" again.
			long length = fc.size();
			fc.position(length-1);
			copy.flip();
			while (copy.hasRemaining())
				fc.write(copy);
			while (out.hasRemaining())
				fc.write(out);
		} catch (IOException x) {
			System.out.println("I/O Exception: " + x);
		}		


	}

	private void method() {
		try (BufferedReader inputReader = Files.newBufferedReader(
				Paths.get(new URI ("file:///C:/home/docs/users.txt")), Charset.defaultCharset());
				BufferedWriter outputWriter = Files.newBufferedWriter(
						Paths.get(new URI("file:///C:/home/docs/users.bak")), Charset.defaultCharset())
				) {

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("")));


			String inputLine;
			while ((inputLine = inputReader.readLine()) != null) {
				outputWriter.write(inputLine);
				outputWriter.newLine();
			}
			System.out.println("Copy complete!");

		} catch (URISyntaxException | IOException ex) {
			ex.printStackTrace();
		}

	}

	private static void loadRandom(String originalFile, int line) {

		try (RandomAccessFile raf = new RandomAccessFile(originalFile, "r");) {

			if (line >0) {
				raf.seek(line);
				System.out.println(raf.readLine());

			} else
				for (int i = 0; i < 1000; i++) {
					System.out.println(raf.getFilePointer());
					System.out.println(raf.readLine());
				}

		} catch (IOException e) {
			System.out.println("here");
			e.printStackTrace();
		} finally{
			System.out.println("there");
		}
	}

	private static void byteArray(){
		byte[] salt = {
				(byte) 0x35, (byte)0x46,
				(byte) 0x25, (byte)0xc6,
				(byte) 0x85, (byte)0xa6,
				(byte) 0xe5, (byte)0x16 };

	}


	public static void main(String[] args) {
		try {
			//TEST.initializeSegments(10, "largedata.dat", "sorting.txt");
			TEST.loadRandom("largedata2.txt",25889);
//			TEST.readLine("largedata2.txt");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}