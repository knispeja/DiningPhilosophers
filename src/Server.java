import java.io.IOException;
import java.net.*;
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
		
		ServerSocket server;
		try{
			server = new ServerSocket(port);
		}catch(IOException e){
			System.err.println(e.getMessage());
			return;
		}

		while(true) {
			//server code that will run continuously
			
			Socket client;
			try{
				client = server.accept();
			}catch(IOException e){
				System.err.println(e.getMessage());
				return;
			}
			
			
			
		}
	}
}
