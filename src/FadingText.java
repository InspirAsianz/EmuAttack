import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class FadingText extends Text {
	
	private int opacity;
	
	public FadingText(int x, int y, String t, Font f, Color c, Graphics g) {
		super(x, y, t, f, c, g);
		opacity = 100;
	}
	
	public FadingText(int x, int y, String t, Font f, Color c, Graphics g, int w) {
		super(x, y, t, f, c, g, w);
		opacity = 100;
	}
	
	@Override
	public void draw(Graphics g) {
		g.setFont(getFont());
		g.setColor(new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), (int)(Math.max(0, opacity) * 1.8)));
		g.drawString(getText(), getX(), getY() + opacity - 175);
		
		opacity--;
	}
	
	public boolean isDone() {
		return opacity <= 0;
	}
}
