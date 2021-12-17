package looter;

import java.io.IOException;

// Basic Layout for all States

import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class LevelPreview extends BasicGameState  {
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GLOBALS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	LooterGame Game;
	Input input; // player input listener
	float delta; // multiply any unit by this value to convert to units per second
	Camera cam;
	Room room;
	boolean debug;
	Vector3f target = new Vector3f();
	Image TestTexture;
	ArrayList<Button> buttons;
	ArrayList<Room> rooms;
	int index = 0;
	int xCoord;
	float yCoord;
	int yOffset;
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	UPDATE LOOP
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		ArrayList<GraphicsObj> renderList = new ArrayList<GraphicsObj>();
		room.render(renderList, cam);
		Collections.sort(renderList);
		g.drawString("Room Index: " + room.ID, xCoord, (int)(yCoord));
		g.drawString("Room Type: " + room.type, xCoord, (int)(yCoord)+yOffset);
		for(GraphicsObj r: renderList) r.draw();
		for(Button button:buttons) {
			button.render(container, g);
		}
	}

	
	@Override
	public void update(GameContainer container, StateBasedGame game, int milidelta) throws SlickException {
		delta = (float)milidelta/1000f;
		handleInput();
		cam.update(Game, delta);
		cam.target = target;
		updateButtons();
	}
	
	private void updateButtons() {
		for(Button button:buttons) {
//			if (button.isMouseOver()) System.out.println("button " + buttons.indexOf(button));
			if (button.isMouseOver() && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				switch (button.type){
				case TRANSITIONBUTTON:
					if ("Edit".equals(button.text)) {
						Game.curr = room;
						Game.rooms = rooms;
					}
					Game.enterState(button.flag, new FadeOutTransition(), new FadeInTransition());
					break;
				case TOGGLEBUTTON:
					switch(button.text) {
					case "Prevous":
						index--;
						if(index == -1) {
							index = rooms.size() - 1;
						}
						room = rooms.get(index);
						break;
					case "next":
						index = (index + 1) % rooms.size();
						room = rooms.get(index);
						break;
					}
					break;
				default:
					System.out.println("button " + buttons.indexOf(button)+ " pressed");
				}
			}
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GAME-STATE HANDLERS
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getID() {
		return GameState.LEVELPREVIEW.ordinal();
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY entrance
		input.clearKeyPressedRecord();
		cam = new Camera();
		cam.zoom = Game.ScreenWidth/5f;
		MuffinHelper MH = new MuffinHelper();
		try {
			room = MH.loadRooms(index); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(room == null) {
			room = new Room();
			int[][][] m = 
					{{
					{3,3,3},
					{2,2,2},
					{1,1,1}}
						,
					{{0,0,0},
					{0,0,0},
					{0,0,0}}};
			room.map = m;
		}
		else {
			rooms = MH.rooms;
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY exit
		// TODO Auto-generated method stub
	
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException { //called on initialization
		Game = (LooterGame) game;
		input = container.getInput();
		createButtons(container);
		xCoord = (int)(Game.ScreenWidth*.1f);
		yCoord = Game.ScreenHeight*.1f;
		yOffset = 15;
	}

	private void createButtons(GameContainer container) throws SlickException {
		buttons = new ArrayList<Button>();
		TestTexture = new Image("looter/resource/tile.png");
		
		Vector2f buttonCoord = new Vector2f(Game.ScreenWidth*.25f, Game.ScreenHeight*.9f);
		int yOffset = TestTexture.getHeight()+5;
		
		Button tempButton = new Button(container, TestTexture, buttonCoord.x, buttonCoord.y, "Prevous");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		buttonCoord.x = Game.ScreenWidth*.5f;
		tempButton = new Button(container, TestTexture, buttonCoord.x-yOffset, buttonCoord.y, "Edit");
		tempButton.setType(ButtonType.TRANSITIONBUTTON);
		tempButton.setFlag(GameState.MAPEDITOR.ordinal());
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset, buttonCoord.y, "test");
		tempButton.setType(ButtonType.TRANSITIONBUTTON);
		tempButton.setFlag(GameState.PLAYINGSTATE.ordinal());
		buttons.add(tempButton);
		
		buttonCoord.x = Game.ScreenWidth*.75f;
		tempButton = new Button(container, TestTexture, buttonCoord.x, buttonCoord.y, "next");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void handleInput() {
		if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) cam.lock = false;
		else cam.lock = true;
		
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			 Game.enterState(Game.getLastState(), new FadeOutTransition(), new FadeInTransition());
		}
		
		Vector3f movDir = new Vector3f();	
		movDir.x = (((input.isKeyDown(Input.KEY_D))? 1 : 0) - ((input.isKeyDown(Input.KEY_A))? 1 : 0))*delta;
		movDir.y = (((input.isKeyDown(Input.KEY_S))? 1 : 0) - ((input.isKeyDown(Input.KEY_W))? 1 : 0))*delta;
		movDir.z = (((input.isKeyDown(Input.KEY_E))? 1 : 0) - ((input.isKeyDown(Input.KEY_Q))? 1 : 0))*delta;
		VectorMath.rotate2d(movDir, VectorMath.getRotation(movDir)+cam.rotation);
		Vector3f.add(movDir, cam.target, cam.target);
	}
	
	public void keyPressed(int key, char code) {
	}
	
	public void mouseDragged(int old_x, int old_y, int new_x, int new_y) {
		Vector2f moved = new Vector2f(new_x - old_x, new_y - old_y);
		moved.scale(delta);
		
		if (!cam.lock) {
			cam.rotation += moved.x;
			cam.angle += moved.y;
		}
	}
	
	public void mouseWheelMoved(int newValue) {
		cam.zoom += newValue/10;
		if (cam.zoom <= 0) cam.zoom = 0;
	}

}


