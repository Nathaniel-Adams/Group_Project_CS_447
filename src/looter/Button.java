package looter;


import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.MouseOverArea;

enum ButtonType {
	TRANSITIONBUTTON,
	TOGGLEBUTTON
}

public class Button {
	
	

	MouseOverArea area;
	String text;
	boolean is_init = false;
	ButtonType type;
	
	int flag;
	
	public Button(MouseOverArea area, String text) {
		this.area = area;
		this.text = text;
	}

	public Button(GameContainer container, Image image, int x, int y, String text) {
		MouseOverArea temp = new MouseOverArea(container,image, x, y);
		temp.setMouseOverColor(Color.green);
		temp.setMouseDownColor(Color.gray);
		this.area = temp;
		this.text = text;
	}
	
	public Button(GameContainer container, Image testTexture, float x, float y, String string) {
		this(container, testTexture, (int)x, (int)y, string);
	}

	public void setType(ButtonType type) {
		this.type = type;
	}
	
	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void render(GameContainer container, Graphics g) {
		area.render(container, g);
		g.drawString(text, area.getX()+area.getWidth()+10, area.getY()+area.getHeight()/3);
	}
	
	public boolean isMouseOver() {
		return area.isMouseOver();
	}
	
}
