package looter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

public class WorldSpace {
	
	int MyPlayerID = 0;
	
	public ArrayList<Player> players;
	public ArrayList<Enemy> enemies;
//	public ArrayList<Weapon> weapons;
	public WorldMap map;
	public Camera cam;
	public Input input;
	
	
	public void init(GameContainer container, LooterGame game) {
		players = new ArrayList<Player>();
		enemies = new ArrayList<Enemy>();
//		weapons = new ArrayList<Weapon>();
		map = new WorldMap();
		cam = new Camera();
		cam.zoom = game.ScreenHeight/5f;
		input = container.getInput();
		
		
	}
	
	
	// grabs spawn room and finds the spawn point, adds player on spawn point
	public void addPlayer() {
		int temp = players.size();
		Room r = map.Dungeon.get(0);
		Vector3f spawn = new Vector3f();
		for(int z=0; z<r.map.length; z++) {
			for(int y=0; y<r.map[z].length; y++) {
				for(int x=0; x<r.map[z][y].length; x++) {
					if(BitMasker.getHOT(r.map[z][y][x]) == 3 
							&& BitMasker.getEffectIndex(r.map[z][y][x]) == 7) {
						spawn.set(x+.5f, y+.5f, BitMasker.getMaxHeight(r.map[z][y][x])/32f);
						
					}
				}
			}
		}
		players.add(new Player(temp, spawn));
		System.out.println("playeradded" + temp);
	}
	
	public void addEnemy() {
		if (!players.isEmpty()) {
			enemies.add(new Enemy(players.get(0).actor.position));
		}
	}
	
	public void render(GameContainer container, LooterGame game, Graphics g) {
		ArrayList<GraphicsObj> renderList = new ArrayList<GraphicsObj>();
		if (!players.isEmpty()) {
			for (Player player : players) {
				player.render(renderList, cam);
			}
//			map.render(renderList, cam, players.get(MyPlayerID).actor.currentRoom);
			
		}
		
		if (!enemies.isEmpty()) {
			for (Enemy enemy: enemies) {
				enemy.render(renderList, cam);
			}
		}
		
		map.render(renderList, cam, 0);
		
//		renderList.add(new GraphicsObj(ImageMap.get(1), new Vector2f(game.ScreenWidth,0), cam, 0));
		
		Collections.sort(renderList);
		for(GraphicsObj r: renderList) r.draw();
	}
	
	public void update(GameContainer container, LooterGame game, float delta) {
		cam.update(game, delta);
//		System.out.println(cam.pos);
		for (Player player : players) {
			player.update(input, delta, this);
		}
           
	}
	
	
}
