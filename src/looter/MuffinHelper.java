package looter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MuffinHelper {
	enum RoomType{
		spawn,
		hallway,
		junction,
		encounter,
		miniboss,
		boss
	}
	ArrayList<Room> rooms = new ArrayList<Room>();
	MyMuffin muffin = new MyMuffin();
	String path;
	public MuffinHelper() {
		try {
			path = new java.io.File(".").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			loadLevelFile(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int id = 0; id < rooms.size(); id++) {
			rooms.get(id).ID = id;
			saveLevelFile(path);
		}
	}
	public void loadLevelFile(String dir) throws IOException {
		rooms = muffin.loadLevelFile(dir, "Data");
	}
	public Room loadRooms(int index) throws IOException{
		loadLevelFile(path);
		if(index > rooms.size()-1) {
			return null;
		}
		return rooms.get(index);
	}
	
	public void saveLevelFile(String dir) {
		try {
			muffin.saveLevelFile(rooms ,dir, "Data");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveLevel(Room room) {
		rooms.add(room.ID, room);
		saveLevelFile(path);
	}
}
