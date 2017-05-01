package zookeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import philosophy.Request;

public class ZServer implements Runnable{

	private int leftID;
	private int rightID;
	private int thisID;
	private int updateMili = 100;
	private String zkAddress;
	private BlockingQueue<Request> requests;
	
	public ZServer(BlockingQueue<Request> requests, String zkAddress, int leftID, int rightID, int thisID) {
		this.requests = requests;
		this.leftID = leftID;
		this.rightID = rightID;
		this.thisID = thisID;
		this.zkAddress = zkAddress;
	}
	
	@Override
	public void run() {

		ZKQueue leftQueue = new ZKQueue(zkAddress, leftID+"-"+thisID);
		ZKQueue rightQueue = new ZKQueue(zkAddress, rightID+"-"+thisID);		
		
		while(true) {
			try {
				Thread.sleep(updateMili);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				System.err.println("Sleep Interrupted");
			}

			try{
				if(!leftQueue.isQueueEmpty()){
					String msg = leftQueue.consume();
					requests.add(new Request(msg, leftID));
				}
			}catch(Exception e){
				System.err.println(e.getMessage());
			}
			
			try{
				if(!rightQueue.isQueueEmpty()){
					String msg = rightQueue.consume();
					requests.add(new Request(msg, rightID));
				}
			}catch(Exception e){
				System.err.println(e.getMessage());
			}
		}
	}
	
}
