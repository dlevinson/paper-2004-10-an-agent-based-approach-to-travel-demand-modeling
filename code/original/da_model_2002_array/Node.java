/**
 * @author Lei Zhang
 * 			Nov 7, 2002
 */

import java.io.*;
import java.awt.*;
import java.applet.*;
import java.net.*;   
import java.util.*;
import java.lang.*;

import Evolve;



public class Node {
	
	
	private int originalWorkers;
	private int originalJobs;
	private int xCoord;
	private int yCoord;
	private float beta;
	
	public int nodeId;
	public int currentWorkers;
	public int currentJobs;
	public int numDemandNodes;
	public int demandNodes[];
	public int shortestPathDistances[];
	public int shortestPaths[][];
	public float turningGuidance[][];
	
	static final int Large = 100;
	
	public Node(){
	}
	
	public Node(int nodeId, int nodeWorkers,int nodeJobs, int xCoord, int yCoord,
				 int numDemandNodes, int[] demandNodes, int numNodes, float beta){
		this.nodeId = nodeId;
		this.originalJobs = nodeJobs;
		this.currentJobs = nodeJobs;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.numDemandNodes = numDemandNodes;
		
		this.demandNodes = new int[numDemandNodes];
		for(int i = 0; i < numDemandNodes; i++){
			this.demandNodes[i] = demandNodes[i];
		}
		
		this.shortestPathDistances = new int[numNodes + 1];
		for(int i = 0; i < numNodes + 1; i++){
			this.shortestPathDistances[i] = Large;
		}
		
		this.shortestPaths = new int[numNodes + 1][Large];
		for(int i = 0; i < numNodes + 1; i++){
			for(int j = 0; j < Large; j++){
				this.shortestPaths[i][j] = -9;
			}
		}
		
		this.turningGuidance = new float[numDemandNodes + 1][numDemandNodes + 1];
		for(int i = 0; i < numDemandNodes + 1; i++){
			for(int j = 0; i < numDemandNodes + 1; i++){
				this.turningGuidance[i][j] = (float) 0.0;
			}
		}
		
		this.beta = beta;
	}
	
	//IMPORTANT The current updating method may generate unreasonable turning
	//			probabilities for nodes with only one demand node. This is OK
	//			for the testing network since there is no such kinkd of node.
	public void updateTurningGuidance(int[] demandNodeJobs){
		float sumJobs = 0;
		float currentSumJobs;
		
		for(int i = 0; i < numDemandNodes; i++){
			sumJobs += (float)demandNodeJobs[i];
		}
		sumJobs += beta*(float)currentJobs;
		
		//update turning probabilities for workers coming 
		//	from one of the demand nodes
		for(int i = 0; i < numDemandNodes; i++){
			currentSumJobs = sumJobs - demandNodeJobs[i];
			//Are there any jobs left in all possible demand nodes or the current node?
			if(currentSumJobs > 0){
				for(int j = 0; j < numDemandNodes; j++){
				if(i != j){
					turningGuidance[i][j] = (float)demandNodeJobs[j]/currentSumJobs;
				}else{
					turningGuidance[i][j] = (float)0.0;
				}
				}
				turningGuidance[i][numDemandNodes] = beta*(float)currentJobs/currentSumJobs;
			}else{
				for(int j = 0; j < numDemandNodes; j++){
				if(i != j){
					turningGuidance[i][j] = (float)1/(float)(numDemandNodes - 1);
				}else{
					turningGuidance[i][j] = (float)0.0;
				}
				}
				turningGuidance[i][numDemandNodes] = (float)0.0;
			}
		}

		//update turning probabilities for workers not coming 
		//	from any of the demand nodes. This is possible if there exists
		//	one-way roads - a supply node may not be a demand node.
		if(sumJobs > 0){
			for(int i = 0; i < numDemandNodes; i++){
				turningGuidance[numDemandNodes][i] = (float)demandNodeJobs[i]/sumJobs;
			}
			turningGuidance[numDemandNodes][numDemandNodes] = beta*(float)currentJobs/sumJobs;
		}else{
			for(int i = 0; i < numDemandNodes; i++){
				turningGuidance[numDemandNodes][i] = (float)1/(float)(numDemandNodes);
			}
			turningGuidance[numDemandNodes][numDemandNodes] = (float)0.0;
		}
		
		for(int i = 0; i < (numDemandNodes + 1); i++){
			System.out.print("    ");
			for(int j = 0; j < (numDemandNodes + 1); j++){
				System.out.print(turningGuidance[i][j] + " ");
			}
			System.out.println();
		}
		
	}
	
	
	
}
