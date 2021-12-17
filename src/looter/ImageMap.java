package looter;

import java.util.ArrayList;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class ImageMap {

	
	
	public static String[][][] imagePathArr= {
			{ // set 1
				// floor
				{"looter/resource/ground_tile.png"},
				// wall
				{"looter/resource/brick_wall.png"}
			},
			{ // set 2
				// floor
				{"looter/resource/tile.png"},
				// wall
				{"looter/resource/tile.png"}
			},
			{ // set 3
				// floor
				{"looter/resource/tile.png"},
				// wall
				{"looter/resource/tile.png"}
			},
			{ // set 4
				// floor
				{"looter/resource/tile.png"},
				// wall
				{"looter/resource/tile.png"}
			},
			{ // set 5
				// floor
				{"looter/resource/tile.png"},
				// wall
				{"looter/resource/tile.png"}
			},
			{ // set 6
				// floor
				{"looter/resource/tile.png"},
				// wall
				{"looter/resource/tile.png"}
			},
			{ // set 7
				// floor
				{"looter/resource/tile.png"},
				// wall
				{"looter/resource/tile.png"}
			},
			{ // set 8
				// floor
				{"looter/resource/tile.png"},
				// wall
				{"looter/resource/tile.png"}
			},
			{ // Hazards and Objectives
				{"looter/resource/tile.png"}, // Hazards
				{"looter/resource/Flag_Capture.png"} // Objectives
			},
			{ // Toggles and Doors
				{"looter/resource/Light_tile.png"}, // Toggles
				{"looter/resource/tile.png"} // Doors
			},
			{ // Spawners
				{"looter/resource/tile.png"}, // Player spawner
				{"looter/resource/tile.png"} // Enemy spawner
			},
			{ // World entities
				{"looter/resource/pistol.png", "looter/resource/rifle.png", "looter/resource/shotgun.png"}, // weapons
				{"looter/resource/player.png", "looter/resource/enemy.png"}, // actors
				{"looter/resource/Bullet.png", "looter/resource/laser.png"}
			}
		};
	
	
	public static ArrayList<ArrayList<ArrayList<Image>>> imageMap = new ArrayList<>();
	
//	public static ArrayList<ArrayList<Image>> skinMap = new ArrayList<>();
	
	
	public static void load_images() throws SlickException {
		Image temp;
		for (int i = 0; i < imagePathArr.length; i++) {
			imageMap.add(new ArrayList<ArrayList<Image>>());
			for(int j = 0; j < imagePathArr[i].length; j++) {
				imageMap.get(i).add(new ArrayList<Image>());
				for (int k = 0; k < imagePathArr[i][j].length; k++) {
					temp = new Image(imagePathArr[i][j][k]);
					
					imageMap.get(i).get(j).add(temp);
				}
				
			}
		}
		System.out.println("IMGs_LOADED");
	}
	
	public static Image get(int i, int j, int k) {
		
		return imageMap.get(i).get(j).get(k);
	}
	
	// Grabs the correct texture from the imageMap based on the BitMasked Value
	
	public static Image get(int num) { //BITMASKED
		int i = BitMasker.getTileSet(num);
		int j = BitMasker.getFW(num);
		int k = BitMasker.getTextureIndex(num);
		return imageMap.get(i).get(j).get(k);
	}

	// returns the image stored in the "World Prop" section of the imageMap
	// for alternate tilesets the get(0) can be changed to get(tileset) later.
	
	enum HOTD{
		HAZARD,
		OBJECTIVE,
		TOGGLE,
		DOOR
	}
	
	public static Image getProp(int num) { //BITMASKED
		HOTD HOT = HOTD.values()[BitMasker.getHOT(num)];
		
		int i = (HOT == HOTD.HAZARD || HOT == HOTD.OBJECTIVE)? 0 : // if HOTD is H or O, i=0
			(HOT == HOTD.TOGGLE || (HOT == HOTD.DOOR && BitMasker.getEffectIndex(num) < 4))? 1 : // if toggle or door, i = 1
				2; // if spawn i = 2
		int j=0;
		switch(i) {
		case 0: // check if H or O
			j = (HOT == HOTD.HAZARD)? 0 : 1;
			break;
		case 1: // check if T or D
			j = (HOT == HOTD.TOGGLE)? 0 : 1;
			break;
		case 2: // check if Player Spawner or Enemy Spawner
			j = (BitMasker.getEffectIndex(num) == 7)? 0 : 1;
			break;
		}
		return imageMap.get(8+i).get(j).get(0);
	}
	
	// returns the image stored in the "World Entity" section of the imageMap for a Weapon
	
	public static Image getWeapon(int num) { //NOT BITMASKED
		return imageMap.get(11).get(0).get(num);
	}
	
	// returns the image stored in the "World Entity" section of the imageMap for an Actor
	
	public static Image getActor(int num) { //NOT BITMASKED
		return imageMap.get(11).get(1).get(num);
	}
	
	// Get image for the projectile
//	======================================
	public static Image getProj(int num) {
		return imageMap.get(11).get(2).get(num);
	}

	// Static initializer so the textures are loaded on runtime
	
	static {
		try {
			load_images();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
}
