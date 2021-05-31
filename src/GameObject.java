
public class GameObject {
	
	private int xPos;
	private int yPos;
	
	public GameObject() {
		setPos(0, 0);
	}
	
	public GameObject (int x, int y)
	{
		setPos(x, y);
	}
	
	public void setPos( int x, int y)
	{
		setX(x);
		setY(y);
	}

	public void setX(int x)
	{
		xPos = x;
	}

	public void setY(int y)
	{
		yPos = y;
	}

	public int getX()
	{
		return xPos;
	}

	public int getY()
	{
		return yPos;
	}
}
