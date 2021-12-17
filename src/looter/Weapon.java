package looter;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

enum WeaponType{
	pistol,
	rifle,
	shotgun,
	melee
}

enum FireRate{
	slow, 
	mid, 
	fast 
}

enum BulletType{
	normal,
	explosive,
	laser
}

public class Weapon {
	public WeaponType guntype;
	public BulletType ammotype;
	public FireRate roftype;
	
	public Vector3f position = new Vector3f();
	public Vector3f dir = new Vector3f(0, 0, 0);
	public float range;
	public float dps;
	public float dmg;
	public float rof;
	public int img;
	public float height = (float)(5.0/32.0);
	
	public float accuracy;
	public float bloom;
	
	public float timeout = 0;
	
	public Weapon() {
		guntype = WeaponType.pistol;
		ammotype = BulletType.normal;
		roftype = FireRate.mid;
		rof = getROF();
		dps = 100;
		range = 10;
		dmg = calcDMG();
		img = 0;
	}
	
	public Weapon(float dps) {
		guntype = WeaponType.pistol;
		ammotype = BulletType.normal;
		roftype = FireRate.mid;
		rof = getROF();
		this.dps = dps;
		range = 10;
		dmg = calcDMG();
		img = 0;
	}
	
	public void fire(ArrayList<Bullet> bullets, float delta) {
		if (timeout <= 0) {
			bullets.add(new Bullet(this));
		}
		else if (timeout > 0) timeout -= delta;
	}
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam) {
		
		Image model = ImageMap.getWeapon(img);
//		System.out.println(ImageMap.imagePathArr[2][0][0]);
		Image slice;
		
		
		int layers = model.getHeight()/model.getWidth();
		
		
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
		while (i<=height) {
			float stack = (zoff-i)*(float)Math.cos(cam.angle);
			temp.set((place.x)*cam.zoom, (place.y+stack)*cam.zoom);
			slice = model.getSubImage(0,model.getHeight()-(model.getWidth()*((int)(i/height*layers)+1)), model.getWidth(), model.getWidth());
			renderList.add(new GraphicsObj(slice, temp, 1/3f,cam, position.z+i, (double)VectorMath.getRotation(dir)));
//			System.out.println(((int)(i/height*layers)+1));
			i += height/layers;
		}
		
		return;
		
	}
	
//	=========================================
	public void updatePosition(Actor actor) {
		Vector3f temp = new Vector3f(-actor.dir.y * 0.1f, actor.dir.x * 0.1f, .25f);
		Vector3f.add(actor.position, temp, position);
		dir.set(-actor.dir.y, actor.dir.x, actor.dir.z);
	}// 
//	=
	
	public float calcDMG() {
		return dps/(getROF()/60f);
	}
	
	public void setType(int type) {
		switch(type) {
		case 1:
			guntype = WeaponType.pistol;
			break;
		case 2:
			guntype = WeaponType.rifle;
			break;
		case 3:
			guntype = WeaponType.shotgun;
			break;
		default:
			guntype = WeaponType.melee;
			break;
		}
		dmg = calcDMG();
		rof = getROF();
	}
	
	public void setROF(int type) {
		switch(type) {
		case 1:
			roftype = FireRate.slow;
			break;
		case 2:
			roftype = FireRate.mid;
			break;
		case 3:
			roftype = FireRate.fast;
			break;
		default:
			roftype = FireRate.mid;
			break;
		}
		dmg = calcDMG();
		rof = getROF();
		
	}
	
	public int getROF() {
		switch(guntype) {
		case pistol:
			switch(roftype) {
			case slow: return 80;
			case mid: return 120;
			case fast: return 150;
			}
			
		case shotgun:
			switch(roftype) {
			case slow: return 65;
			case mid: return 90;
			case fast: return 120;
			}
		
		case rifle:
			switch(roftype) {
			case slow: return 450;
			case mid: return 600;
			case fast: return 900;
			}
		default:
			return 30;
		}
	}

	
}
