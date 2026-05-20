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

public class Worker {
	public int status;
	public int currentNode;
	public int previousNode;
	public int pathLength;
	public int path[];
	public int knowledge[];
	
	public Worker(){
	}
	
	final static int Large = 50;
	public Worker(int nodeId, int numNodes){
		this.status = 0;
		this.currentNode = nodeId;
		this.previousNode = nodeId;
		this.pathLength = 1;
		this.path = new int[Large];
		this.path[0] = nodeId;
		for(int i = 1; i < Large; i++){
			this.path[i] = -9;
		}
		this.knowledge = new int[Large];
		for(int i = 0; i < Large; i++){
			this.knowledge[i] = -9;
		}
	}  
	
	public void addNextNode(int nextNode, int arcLength){
		path[pathLength] = nextNode;
		knowledge[pathLength - 1] = arcLength;
		pathLength++;
		previousNode = currentNode;
		currentNode = nextNode;
	}
	
	public void updatePathLength(){
		int counter = 0;
		while(path[counter] != -9){
			counter++;
		}
		pathLength = counter;
	}

}
