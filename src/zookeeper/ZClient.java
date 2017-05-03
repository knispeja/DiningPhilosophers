package zookeeper;

import java.util.concurrent.BlockingQueue;

import org.apache.zookeeper.KeeperException;

import philosophy.ClientInterface;

public class ZClient implements ClientInterface {

	private static final int QUEUE_SIZE = 3;
	
	private BlockingQueue<String> leftMessageQueue;
	private BlockingQueue<String> rightMessageQueue;
	
	private int leftID;
	private int rightID;
	private int thisID;
	
	private ZKQueue leftZKQueue;
	private ZKQueue rightZKQueue;
	
	public ZClient(int leftID, int rightID, int thisID, String zkAddress){
		this.leftID = leftID;
		this.rightID = rightID;
		this.thisID = thisID;
		
		leftZKQueue = new ZKQueue(zkAddress, "/" + thisID+"-"+leftID);
		rightZKQueue = new ZKQueue(zkAddress, "/" + thisID+"-"+rightID);
	}
	

	@Override
	public void sendMessageToNeighbor(String message, boolean left) throws InterruptedException, KeeperException {
		if(left)
			this.leftZKQueue.produce(message);
			//this.leftMessageQueue.put(message);
		else
			this.rightZKQueue.produce(message);
			//this.rightMessageQueue.put(message);
	}


}
