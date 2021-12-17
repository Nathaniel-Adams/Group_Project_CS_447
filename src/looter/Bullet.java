package looter;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Bullet {

	BulletType type;
	float range;
	float damage;
	
	Vector3f position = new Vector3f();
	Vector3f dir = new Vector3f();
	public float speed;
	
//	===========================
	public Bullet(Weapon gun) {
		type = gun.ammotype;
		range = gun.range;
		damage = gun.dmg;
		speed = 3+gun.rof/80f;
		position = gun.position;
		dir = gun.dir;
	}// Bullet()
//	============
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam) {
		
	}
	
	public void update() {
		
	}
	
	public void move() {
		
	}
	
	
}
