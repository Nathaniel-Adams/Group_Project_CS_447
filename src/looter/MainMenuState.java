package looter;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class MainMenuState extends BasicGameState  {
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GLOBALS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	LooterGame Game;
	Input input; // player input listener
	float delta; // multiply any unit by this value to convert to units per second
//	Camera cam;
	
	Image TestTexture;
	ArrayList<Button> buttons;
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	UPDATE LOOP
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
//		g.resetTransform();
//		g.translate(cam.pos.x, cam.pos.y);
//		g.scale(cam.zoom, cam.zoom);
		g.setColor(Color.green);
		String temp = "Main Menu";
		g.drawString(temp, Game.ScreenWidth/2-(float)temp.length()*4.58f, Game.ScreenHeight/2);
		g.resetTransform();
		g.setColor(Color.white);
		
		for(Button button:buttons) {
			button.render(container, g);
		}
		
	}

	
	@Override
	public void update(GameContainer container, StateBasedGame game, int milidelta) throws SlickException {
		delta = (float)milidelta/1000f;
		handleInput();
		updateButtons();
	}


	private void updateButtons() {
		for(Button button:buttons) {
//			if (button.isMouseOver()) System.out.println("button " + buttons.indexOf(button));
			if (button.isMouseOver() && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				switch (button.type){
				case TRANSITIONBUTTON:
//					if ("Host Game".equals(button.text)) Game.is_hosting = true;
					Game.enterState(button.flag, new FadeOutTransition(), new FadeInTransition());
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
		return GameState.MAINMENUSTATE.ordinal();
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY entrance
		input.clearKeyPressedRecord();
		Game.setLastState(getID());
//		cam = new Camera();
//		cam.zoom = 1;
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY exit
		
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException { //called on initialization
		Game = (LooterGame) game;
		input = container.getInput();
		createButtons(container);
			
		System.out.println(TestTexture.getHeight());
		
	}


	private void createButtons(GameContainer container) throws SlickException {
		buttons = new ArrayList<Button>();
		TestTexture = new Image("looter/resource/tile.png");
		
		int xCoord = (int)(Game.ScreenWidth*.75f);
		float yCoord = Game.ScreenHeight/2f;
		int yOffset = TestTexture.getHeight()+10;
		Button tempButton = new Button(container, TestTexture, xCoord, (int)(yCoord), "Host Game");
		tempButton.setType(ButtonType.TRANSITIONBUTTON);
		tempButton.setFlag(GameState.LOBBYSTATE.ordinal());
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, xCoord, (int)(yCoord)+yOffset, "Join Game");
		tempButton.setType(ButtonType.TRANSITIONBUTTON);
		tempButton.setFlag(GameState.LOBBYSTATE.ordinal());
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, xCoord, (int)(yCoord)+2*yOffset, "Single Player");
		tempButton.setType(ButtonType.TRANSITIONBUTTON);
		tempButton.setFlag(GameState.PLAYINGSTATE.ordinal());
		buttons.add(tempButton);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void handleInput() {
		if (input.isKeyPressed(Input.KEY_ESCAPE)) Game.enterState(Game.getLastState(), new FadeOutTransition(), new FadeInTransition());
	}
	
	public void keyPressed(int key, char code) {
//		System.out.println(code);
	}
	
	public void mouseDragged(int old_x, int old_y, int new_x, int new_y) {
//		System.out.println(old_x+","+old_y+"|"+new_x+","+new_y);
		Vector2f moved = new Vector2f(new_x - old_x, new_y - old_y);
//		cam.pos.setX(moved.x+cam.pos.x);
//		cam.pos.setY(moved.y+cam.pos.y);
	}

}



