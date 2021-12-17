package looter;

import java.io.*;
import java.util.*;
import java.net.*;

//Server class
public class Server
{

	// Vector to store active clients
	static Vector<ClientHandler> ar = new Vector<>();
	
	// counter for clients
	static int i = 0;

	public static void main(String[] args) throws IOException
	{
		// server is listening on port 1234
		ServerSocket ss = new ServerSocket(1234);
		
		Socket s;
		
		// running infinite loop for getting
		// client request
		while (true)
		{
			// Accept the incoming request
			s = ss.accept();

			System.out.println("New client request received : " + s);
			
			// obtain input and output streams
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			
			System.out.println("Creating a new handler for this client...");

			// Create a new handler object for handling this request.
			ClientHandler mtch = new ClientHandler(s,"" + i, dis, dos);

			// Create a new Thread with this object.
			Thread t = new Thread(mtch);
			
			System.out.println("Adding this client to active client list");

			// add this client to active clients list
			ar.add(mtch);
			for (ClientHandler handle:ar) {
				System.out.print(handle.s + " " + handle.name);
			}
			

			// start the thread.
			t.start();

			// increment i for new client.
			// i is used for naming only, and can be replaced
			// by any naming scheme
			i++;

		}
	}
}

//ClientHandler class
class ClientHandler implements Runnable
{
	Scanner scn = new Scanner(System.in);
	String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket s;
	boolean isloggedin;
	
	// constructor
	public ClientHandler(Socket s, String name,
							DataInputStream dis, DataOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.name = name;
		this.s = s;
		this.isloggedin=true;
	}

	@Override
	public void run() {

		System.out.println(s.getLocalAddress());
		String received;
		while (true)
		{
			try
			{
				// receive the string
				received = dis.readUTF();
				
				System.out.println(received);
				
				if(received.equals("logout")){
					this.isloggedin=false;
					this.s.close();
					break;
				}
				
				// break the string into message and recipient part on # delim
				StringTokenizer st = new StringTokenizer(received, "#");
				String MsgToSend = st.hasMoreTokens() ? st.nextToken() : "";
				String recipient = st.hasMoreTokens() ? st.nextToken() : "";

				if ("".equals(recipient)) {
					for (ClientHandler mc : Server.ar)
					{
						// if the recipient is found, write on its
						// output stream
						if (!mc.name.equals(name) && mc.isloggedin==true)
						{
							mc.dos.writeUTF(this.name+" : "+MsgToSend);
						}
					}
				}
				else {
					for (ClientHandler mc : Server.ar)
					{
						// if the recipient is found, write on its
						// output stream
						if (mc.name.equals(recipient) && mc.isloggedin==true)
						{
							mc.dos.writeUTF(this.name+" : "+MsgToSend);
							break;
						}
					}
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		try
		{
			// closing resources
			this.dis.close();
			this.dos.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
