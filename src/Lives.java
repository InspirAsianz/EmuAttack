import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class Lives extends GameObject {
	
	private Image heart;
	private Image blankHeart;
	private int lives;
	private int maxLives;
	
	private int width;
	private int height;
	
	public Lives(int x, int y, int l) {
		super(x, y);
		lives = l;
		maxLives = l;
		
		Image image = null;
		Image image1 = null;
		try {
			URL loc = getClass().getResource("/heart.png");
			image = ImageIO.read(loc);
			
			loc = getClass().getResource("/blankheart.png");
			image1 = ImageIO.read(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		image = image.getScaledInstance(image.getWidth(null) * 3, image.getHeight(null) * 3, 2);
		image1 = image1.getScaledInstance(image1.getWidth(null) * 3, image1.getHeight(null) * 3, 2);
		
		width = image.getWidth(null);
		height = image.getHeight(null);
		
		heart = image;
		blankHeart = image1;
	}
	
	public int getLives() {
		return lives;
	}
	
	public int getMaxLives() {
		return maxLives;
	}
	
	public void setTotalLives(int l) {
		lives = l;
		maxLives = l;
	}
	
	public void gainLives(int l) {
		lives = Math.min(lives + l, maxLives);
	}
	
	public void loseLife() {
		lives--;
	}
	
	public void drawLives (Graphics window) {
		int curX = getX();
		int curY = getY();
		for (int i = 0; i < maxLives; i++) {
			if (i % 10 == 0 && i != 0) {
				curY -= 2 + height;
				curX = getX();
			}
			if (i < lives) window.drawImage(heart, curX, curY, null);
			else window.drawImage(blankHeart, curX, curY, null);
			curX += 2 + width;
		}
	}
}
