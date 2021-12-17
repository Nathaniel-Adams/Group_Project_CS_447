package looter;

import java.util.ArrayList;
import java.io.IOException;
import java.net.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class LobbyState extends BasicGameState  {
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GLOBALS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	LooterGame Game;
	Input input; // player input listener
	float delta; // multiply any unit by this value to convert to units per second
	Camera cam;
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	UPDATE LOOP
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
//		g.resetTransform();
		g.translate(cam.pos.x, cam.pos.y);
		try {
			renderText(g);
		} catch (UnknownHostException e) {
		}
	}
	
	public void renderText(Graphics g) throws UnknownHostException {
		g.setColor(Color.green);
		String temp = "Lobby";
		g.drawString(temp, Game.ScreenWidth/2/cam.zoom-(float)temp.length()*4.58f, Game.ScreenHeight/2/cam.zoom);
		
		if (!Game.is_hosting) {
			g.setColor(Color.red);
			g.drawString("JOINING", Game.ScreenWidth/2/cam.zoom-(float)"JOINING".length()*4.58f, Game.ScreenHeight/2/cam.zoom+40);
		}
		else {
			temp = InetAddress.getLocalHost().getHostAddress();
			g.drawString(temp, Game.ScreenWidth/2/cam.zoom-(float)temp.length()*4.58f, Game.ScreenHeight/2/cam.zoom+20);
			g.drawString("HOSTING", Game.ScreenWidth/2/cam.zoom-(float)"HOSTING".toString().length()*4.58f, Game.ScreenHeight/2/cam.zoom+40);
		}
		temp = Game.mpHandler.status;
		g.drawString(temp, Game.ScreenWidth/2/cam.zoom-(float)temp.toString().length()*4.58f, Game.ScreenHeight*.9f/cam.zoom);
		
	}

	
	@Override
	public void update(GameContainer container, StateBasedGame game, int milidelta) throws SlickException {
		delta = (float)milidelta/1000f;
		handleInput();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GAME-STATE HANDLERS
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getID() {
		return GameState.LOBBYSTATE.ordinal();
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY entrance
		input.clearKeyPressedRecord();
		Game.setLastState(getID());
		cam = new Camera();
		Game.mpHandler = new MultiPlayerHandler(Game);
		Game.mpHandler.startMultiplayer();
	}
	
	


	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY exit
		
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException { //called on initialization
		Game = (LooterGame) game;
		input = container.getInput();
		
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void handleInput() {
		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			Game.is_hosting = false;
			Game.mpHandler.stopMultiplayer();
//			GameServer.closeServer();
//			if (Game.server != null && Game.server.isAlive()) Game.server.stop();
//			if (Game.client != null && Game.client.isAlive()) Game.client.stop();
//			
			Game.enterState(Game.getLastState(), new FadeOutTransition(), new FadeInTransition());
		}
		
		if (input.isKeyPressed((Input.KEY_SPACE))){
			GameClient.send(new DataPacket(TransmissionType.declaration, DataType.STRING, "LOAD"));
			
		}
	}
	
	public void keyPressed(int key, char code) {
//		System.out.println(code);
	}
	
	public void mouseDragged(int old_x, int old_y, int new_x, int new_y) {
//		System.out.println(old_x+","+old_y+"|"+new_x+","+new_y);
		Vector2f moved = new Vector2f(new_x - old_x, new_y - old_y);
		cam.pos.setX(moved.x+cam.pos.x);
		cam.pos.setY(moved.y+cam.pos.y);
	}

}


