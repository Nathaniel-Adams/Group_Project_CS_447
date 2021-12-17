package looter;

//Java implementation for multithreaded chat client
//Save file as Client.java
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

public class GameClient
{
	final static int ServerPort = 1234;
	static Queue<Object> queue = new LinkedList<>();
	static Queue<Object> received = new LinkedList<>();
	
	private static Thread sendThread;
	static boolean sending = true;
	private static Thread readThread;
	static boolean reading = true;

	public static void send(Object data) {
//		System.out.println("tryin to send");
		queue.add(data);
//		System.out.println(queue);
	}
	
	public static void closeClient() {
		sending = false;
		reading = false;
	}
	
	public static void runClient() throws UnknownHostException, IOException
	{
	    String my_ip = InetAddress.getLocalHost().getHostAddress();
		// getting localhost ip
	    
		InetAddress ip = InetAddress.getByName("192.168.137.57");
		
		// establish the connection
		Socket s = new Socket(my_ip, ServerPort);
		
		// obtaining input and out streams
		ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
//		dos.flush();
		ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
		
		// sendMessage thread
		Runnable sendMessage = () -> {
			while (sending) {
				// read the message to deliver.
//				String msg = scn.nextLine();
				if(!queue.isEmpty()) {
					try {
//						System.out.println("Things to send: "+queue.size());
						// write on the output stream
						dos.writeObject(queue.poll());
						dos.flush();
					} catch (IOException e) {
						//only errors if message is attempted to be sent
						break;
					}
				} else
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
						break;
					}
			}
		};
		
		
		// readMessage thread
		Runnable readMessage = () -> {
			while (reading) {
				try {
					// read the message sent to this client
					Object msg = dis.readObject();
//					System.out.println("Client Received "+msg);
					received.add(msg);
				} catch (IOException | ClassNotFoundException e) {
					//errors when connection to host is interrupted
					break;
				}
			}
		};

		sendThread = new Thread(sendMessage);
		sendThread.setName("Client_sendThread");
		sendThread.start();
		readThread = new Thread(readMessage);
		readThread.setName("Client_readThread");
		readThread.start();
	}
}

