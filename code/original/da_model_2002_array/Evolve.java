/**
 * @author Lei Zhang
 * 			Nov 8, 2002
 */
import java.io.*;
import java.awt.*;
import java.applet.*;
import java.net.*;   
import java.util.*;
import java.lang.*;

public class Evolve {

	private String inputFile;
	private int numNodes, numWorkers, currentStep, currentWorkers;
			
	private Node node[];
	private Arc arc[][];
	private Worker worker[];
	
	final private int MAX_STEPS = 40;
	final private int MIN_WORKERS = 0;
	final private long seed = 9327;
	
	Random rand = new Random(seed);
	float r;
	
	public Evolve(String inputFile){
		this.inputFile = inputFile;
	}
	
	
	public static void main(String[] args) throws IOException{
		Evolve evolve = new Evolve("c://grid2.txt");
		
		evolve.initialization();
		evolve.iteration();
		evolve.end();		
	}
	
	public void end(){
		System.out.println("_________Old END__________");
	}
	
	public void iteration(){
		System.out.println("_______BEGIN ITERATION_______");
		while((currentStep < MAX_STEPS) && (currentWorkers > MIN_WORKERS)){
			
			System.out.println("----Begin Iteration " + currentStep + " currentWorker" + currentWorkers);
					
			//update turning probability matrix for each node
			for(int i = 1; i < numNodes + 1; i++){
				updateNode(node[i]);
			}
			//update the path of each worker
			for(int i = 0; i < numWorkers; i++){
				//only update the worker who has not a job
				System.out.println("Begin update worker " + (i + 1));
				if(worker[i].status == 0){
					System.out.println("  status " + 0);
					updateWorker(worker[i]);
					//new information change only occurs for workers 
					//	who still need to move
					if(worker[i].status == 0){
						///IMPORTANT Link flow should also be updated at this point
						//exchange information with the next node
						exchangeInformation(worker[i]);
					}
				}
			}
			
			currentStep++;
			
		}
		////System.out.println("_______AFTER ITERATION_______");
	}
	
	public void initialization() throws IOException{
		System.out.println("_______BEGIN INITIALIZATION_______");
		
		FileInputStream fin = null;
		ReadANumber read = new ReadANumber();
		int temp, workerCounter = 0;
		float beta;
		
		int nodeId, nodeWorkers, nodeJobs, xCoord, yCoord, 
			numDemandNodes, demandNodes[];
				
		int length;	
		
		try{
			fin = new FileInputStream(inputFile);
		}catch(FileNotFoundException e) {
			System.out.println("File Error");
			return;
		}
		
		numNodes = read.readint(fin);
		System.out.println(numNodes);
		numWorkers = read.readint(fin);
		System.out.println(numWorkers);
		beta = read.readfloat(fin);
		System.out.println(beta);
		
		currentStep = 0;
		currentWorkers = numWorkers;
		
		////  Initializing the variables
		node = new Node[numNodes + 1];
		arc = new Arc[numNodes + 1][numNodes + 1];
		worker = new Worker[numWorkers];
		
		for(int i = 1; i < numNodes + 1; i++) {
			if(read.end == -1) break;
			nodeId= read.readint(fin);
			System.out.print(nodeId + " ");
			if(read.end == -1) break;
			nodeWorkers= read.readint(fin);
			System.out.print(nodeWorkers + " ");
			if(read.end == -1) break;
			nodeJobs = read.readint(fin);
			System.out.print(nodeJobs + " ");
			if(read.end == -1) break;
			xCoord = read.readint(fin);
			System.out.print(xCoord + " ");
			if(read.end == -1) break;
			yCoord = read.readint(fin);
			System.out.print(yCoord + " ");

			if(read.end == -1) break;
			numDemandNodes = read.readint(fin);
			System.out.print(numDemandNodes + " ");
			demandNodes = new int[numDemandNodes];
			for(int j =0; j<numDemandNodes; j++) {
				if(read.end == -1) break;
				temp = read.readint(fin);
				demandNodes[j] = temp;
				System.out.print(demandNodes[j] + " ");
				if(read.end == -1) break;
				length = read.readint(fin);
				System.out.print(length + " ");
				arc[nodeId][temp] = new Arc(length);				
			}
			System.out.println();
			node[i] = new Node(nodeId, nodeWorkers, nodeJobs, xCoord, yCoord, 
								numDemandNodes, demandNodes, numNodes, beta);
			
			for(int k = 0; k < nodeWorkers; k++){
				worker[workerCounter] =  new Worker(nodeId, numNodes);
				workerCounter++;
			}
			  	
		}
		
		fin.close();
		System.out.println("_______AFTER INITIALIZATION_______");
	}	
	
