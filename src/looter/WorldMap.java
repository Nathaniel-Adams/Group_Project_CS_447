package looter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorldMap implements Serializable{


	private static final int ROOM_NOT_INIT = -1;
	private static final long serialVersionUID = -4730916915548466989L;
	HashMap<Integer,Room> Dungeon;
	int numRooms;
	
	public WorldMap() {
		Dungeon = new HashMap<Integer,Room>();
		numRooms = 0;
		demoAdd();
		demoAdd();
	}
	
	public void render(ArrayList<GraphicsObj> renderList, Camera cam, int roomNum) {
		if(!Dungeon.isEmpty()) {
			Room room = Dungeon.get(roomNum);
			room.render(renderList, cam);
			if (room.left != ROOM_NOT_INIT) {
				Dungeon.get(room.left).render(renderList, cam);
			}
			if (room.right != ROOM_NOT_INIT) {
				Dungeon.get(room.right).render(renderList, cam);
			}
			if (room.up != ROOM_NOT_INIT) {
				Dungeon.get(room.up).render(renderList, cam);
			}
			if (room.down != ROOM_NOT_INIT) {
				Dungeon.get(room.down).render(renderList, cam);
			}
		}
		
	}
	
	public void demoAdd() {
		Room temp = new Room();
		int [][][] m = {
				{
					{0,7,7,0},
					{7,7,7,63},
					{1,7,7,63},
					{0,7,7,0}
				},
				{
					{0,0,0,7},
					{0,0,0,7},
					{0,0,0,0},
					{0,0,0,0}
				}
		};
		
		m[0][2][3] = BitMasker.setFW(m[0][2][3], 1);
		m[0][1][3] = BitMasker.setFW(m[0][1][3], 1);
//		{0,0,1,1,0,0},
//		{0,1,0,1,1,0},
//		{0,1,1,1,1,0},
//		{0,0,1,1,0,0}
		temp.map=m;
		temp.ID = numRooms;
		if(temp.ID > 0) {
			temp.down = temp.ID-1;
			Room previous = Dungeon.get(temp.down);
			previous.up = temp.ID;
			temp.offset.y = previous.offset.y - previous.map[0].length;
			temp.offset.z = temp.ID;
			temp.type = RoomType.hallway;
			
			temp.map[1][0][3] = BitMasker.setActive(temp.map[1][0][3], 1);
			temp.map[1][0][3] = BitMasker.setHOT(temp.map[1][0][3], 2);
		}
		else {
			temp.type = RoomType.spawn;
			temp.map[0][2][0] = BitMasker.setActive(temp.map[0][2][0], 1);
			temp.map[0][2][0] = BitMasker.setHOT(temp.map[0][2][0], 1);
			
			temp.map[0][1][2] = BitMasker.setActive(temp.map[0][1][2], 1);
			System.out.println(Integer.toBinaryString(temp.map[0][1][2]));
			temp.map[0][1][2] = BitMasker.setHOT(temp.map[0][1][2], 3);
			System.out.println(Integer.toBinaryString(temp.map[0][1][2]));
			temp.map[0][1][2] = BitMasker.setEffectIndex(temp.map[0][1][2], 7);
			System.out.println(Integer.toBinaryString(temp.map[0][1][2]));
			System.out.println(BitMasker.getActive(temp.map[0][1][2]) +" | "+ BitMasker.getHOT(temp.map[0][1][2]) + " | " + BitMasker.getEffectIndex(temp.map[0][1][2]));
			System.out.println(Integer.toBinaryString(temp.map[0][1][2]));
		}
		Dungeon.put(temp.ID, temp);
		numRooms++;
	}
	
	public void demoAdd2() {
		Room temp = new Room();
		int [][][] m = {
				{
					{15}
				}
		};
//		{0,0,1,1,0,0},
//		{0,1,0,1,1,0},
//		{0,1,1,1,1,0},
//		{0,0,1,1,0,0}
		temp.map=m;
		temp.ID = numRooms;
		if(temp.ID > 0) {
			temp.down = temp.ID-1;
			Room previous = Dungeon.get(temp.down);
			previous.up = temp.ID;
			temp.offset.y = previous.offset.y - previous.map[0].length;
			temp.offset.z = temp.ID;
			temp.type = RoomType.hallway;
		}
		else {
			temp.type = RoomType.spawn;
			temp.map[0][0][0] = BitMasker.setHOT(temp.map[0][0][0], 3);
			temp.map[0][0][0] = BitMasker.setEffectIndex(temp.map[0][0][0], 7);
		}
		Dungeon.put(temp.ID, temp);
		numRooms++;
	}
	
	public void print() {
		for(Map.Entry<Integer, Room> entry: Dungeon.entrySet()) {
			entry.getValue().print();
		}
	}
	
}
