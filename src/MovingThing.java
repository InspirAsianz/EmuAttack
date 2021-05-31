import java.awt.Graphics;
import java.awt.Image;

public class MovingThing extends GameObject {
	
	private int width;
	private int height;
	private int xSpeed;
	private int ySpeed;
	private Image image;
	
	public MovingThing () {
		setWidth(0);
		setHeight(0);
		setXSpeed(0);
		setYSpeed(0);
		image = null;
	}
	
	public MovingThing (int x, int y, int w, int h, int xs, int ys) {
		super(x, y);
		setWidth(w);
		setHeight(h);
		setXSpeed(xs);
		setYSpeed(ys);
		image = null;
	}

	public void setWidth(int w) {
		width = w;
	}
	
	public void setHeight (int h) {
		height = h;
	}
	
	public void setXSpeed (int x) {
		xSpeed = x;
	}
	
	public void setYSpeed (int y) {
		ySpeed = y;
	}
	
	public void setImage (Image i) {
		image = i;
	}
	
	public int getWidth () {
		return width;
	}
	
	public int getHeight () {
		return height;
	}
	
	public int getXSpeed () {
		return xSpeed;
	}
	
	public int getYSpeed () {
		return ySpeed;
	}
	
	public Image getImage () {
		return image;
	}
	
	public void move () {
		setX(getX() + getXSpeed());
		setY(getY() + getYSpeed());
	}
	
	public void draw (Graphics window)
	{
		window.drawImage(image, getX(), getY(), getWidth(), getHeight(), null);
	}
}
