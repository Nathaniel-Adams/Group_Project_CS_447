package looter;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Image;


public class GraphicsObj implements Comparable<GraphicsObj>{

	Image sprite;
	Vector2f pos;
	float  scale;
	float layer;
	double angle;
	double Hangle;
	int frames;
	int frame;
//	LevelState level;
	
	
//	public GraphicsObj(Image sprite, Vector2f pos, float scale, double angle, double Hangle, float layer) {
//		this.sprite = sprite;
//		this.pos = pos;
//		this.scale = scale/sprite.getWidth();
//		this.angle = angle - 45;
//		this.layer = layer*(float)(Hangle/Math.abs(Hangle)*100);
//		this.Hangle = Hangle;
//	}
	
	public GraphicsObj(Image sprite, Vector2f pos, Camera cam, float layer) {
		this.sprite = sprite;
		this.pos = new Vector2f(pos);
		this.scale = cam.zoom/sprite.getWidth();
		this.angle = cam.rotation - Math.PI/4;
		this.Hangle = cam.angle;
		this.layer = layer*(float)(Hangle/Math.abs(Hangle)*100);
	}
	
	public GraphicsObj(Image sprite, Vector2f pos, float scalar, Camera cam, float layer) {
		this.sprite = sprite;
		this.pos = new Vector2f(pos);
		this.scale = cam.zoom/sprite.getWidth()*scalar;
		this.angle = cam.rotation - Math.PI/4;
		this.Hangle = cam.angle;
		this.layer = layer*(float)(Hangle/Math.abs(Hangle)*100);
	}
	
	public void draw() {
		Vector2f left_top = calculate_point(angle);
		Vector2f right_top = calculate_point(angle+Math.PI/2);
		Vector2f right_bot = calculate_point(angle+Math.PI);
		Vector2f left_bot = calculate_point(angle+Math.PI*3/2);
		sprite.drawWarped(
				pos.getX()+left_top.getX(), (pos.getY()-left_top.getY()),
				pos.getX()+right_top.getX(), (pos.getY()-right_top.getY()),
				pos.getX()+right_bot.getX(), (pos.getY()-right_bot.getY()),
				pos.getX()+left_bot.getX(), (pos.getY()-left_bot.getY())
				);
	}
	
	private Vector2f calculate_point(double angle) {
		float x = (float)Math.cos(angle);
		float y = (float)Math.sin(angle)*(float)Math.sin(Hangle);
		float scalar = sprite.getWidth()*.45f*(float)Math.PI*scale/2;
		Vector2f ret = new Vector2f(x*scalar,y*scalar);
		return ret;
		
	}
	

	public int compareTo(GraphicsObj o) {
		if(this.layer < o.layer) return -1;
		if(this.layer > o.layer) return 1;
		else return 0;
//		return (int)((this.layer- o.layer));
	}

}
