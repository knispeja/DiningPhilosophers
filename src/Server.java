import java.util.concurrent.BlockingQueue;

class Server implements Runnable{

	private int port;
	private BlockingQueue<String> requests;
	
	public Server(int port, BlockingQueue<String> requests) {
		this.port = port;
		this.requests = requests;
	}
	
	@Override
	public void run() {
		//all server code here
		//you should have a "left" server connection
		//and a "right" server connection

		while(true) {
			//server code that will run continuously
		}
	}
}
