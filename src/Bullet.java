import java.awt.Graphics;
import java.awt.Color;

public class Bullet extends Projectile {
	public Bullet (int x, int y) {
		super (x, y, 10, 6, 15, 0, true);	
	}
	
	public void draw (Graphics window)
	{
		window.setColor(Color.BLACK);
		window.fillRect(getX(), getY(), getWidth(), getHeight());
	}
}
