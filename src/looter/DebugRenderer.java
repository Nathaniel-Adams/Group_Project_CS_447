package looter;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class DebugRenderer {

	public static void renderDebugGrid(Graphics g, Camera cam, Room room) {
		g.setColor(Color.red);
		
		Vector2f temp1;
		Vector2f temp2;
		int[][][] map = room.map;
		for(int y=0; y<=map[0].length; y++) { // loops through y
			temp1 = new Vector2f(0+room.offset.x, (float)y+room.offset.y);
			temp2 = new Vector2f((float)map[0][0].length+room.offset.x, (float)y+room.offset.y);
			renderLine2d(g,cam, temp1, temp2);
		}
		for(int x=0; x<=map[0][0].length; x++) { //loops through x
			temp1 = new Vector2f((float)x+room.offset.x, 0+room.offset.y);
			temp2 = new Vector2f((float)x+room.offset.x, (float)map[0].length+room.offset.y);
			renderLine2d(g,cam, temp1, temp2);
		}
		g.setColor(Color.white);
	}
	
	public static void renderDebugScaffold(Graphics g, Camera cam, Room room) {
		g.setColor(Color.red);
		Vector3f temp1 = new Vector3f(room.offset.x, room.offset.y, room.offset.z-cam.height);
		Vector3f temp2 = new Vector3f(room.offset.x, room.offset.y,room.map.length+room.offset.z-cam.height);
		renderLine3d(g,cam,temp1, temp2);
		
		for(int z = 0; z<=room.map.length; z++) {
			temp1 = new Vector3f(room.offset.x, room.offset.y, z+room.offset.z-cam.height);
			temp2 = new Vector3f(room.offset.x+room.map[0][0].length,room.offset.y,z+room.offset.z-cam.height);
			renderLine3d(g,cam,temp1, temp2);
			temp1 = new Vector3f(room.offset.x, room.offset.y, z+room.offset.z-cam.height);
			temp2 = new Vector3f(room.offset.x,room.offset.y+room.map[0].length,z+room.offset.z-cam.height);
			renderLine3d(g,cam,temp1, temp2);
		}
		
		g.setColor(Color.white);
	}
	
	public static void renderSelectedCube(Graphics g, Camera cam, Room room, Vector3f pos) {
		
		g.setColor(Color.green);
		
		
		// Draw 
		Vector3f temp1 = new Vector3f(room.offset.x+pos.x, room.offset.y+pos.y, pos.z+room.offset.z-cam.height);
		Vector3f temp2 = new Vector3f(room.offset.x+pos.x,room.offset.y+pos.y,pos.z+1+room.offset.z-cam.height);
		renderLine3d(g,cam,temp1, temp2);
		
		temp1 = new Vector3f(room.offset.x+pos.x+1, room.offset.y+pos.y, pos.z+room.offset.z-cam.height);
		temp2 = new Vector3f(room.offset.x+pos.x+1,room.offset.y+pos.y,pos.z+1+room.offset.z-cam.height);
		renderLine3d(g,cam,temp1, temp2);
		
		temp1 = new Vector3f(room.offset.x+pos.x+1, room.offset.y+pos.y+1, pos.z+room.offset.z-cam.height);
		temp2 = new Vector3f(room.offset.x+pos.x+1,room.offset.y+pos.y+1,pos.z+1+room.offset.z-cam.height);
		renderLine3d(g,cam,temp1, temp2);
		
		temp1 = new Vector3f(room.offset.x+pos.x, room.offset.y+pos.y+1, pos.z+room.offset.z-cam.height);
		temp2 = new Vector3f(room.offset.x+pos.x,room.offset.y+pos.y+1,pos.z+1+room.offset.z-cam.height);
		renderLine3d(g,cam,temp1, temp2);
		
		
		g.setColor(Color.white);
	}
	
	public static void renderLine2d(Graphics g, Camera cam, Vector2f a, Vector2f b) {
		
		//calculate start point in screen space
		
		double angle = VectorMath.angleTo(cam.pos, a);
		
		float dist = VectorMath.distance(cam.pos, a);
		Vector2f temp1 = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist);
		System.out.println(temp1);
		Vector2f temp2 = new Vector2f();
		Vector2f.sub(cam.offset, cam.pos, temp2);
		Vector2f temp3 = new Vector2f();
		Vector2f.sub(temp1, temp2, temp3);
		Vector2f pos = new Vector2f(temp3.x*cam.zoom, temp3.y*cam.zoom); // (Vector2f) temp3.scale(cam.zoom);
		
		//calculate end point in screen space
		
		angle = VectorMath.angleTo(cam.pos, b);
		dist = VectorMath.distance(cam.pos, b);
		temp1 = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist);
		Vector2f.sub(cam.offset, cam.pos, temp2);
		Vector2f.sub(temp1, temp2, temp3);
		Vector2f pos2 = new Vector2f(temp3.x*cam.zoom, temp3.y*cam.zoom);
		System.out.println(pos +" | "+ pos2);
		g.drawLine((pos.getX()), (pos.getY()), 
				(pos2.getX()), (pos2.getY()));
	}
	
	public static void renderLine3d(Graphics g, Camera cam, Vector3f a, Vector3f b) {
		
		//calculate start point in screen space
		
		Vector2f temp1 = new Vector2f(a.x, a.y);
		Vector2f temp2 = new Vector2f();
		Vector2f temp3 = new Vector2f();
		
		
		double angle = VectorMath.angleTo(cam.pos, temp1);
		
		float dist = VectorMath.distance(cam.pos, temp1);
		temp1 = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist);
		System.out.println(temp1);
		Vector2f.sub(cam.offset, cam.pos, temp2);
		Vector2f.sub(temp1, temp2, temp3);
		Vector2f pos = new Vector2f(temp3.x*cam.zoom, (temp3.y-a.z*(float)Math.cos(cam.angle))*cam.zoom); // (Vector2f) temp3.scale(cam.zoom);
		
		//calculate end point in screen space
		temp1.set(b.x, b.y);
		
		angle = VectorMath.angleTo(cam.pos, temp1);
		dist = VectorMath.distance(cam.pos, temp1);
		temp1 = (Vector2f) VectorMath.calculatePoint(angle-cam.rotation, cam.angle).scale(dist);
		Vector2f.sub(cam.offset, cam.pos, temp2);
		Vector2f.sub(temp1, temp2, temp3);
		Vector2f pos2 = new Vector2f(temp3.x*cam.zoom, (temp3.y-b.z*(float)Math.cos(cam.angle))*cam.zoom);
		System.out.println(pos +" | "+ pos2);
		g.drawLine((pos.getX()), (pos.getY()), 
				(pos2.getX()), (pos2.getY()));
	}
	
	
}