	public void exchangeInformation(Worker currentWorker){
		
		
		int currentNodeId = currentWorker.currentNode;

		System.out.println("  exchangeInformation with node " + currentNodeId);
		
		int currentOriginNode;
		int workerPathLength = currentWorker.pathLength;
		
		System.out.println("    workerPathLength " + workerPathLength);
		for(int i = 0; i < workerPathLength; i++)System.out.print(currentWorker.path[i]);
		System.out.println();
		for(int i = 0; i < workerPathLength; i++)System.out.print(currentWorker.knowledge[i]);
		
		int workerPathDistance = (int)0;
		int counter;
		int copyingNode, nextCopyingNode, copyingArcLength;
		
		for(int i = workerPathLength - 2; i >= 0; i--){
			currentOriginNode = currentWorker.path[i];
			////System.out.println("    currentOriginNode " + currentOriginNode);
			workerPathDistance += currentWorker.knowledge[i];
			counter = (int)0;
			if(workerPathDistance > node[currentNodeId].shortestPathDistances[currentOriginNode]){
				//The worker learns from the node if the node knows a shorter
				//	path from the currentOriginNode to the currentNode
				while((copyingNode = node[currentNodeId].shortestPaths[currentOriginNode][counter]) != -9){
					currentWorker.path[i + counter] = copyingNode;
					if((nextCopyingNode = node[currentNodeId].shortestPaths[currentOriginNode][counter + 1]) != -9){
						copyingArcLength = arc[copyingNode][nextCopyingNode].length;
						currentWorker.knowledge[i + counter] = copyingArcLength;
					}
					counter++;
				}
				while(currentWorker.path[i + counter] != -9){
					currentWorker.path[i + counter] = -9;
					currentWorker.knowledge[i + counter - 1] = -9;
					counter++;
				}
				workerPathDistance = node[currentNodeId].shortestPathDistances[currentOriginNode]; 
			}else if(workerPathDistance < node[currentNodeId].shortestPathDistances[currentOriginNode]){
				//The node learns from the worker if the worker knows a shorter
				//	path from the currentOriginNode to the currentNode
				node[currentNodeId].shortestPathDistances[currentOriginNode] = workerPathDistance;
				while((copyingNode = currentWorker.path[counter + i]) != -9){
					node[currentNodeId].shortestPaths[currentOriginNode][counter] = copyingNode;
					counter++;
				}
				while(node[currentNodeId].shortestPaths[currentOriginNode][counter] != -9){
					node[currentNodeId].shortestPaths[currentOriginNode][counter] = -9;
					counter++;
				}
			}else{
				//No information exchange if the paths known by the node and
				// the worker have equal lengths
			}	
		}
		currentWorker.updatePathLength();
		currentWorker.previousNode = currentWorker.path[currentWorker.pathLength - 2];
	}
	

	
	public void updateNode(Node currentNode){
		System.out.println("Begin updateNode " + currentNode.nodeId);
	
		int demandNodeJobs[];
		int numDemandNodes;
		int demandNodes[];
		numDemandNodes = currentNode.numDemandNodes;
		System.out.println("  numDemandNodes " + numDemandNodes);
		demandNodes = currentNode.demandNodes;
		demandNodeJobs = new int[numDemandNodes];
		//get current jobs at adjacent nodes
		for(int i = 0; i < numDemandNodes; i++){
			demandNodeJobs[i] = node[demandNodes[i]].currentJobs;
			System.out.println("    demandNode " + demandNodes[i] + " Jobs " + demandNodeJobs[i]);
		}
		//update matrix for the current node
		currentNode.updateTurningGuidance(demandNodeJobs);
		System.out.println("    Node knowledge");
		for(int i = 1; i < numNodes + 1; i++){
			System.out.print("    " + i + " " + currentNode.shortestPathDistances[i] + " ");
			for(int j = 0; j <= 15; j++){
				System.out.print(currentNode.shortestPaths[i][j]);
			}
			System.out.println();
		}
	}
	
	
	public void updateWorker(Worker currentWorker){
		
		int previousNode, currentNode, nextNode = -9, numDemandNodes, demandNodes[];
		int row = -9, column = -9;
		boolean rowIdentified = false, columnIdentified = false;
		float cumProbability = (float)0.0;
		
		int arcLength;
		
		previousNode = currentWorker.previousNode;
		currentNode = currentWorker.currentNode;
		numDemandNodes = node[currentNode].numDemandNodes;
		demandNodes = node[currentNode].demandNodes;
		
		System.out.println("    currentNode " + currentNode);
		
		//find the correponding row in the turning guidance matrix
		for(int j = 0; j < numDemandNodes; j++){
			if(previousNode == demandNodes[j]){
				row = j;
				rowIdentified = true;
				break;
			}
		}
		if(rowIdentified == false){
			row = numDemandNodes;
		}else{
			rowIdentified = false;
		}
		if(row == -9)System.out.println("ERROR --- Row Not Identified");
		//find the correponing column in the turning guidance matrix
		//	i.e. find the next node for the current worker
		r = rand.nextFloat();
		System.out.println("    random " + r);
		cumProbability = (float)0.0;
		for(int j = 0; j < numDemandNodes; j++){
			cumProbability += node[currentNode].turningGuidance[row][j];
			if(r <= cumProbability){
				column = j;
				nextNode = demandNodes[j];
				if(nextNode == previousNode){
					System.out.println("ERROR --- Small Cyclic Route in findNextNode");
				}
				System.out.println("    nextNode " + nextNode);
				arcLength = arc[currentNode][nextNode].length;
				currentWorker.addNextNode(nextNode, arcLength);
				columnIdentified = true;
				break;
			}
		}
		if(columnIdentified == false){
			column = numDemandNodes;
			nextNode = currentNode;
			//the worker takes a job at the current node. 
			currentWorker.status = 1;
			System.out.println("    job Found ");
			currentWorkers--;
			node[currentNode].currentJobs--;
		}else{
			columnIdentified = false;
		}
		if(nextNode == -9)System.out.println("ERROR --- Column Not Identified");
		

	}
	
