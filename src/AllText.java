import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

public class AllText {
	
	private ArrayList<Text> texts;
	private ArrayList<FadingText> fadingTexts;
	
	private long lastRed;
	
	public AllText() {
		texts = new ArrayList<Text>();
		fadingTexts = new ArrayList<FadingText>();
		lastRed = 0;
	}
	
	public void add(String te, int x, int y, Font f, Color c, Graphics g) {
		Text t = new Text(x, y, te, f, c, g);
		texts.add(t);
	}
	
	public void add(String te, int x, int y, Font f, Color c, Graphics g, int w) {
		Text t = new Text(x, y, te, f, c, g, w);
		texts.add(t);
	}
	
	public void addFading(String te, int x, int y, Font f, Color c, Graphics g, int w) {
		if (System.currentTimeMillis() < lastRed + 500 && c.equals(Color.RED)) {
			return;
		}
		FadingText t = new FadingText(x, y, te, f, c, g, w);
		fadingTexts.add(t);
		if (c.equals(Color.RED)) {
			lastRed = System.currentTimeMillis();
		}
	}
	
	public void drawAll(Graphics graphToBack) {
		for (Text t : texts) {
			t.draw(graphToBack);
		}
	}
	
	public void drawAllFading(Graphics graphToBack) {
		for (int i = 0 ; i < fadingTexts.size(); i++) {
			try {
				fadingTexts.get(i).draw(graphToBack);
				if (fadingTexts.get(i).isDone()) {
					fadingTexts.remove(i);
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	public void clearFading() {
		fadingTexts.clear();
	}
	
	public int getSize() {
		return texts.size();
	}
	
	public Text getText(String s) {
		for (Text t : texts) {
			if (s.equals(t.getText())) return t;
		}
		return null;
	}
}
