package zookeeper;

import org.apache.zookeeper.KeeperException;

import philosophy.ClientInterface;

public class ZClient implements ClientInterface {
	
	private ZKQueue leftZKQueue;
	private ZKQueue rightZKQueue;
	
	public ZClient(int leftID, int rightID, int thisID, String zkAddress){
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
