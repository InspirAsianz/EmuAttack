import java.awt.Graphics;
import java.util.ArrayList;

public class AllWheat {
	
	private ArrayList<Wheat> wheats;
	
	public AllWheat () {
		wheats = new ArrayList<Wheat>();
	}
	
	public void add (Wheat w) {
		wheats.add(w);
	}
	
	public ArrayList<Wheat> getArray() {
		return wheats;
	}
	
	public void drawAll(Graphics window) {
		for (int i = 0; i < wheats.size();i++) {
			try {
				wheats.get(i).draw(window);
				if (wheats.get(i).isDone()) {
					wheats.remove(i);
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	public void reset() {
		wheats.clear();
	}
}
