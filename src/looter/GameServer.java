package looter;

import java.io.*;
import java.util.*;
import java.net.*;

//Server class
public class GameServer {

	static boolean isonline = true;

	// Vector to store active clients
	static ArrayList<GameClientHandler> ar = new ArrayList<>();
	static Map<GameClientHandler, Thread> arMap = new HashMap<>();
	static WorldSpace world;

	static ServerSocket ss;
	static Socket s;

	// counter for clients
	static int i = 0;

	public static void closeServer() throws InterruptedException {
		System.out.println("closing server");
		for (GameClientHandler ch : ar) {
			ch.close();
			arMap.get(ch).join();
			arMap.get(ch).stop();
		}
		try {
			ss.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isonline = false;
	}

	public static void runServer() {

		try {
			// server is listening on port 1234
			ss = new ServerSocket(1234);

			// running infinite loop for getting
			// client request
			while (true) {
				System.out.println("waiting");
				// Accept the incoming request
				try {
					s = ss.accept();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("ss is closed");
					break;
				}

				System.out.println("New client request received : " + s);

				ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
//				dos.flush();

				// obtain input and output streams
				InputStream inputStream = s.getInputStream();
				System.out.println("input stream");
				ObjectInputStream dis = new ObjectInputStream(inputStream);
				System.out.println("input streamed");

				System.out.println("Creating a new handler for this client...");

				// Create a new handler object for handling this request.
				GameClientHandler mtch = new GameClientHandler(s, i, dis, dos);

				// Create a new Thread with this object.
				Thread t = new Thread(mtch);

				arMap.put(mtch, t);
				System.out.println("Adding this client to active client list");

				// add this client to active clients list
				ar.add(mtch);

				t.setName("Server_CH" + i);
				// start the thread.
				t.start();

				// increment i for new client.
				// i is used for naming only, and can be replaced
				// by any naming scheme
				i++;

			}
		} catch (IOException e) {
//			throw new RuntimeException(e);
		}
	}

	public static HashMap<Integer, InetAddress> getConnections() {
		HashMap<Integer, InetAddress> ret = new HashMap<Integer, InetAddress>();
		for (GameClientHandler handle : ar) {
			ret.put(handle.ID, handle.s.getInetAddress());
		}
		return ret;
	}

	public static int getHostID() {
		for (GameClientHandler handle : ar) {
			if (handle.isHost)
				return handle.ID;
		}
		return -1;
	}
}

//ClientHandler class
class GameClientHandler implements Runnable {
	Scanner scn = new Scanner(System.in);
	int ID;
	final ObjectInputStream dis;
	final ObjectOutputStream dos;
	Socket s;
	boolean isloggedin = false;
	boolean isHost = false;
	

	// constructor
	public GameClientHandler(Socket s, int ID, ObjectInputStream dis, ObjectOutputStream dos) {
		this.dis = dis;
		this.dos = dos;
		this.ID = ID;
		this.s = s;
	}

	public void close() {
		try {
			dis.close();
			dos.close();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			throw new RuntimeException(e);
			System.out.println("failed to close ClientHandler");
		}
	}

	@Override
	public void run() {
		DataPacket received = null;
		while (true) {
			try {
				// receive the string
				try {
					received = (DataPacket) dis.readObject();
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					break;
				}

				if (received != null && received.tType == TransmissionType.logoutrequest) {
					System.out.println(ID + " wants to logout");
					this.isloggedin = false;
					break;
				}

				switch (received.tType) {
				case declaration:
					declare(received);
					break;
				case broadcast:
					broadCast(received);
					break;
				case update:
					send(received);
				case heartbeat:
					isloggedin =true;
					break;
				}

			} catch (IOException e) {
				isloggedin = false;
//				e.printStackTrace();
			}

		}
	}
	
	public int startGame() {
		int ret = 0;
		for (GameClientHandler mc: GameServer.ar) {
			if (mc.isloggedin)ret++;
		}
		return ret;
	}
	
	public void send(DataPacket received) throws IOException {
		for (GameClientHandler mc : GameServer.ar) {
			if (mc.isHost) {
				mc.dos.writeObject(received);
			}
		}
	}

	public void declare(DataPacket received) throws IOException {
		if (received.dType == DataType.STRING && "HOST".equals((String) received.data))
			this.isHost = true;
		if (this.isHost && received.dType == DataType.STRING && "LOAD".equals((String)received.data)) {
			broadCast(new DataPacket(TransmissionType.broadcast, DataType.STRING, "LOAD"));
			send(new DataPacket(TransmissionType.update, DataType.INT, startGame()));
		}
		if (!this.isHost && received.dType == DataType.STRING && "WORLD".equals((String)received.data)){
			if(GameServer.world != null) {
				direct(new DataPacket(TransmissionType.broadcast, DataType.WORLD, GameServer.world));
			}
		}
			
	}
	
	public void direct(DataPacket received) throws IOException {
		for (GameClientHandler mc : GameServer.ar) {
			if (this.ID == mc.ID) {
				mc.dos.writeObject(received);
			}
		}
	}

	public void broadCast(DataPacket received) throws IOException {
		if (received.dType != DataType.WORLD) {
			for (GameClientHandler mc : GameServer.ar) {
				if (this.ID != mc.ID) {
					mc.dos.writeObject(received);
					mc.dos.writeObject(new DataPacket(TransmissionType.broadcast, DataType.INT, mc.ID));
				}
			}
		}
		else {
			GameServer.world = (WorldSpace) received.data;
			System.out.println(GameServer.world.map);
		}
		
	}
}
