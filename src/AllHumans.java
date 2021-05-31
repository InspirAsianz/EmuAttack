import java.awt.Graphics;
import java.util.*;

public class AllHumans {

	private List<Human> humans;
	
	public static Random rand = new Random();
	
	public AllHumans () {
		humans = new ArrayList<Human>();
	}
	
	public void add(Human h) {
		humans.add(h);
	}
	
	public int getSize() {
		return humans.size();
	}
	
	public void moveAll (Graphics window, AllProjectiles proj, int emuy) {
		for (Human h : humans) {						
			if (h.getX() + h.getWidth()/2 == h.getDestination()[0] && 
					h.getY() + h.getHeight()/2 == h.getDestination()[1]) {
				h.setArrived(System.currentTimeMillis());
				if (h.shoot(proj) && System.currentTimeMillis() > h.getArrived() + 350) {
					if (!Game.IS_MULTIPLAYER) {
						h.generateDestination(emuy);
					}
					else{
						h.setStandby(true);
					}
				}
			}
			
			h.move();
			h.draw(window);
		}
	}
	
	public Human getHumanClick(int x, int y) {
		for (Human h : humans) {
			if (h.getX() < x && h.getX() + h.getWidth() > x && h.getY() < y && h.getY() + h.getHeight() > y) {
				h.setSelected(!h.getSelected());
				if (h.getSelected()) return h;
			}
		}
		return null;
	}
	
	public boolean clickSameHuman(Human h, int x, int y) {
		return (h.getX() < x && h.getX() + h.getWidth() > x && h.getY() < y && h.getY() + h.getHeight() > y);
	}
	
	public boolean checkCollides(ArrayList<Projectile> proj) {
		for (int i = 0; i < humans.size(); i++) {
			for (int j = 0; j < proj.size(); j++) {
				try {
					if (humans.get(i).didCollideAny(proj.get(j)) && proj.get(j).getLeft() == false) {
						humans.remove(i);
						if (proj.get(j).getHeight() < 100) proj.remove(j);
						return true;
					}
				} catch (Exception e) {
				}
			}
		}
		return false;
	}
	
	public void reset() {
		humans.clear();
	}
}
