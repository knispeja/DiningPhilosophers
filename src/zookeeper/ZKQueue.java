package zookeeper;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

/*
 * Adapted from https://zookeeper.apache.org/doc/trunk/zookeeperTutorial.html
 *
 * 
 * 
 */

public class ZKQueue extends SyncPrimitive{
    /**
     * Constructor of producer-consumer queue
     *
     * @param address
     * @param name
     */
	ZKQueue(String address, String name) {
        super(address);
        this.root = name;
        // Create ZK node name
        if (zk != null) {
            try {
                Stat s = zk.exists(root, false);
                if (s == null) {
                    zk.create(root, new byte[0], Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                System.out
                        .println("Keeper exception when instantiating queue: "
                                + e.toString());
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception");
            }
        }
    }
	
	
    /**
     * Add element to the queue.
     *
     * @param i
     * @return
     */

    boolean produce(String m) throws KeeperException, InterruptedException{
        //ByteBuffer b = ByteBuffer.allocate(4);
        byte[] value;

        // Add child with value i
        //b.putInt(i);
        //value = b.array();
        value  = m.getBytes(StandardCharsets.UTF_8);
        zk.create(root + "/element", value, Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL);

        return true;
    }
    
    /**
     * Remove first element from the queue.
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    String consume() throws KeeperException, InterruptedException {
        int retvalue = -1;
        Stat stat = null;

        // Get the first element available
        while (true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren(root, true);
                if (list.size() == 0) {
                    System.out.println("Going to wait");
                    mutex.wait();
                } else {
                    Integer min = new Integer(list.get(0).substring(7));
                    for(String s : list){
                        Integer tempValue = new Integer(s.substring(7));
                        //System.out.println("Temporary value: " + tempValue);
                        if(tempValue < min) min = tempValue;
                    }
                    String padded = String.format("%010d", min);
                    //System.out.println("Temporary value: " + root + "/element" + padded);
                    byte[] b = zk.getData(root + "/element" + padded,
                                false, stat);
                    zk.delete(root + "/element" + padded, 0);
                    
                    String returnedString =  new String(b, StandardCharsets.UTF_8);
                    //ByteBuffer buffer = ByteBuffer.wrap(b);
                    //retvalue = buffer.getInt();

                    return returnedString;
                }
            }
        }
    }
    
    Boolean isQueueEmpty() throws KeeperException, InterruptedException{
        int retvalue = -1;
        Stat stat = null;

        // Get the first element available
        while (true) {
            synchronized (mutex) {
                List<String> list = zk.getChildren(root, true);
                if (list.size() == 0) {
                    return true;
                } else {
                	return false;
                }
            }
        }
    }
     
}
