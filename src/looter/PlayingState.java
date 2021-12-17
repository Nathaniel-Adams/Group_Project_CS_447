package looter;


import java.util.ArrayList;

import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;


public class PlayingState extends BasicGameState  {
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GLOBALS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	LooterGame Game;
	Input input; // player input listener
	float delta; // multiply any unit by this value to convert to units per second
	
	WorldSpace world;
	
	
	boolean debug = false;
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	UPDATE LOOP
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		world.render(container, Game, g);
		if (debug) {
			
		}
	}

	
	@Override
	public void update(GameContainer container, StateBasedGame game, int milidelta) throws SlickException {
//		System.out.println(input.getMouseX() + " | "+ input.getMouseY());
		delta = (float)milidelta/1000f;
		handleInput();
		world.update(container, Game, delta);
//		System.out.println(cam.zoom);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GAME-STATE HANDLERS
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getID() {
		return GameState.PLAYINGSTATE.ordinal();
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY entrance
		input.clearKeyPressedRecord();
		world = new WorldSpace();
		world.init(container, Game);
//		world.map.print();
		Game.worldspace=world;

	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY exit
		Game.worldspace = null;
	
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
		if(input.isKeyDown(Input.KEY_UP)) {
			int set = BitMasker.getMaxHeight(world.map.Dungeon.get(0).map[0][2][0]);
			world.map.Dungeon.get(0).map[0][2][0] = BitMasker.setMaxHeight(world.map.Dungeon.get(0).map[0][2][0], set+1);   
		}
		if(input.isKeyDown(Input.KEY_DOWN)) {
			int set = BitMasker.getMaxHeight(world.map.Dungeon.get(0).map[0][2][0]);
			world.map.Dungeon.get(0).map[0][2][0] = BitMasker.setMaxHeight(world.map.Dungeon.get(0).map[0][2][0], set-1);
		}
		if(input.isKeyDown(Input.KEY_LEFT)) {
			System.out.println(world.map.Dungeon.get(0).map[0][2][0] +" | "+ Integer.toBinaryString(world.map.Dungeon.get(0).map[0][2][0]));
		}
		if(input.isKeyDown(Input.KEY_RIGHT)) {
			System.out.println(BitMasker.setEffectIndex(0, 7));
		}
		
		if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) world.cam.lock = false;
		else world.cam.lock = true;
		
		if(input.isKeyPressed(Input.KEY_ESCAPE)) {
			 Game.enterState(Game.getLastState(), new FadeOutTransition(), new FadeInTransition());
		}
		
//		if(input.isKeyPressed(Input.KEY_P)) {
//			world.addPlayer();
//		}
//		
//		if(input.isKeyPressed(Input.KEY_O)) {
//			world.addEnemy();
//		}
		
		if (input.isKeyPressed(Input.KEY_TAB)) {
			debug = !debug;
		}
	}
	
	public void keyPressed(int key, char code) {
	}
	
	public void mouseDragged(int old_x, int old_y, int new_x, int new_y) {
		Vector2f moved = new Vector2f(new_x - old_x, new_y - old_y);
		moved.scale(delta);
		
		if (!world.cam.lock) {
			world.cam.rotation += moved.x;
			world.cam.angle += moved.y;
		}
//		else {
//			world.cam.pos.x -= moved.x;
//			world.cam.pos.y -= moved.y;
//		}
	}
	
	public void mouseWheelMoved(int newValue) {
		world.cam.zoom += newValue/10;
		if (world.cam.zoom <= 0) world.cam.zoom = 0;
	}

}


