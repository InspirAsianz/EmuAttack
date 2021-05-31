import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Egg extends Projectile {
	public Egg (int x, int y) {
		super (x, y, 0, 0, 12, 0, false);
		
		Image image = null;
		try {
			URL loc = getClass().getResource("/eggimage.png");
			image = ImageIO.read(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		image = image.getScaledInstance(image.getWidth(null)/10, image.getHeight(null)/10, 2);
		
		setWidth(image.getWidth(null));
		setHeight(image.getHeight(null));
		
		setImage(image);
	}
	
	public Egg(int x, int y, boolean op) {
		super (x, y, 0, 0, 12, 0, false);
		
		Image image = null;
		try {
			URL loc = getClass().getResource("/eggimage.png");
			image = ImageIO.read(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		image = image.getScaledInstance(image.getWidth(null), image.getHeight(null), 2);
		
		setWidth(image.getWidth(null));
		setHeight(image.getHeight(null));
		
		setImage(image);

	}
}
