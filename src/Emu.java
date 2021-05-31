import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.util.*;


public class Emu extends MovingThing implements Collideable {
	
	private int diagonalXSpeed;
	private int diagonalYSpeed;
	
	private boolean dashing;
	
	public Emu (int x, int y, int xs, int ys) {
		super (x, y, 0, 0, xs, ys);
		
		Image image = null;
		try {
			URL loc = getClass().getResource("/emuimage.png");
			image = ImageIO.read(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		diagonalXSpeed = (int)((double)getXSpeed() / Math.sqrt(2));
		diagonalYSpeed = (int)((double)getYSpeed() / Math.sqrt(2));
		
		image = image.getScaledInstance(image.getWidth(null)/3, image.getHeight(null)/3, 2);
		
		setWidth(image.getWidth(null));
		setHeight(image.getHeight(null));
		
		setImage(image);
		
		dashing = false;
	}
	
	public void updateDiagonalSpeed() {
		diagonalXSpeed = (int)((double)getXSpeed() / Math.sqrt(2));
		diagonalYSpeed = (int)((double)getYSpeed() / Math.sqrt(2));
	}
	
	public void setDashing(boolean b) {
		dashing = b;
	}
	
	public void move (String direction) {
		switch (direction) {
			case "UP":
				setY(getY() - getYSpeed());
				break;
			case "DOWN":
				setY(getY() + getYSpeed());
				break;
			case "LEFT":
				setX(getX() - getXSpeed());
				break;
			case "RIGHT":
				setX(getX() + getXSpeed());
				break;
			case "UPLEFT":
				setX(getX() - diagonalXSpeed);
				setY(getY() - diagonalYSpeed);
				break;
			case "UPRIGHT":
				setX(getX() + diagonalXSpeed);
				setY(getY() - diagonalYSpeed);
				break;
			case "DOWNLEFT":
				setX(getX() - diagonalXSpeed);
				setY(getY() + diagonalYSpeed);
				break;
			case "DOWNRIGHT":
				setX(getX() + diagonalXSpeed);
				setY(getY() + diagonalYSpeed);
				break;
		}
	}
	
	public void draw(Graphics graphToBack) {
		super.draw(graphToBack);
		if (dashing) {
			graphToBack.setColor(Color.BLACK);
			graphToBack.fillRect(getX(), getY(), 5, 5);
		}
	}
	
	public boolean didCollideAny(Object obj) {
		MovingThing other = (MovingThing)obj;
		
		if (getX() < other.getX() + other.getWidth() &&
				   getX() + getWidth() > other.getX() &&
				   getY() < other.getY() + other.getHeight() &&
				   getY() + getHeight() > other.getY()) {
			return true;
		}
		
		return false;
	}
	
	public boolean checkCollisionsProjectiles (ArrayList<Projectile> proj) {
		boolean out = false;
		for (int j = 0; j < proj.size(); j++) {
			try {
				if (didCollideAny(proj.get(j)) && proj.get(j).getLeft()) {
					proj.remove(j);
					out = true;
				}
			} catch (Exception e) {
			}
		}
		return out;
	}
	
	public boolean checkCollisionsWheat (ArrayList<Wheat> wheats) {
		for (int j = 0; j < wheats.size(); j++) {
			try {
				if (didCollideAny(wheats.get(j))) {
					wheats.remove(j);
					return true;
				}
			} catch (Exception e) {
			}
		}
		return false;
	}
}
