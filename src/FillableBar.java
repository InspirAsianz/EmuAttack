import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class FillableBar extends GameObject {
	
	private double filled;
	private double max;
	
	private int width;
	private int height;
	
	private Color color;
	
	private Image icon;
	
	public FillableBar(int x, int y, int w, int h, Color c, double f, double m) {
		super(x, y);
		width = w;
		height = h;
		color = c;
		filled = f;
		max = m;
		icon = null;
	}
	
	public FillableBar(int x, int y, int w, int h, Color c, double f, double m, String imageLoc, double scale) {
		super(x, y);
		width = w;
		height = h;
		color = c;
		filled = f;
		max = m;
		Image image = null;
		try {
			URL loc = getClass().getResource(imageLoc);
			image = ImageIO.read(loc);
		} catch (IOException e) {
			e.printStackTrace();
		}
		image = image.getScaledInstance((int)(image.getWidth(null)* scale), 
				(int)(image.getHeight(null) * scale), 2);
		icon = image;
	}
	
	public void increase(double d) {
		filled = Math.min(filled + d, max);
	}
	
	public void decrease(double d) {
		filled = Math.max(filled - d, 0);
	}
	
	public boolean isEmpty() {
		return filled == 0;
	}
	
	public boolean isFull() {
		return filled == max;
	}
	
	public void reset() {
		filled = max;
	}
	
	public void setFilled(int i) {
		filled = i;
	}
	
	public void draw(Graphics graphToBack) {
		
		graphToBack.setColor(color);
		graphToBack.fillRoundRect(getX(), getY(), (int)(filled/max * width), height, 25, 25);
		
		/*
		 * Changing stroke size from https://stackoverflow.com/questions/4219511/draw-rectangle-border-thickness
		 */
		graphToBack.setColor(Color.GRAY);
		Graphics2D g2 = (Graphics2D) graphToBack;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(getX(), getY(), width, height, 25, 25);
		g2.setStroke(oldStroke);
		
		if (icon != null) {
			graphToBack.drawImage(icon, getX() - icon.getWidth(null) - 10, 
					getY() + height/2 - icon.getHeight(null)/2, null);
		}
	}
}
