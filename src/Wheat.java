import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Wheat extends MovingThing {
	
	private long spawnTime;
	private int aliveTime;
	
	public Wheat(int x, int y, int alive) {
		super (x, y, 0, 0, 0, 0);
		
		Image image = null;
		try {
			URL loc = getClass().getResource("/wheatsmall.png");
			image = ImageIO.read(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//image = image.getScaledInstance(image.getWidth(null)/15, image.getHeight(null)/15, 2);
		
		setWidth(image.getWidth(null));
		setHeight(image.getHeight(null));
		
		setImage(image);
		
		spawnTime = System.currentTimeMillis();
		aliveTime = alive;
	}
	
	public boolean isDone () {
		return spawnTime + aliveTime < System.currentTimeMillis();
	}
}
