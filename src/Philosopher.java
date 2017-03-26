/**
 * @author Peter Larson
 * @author Jacob Knispel
 * @author Ruinan Zhang
 */
public class Philosopher {


	public static void main(String[] args) {

		//create new instances of Client and Server
		Runnable r1 = new Client();
		Runnable r2 = new Server();


		//Create threads to run Client and Server as Threads
		Thread t1 = new Thread(r1);
		Thread t2 = new Thread(r2);

		//start the threads
		t1.start();
		t2.start();

	}

}
