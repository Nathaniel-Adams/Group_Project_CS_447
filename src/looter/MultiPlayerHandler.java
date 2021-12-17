package looter;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.state.StateBasedGame;

public class MultiPlayerHandler {

	LooterGame Game;
	Thread server;
	Thread client;
	HashMap<Integer, String> connections = new HashMap<Integer,String>();
	String status;
	
	
	public MultiPlayerHandler(LooterGame game) {
		this.Game = game;
	}

	public void startMultiplayer() {
		Game.is_online = true;
		if (Game.is_hosting) {
			startHostThread();
		}
		startClientThread();
		if (Game.is_hosting) GameClient.send(new DataPacket(TransmissionType.declaration, DataType.STRING, "HOST"));
		GameUpdate updateThread = new GameUpdate(Game);
	}
	
	

	public void stopMultiplayer() {
		Game.is_online = true;
		if(server != null && server.isAlive()) {
			//
		}
		GameClient.send(new DataPacket(TransmissionType.logoutrequest, DataType.STRING, "NOOOO"));
		GameClient.closeClient();
	}
	
	private void startClientThread() {
		status = "---";
		Runnable task = () -> {
			System.out.println("Client started");
			try {
				GameClient.runClient();
				status = "CONNECTED";
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
				status = "COULD NOT CONNECT";
			}
        };
 
        client = new Thread(task);
        client.setName("Game_Client");
 
        client.start();
	}


	private void startHostThread() {
		Runnable task = () -> {
			System.out.println("booting up server");
			GameServer.runServer();
        };
 
        server = new Thread(task);
        server.setName("Game_Server");
 
        server.start();
	}
	
	
	
}
