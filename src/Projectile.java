
public class Projectile extends MovingThing {
	private boolean left;
	
	public Projectile () {
		super();
		left = false;
	}
	
	public Projectile (int x, int y, int w, int h, int xs, int ys, boolean l) {
		super(x, y, w, h, xs, ys);
		left = l;
	}
	
	public boolean getLeft() {
		return left;
	}
	
	public void move () {
		if (left) setX(getX() - getXSpeed());
		else setX(getX() + getXSpeed());
	}
}
