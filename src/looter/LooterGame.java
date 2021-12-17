package looter;

import java.util.ArrayList;
import java.util.Stack;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.state.StateBasedGame;

enum GameState{
	TITLESCREENSTATE,
	MAINMENUSTATE,
	LOBBYSTATE,
	PLAYINGSTATE,
	MAPEDITOR,
	LEVELPREVIEW
}

public class LooterGame extends StateBasedGame{

	Stack<Integer> stateStack = new Stack<Integer>();
	
	// States
	
//	public static final int TITLESCREENSTATE = 0;
//	public static final int MAINMENUSTATE = 1;
//	public static final int LOBBYSTATE = 2;
//	public static final int PLAYINGSTATE = 3;

	// Global
	public final int ScreenWidth;
	public final int ScreenHeight;
	
	public boolean is_hosting = false;
	public boolean is_online = false;
	MultiPlayerHandler mpHandler;
	WorldSpace worldspace;
	int mpID = 0;
	Room curr = null;
	ArrayList<Room> rooms;
	// Entities
	
	// Resources
	
	
	public LooterGame(String name, int width, int height){
		super(name);
		ScreenHeight = height;
		ScreenWidth = width;
		
		
//		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		addState(new TitleScreenState());
		addState(new MainMenuState());
		addState(new LobbyState());
		addState(new PlayingState());
		addState(new MapEditor());
		addState(new LevelPreview());
	}
	
	public void setLastState(int curState) {
		stateStack.push(curState);
	}
	
	public int getLastState() {
		stateStack.pop();
		return stateStack.pop();
	}
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new LooterGame("Game", 1920, 1080));
			app.setDisplayMode(1920, 1080, false);
			app.setVSync(true);
			app.setAlwaysRender(true);
			app.setShowFPS(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

}
