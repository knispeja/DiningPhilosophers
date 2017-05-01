package philosophy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.BlockingQueue;

class Server implements Runnable{
	
	private int port;
	private BlockingQueue<Request> requests;
	
	public Server(int port, BlockingQueue<Request> requests) {
		this.port = port;
		this.requests = requests;
	}
	
	@Override
	public void run() {

		ServerSocket server;
		try{
			server = new ServerSocket(port);
		}catch(IOException e){
			System.err.println(e.getMessage());
			return;
		}

		while(true) {
			Socket client;
			try{
				client = server.accept();
				InputStreamReader isr = new InputStreamReader(client.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				while(true) {
					if(br.ready()) {
						String message = br.readLine();
						//requests.add(new Request(message, client.getInetAddress().getHostAddress()));
						break;
					}
				}
				client.close();
			}catch(IOException e){
				System.err.println(e.getMessage());
				try {
					server.close();
				} catch (IOException e1) {
				}
				return;
			}
		}
	}
}
