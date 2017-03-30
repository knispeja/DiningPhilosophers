import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client implements Runnable {
	
	private static final int QUEUE_SIZE = 2;
	
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
		
		this.leftMessageQueue = new ArrayBlockingQueue<String>(QUEUE_SIZE);
		this.rightMessageQueue = new ArrayBlockingQueue<String>(QUEUE_SIZE);
	}
	
	public void sendMessageToNeighbor(String message, boolean left) throws InterruptedException {
		if(left)
			this.leftMessageQueue.put(message);
		else
			this.rightMessageQueue.put(message);
	}
	
	@Override
	public void run() {
		while(true) {
			//client code that will run continuously
			if(!leftMessageQueue.isEmpty()) {
				Socket clientLeft = null;
				PrintStream pwLeft = null;
				try {
					clientLeft = new Socket(this.ipLeft, this.portLeft);
					pwLeft = new PrintStream(clientLeft.getOutputStream());
					pwLeft.println(leftMessageQueue.take());
					pwLeft.close();
					clientLeft.close();
				} catch (IOException | InterruptedException e) {
					System.out.println("My left neighbor isn't responding...");
				}
			}
			
			if(!rightMessageQueue.isEmpty()) {
				PrintStream pwRight = null;
				Socket clientRight = null;
				try {
					clientRight = new Socket(this.ipRight, this.portRight);
					pwRight = new PrintStream(clientRight.getOutputStream());
					pwRight.println(rightMessageQueue.take());
					pwRight.close();
					clientRight.close();
				} catch (IOException | InterruptedException e) {
					System.out.println("My right neighbor isn't responding...");
				}
			}
		}
	}	
}
