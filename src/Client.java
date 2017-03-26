import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client implements Runnable {
	
	private BlockingQueue<String> leftMessageQueue;
	private BlockingQueue<String> rightMessageQueue;
	
	private String ipLeft;
	private int portLeft;
	
	private String ipRight;
	private int portRight;
	
	public Client(String ipLeft, int portLeft, String ipRight, int portRight) {
		this.ipLeft = ipLeft;
		this.portLeft = portLeft;
		this.ipRight = ipRight;
		this.portRight = portRight;
		
		this.leftMessageQueue = new ArrayBlockingQueue<String>(2);
		this.rightMessageQueue = new ArrayBlockingQueue<String>(2);
	}
	
	public void sendMessageToNeighbor(String message, boolean left) throws InterruptedException {
		if(left)
			this.leftMessageQueue.put(message);
		else
			this.rightMessageQueue.put(message);
	}
	
	@Override
	public void run() {
		Socket clientLeft = null;
		Socket clientRight = null;
		PrintStream pwLeft = null;
		PrintStream pwRight = null;
		boolean left = false;
		boolean right = false;
		
		// While neighbor servers are not up
		while(!left || !right) {
			if(!left) {
				try {
					clientLeft = new Socket(this.ipLeft, this.portLeft);
					pwLeft = new PrintStream(clientLeft.getOutputStream());
					left = true;
				} catch (IOException e) {
					System.err.print("Left: ");
					System.err.println(e.getMessage());
				}
			}
			
			if(!right) {
				try {
					clientRight = new Socket(this.ipRight, this.portRight);
					pwRight = new PrintStream(clientRight.getOutputStream());
					right = true;
				} catch (IOException e) {
					System.err.print("Right: ");
					System.err.println(e.getMessage());
				}
			}
		}
		
		while(true) {
			//client code that will run continuously
			if(!leftMessageQueue.isEmpty()) {
				// Send message to left neighbor
				try {
					pwLeft.println(leftMessageQueue.take());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(!rightMessageQueue.isEmpty()) {
				// Send message to right neighbor
				try {
					pwRight.println(rightMessageQueue.take());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
//			if(something)
//				break;
		}
		
//		try {
//			clientLeft.close();
//			clientRight.close();
//		} catch (IOException e) {
//		}	
	}
	
	
	
}
