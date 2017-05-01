package zookeeper;

import org.apache.zookeeper.KeeperException;

public class zktest {

	public static void main(String[] args) throws KeeperException, InterruptedException {
		// TODO Auto-generated method stub
		ZKQueue queue1 = new ZKQueue("137.112.223.149", "/testqueue");
		System.out.println(queue1.produce("test1"));
		//System.out.println(queue1.produce("test2"));
		String value = queue1.consume();
		System.out.println(value);
		
	}

}
