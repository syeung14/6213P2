import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

import edu.gwu.cs6213.p2.util.NameGenerator;

public class CreateLargeFile {
	private static String fileName = "input/inputfile.1.txt";

	public static void main(String[] args) throws Exception {
		
		CreateLargeFile.phoneBookGen(fileName,4_111);
//		CreateLargeFile.writeBinFile(fileName);
	}

	private static void phoneBookGen(String fileName, int size) {

		BufferedWriter osw=null;
		try {
			osw= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));

			String entry = "";
			NameGenerator ngen = new NameGenerator();
			for (int i = 0; i< size; i++) {
				entry = ngen.getName()+ " " + ngen.getName()  + "," +
						Integer.toString(100+(int)(new Random().nextInt(400)))+ "-"+
						Integer.toString(100+(int)(new Random().nextInt(900)) )+ "-"+  
						Integer.toString(1000+(int)(new Random().nextInt(9000)));

//				entry = ngen.getName() + "," + "100";
				
				osw.write(entry);
				osw.newLine(); //cost 2 bytes
			}
			osw.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {osw.close();} catch (IOException e) {}
		}
	}

	private static void writeTextFile(String fileName) throws IOException {
		FileOutputStream fout = new FileOutputStream(fileName);
		BufferedWriter osw = new BufferedWriter(new OutputStreamWriter(fout));

		String entry = "";
		for (int i = 0; i < 800004; i++) {
			entry = Integer.toString((int) (Math.random() * 1000000));
			osw.write( entry );
			osw.newLine();
		}

		osw.close();


		FileInputStream fin = new FileInputStream(fileName);
		InputStreamReader bis = new InputStreamReader(fin);
		BufferedReader br  =new BufferedReader(bis);

		String tmp;
		int cnt = 0;
		while ( (tmp = br.readLine()) !=null && cnt <50){

			System.out.println(cnt++ + "=" + tmp);
		}
	}

	private static void writeBinFile(String fileName) throws IOException {

		DataOutputStream output = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(fileName)));

		for (int i = 0; i < 800004; i++)
			output.writeInt((int) (Math.random() * 1000000));

		output.close();

		// Display first 100 numbers
		DataInputStream input = new DataInputStream(new FileInputStream(
				fileName));
		for (int i = 0; i < 100; i++)
			System.out.print(input.readInt() + " ");

		input.close();

	}

}
