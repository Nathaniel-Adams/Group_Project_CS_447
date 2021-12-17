package looter;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;


public class Actor {

	public Vector3f position;
	public float velocity;
	public int currentRoom;
	public Vector3f dir;
	
	int img = 0;

	int MAXJUMP = 1;
	int numJump = 1;
	int jumpHeight = 2;
	int jumpVal;
	
	float height = .6f;
	float width = 1/6f;
	float step = 0.26f;
	float speed = 1f;
	
	Vector3f safePosition;
	
	boolean isOnFloor = false;
	
	public Actor(Vector3f position) {
		this.position = position;
		this.velocity = 0;
		this.currentRoom = 0;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//Render
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam) {
		renderModel(renderList, cam);
	}
	
	public void renderModel(ArrayList<GraphicsObj> renderList, Camera cam) {
		Image model = ImageMap.getActor(img);
//		System.out.println(ImageMap.imagePathArr[2][0][0]);
		Image slice;
		
		int layers = model.getHeight()/model.getWidth();
		
		
//		Image slice = model.getSubImage(0, 0, model.getWidth(), model.getWidth());
//		slice.draw((int)1920*.9f, (int)1080*.9f);
		
		Vector2f temp = new Vector2f(); // temporary location vector for assignments
		Vector2f xy = new Vector2f(position.x,position.y); // position in world space
		
		float dist = VectorMath.distance(cam.pos, xy);
		double angle = VectorMath.angleTo(cam.pos, xy);
		Vector2f place = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist); // placement for bottom of the block in world space
		
		Vector2f.sub(cam.offset, cam.pos, temp); // offset from camera stored in temp
		Vector2f.sub(place, temp, place); // apply camera offsets
		
		float zoff = ((cam.height -position.z *(float)Math.cos(cam.angle)));
		float i = 0;
		while (i<=height) {
			float stack = (zoff-i)*(float)Math.cos(cam.angle);
			temp.set((place.x)*cam.zoom, (place.y+stack)*cam.zoom);
			slice = model.getSubImage(0,model.getHeight()-(model.getWidth()*((int)(i/height*layers)+1)), model.getWidth(), model.getWidth());
			renderList.add(new GraphicsObj(slice, temp, 1/3f,cam, position.z+i));
			System.out.println(((int)(i/height*layers)+1));
			i += height/layers;
		}
		
//		renderList.add(new GraphicsObj(slice, place , cam, position.z + height));
		
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
//Update
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void update(WorldSpace world, float delta) {
		applyGravity(world.map, delta);
		position.z += velocity*delta;
	}


///////////////////////////////////////////////////////////////////////////////////////////////////////////
//Checks & Misc
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// checks the given map and position against the actors current position for a collision
	
	public boolean checkMapCollision(WorldMap map, Vector3f next_pos) {
		Room room = map.Dungeon.get(currentRoom);
		int next = room.getBlock(next_pos);
		if(next > 0 && BitMasker.getMaxHeight(next)/32f - position.z > step) { // if the actors can step to the next position
			return true;
		}
		return false;
	}
	
	// isOnFloor returns the true if the actor is on the floor, else false
	
	public boolean isOnFloor(WorldMap map) {
		Room room = map.Dungeon.get(currentRoom);
		int current = room.getBlock(position);
//		System.out.println(current);
		if (BitMasker.getExists(current) != 0) {
			float floorHeight = BitMasker.getMaxHeight(current)/32f+room.offset.z;
			if(floorHeight >= position.z 
					&& BitMasker.getMinHeight(current) <= position.z) { // we are inside the block
				if (position.z < floorHeight) position.z = floorHeight;
				isOnFloor = true;
				return true;
			}
		}
		isOnFloor = false;
		return isOnFloor;
	}
	
	// Moves the player if the collision check for the given world returns false
	
	public void move(WorldMap map, Vector2f mov_dir, float delta) {
		if (!checkMapCollision(map, VectorMath.add3d(position, mov_dir))) {
			position.set(VectorMath.add3d(position,(Vector2f)mov_dir.scale(speed)));
		}
	}
	
	// makes the actor jump
	
	public void jump() {
		if(numJump > 0) {
			
			velocity = 2;
		}
	}
	
	// applies gravity to the actor based on the rooms gravity value
	
	private void applyGravity(WorldMap map, float delta) {
		Room room = map.Dungeon.get(currentRoom);
		if (isOnFloor(map)) {
			velocity = 0;
			numJump = MAXJUMP;
			safePosition = position;
		}
		else velocity += room.gravity*delta;
	}
	
	
}
