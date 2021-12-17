package looter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.HTMLReader.BlockAction;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


enum RoomType{
	spawn,
	hallway,
	junction,
	encounter,
	miniboss,
	boss
}

public class Room implements Serializable{

	
	
	private static final long serialVersionUID = 1L;
	boolean isActive = false;
	int [][][] map;
	Vector3f offset = new Vector3f();
	int ID;
	RoomType type;
	
	int left=-1;
	int right=-1;
	int up=-1;
	int down=-1;
	
	float gravity = -2;
	int RoomLight = 15;
	
//	ArrayList<Objective> objectiveList;
	
	
	ArrayList<Vector3f> hazards;
	ArrayList<Vector3f> enemyspawners;
	ArrayList<Vector3f> switches;
	ArrayList<Vector3f> capturepoints;
	ArrayList<Vector3f> lights;
	
	
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam) {
		Image temp;
		for(int z=0; z<map.length; z++) {
			for(int y=0; y<map[z].length; y++) {
				for(int x=0; x<map[z][y].length; x++) {
					int val = map[z][y][x];
					if (BitMasker.getExists(val) != 0) {
						temp = ImageMap.get(val);
						if (temp.getHeight() != temp.getWidth()) drawModel(renderList, cam, val, new Vector3f(x,y,z));
						else drawStack(renderList, cam, val, new Vector3f(x,y,z));
						if (BitMasker.getActive(val)!=0) { // if capture point
							drawProp(renderList, cam, val, new Vector3f(x,y,z));
						}
					}
					
				}
			}
		}
	}
	
	public void drawStack(ArrayList<GraphicsObj> renderList, Camera cam, int val, Vector3f vec) {
		Image block;
		float lightlevel;
		Vector2f temp = new Vector2f();
		Vector2f xy;
		float dist;
		double angle;
		float min;
		float max;
		int ratio;
		Vector2f place;
		lightlevel = BitMasker.getLight(val)/15;
		block = ImageMap.get(val);
//		System.out.println(block.getAlpha());
//		Image shaded = block.copy();
//		shaded.setAlpha((RoomLight - lightlevel)/15);
		xy = new Vector2f(vec.x+offset.x+.5f,vec.y+offset.y+.5f);
		dist = VectorMath.distance(cam.pos, xy);
		angle = VectorMath.angleTo(cam.pos, xy);
		min = BitMasker.getMinHeight(val);
		max = BitMasker.getMaxHeight(val);
		place = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist);
		Vector2f.sub(cam.offset, cam.pos, temp);
		Vector2f.sub(place, temp, place);
		float zoff =((cam.height -vec.z-offset.z )*(float)Math.cos(cam.angle));
		while (min <= max) {
			float stack = zoff-(min/32f)*(float)Math.cos(cam.angle);
			temp.set((place.x)*cam.zoom, (place.y+stack)*cam.zoom);
			renderList.add(new GraphicsObj(block, temp, cam, vec.z+min/32+offset.z));
			min += 1f;
		}
	}
	
	public void drawModel(ArrayList<GraphicsObj> renderList, Camera cam, int val, Vector3f vec) {
		Image block = ImageMap.get(val); // Image Value
		Image img; // temp Image
		float lightlevel = BitMasker.getLight(val)/15;
		Vector2f temp = new Vector2f(); // temporary location vector for assignments
		Vector2f xy = new Vector2f(vec.x+offset.x+.5f,vec.y+offset.y+.5f); // position in world space
		float dist = VectorMath.distance(cam.pos, xy);
		double angle = VectorMath.angleTo(cam.pos, xy);
		float min = BitMasker.getMinHeight(val);
		float max = BitMasker.getMaxHeight(val);
		
		Vector2f place = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist); // placement for bottom of the block in world space
		Vector2f.sub(cam.offset, cam.pos, temp); // offset from camera stored in temp
		Vector2f.sub(place, temp, place); // apply camera offsets
		
		float zoff =((cam.height -vec.z-offset.z )*(float)Math.cos(cam.angle)); // height offset based on camera angle
		
		int i = 0;
		while (max >= min) { // walk down the block
			float stack = zoff-(max/32f)*(float)Math.cos(cam.angle); // add layer to height offset
			temp.set((place.x)*cam.zoom, (place.y+stack)*cam.zoom); // convert position from world space to screen space
			
			img = block.getSubImage(0, 0 - block.getWidth()*i++, block.getWidth(), block.getWidth()); // grab the offset in the image
			renderList.add(new GraphicsObj(img, temp, cam, vec.z+max/32+offset.z)); // add Graphic to renderlist
			max -= 1f;
		}
	}
	
	public void drawProp(ArrayList<GraphicsObj> renderList, Camera cam, int val, Vector3f vec) {
		Image block = ImageMap.getProp(val); // Image Value
		Image img; // temp Image
		
		Vector2f temp = new Vector2f(); // temporary location vector for assignments
		Vector2f xy = new Vector2f(vec.x+offset.x+.5f,vec.y+offset.y+.5f); // position in world space
		
		float dist = VectorMath.distance(cam.pos, xy);
		double angle = VectorMath.angleTo(cam.pos, xy);
		Vector2f place = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist); // placement for bottom of the block in world space
		
		float lightlevel = BitMasker.getLight(val)/15; // needed for when shading is figured out
		float max = BitMasker.getMaxHeight(val);
		
		Vector2f.sub(cam.offset, cam.pos, temp); // offset from camera stored in temp
		Vector2f.sub(place, temp, place); // apply camera offsets
		
		float zoff =((cam.height -vec.z-offset.z - (max-2)/32)*(float)Math.cos(cam.angle)); // height (z) offset based on camera angle
		
		int height = block.getHeight()/block.getWidth();
		if (height < 3) height = 3;
		int i = 1;
		
		
		while (i <= height) { // walk up the block
			float stack = zoff-((float)i/32)*(float)Math.cos(cam.angle); // add layer to height offset
			temp.set((place.x)*cam.zoom, (place.y+stack)*cam.zoom); // convert position from world space to screen space
			
			img = block.getSubImage(0, block.getHeight()-block.getWidth()*i, block.getWidth(), block.getWidth());
			renderList.add(new GraphicsObj(img, temp, cam, vec.z+(max-2)/32+((float)i/32)+offset.z)); // add Graphic to renderlist
//			System.out.println((float)i/height);
			i++;
		}
	
	}
	
	

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// Checks
///////////////////////////////////////////////////////////////////////////////////////////////////////////


