package philosophy;

import org.apache.zookeeper.KeeperException;

public interface ClientInterface {

	void sendMessageToNeighbor(String message, boolean left) throws InterruptedException, KeeperException;

}