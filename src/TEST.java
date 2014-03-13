import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TEST {
	
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

	public static void main(String[] args) {
			try {
				TEST.initializeSegments(10, "largedata.dat", "sorting.txt");
			} catch (Exception e) {
				e.printStackTrace();
			}
		System.out.println("hell");

	}
}