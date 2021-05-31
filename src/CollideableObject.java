
public class CollideableObject extends MovingThing implements Collideable{
	public CollideableObject() {
		super();
	}
	
	public CollideableObject(int x, int y, int w, int h, int xs, int ys) {
		super (x, y, w, h, xs, ys);
	}
	
	public boolean didCollideLeft(Object obj){
		MovingThing other = (MovingThing)obj;
		if (getX() > other.getX() && getX() <= other.getX()+other.getWidth() && 
				(getY() >= other.getY() && getY()+getHeight() <= other.getY()+other.getHeight())){
			return true;
		}
		return false;
	}
	public boolean didCollideRight(Object obj){
		MovingThing other = (MovingThing)obj;
		if (getX()<other.getX() && getX()+getWidth()>=other.getX() && 
				(getY() >= other.getY() && getY()+getHeight() <= other.getY()+other.getHeight())){
			return true;
		}
		return false;
	}
	public boolean didCollideTop(Object obj){
		MovingThing other = (MovingThing)obj;
		if (getY()+getHeight() >= other.getY() && getY() < other.getY()+other.getHeight() && 
				(getX()>=other.getX() && getX()+getWidth()<=other.getX()+other.getWidth())){
			return true;
		}
		return false;
	}
	public boolean didCollideBottom(Object obj){
		MovingThing other = (MovingThing)obj;
		if (getY() <= other.getY()+other.getHeight() && getY()+getHeight() > other.getY() && 
				(getX()>=other.getX() && getX()+getWidth()<=other.getX()+other.getWidth())){
			return true;
		}
		return false;
	}

	public boolean didCollideAny(Object obj) {
		if (didCollideBottom(obj) || didCollideLeft(obj) || didCollideRight(obj) || didCollideTop(obj)) {
			System.out.println("hi");
			return true;
		}
		return false;
	}
}
