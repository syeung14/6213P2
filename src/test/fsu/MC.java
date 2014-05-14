package fsu;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * 
 */
public class MC {
	private static final int START_ACTOR = 0; //WARN: this is zero based 
	private static final String OUTPUT_FILE_NAME = "./output.txt";
	private static final String INPUT_FILE_NAME = "./bin/input2.txt";
	
	private int numOfActor; 
	private Actor[] actorList; //all the available actors
	private float[][]acqMatrix;//acquaint weight; adjacent matrix in graph term
	private int intendedActor;  //actor to be invited
	private InvitationCost[] invitaCost;  //store the cheapest invitation chain
	
	private int currActor=0;  //temp variable for algorithm
	private float accumuCost=0; //temp variable for algorithm
	
	public MC() {}

	/**
	 * Represents Actor object
	 * its invitation cost and name are stored
	 * invited is a flag to indicate whether it has been invited or not 
	 * */
	class Actor {
		int name;  //a number in this project  
		float cost;  //direct invitation cost
		boolean invited; 
		public Actor(int i, float cost) {
			this.name =i;
			this.cost = cost;
			this.invited = false;
		}
		public String toString() {
			return name+"|"+cost+"|"+invited;
		}
	}

	/**
	 * Records the invitation cost 
	 * */
	class InvitationCost {
		float cost;   //total cost to invite intended actor, may include indirect cost 
		int srcInvite;// indirect invitation source actor
		public InvitationCost(float cost, int srcInvite) {
			this.cost = cost;
			this.srcInvite = srcInvite;
		}
		public String toString() {return srcInvite+"|"+cost;}
	}

	/**
	 * Reading the file content and construct the graph
	 * 
	 * @param fileName
	 */
	private void readFile(String fileName) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName));) {
			
			String tmp;
			List<String> data = new ArrayList<>();
			while ((tmp = br.readLine()) != null) {
				data.add(tmp);
			}

			numOfActor = Integer.parseInt(data.get(0));
			actorList = new Actor[numOfActor];
			tmp = data.get(1);// invitation cost
			String[] in = tmp.split("[ ]");
			for (int i = 0; i < in.length; i++) {
				float cost = Float.parseFloat(in[i]);
				actorList[i] = new Actor(i, cost);
			}
			
			int a = 0;
			acqMatrix = new float[numOfActor][numOfActor];
			for (int i = 2; i < numOfActor+2; i++) {
				tmp = data.get(i);
				String[] wei = tmp.split("[ ]");
				for (int j = 0; j < wei.length; j++) {
					acqMatrix[a][j] = Float.parseFloat(wei[j]);
				}
				a++;
			}
			
			//WARN starts from one (1).
			intendedActor = Integer.parseInt(data.get(numOfActor + 2));
			invitaCost = new InvitationCost[numOfActor];
			
			System.out.println("-Data given-----------------------");
			System.out.println("total actors: "+ numOfActor);
			System.out.println("actors: "+ Arrays.toString(actorList));
			
			System.out.println("actors' acquaint matrix: ");
			printMatrix(acqMatrix);
			
			System.out.println("actor to invite: " + intendedActor);
			System.out.println("reading input done.");
			System.out.println("----------------------------------");
			
		} catch (IOException e) {
			System.out.println("incorrect data in input file.");
			e.printStackTrace();
		}
	}

	/**
	 * find the minimum invitation cost with a variation Dijkstra's algorithm
	 *  
	 * */
	private void findMinimumCost(int startActor) {
		
		actorList[startActor].invited = true;
		//construct and initialize all the actors into invitation chain 
		for (int i = 0; i < numOfActor; i++) {
			float startCost = actorList[startActor].cost;
			float degree = acqMatrix[startActor][i];
			float aCost = actorList[i].cost;
			float totalCost = startCost + getiAskCost(aCost, degree);
			invitaCost[i] = new InvitationCost(totalCost, startActor);
		}
		
		int nActor = 1;//start from the second actor, since the initial actor is already considered.
		//find the minimum cost with Dijkstra algorithm
		while (nActor < numOfActor) {
			currActor = getLeastCost();
			accumuCost = invitaCost[currActor].cost;
			
			actorList[currActor].invited = true; //mark actor as invited
			nActor++;
			calculateCost();
		}

		printResult();
		System.out.println("results are in " + OUTPUT_FILE_NAME);
		
	}
	
	/**
	 * output invitation chain to output file
	 * */
	private void printResult () {
		
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(OUTPUT_FILE_NAME)));) {

			float cost = invitaCost[intendedActor-1].cost;
			String chain;
			if ((intendedActor-1) == START_ACTOR) {//direct invitation
				chain = intendedActor + ""; 
			} else { //indirect invitation
				chain = getInviteChain(intendedActor-1);
			}

			bw.write(Float.toString(cost));
			bw.newLine();
			bw.write(chain);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getInviteChain(int intendActor) {
		
		if (intendActor == START_ACTOR) {
			return (START_ACTOR+1)+""; //add 1 to convert to 1 based naming
		} else {
			int src = invitaCost[intendActor].srcInvite;
			return getInviteChain(src) + " " + (intendActor+1);
		}
		
	}
	
	/**
	 * Core of Dijkstra's algorithm. <BR>
	 * calculate the minimum invitation cost and update invitation chain in necessary.
	 * */
	private void calculateCost() {
		int otherActor =1;
		while (otherActor < numOfActor) {
			if (!actorList[otherActor].invited) {
				
				float degree = acqMatrix[currActor][otherActor];
				float oCost = actorList[otherActor].cost;
				float iaskOtherCost = getiAskCost(oCost, degree) + accumuCost;
				
				float otherCurrCost = invitaCost[otherActor].cost;
				
				if (iaskOtherCost < otherCurrCost) {
					invitaCost[otherActor].srcInvite = currActor;
					invitaCost[otherActor].cost = iaskOtherCost;
				}
			}
			
			otherActor++;
		}
	}
	private float getiAskCost(float init, float acq) {
		return init * (1 - acq);
	}
	/**
	 * find the minimum cost to invite the actor  <BR> 
	 * determine who should invite next from their cost
	 * */
	private int getLeastCost() {
		float minCost = Float.POSITIVE_INFINITY;
		int actorIdx = 0;
		for (int i = 0; i < numOfActor; i++) {
			if (!actorList[i].invited && invitaCost[i].cost < minCost) {
				minCost = invitaCost[i].cost;
				actorIdx = i;
			}
		} //
		return actorIdx;
	}
	
	private void printMatrix(float[][] data) {		
		for (float[] fs : data) {
			for (float f : fs) {
				System.out.print("\t"+f +" ");
			}
			System.out.println();
		}
	}
	
	private void invitaActor(String fileName) {
		readFile(fileName);
		findMinimumCost(START_ACTOR);
	}
	
	
	public static void main(String[] args) {
		MC run = new MC();
		try {
			if (args.length == 1) {
				run.invitaActor(args[0]);
			} else {
				run.invitaActor(INPUT_FILE_NAME);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
