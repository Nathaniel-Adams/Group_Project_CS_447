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
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class MapEditor extends BasicGameState  {
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GLOBALS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	LooterGame Game;
	Input input; // player input listener
	float delta; // multiply any unit by this value to convert to units per second
	Camera cam;
	Vector3f target = new Vector3f();
	Room room;
	boolean debug;
	
	Image TestTexture;
	ArrayList<Button> buttons;
	
	Vector3f next = new Vector3f(0,0,0);
	
	Vector3f cur = new Vector3f();
	
	Vector3f mapSize = new Vector3f(3,3,3);
	
	char active = 'x';
	
	String mode = "location";
	
	MuffinHelper MH = new MuffinHelper();
	
//	enum ActiveAxis{
//		X,
//		Y,
//		Z
//	}
	
	String[] settings = {"Hazard", "Objective","Toggle", "Door"};
	int hotd = 0;
	int Eindx = 0;
	
	int xCoord;
	float yCoord;
	int yOffset;
///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	UPDATE LOOP
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		if(room != null) {
			ArrayList<GraphicsObj> renderList = new ArrayList<GraphicsObj>();
			room.render(renderList, cam);
			Collections.sort(renderList);
			for(GraphicsObj r: renderList) r.draw();
			DebugRenderer.renderDebugGrid(g, cam, room); // Render the x/y grid
			DebugRenderer.renderDebugScaffold(g, cam, room); // Render the z height bars
			DebugRenderer.renderSelectedCube(g, cam, room, next);
			g.drawString("RoomID: "  + room.ID, xCoord, (int)(yCoord)+(yOffset*-3));
			g.drawString("Room Type: "  + room.type, xCoord, (int)(yCoord)+(yOffset*-2));
			if(next.z <= cur.z && next.y <= cur.y && next.x <= cur.x && 1 == BitMasker.getExists(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)])) {
				g.drawString("current Prop Type:"  + settings[BitMasker.getHOT(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)])], xCoord, (int)(yCoord)+(yOffset*6)); 
				g.drawString("current E-index:"  + BitMasker.getEffectIndex(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]), xCoord, (int)(yCoord)+(yOffset*7)); 
			}
		}
		 
		g.drawString("mode:"  + mode, xCoord, (int)(yCoord)+(yOffset*-1)); 
		if(mode == "location") {
			g.drawString("loc x: " + next.x, xCoord, (int)(yCoord)+(yOffset*0));
			g.drawString("loc y: " + next.y, xCoord, (int)(yCoord)+(yOffset*1));
			g.drawString("loc z: " + next.z, xCoord, (int)(yCoord)+(yOffset*2));
		} else if(mode == "size") {
			g.drawString("size x: " + mapSize.x, xCoord, (int)(yCoord)+(yOffset*0));
			g.drawString("size y: " + mapSize.y, xCoord, (int)(yCoord)+(yOffset*1));
			g.drawString("size z: " + mapSize.z, xCoord, (int)(yCoord)+(yOffset*2));
		}
		g.drawString("active:"  + active, xCoord, (int)(yCoord)+(yOffset*3)); 
		g.drawString("Prop Type:"  + settings[hotd], xCoord, (int)(yCoord)+(yOffset*4)); 
		g.drawString("E-index:"  + Eindx, xCoord, (int)(yCoord)+(yOffset*5)); 
		
		for(Button button:buttons) {
			button.render(container, g);
		}
		
	}

	
	@Override
	public void update(GameContainer container, StateBasedGame game, int milidelta) throws SlickException {
		delta = (float)milidelta/1000f;
		handleInput();
		moveTarget();
		cam.update(Game, delta);
		cam.target = target;
		updateButtons();
		
	}
	
	private void updateButtons() {
		for(Button button:buttons) {
//			if (button.isMouseOver()) System.out.println("button " + buttons.indexOf(button));
			if (button.isMouseOver() && input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
				switch (button.text){
				case "x":
					active = 'x';
					break;
				case "y":
					active = 'y';
					break;
				case "z":
					active = 'z';
					break;
				case "shift up":
					shiftarr('u');
					break;
				case "shift down":
					shiftarr('d');
					break;
				case "shift left":
					shiftarr('l');
					break;
				case "shift right":
					shiftarr('r');
					break;
				case "shift forward":
					shiftarr('f');
					break;
				case "shift back":
					shiftarr('b');
					break;
				case "save":
					MH.saveLevel(room);
					break;
				case "load":
					try {
						room = MH.loadRooms(room.ID);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case "resize":
					if(mapSize.x - 1 < next.x) {
						next.x = mapSize.x-1;
					}
					if(mapSize.y -1 < next.y) {
						next.y = mapSize.y-1;
					}
					if(mapSize.z -1 < next.z) {
						next.z = mapSize.z-1;
					}
					sizeMap(mapSize.x,mapSize.y,mapSize.z);
					break;
				}
			}
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	GAME-STATE HANDLERS
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public int getID() {
		return GameState.MAPEDITOR.ordinal();
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException { //called for EVERY entrance
		input.clearKeyPressedRecord();
		LooterGame LG = (LooterGame) game;
		cam = new Camera();
		cam.zoom = Game.ScreenWidth/5f;
		if(LG.curr != null) {
			room = LG.curr;
		}
		cur.x = room.map[0][0].length;
		cur.y = room.map[0].length;
		cur.z = room.map.length;
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
		room = null;
		xCoord = (int)(Game.ScreenWidth*.1f);
		yCoord = Game.ScreenHeight*.1f;
		yOffset = 15;
	}
	
	private void createButtons(GameContainer container) throws SlickException {
		buttons = new ArrayList<Button>();
		TestTexture = new Image("looter/resource/tile.png");
		
		Vector2f buttonCoord = new Vector2f(Game.ScreenWidth*.7f, Game.ScreenHeight*.1f);
		int yOffset = TestTexture.getHeight()+5;
		
		Button tempButton = new Button(container, TestTexture, buttonCoord.x, buttonCoord.y, "x");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x, buttonCoord.y+yOffset, "y");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x, buttonCoord.y+2*yOffset, "z");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset*2, buttonCoord.y, "shift forward");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset*5, buttonCoord.y, "shift back");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset*2, buttonCoord.y+yOffset, "shift up");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset*5, buttonCoord.y+yOffset, "shift down");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset*2, buttonCoord.y+2*yOffset, "shift right");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset*5, buttonCoord.y+2*yOffset, "shift left");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset*2, buttonCoord.y+3*yOffset, "save");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x+yOffset*5, buttonCoord.y+3*yOffset, "load");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
		
		tempButton = new Button(container, TestTexture, buttonCoord.x, buttonCoord.y+3*yOffset, "resize");
		tempButton.setType(ButtonType.TOGGLEBUTTON);
		buttons.add(tempButton);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//	CUSTOM METHODS
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void handleInput() {
	
		
	if (room != null) {
		if(input.isKeyPressed(Input.KEY_I)) {
			room.ID = (room.ID + 1);
			if(Game.rooms.size() < room.ID){
				room.ID = 0;
			}
		}
		if(input.isKeyPressed(Input.KEY_O)) {
			switch(room.type) {
				case spawn:
					room.type = RoomType.boss;
					break;
				case boss:
					room.type = RoomType.hallway;
					break;
				case hallway:
					room.type = RoomType.junction;
					break;
				case junction:
					room.type = RoomType.encounter;
					break;
				case encounter:
					room.type = RoomType.miniboss;
					break;
				case miniboss:
					room.type = RoomType.spawn;
					break;
			}
					
			
		}
		if(mode == "location") {
			if(input.isKeyPressed(Input.KEY_UP)) {
				if(active == 'x' && next.x < room.map[0][0].length-1) {
					next.x++;
				}else if(active == 'y' && next.y < room.map[0].length-1) {
					next.y++;
				}
			}
			
			if(input.isKeyPressed(Input.KEY_DOWN)) {
				if(active == 'x' && next.x > 0) {
					next.x--;
				}else if(active == 'y' && next.y > 0) {
					next.y--;
				}
			}

		} else if(mode == "size") {
			if(input.isKeyPressed(Input.KEY_UP)) {
				if(active == 'x') {
					mapSize.x++;
				}else if(active == 'y') {
					mapSize.y++;
				}else if(active == 'z') {
					mapSize.z++;
				}
			}
			
			if(input.isKeyPressed(Input.KEY_DOWN)) {
				if(active == 'x' && mapSize.x > 0) {
					mapSize.x--;
				}else if(active == 'y' && mapSize.y > 0) {
					mapSize.y--;
				}else if(active == 'z' && mapSize.z > 0) {
					mapSize.z--;
				}
			}
		} 
	// Toggles for shifting around settings
	if(input.isKeyPressed(Input.KEY_TAB)) {
		if(mode == "size") {
			mode = "location";
		} else {
			mode = "size";
		}
	}
	if(input.isKeyPressed(Input.KEY_LEFT)) {
		hotd = (hotd+1)%4;
	}
	if(input.isKeyPressed(Input.KEY_RIGHT)) {
		Eindx = (Eindx+1)%8;
	}
	//
	
	
	//// Adjust Tile up or down
	if(input.isKeyDown(Input.KEY_R)) {
		if(next.z <= cur.z && next.y <= cur.y && next.x <= cur.x && 1 == BitMasker.getExists(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)])) {
			int set = BitMasker.getMaxHeight(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]);
			room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setMaxHeight(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], set+1);
		}
	}
	if(input.isKeyDown(Input.KEY_F)) {
		if(next.z <= cur.z && next.y <= cur.y && next.x <= cur.x && 1 == BitMasker.getExists(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)])) {
			int set = BitMasker.getMaxHeight(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]);
			room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setMaxHeight(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], set-1);
		}
	}
	////
	
	
	//// Setting Floor or wall
	if(input.isKeyPressed(Input.KEY_T)) {
		if(next.z <= cur.z && next.y <= cur.y && next.x <= cur.x && 1 == BitMasker.getExists(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)])) {
			if(0 == BitMasker.getFW(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)])) {
				room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setFW(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], 1);
			} else if (1 == BitMasker.getFW(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)])){
				room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setFW(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], 0);
			}
		}
	}
	////
	
	//// Setting Existence
	if(input.isKeyPressed(Input.KEY_SPACE)) {
		if(next.z <= cur.z && next.y <= cur.y && next.x <= cur.x) {
			room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setExists(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]);	
		}
	}
	////
	
	///// dropping props
	if(input.isKeyPressed(Input.KEY_G)) {
		if(next.z <= cur.z && next.y <= cur.y && next.x <= cur.x && 1 == BitMasker.getExists(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)])) {
			if(BitMasker.getActive(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]) == 0 || (BitMasker.getHOT(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]) != hotd || BitMasker.getEffectIndex(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]) != Eindx)) {
				room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setHOT(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], hotd);
				room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setEffectIndex(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], Eindx);
				room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setActive(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], 1);
			} else {
				room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setActive(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], 0);
				if(BitMasker.getHOT(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]) == hotd && BitMasker.getEffectIndex(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)]) == Eindx){
					room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setHOT(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], 0);
					room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)] = BitMasker.setEffectIndex(room.map[(int) (next.z)][(int) (next.y)][(int) (next.x)], 0);
				}
			}
		}
	}
	
	/////
	
	
	if(input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) cam.lock = false;
	else cam.lock = true;
	
	if(input.isKeyPressed(Input.KEY_ESCAPE)) {
		 Game.enterState(Game.getLastState(), new FadeOutTransition(), new FadeInTransition());
	}
	}
	}


	private void moveTarget() {
		if (room != null) {
			Vector3f movDir = new Vector3f();	
			movDir.x = (((input.isKeyDown(Input.KEY_D))? 1 : 0) - ((input.isKeyDown(Input.KEY_A))? 1 : 0))*delta;
			movDir.y = (((input.isKeyDown(Input.KEY_S))? 1 : 0) - ((input.isKeyDown(Input.KEY_W))? 1 : 0))*delta;
			movDir.z = (((input.isKeyDown(Input.KEY_E))? 1 : 0) - ((input.isKeyDown(Input.KEY_Q))? 1 : 0))*delta;
			VectorMath.rotate2d(movDir, VectorMath.getRotation(movDir)+cam.rotation);
			Vector3f.add(movDir, cam.target, cam.target);
			next.z = (int)cam.target.z;
			if (next.z < 0) next.z = 0;
			else if(next.z > room.map.length-1) next.z = room.map.length-1; 
		}
		
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
//		else {
//			world.cam.pos.x -= moved.x;
//			world.cam.pos.y -= moved.y;
//		}
	}
	
	public void mouseWheelMoved(int newValue) {
		cam.zoom += newValue/10;
		if (cam.zoom <= 0) cam.zoom = 0;
	}
	
	public void sizeMap(float x, float y, float z) {
		cur.x = x;
		cur.y = y;
		cur.z = z;
		int[][][] m = new int[(int) z][(int) y][(int) x];
		Room temp = new Room();
		temp.map = m;
		temp.ID = room.ID;
		temp.type = room.type;
		if(room == null) {
			for(int j = 0; j < y; j++) {
				for(int i = 0; i < x; i++) {
					temp.map[0][j][i] = 1;	
				}
			}
		}else {
			combinarr(temp.map,room.map);
		}
		room = temp;
	}
	
	public void combinarr(int[][][] NEW, int[][][] OLD) {
		int minx = Math.min(NEW[0][0].length, OLD[0][0].length);
		int miny = Math.min(NEW[0].length, OLD[0].length);
		int minz = Math.min(NEW.length, OLD.length);
		for(int k = 0; k < minz; k++) {
			for(int j = 0; j < miny; j++) {
				for(int i = 0; i < minx; i++) {
					NEW[k][j][i] = OLD[k][j][i];	
				}
			}
		}
	}
	
	public void shiftarr(char dir) {
		switch (dir) {
		case 'u':
			for(int k = room.map.length-1; k > 0; k--) {
				for(int j = 0; j < room.map[k].length; j++) {
					for(int i = 0; i < room.map[k][j].length; i++) {
						room.map[k][j][i] = room.map[k-1][j][i];
					}
				}
			}
			for(int j = 0; j < room.map[0].length; j++) {
				for(int i = 0; i < room.map[0][j].length; i++) {
					room.map[0][j][i] = 0;
				}
			}
			break;
		case 'd':
			for(int k = 0; k < room.map.length-1; k++) {
				for(int j = 0; j < room.map[k].length; j++) {
					for(int i = 0; i < room.map[k][j].length; i++) {
						room.map[k][j][i] = room.map[k+1][j][i];
					}
				}
			}
			for(int j = 0; j < room.map[0].length; j++) {
				for(int i = 0; i < room.map[0][j].length; i++) {
					room.map[room.map.length-1][j][i] = 0;
				}
			}
			break;
		case 'f':
			for(int k = 0; k < room.map.length; k++) {
				for(int j = room.map[k].length-1; j > 0; j--) {
					for(int i = 0; i < room.map[k][j].length; i++) {
						room.map[k][j][i] = room.map[k][j-1][i];
					}
				}
			}
			for(int k = 0; k < room.map.length; k++) {
				for(int i = 0; i < room.map[k][0].length; i++) {
					room.map[k][0][i] = 0;
				}
			}
			break;
		case 'b':
			for(int k = 0; k < room.map.length; k++) {
				for(int j = 0; j < room.map[k].length-1; j++) {
					for(int i = 0; i < room.map[k][j].length; i++) {
						room.map[k][j][i] = room.map[k][j+1][i];
					}
				}
			}
			for(int k = 0; k < room.map.length-1; k++) {
				for(int i = 0; i < room.map[k][0].length; i++) {
					room.map[k][room.map[k].length-1][i] = 0;
				}
			}
			break;
		case 'r':
			for(int k = 0; k < room.map.length; k++) {
				for(int j = 0; j < room.map[k].length; j++) {
					for(int i = room.map[k][j].length-1; i > 0; i--) {
						room.map[k][j][i] = room.map[k][j][i-1];
					}
				}
			}
			for(int k = 0; k < room.map.length; k++) {
				for(int j = 0; j < room.map[k].length; j++) {
					room.map[k][j][0] = 0;
				}
			}
			break;
		case 'l':
			for(int k = 0; k < room.map.length; k++) {
				for(int j = 0; j < room.map[k].length; j++) {
					for(int i = 0; i < room.map[k][j].length-1; i++) {
						room.map[k][j][i] = room.map[k][j][i+1];
					}
				}
			}
			for(int k = 0; k < room.map.length; k++) {
				for(int j = 0; j < room.map[k].length; j++) {
					room.map[k][j][room.map[k][j].length-1] = 0;
				}
			}
			break;
		}
	}
}


