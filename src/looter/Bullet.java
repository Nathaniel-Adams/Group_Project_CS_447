package looter;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Image;

public class Bullet {

	BulletType type;
	float range;
	float damage;
	int img;
	public boolean remove = false;
	float traveldistance = 0;
	public int room = 0;
	
	Vector3f position = new Vector3f();
	Vector2f dir = new Vector2f(0, 0);
	public float speed;
	
//	===========================
	public Bullet(Weapon gun, int Room, float angle) {
		type = gun.ammotype;
		range = gun.range;
		damage = gun.dmg;
		speed = 3+gun.rof/80f;
		position.set(gun.position);
		if (angle == 0.0)
			dir.set(gun.dir.y, -gun.dir.x);
		else {
			dir.set(gun.dir.y, -gun.dir.x);
			VectorMath.rotate(dir, angle);
		}// else
		img = 0;
		room = Room;
	}// Bullet()
//	============
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam) {
		Image model = ImageMap.getProj(img);
//		System.out.println(ImageMap.imagePathArr[2][0][0]);		
		
//		Image slice = model.getSubImage(0, 0, model.getWidth(), model.getWidth());
//		slice.draw((int)1920*.9f, (int)1080*.9f);
		
		Vector2f temp = new Vector2f(); // temporary location vector for assignments
		Vector2f xy = new Vector2f(position.x ,position.y); // position in world space
		
		float dist = VectorMath.distance(cam.pos, xy);
		double angle = VectorMath.angleTo(cam.pos, xy);
		Vector2f place = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist); // placement for bottom of the block in world space
		
		Vector2f.sub(cam.offset, cam.pos, temp); // offset from camera stored in temp
		Vector2f.sub(place, temp, place); // apply camera offsets
		
		float zoff = ((cam.height -position.z *(float)Math.cos(cam.angle)));
		float i = 0;
		float stack = (zoff-i)*(float)Math.cos(cam.angle);
		temp.set((place.x)*cam.zoom, (place.y+stack)*cam.zoom);
		renderList.add(new GraphicsObj(model, temp, 1/3f,cam, position.z+i, (double)VectorMath.getRotation(dir)));
		
		return;
	}
	
	public void update(float delta, WorldSpace world) {
		move(delta, world);
		traveldistance += delta;
		if (traveldistance >= range)
			remove = true;
	}// update()
	
	public void move(float delta, WorldSpace world) {
		
		position.set(position.x + (dir.x * (delta * speed)), position.y + (dir.y * (delta * speed)), position.z);
		if (!checkMapCollision(world.map, position)) {
			remove = true;
		}
	}
	
	public boolean checkMapCollision(WorldMap map, Vector3f next_pos) {
		Room room = map.Dungeon.get(this.room);
		int next = room.getBlock(next_pos);
		float test = BitMasker.getMaxHeight(next)/32f + (int)(Math.floor(position.z)); 
		if(BitMasker.getMaxHeight(next)/32f + (int)(Math.floor(position.z)) < position.z) { // make sure the bullet is higher than the block
			return true;
		}
		return false;
	}
	
	
}
