import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.Random;

public class Human extends MovingThing implements Collideable{
	
	private static Random rand = new Random();
	
	private int[] destination;
	
	private boolean standby;
	
	private boolean selected;
	
	private long arrived;
	private long lastFired;
			
	public Human (int x, int y, int xs, int ys) {
		super (x, y, 0, 0, xs, ys);
		
		Image image = null;
		try {
			URL loc = getClass().getResource("/personwithgun.png");
			image = ImageIO.read(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		destination = new int[2];
		int x_rand = rand.nextInt(200) + 1350;
		int y_rand = rand.nextInt(800) + 50;
		setDestination(x_rand, y_rand);
		setY(y_rand);
		
		arrived = 0;
		lastFired = 0;
		
		image = image.getScaledInstance(image.getWidth(null)/3 * 2, image.getHeight(null)/3 * 2, 2);
		
		setWidth(image.getWidth(null));
		setHeight(image.getHeight(null));
		
		setImage(image);
		
		standby = false;
		selected = false;
	}
	
	public void setStandby(boolean b) {
		standby = b;
	}
	
	public void setSelected(boolean b) {
		selected = b;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setDestination(int x, int y) {		
		destination[0] = x;
		destination[1] = y;
	}
	
	public int[] getDestination() {
		return destination;
	}
	
	public void generateDestination(int emuy) {		
		int followHuman = rand.nextInt(5);
		int x_rand, y_rand;
		if (followHuman == 2) {
			x_rand = rand.nextInt(300) + 1250;
			y_rand = rand.nextInt(100) + emuy - 50;
		}
		else {
			x_rand = rand.nextInt(300) + 1250;
			y_rand = rand.nextInt(800) + 50;
		}
		setDestination(x_rand, y_rand);
	}
	
	public void move () {		
		if (getX() + getWidth()/2 < destination[0]) {
			setX(getX() + getXSpeed());
			if (getX() + getWidth()/2 > destination[0]) {
				setX(destination[0] - getWidth()/2);
			}
		}
		else if (getX() + getWidth()/2 > destination[0]) {
			setX(getX() - getXSpeed());
			if (getX() + getWidth()/2 < destination[0]) {
				setX(destination[0] - getWidth()/2);
			}
		}
		
		if (getY() + getHeight()/2 < destination[1]) {
			setY(getY() + getYSpeed());
			if (getY() + getHeight()/2 > destination[1]) {
				setY(destination[1] - getHeight()/2);
			}
		}
		else if (getY() + getHeight()/2 > destination[1]) {
			setY(getY() - getYSpeed());
			if (getY() + getHeight()/2 < destination[1]) {
				setY(destination[1] - getHeight()/2);
			}
		}
	}
	
	public boolean shoot (AllProjectiles proj) {
		if (System.currentTimeMillis() < arrived + 100 || System.currentTimeMillis() > arrived + 100 + 300) return false;
		if (System.currentTimeMillis() > lastFired + 50) {
			Bullet b = new Bullet(getX(), getY() + getWidth()/3-3);
			proj.add(b);
			lastFired = System.currentTimeMillis();
		}
		return true;
	}
	
	public void setArrived (long i) {
		int wait = 400;
		if (standby) wait += 600;
		if (i - wait < arrived) return;
		arrived = i;
	}
	
	public long getArrived() {
		return arrived;
	}
	
	public boolean didCollideAny(Object obj) {
		MovingThing other = (MovingThing)obj;
		
		if (getX() + getWidth()/2 < other.getX() + other.getWidth() &&
				   getX() + getWidth() > other.getX() &&
				   getY() < other.getY() + other.getHeight() &&
				   getY() + getHeight() > other.getY()) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void draw(Graphics graphToBack) {
		super.draw(graphToBack);
		
		if (selected) {
			graphToBack.setColor(new Color(123, 212, 123));
			Graphics2D g2 = (Graphics2D) graphToBack;
			Stroke oldStroke = g2.getStroke();
			g2.setStroke(new BasicStroke(5));
			g2.drawRect(getX(), getY(), getWidth(), getHeight());
			g2.setStroke(oldStroke);
		}
	}
}