//	public void checkCollision(Vector3f currentpos, Vector3f nextpos) {
//		
//	}
	
	public Integer getBlock(Vector3f pos) {
		if (pos.x>=0 && pos.y >=0) {
			int z = (int) (pos.z - offset.z);
			int y = (int) (pos.y - offset.y);
			int x = (int) (pos.x - offset.x);
			if (!(z < 0 || z>=map.length)) {
				if(y >= 0 && y < map[0].length) {
					if(x >= 0 && x < map[0][0].length) {
						return map[z][y][x];
					}
				}
			}
		}
		return 0;
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
// position
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	public void rotateRight() {
		int [][][] temp = new int [map.length][map[0][0].length][map[0].length]; // initialize array with width/height swapped
		for (int z = 0; z < temp.length; z++) {
			for (int y = 0; y < temp[0].length; y++) {
				for (int x = 0; x < temp[0][0].length; x++) {
					temp[z][y][temp[0][0].length - 1 - x] = map[z][x][y];
				}
			}
		}
		map = temp;
		
	}
	
	// changes where the top left of the map is in global space
	public void setOffset(Vector3f pos) {
		offset = pos;
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////
// Prints
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	public String toString() {
		String ret = "";
		if (map != null) {
			for (int i=0;i<map.length;i++) {
				for(int j = 0; j<map[0].length; j++) {
					for(int k = 0; k<map[0][0].length; k++) {
						ret = ret.concat("|"+map[i][j][k]);
					}
					ret = ret.concat("|\n");
				}
			}
		}
		return ret;
	}

	public void print() {
		System.out.printf("Room:%d\nType:%s\n|%d|%d|%d|%d|\n", ID, type.toString(), left, right, down, up);
		System.out.println("Offsets: "+offset);
		System.out.println(this.toString());
	}
}