	//////// a class used for reading numbers from the file.  this class is written in order to reduce the redundancy of the code
	class ReadANumber{
		
		public int end;
		
		ReadANumber() {
			end = 0;
		}
		
		int readint(InputStream f)
			throws IOException
		 {
			String msg = "";
			int i;
			do {
				i = f.read();
				if(i != -1 && i != 13 && i != 32 && i != 10)
				msg += (char)i;
			} while(i != -1 && i != 13 && i !=32 && i != 10 );
			
			end = i;
		
			try {
				if(msg != null)  {
					i = Integer.parseInt(msg);
					return( i );
				}
				else
					return ( 0 );
			}	catch(NumberFormatException e) {
				System.out.println("NumberFormatException while reading an integer." + msg);
				for(int p =0; p<msg.length();p++)
					System.out.print((int)msg.charAt(p)+" ");
				System.out.println();
				return ( 0 );
			}
		}
		
		float readfloat(InputStream f) 
			throws IOException
		{
			String msg = "";
			int i;
			do {
				i = f.read();
				if(i != -1 & i != 13 & i != 32 && i != 10)
				msg += (char)i;
			} while(i != -1 && i!= 13 && i !=32 && i != 10);
			
			end = i;
		
			try {
				if(msg != null)  {
					return( Float.valueOf(msg).floatValue() );
				}	
				else
					return ( 0 );
			}	catch(NumberFormatException e) {
				System.out.println("NumberFormatException while reading a float value.");
				return (0);
			}
		}
		
	}
	/////////////////////////    End of ReadANumber class	

}
