package looter;

import java.util.Iterator;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class Player {

	public int id;
	Actor actor;
	ArrayList<Bullet> bullets;
	Weapon weapon;
	
	double temp = 0;
	
	public Player(int id, Vector3f position) {
		this.id = id;
		actor = new Actor(position);
		bullets = new ArrayList<Bullet>();
		weapon = new Weapon();
		weapon.updatePosition(actor); // set the position
	}
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam) {
		if (actor != null) actor.render(renderList, cam);
		if (weapon != null) weapon.render(renderList, cam);
		if (!bullets.isEmpty()) {
			for (Bullet bullet : bullets) {
				bullet.render(renderList, cam);
			}
		}
	}
	
	public void update(Input input, float delta, WorldSpace world) {
		if(this.id == world.MyPlayerID) {
			move(input, world, delta);
			world.cam.target = actor.position;
//			System.out.println(actor.position);
		}
		actor.update(world, delta);
		if (weapon != null) {
			weapon.updatePosition(actor);
		}
		if (!bullets.isEmpty()) {
			for (Bullet bullet : bullets) {
				bullet.update(delta, world);
			}
		}
		for (Iterator<Bullet> i = bullets.iterator(); i.hasNext();) {
			if (i.next().remove) {
				i.remove();
			}
		}
	}
	
	
	public void move(Input input, WorldSpace world, float delta) {
		
		Vector2f mouse_pos = new Vector2f(input.getMouseX(), input.getMouseY()); 
		Vector2f offset = new Vector2f(actor.position.x, actor.position.y - (actor.height*.6f)*(float)Math.cos(world.cam.angle));
		double angle = VectorMath.angleTo(offset, VectorMath.calculateSkewDir(mouse_pos, world.cam)) + world.cam.rotation;
		VectorMath.setRotation2d(actor.dir, angle);
		
		
		System.out.println(offset);
		
		float x = (input.isKeyDown(Input.KEY_D)? 1 : 0) - (input.isKeyDown(Input.KEY_A)? 1 : 0);
		float y = (input.isKeyDown(Input.KEY_S)? 1 : 0) - (input.isKeyDown(Input.KEY_W)? 1 : 0);
		Vector2f mov_dir = (Vector2f) new Vector2f(x,y).scale(delta);
		VectorMath.rotate(mov_dir, world.cam.rotation);
		actor.move(world.map,mov_dir, delta);

		
		if (input.isKeyPressed(Input.KEY_SPACE)) {
			actor.jump();
		}
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", actor=" + actor + ", weapon=" + weapon.guntype + "]";
	}
	
}
