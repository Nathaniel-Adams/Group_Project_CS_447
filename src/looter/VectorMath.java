package looter;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.util.FastTrig;


public class VectorMath {

	public static void rotate(Vector2f vec, double angle) {
		setRotation2d(vec, getRotation(vec) + angle);
	}
	
	public static double getRotation(Vector2f vec) {
		return StrictMath.atan2(vec.y, vec.x) % 360.0;
	} 
	
	public static double getRotation(Vector3f vec) {
		return StrictMath.atan2(vec.y, vec.x) % 360.0;
	} 
	
	public static void setRotation2d(Vector2f vec, double angle) {
		double t = angle % (2*Math.PI);
		float len = vec.length();
		vec.set(len *(float) FastTrig.cos(t),
				len *(float) FastTrig.sin(t));
	}
	
	public static void rotate2d(Vector3f vec, double angle) {
		double t = angle % (2*Math.PI);
		float len = new Vector2f(vec.x, vec.y).length();
		vec.set(len *(float) FastTrig.cos(t),
				len *(float) FastTrig.sin(t),
				vec.z);
	}
	
	public static double angleTo(Vector2f vec1, Vector2f vec2) {
		return StrictMath.atan2(vec2.y - vec1.y, vec2.x - vec1.x) % (2*Math.PI);
	}
	
	public static double angleTo2d(Vector3f vec1, Vector2f vec2) {
		return StrictMath.atan2(vec1.y - vec2.y, vec1.x - vec2.x) % (2*Math.PI);
	}
	
	public static float distance(Vector2f vec1, Vector2f vec2) {
		return (float) Math.sqrt(distanceSquared(vec1, vec2));
	}
	
	public static float distance(Vector3f vec1, Vector2f vec2) {
		return (float) Math.sqrt(distanceSquared2d(vec1, vec2));
	}
	
	public static float distanceSquared(Vector2f vec1, Vector2f vec2) {
		float dx = vec1.x - vec2.x;
		float dy = vec1.y - vec2.y;
		return (dx*dx) + (dy*dy);
	}
	
	public static float distanceSquared2d(Vector3f vec1, Vector2f vec2) {
		float dx = vec1.x - vec2.x;
		float dy = vec1.y - vec2.y;
		return (dx*dx) + (dy*dy);
	}
	
	public static Vector2f add2d(Vector3f vec1, Vector2f vec2) {
		return new Vector2f(vec1.x+vec2.x, vec1.y+vec2.y);
	}
	
	public static Vector3f add3d(Vector3f vec1, Vector2f vec2) {
		return new Vector3f(vec1.x+vec2.x, vec1.y+vec2.y,vec1.z);
	}
	
	public static Vector3f add3d(Vector3f vec1, Vector2f vec2, float h) {
		return new Vector3f(vec1.x+vec2.x, vec1.y+vec2.y,vec1.z+h);
	}
	
	public static Vector2f subtract2d(Vector3f vec1, Vector2f vec2) {
		return new Vector2f(vec1.x-vec2.x, vec1.y-vec2.y);
	}
	
	public static Vector2f subtract2d(Vector2f vec1, Vector3f vec2) {
		return new Vector2f(vec1.x-vec2.x, vec1.y-vec2.y);
	}
	
	public static Vector2f calculatePoint(double angle, double hangle) {
		return new Vector2f((float)Math.cos(angle),(float)Math.sin(angle)*(float)Math.sin(hangle));
	}

	public static Vector2f normalise(Vector2f vec) {
		return new Vector2f((float)Math.cos(vec.x), (float)Math.sin(vec.y));
	}

	
}
