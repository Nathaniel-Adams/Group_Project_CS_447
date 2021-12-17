package looter;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class TitleScreenState extends BasicGameState  {
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
		g.setColor(Color.green);
		String temp = "Looter Game";
		g.drawString(temp, Game.ScreenWidth/2/cam.zoom-(float)temp.length()*4.58f, Game.ScreenHeight/2/cam.zoom);
		temp = "PRESS SPACE";
		g.drawString(temp, Game.ScreenWidth/2/cam.zoom-(float)temp.length()*4.58f, Game.ScreenHeight/2/cam.zoom+50f);
		g.resetTransform();
		g.setColor(Color.white);
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
		return GameState.TITLESCREENSTATE.ordinal();
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY entrance
		input.clearKeyPressedRecord();
		Game.setLastState(getID());
		cam = new Camera();
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
		if (input.isKeyPressed(Input.KEY_SPACE)) Game.enterState(GameState.MAINMENUSTATE.ordinal(), new FadeOutTransition(), new FadeInTransition());
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


