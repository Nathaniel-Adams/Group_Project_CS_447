package looter;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class Player {

	public int id;
	Actor actor;
//	ArrayList<Bullet> bullets;
	Weapon weapon;
	
	
	public Player(int id, Vector3f position) {
		this.id = id;
		actor = new Actor(position);
//		bullets = new ArrayList<Bullet>();
		weapon = new Weapon();
	}
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam) {
		if (actor != null) actor.render(renderList, cam);
		if (weapon != null) weapon.render(renderList, cam);
		
	}
	
	public void update(Input input, float delta, WorldSpace world) {
		if(this.id == world.MyPlayerID) {
			move(input, world, delta);
			world.cam.target = actor.position;
//			System.out.println(actor.position);
		}
		actor.update(world, delta);
//		if (weapon != null)
	}
	
	
	public void move(Input input, WorldSpace world, float delta) {
		
		float x = (input.isKeyDown(Input.KEY_D)? 1 : 0) - (input.isKeyDown(Input.KEY_A)? 1 : 0);
		float y = (input.isKeyDown(Input.KEY_S)? 1 : 0) - (input.isKeyDown(Input.KEY_W)? 1 : 0);
		Vector2f mov_dir = (Vector2f) new Vector2f(x,y).scale(delta);
//		if(mov_dir.length()!=0) {
//		mov_dir.normalise();
//		System.out.println(VectorMath.normalise(mov_dir));
		VectorMath.rotate(mov_dir, world.cam.rotation);
//			System.out.println(mov_dir);
		actor.move(world.map,mov_dir, delta);
//		}
		
		if (input.isKeyPressed(Input.KEY_SPACE)) {
			actor.jump();
		}
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", actor=" + actor + ", weapon=" + weapon.guntype + "]";
	}
	
}
