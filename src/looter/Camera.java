package looter;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;


public class Camera{

	Vector2f pos = new Vector2f();
	float height = 0;
	Vector2f offset = new Vector2f();
	float zoom = 1f;
	double rotation = 0;
	double angle = Math.PI/2;
	public boolean lock = true;
	
	Vector3f target = new Vector3f();
	
	public void update(LooterGame game, float delta) {
		Vector2f viewport = new Vector2f(game.ScreenWidth, game.ScreenHeight);
		Vector2f tempXY = new Vector2f();
		float tempH;
		
		if(angle >= Math.PI/2) angle = Math.PI/2;
		else if (angle <= -Math.PI/2) angle = -Math.PI/2;
		
		tempXY = (Vector2f) VectorMath.subtract2d(target, pos).scale(delta);
		Vector2f.add(tempXY, pos, pos);
		tempH = (target.z-height);
		height = height+tempH;
		
		tempXY = Vector2f.sub(pos, (Vector2f) viewport.scale(.5f/zoom), tempXY);
		offset = tempXY;
	}
	
	
}
