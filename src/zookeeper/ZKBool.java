
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
public class ZKBool extends SyncPrimitive{
    /**
     * Constructor
     *
     * @param address
     * @param name
     */
	
	private String TRUE = "TRUE";
	private String FALSE = "FALSE";
	
	
	public ZKBool(String address, String name, boolean initVal) {
        super(address);
        
        byte[] value;
    	if(initVal){
    		value = this.TRUE.getBytes(StandardCharsets.UTF_8);	
    	} else {
    		value = this.FALSE.getBytes(StandardCharsets.UTF_8);	
    	}
    	
        this.root = "/" + name;
        // Create ZK node name
        if (zk != null) {
            try {
                Stat s = zk.exists(root, false);
                if (s == null) {
                    zk.create(root, value, Ids.OPEN_ACL_UNSAFE,
                            CreateMode.EPHEMERAL);
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

    public void set(Boolean b) throws KeeperException, InterruptedException{
        //ByteBuffer b = ByteBuffer.allocate(4);
        byte[] value;
    	if(b){
    		value = this.TRUE.getBytes(StandardCharsets.UTF_8);	
    	} else {
    		value = this.FALSE.getBytes(StandardCharsets.UTF_8);	
    	}
    	
    	synchronized (mutex){
    		zk.setData(root, value, -1);
    	}
    }
    
    public boolean get() throws KeeperException, InterruptedException{
        byte[] b;
        Stat stat = null;
        
    	synchronized (mutex){
    		b = zk.getData(root, false, stat);
    	}
    	
    	String returnedString =  new String(b, StandardCharsets.UTF_8);
    	if(returnedString.equals(this.TRUE)){
    		return true;
    	} else if (returnedString.equals(this.FALSE)){
    		return false;
    	} else {
    		System.err.println("Invalid Value");
    		return false;
    	}
    }
 
     
}
