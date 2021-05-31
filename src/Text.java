import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Text extends GameObject {
	
	private String text;
	
	private Font font;
	
	private Color color;
	
	private int width;
	private int height;
	
	public Text() {
		super();
	}
		
	public Text(int x, int y, String t, Font f, Color c, Graphics g) {
		super(x, y);
		setText(t);
		font = f;
		color = c;
		
		g.setFont(font);
		g.setColor(color);
		width = g.getFontMetrics().stringWidth(text);
		height = g.getFontMetrics().getHeight();

	}
	
	public Text(int x, int y, String t, Font f, Color c, Graphics g, int w) {
		super(x, y);
		setText(t);
		font = f;
		color = c;
		
		g.setFont(font);
		g.setColor(color);
		width = g.getFontMetrics().stringWidth(text);
		height = g.getFontMetrics().getHeight();

		
		setX(w/2 - getWidth()/2);
	}
	
	public void setText(String t) {
		text = t;
	}
	
	public String getText() {
		return text;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Font getFont() {
		return font;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void draw (Graphics g) {
		g.setFont(font);
		g.setColor(color);
		g.drawString(text, getX(), getY());
	}
	
	public boolean isDone() {
		return false;
	}
}
