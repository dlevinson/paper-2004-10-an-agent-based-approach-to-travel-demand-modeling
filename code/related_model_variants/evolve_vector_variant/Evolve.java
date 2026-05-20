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
		System.out.println("_________END__________");
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
				worker[workerCounter] =  new Worker(nodeId);
				workerCounter++;
			}
			  	
		}
		
		fin.close();
		System.out.println("_______AFTER INITIALIZATION_______");
	}	
	
	public void exchangeInformation(Worker currentWorker){
		
		Enumeration enum;
				
		int currentNodeId = currentWorker.currentNode;
		int currentOriginNodeId = -9, previousOriginNodeId = currentNodeId;
		int workerPathLength = currentWorker.pathLength;
		int tempPathLength;
			
		System.out.println("  exchangeInformation with node " + currentNodeId);
		System.out.print("    workerPathLength " + workerPathLength + " ");
		
		enum = currentWorker.path.elements();
		while(enum.hasMoreElements()){
			System.out.print(enum.nextElement());
		}
		System.out.println();

		int workerPathDistance = (int)0, nodePathDistance;
		int copyingNode, nextCopyingNode, copyingArcLength;
		
		for(int i = workerPathLength - 2; i >= 0; i--){
			currentOriginNodeId = ((Integer)currentWorker.path.elementAt(i)).intValue();
			////System.out.println("    currentOriginNode " + currentOriginNode);
			workerPathDistance += arc[currentOriginNodeId][previousOriginNodeId].length;
			nodePathDistance = node[currentNodeId].shortestPathDistances[currentOriginNodeId];
			if(workerPathDistance > nodePathDistance){
				//The worker learns from the node if the node knows a shorter
				//	path from the currentOriginNode to the currentNode
				workerPathDistance = nodePathDistance;
				tempPathLength = currentWorker.path.size();
				for(int j = (i + 1); j < (tempPathLength - 1); j++){
					currentWorker.path.removeElementAt(i + 1);
				}
				enum = node[currentNodeId].shortestPathsV[currentOriginNodeId].elements();
				while(enum.hasMoreElements()){
					currentWorker.path.insertElementAt(enum.nextElement(), (i + 1));
				
				}
			}else if(workerPathDistance < nodePathDistance){
				//The node learns from the worker if the worker knows a shorter
				//	path from the currentOriginNode to the currentNode
				node[currentNodeId].shortestPathDistances[currentOriginNodeId] = workerPathDistance;
				node[currentNodeId].shortestPathsV[currentOriginNodeId].removeAllElements();
				for(int j = (i + 1); j < (currentWorker.path.size() - 1); j++){
					node[currentNodeId].shortestPathsV[currentOriginNodeId].addElement(currentWorker.path.elementAt(j));
				}
			}else{
				//No information exchange if the paths known by the node and
				// the worker have equal lengths
			}
			previousOriginNodeId = currentOriginNodeId;	
		}
		currentWorker.updateWorkerVariables();
			
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
		
		Enumeration enum;
		
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
			enum = currentNode.shortestPathsV[i].elements();
			while(enum.hasMoreElements()){
				System.out.print(enum.nextElement());
			}
			System.out.println();
		}
	}
	
	
	public void updateWorker(Worker currentWorker){
		
		int pathLength, previousNode, currentNode, nextNode = -9; 
		int numDemandNodes, demandNodes[];
		int row = -9, column = -9;
		boolean rowIdentified = false, columnIdentified = false;
		float cumProbability = (float)0.0;
		
		pathLength = currentWorker.pathLength;
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
				//arcLength = arc[currentNode][nextNode].length;
				currentWorker.addNextNode(nextNode);
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
