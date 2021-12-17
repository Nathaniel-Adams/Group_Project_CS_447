package looter;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Input;

public class Enemy {

	int id;
	Actor actor;
//	ArrayList<Bullet> bullets;
//	Weapon weapon;
	
	
	public Enemy(Vector3f position) {
		this.id = id;
		actor = new Actor(position);
		actor.img=1;
//		bullets = new ArrayList<Bullet>();
//		weapon = new Weapon();
	}
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam) {
		if (actor != null) actor.render(renderList, cam);
//		if (weapon != null) weapon.render(renderList, cam);
		
	}
	
	public void update(Input input, float delta, WorldSpace world) {
		actor.update(world, delta);
		
	}
}
