import javax.swing.JFrame;
import java.awt.Component;
import java.awt.Dimension;


public class GameRunner {

	public static final int WIDTH = 1600;
	public static final int HEIGHT = 900;
	
	JFrame frame;
	
	public GameRunner () {
		
		frame = new JFrame("Emu Attack");
		Game game = new Game(WIDTH, HEIGHT);
		
		frame.add(game);
		frame.addKeyListener(game);
		frame.addMouseListener(game);
		frame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		GameRunner start = new GameRunner();
	}
}
