import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Score extends GameObject {
	
	private int score;
	private Font font;
	
	private long lastPassive;
	
	public Score(Font f) {
		super();
		setScore(0);
		font = f;
		lastPassive = 0;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int s) {
		score = s;
	}
	
	public void incScore(int i) {
		score += i;
	}
	
	public void passiveGainScore() {
		if (System.currentTimeMillis() >= lastPassive + 1000) {
			score++;
			lastPassive = System.currentTimeMillis();
		}
	}
	
	public void draw(Graphics graphToBack) {
		graphToBack.setFont(font);
		graphToBack.setColor(Color.BLACK);
		
		int w = graphToBack.getFontMetrics().stringWidth("Score : " + score);
		
		graphToBack.drawString("Score : " + score, Game.WIDTH - w - 15, 25);
	}
}
