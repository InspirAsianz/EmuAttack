import java.awt.Graphics;
import java.util.*;

public class AllProjectiles {
	
	private ArrayList<Projectile> projectiles;
	
	private static final int MAX_PROJECTILES = 250;
	
	public AllProjectiles () {
		projectiles = new ArrayList<Projectile>();
	}
	
	public void add(Projectile p) {
		if (projectiles.size() > MAX_PROJECTILES) return;
		projectiles.add(p);
	}
	
	public void moveAll(Graphics window) {
		for (Projectile p : projectiles) {
			p.move();
			p.draw(window);
		}
	}
	
	public void cleanAll (int w) {
		for (int i = 0; i < projectiles.size(); i++){
			if (projectiles.get(i).getLeft() && projectiles.get(i).getX() < 0) {
				projectiles.remove(i);
			}
		}
	}
	
	public ArrayList<Projectile> getArray() {
		return projectiles;
	}
	
	public void reset() {
		projectiles.clear();
	}
	
}
