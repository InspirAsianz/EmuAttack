import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import java.awt.Image;
import java.awt.Stroke;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;


public class Game extends JPanel implements KeyListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Random rand = new Random();
	
	private HashMap<String, Boolean> keys;
	private BufferedImage back;
	private long tick;
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static boolean IS_MULTIPLAYER = false;
	private Human selectedHuman;
	
	// FPS stuff
	private boolean showFPS;
	private long fpsStart;
	private int fps;
	private int fpsLow;
	private int frameCount;
	private int highestGap;
	private long lastFrame;
	
	// Assets
	private Font megaFont;
	private Font bigFont;
	private Font mediumFont;
	private Font smallFont;
	private Font costFont;
	private Font purchaseFont;
	private Font howToPlayFont;
	private Font highScoreFont;
	private Image river;
	private Image emuImage;
	private Image humanImage;
	private Image coinImage;
	private Image coinImage2;
	
	// Enemy timers
	private long startTime;
	private long lastSpawn;
	
	// Game Objects
	private Emu emu;
	private AllProjectiles projectiles;
	private AllHumans humans;
	private Lives lives;
	private AllWheat wheats;
	private Score score;
	private FillableBar dashBar;
	private FillableBar megaBar;
	private AllText titleTexts;
	private AllText upgradeTexts;
	private AllText optionTexts;
	
	// Emu
	private int START_SPEED;
	private int DASH_SPEED;
	private double DASH_USAGE;
	private double DASH_REGEN;

	
	// Eggs
	private long lastEgg;
	private int EGG_COOLDOWN;
	
	// Wheat timers
	private long lastWheat;
	private int wheatCooldown;
	private int WHEAT_ALIVE_TIME;
	private int WHEAT_LIVES_GAINED;
	private int LOW_WHEAT_COOLDOWN;
	private int HIGH_WHEAT_COOLDOWN;
	
	// Coins
	private int coins;
	private int enemiesKilled;
	private int COINS_PER_ENEMY;
		
	private int highScore;
	private boolean newHighScore;
	
	private boolean paused;
	
	private boolean WASD;
	
	private static boolean hacking = true;
	
	/*
	 * Preferences code references from https://www.vogella.com/tutorials/JavaPreferences/article.html
	 */
	private Preferences prefs;
	
	/*
	 * Enum next/previous code from https://stackoverflow.com/questions/17006239/whats-the-best-way-to-implement-next-and-previous-on-an-enum-type
	 */
	private enum Scene {
		TitleScene,
		UpgradeScene,
		HowToPlayScene,
		OptionScene,
		GameScene,
		EndScene;
	};
	private Scene currentScene;
	
	private enum TitleSelection {
		Play,
		Upgrades,
		HowToPlay,
		Options,
		Quit;
	    private static TitleSelection[] vals = values();
	    public TitleSelection next()
	    {
	        return vals[(this.ordinal()+1) % vals.length];
	    }
	    public TitleSelection previous() {
	    	return vals[(this.ordinal()+vals.length-1) % vals.length];
	    }
	}
	private TitleSelection currentTitleSelection;
	
	private enum UpgradeSelection {
		Health,
		Egg,
		Dash,
		CoinsPerKill,
		Powerup;
	    private static UpgradeSelection[] vals = values();
	    public UpgradeSelection next()
	    {
	        return vals[(this.ordinal()+1) % vals.length];
	    }
	    public UpgradeSelection previous() {
	    	return vals[(this.ordinal()+vals.length-1) % vals.length];
	    }
	}
	private UpgradeSelection currentUpgradeSelection;
	
	private enum OptionSelection {
		ShowFPS,
		Controls,
		Multiplayer,
		Reset;
	    private static OptionSelection[] vals = values();
	    public OptionSelection next()
	    {
	        return vals[(this.ordinal()+1) % vals.length];
	    }
	    public OptionSelection previous() {
	    	return vals[(this.ordinal()+vals.length-1) % vals.length];
	    }
	}
	private OptionSelection currentOptionSelection;
	
	private int healthLevel;
	private int eggLevel;
	private int dashLevel;
	private int coinsLevel;
	private int powerupLevel;
	
	private String[] healthCosts;
	private String[] eggCosts;
	private String[] dashCosts;
	private String[] coinsCosts;
	private String[] powerupCosts;
	
	/*
	 * TO DO:
	 * No stats gained from multiplayer
	 * Timer for multiplayer
	 * 
	 * MAYBE:
	 * Mega powerup that is charged by wheat
	 * Visual indication of dashing
	 * Total Stats "x enemies killed, y eggs shot, z total coins gained"
	 */
	
	public Game (int w, int h) {
		keys = new HashMap<String, Boolean>();
		keys.put("UP", false);
		keys.put("DOWN", false);
		keys.put("LEFT", false);
		keys.put("RIGHT", false);
		keys.put("SHOOT", false);
		keys.put("DASH", false);
		keys.put("SUPER", false);
		keys.put("MENU UP", false);
		keys.put("MENU DOWN", false);
		keys.put("ENTER", false);
		keys.put("ESCAPE", false);
		keys.put("COINS", false);
		
		currentScene = Scene.TitleScene;
		currentTitleSelection = TitleSelection.Play;
		currentUpgradeSelection = UpgradeSelection.Health;
		currentOptionSelection = OptionSelection.ShowFPS;
		tick = 0;
		
		WIDTH = w;
		HEIGHT = h;
		
		showFPS = false;
		
		megaFont = new Font("Courier", Font.BOLD, 144);
		bigFont = new Font("Courier", Font.BOLD, 96);
		mediumFont = new Font("Courier", Font.PLAIN, 48);
		smallFont = new Font("Courier", Font.PLAIN, 24);
		costFont = new Font("Courier", Font.BOLD, 32);
		purchaseFont = new Font("Courier", Font.BOLD, 48);
		howToPlayFont = new Font("Courier", Font.PLAIN, 28);
		highScoreFont = new Font("Courier", Font.PLAIN, 32);
		
		try {
			URL loc = getClass().getResource("/river.png");
			river = ImageIO.read(loc);
			river = river.getScaledInstance((int)(river.getWidth(null) * 1.5), 
					(int)(river.getHeight(null) * 1.5), 2);

			loc = getClass().getResource("/emuimage.png");
			emuImage = ImageIO.read(loc);
			
			loc = getClass().getResource("/personwithgun.png");
			humanImage = ImageIO.read(loc);
			humanImage = humanImage.getScaledInstance((int)(humanImage.getWidth(null) * 1.5), 
					(int)(humanImage.getHeight(null) * 1.5), 2);
			
			loc = getClass().getResource("/coin.png");
			coinImage = ImageIO.read(loc);
			coinImage2 = coinImage.getScaledInstance((int)(coinImage.getWidth(null) * 0.65), 
					(int)(coinImage.getHeight(null) * 0.65), 2);

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		startTime = 0;
		lastSpawn = 0;
		
		START_SPEED = 7;
		DASH_SPEED = 9;
		DASH_USAGE = 0.5;
		DASH_REGEN = 0.25;
		
		emu = new Emu (WIDTH/8, HEIGHT/2, START_SPEED, START_SPEED);
		projectiles = new AllProjectiles();
		humans = new AllHumans();
		lives = new Lives(10, HEIGHT-35, 25);
		wheats = new AllWheat();
		score = new Score(smallFont);
		dashBar = new FillableBar(375, HEIGHT-75, 200, 50, new Color(96, 175, 224), 
				100, 100, "/dashicon.png", 0.2);
		megaBar = new FillableBar(675, HEIGHT-75, 200, 50, new Color(217, 58, 50), 
				0, 100, "/eggimage.png", 0.2);
		titleTexts = new AllText();
		upgradeTexts = new AllText();
		optionTexts = new AllText();
		
		lastEgg = 0;
		EGG_COOLDOWN = 1000;
		
		lastWheat = 0;
		WHEAT_ALIVE_TIME = 5000;
		WHEAT_LIVES_GAINED = 2;
		LOW_WHEAT_COOLDOWN = 10000;
		HIGH_WHEAT_COOLDOWN = 25000;
		wheatCooldown = rand.nextInt(HIGH_WHEAT_COOLDOWN - LOW_WHEAT_COOLDOWN) + LOW_WHEAT_COOLDOWN;
		
		enemiesKilled = 0;
		coins = 0;
		COINS_PER_ENEMY = 10;
		
		highScore = 0;
		newHighScore = false;
		
		paused = false;
		
		WASD = true;
				
		healthCosts = new String[]{"100", "150", "200", "250", "500", "MAX"};
		eggCosts = new String[]{"250", "500", "1000", "2500", "5000", "MAX"};
		dashCosts = new String[]{"100", "2500", "500", "1000", "2500", "MAX"};
		coinsCosts = new String[]{"50", "100", "500", "1000", "2500", "MAX"};
		powerupCosts = new String[]{"100", "250", "500", "1000", "2500", "MAX"};

		prefs = Preferences.userRoot().node(this.getClass().getName());
		
		readFile();
	}
	
	private void readFile() {
		// HighScore, Coins, Health, EggCooldown, Dash, CoinsPerKill, Powerup, ShowFPS, WASD, Multiplayer
//		HashMap<String, String> save = new HashMap<String, String>();
//		try {
//			File f = new File("C:\\Users\\andre\\eclipse-workspace\\EmuAttack\\src\\gamedata.txt");
//			Scanner read = new Scanner(f);
//			String[] line;
//			while (read.hasNextLine()) {
//				line = read.nextLine().split(":");
//				save.put(line[0], line[1]);
//			}
//			read.close();
//		} catch (Exception e){
//			e.printStackTrace();
//			System.exit(0);
//		}
//				
//		highScore = Integer.parseInt(save.getOrDefault("HighScore", "0"));
//		coins = Integer.parseInt(save.getOrDefault("Coins", "0"));
//		setLivesInfo(Integer.parseInt(save.getOrDefault("HealthLevel", "0")));
//		setEggCooldown(Integer.parseInt(save.getOrDefault("EggLevel", "0")));
//		setDashInfo(Integer.parseInt(save.getOrDefault("DashLevel", "0")));
//		setCoinsPerEnemy(Integer.parseInt(save.getOrDefault("CoinsLevel", "0")));
//		setWheatInfo(Integer.parseInt(save.getOrDefault("PowerupLevel", "0")));
//		showFPS = Boolean.parseBoolean(save.getOrDefault("FPS", "false"));
//		WASD  = Boolean.parseBoolean(save.getOrDefault("WASD", "true"));
//		IS_MULTIPLAYER = Boolean.parseBoolean(save.getOrDefault("Multiplayer", "false"));
		
		highScore = prefs.getInt("HighScore", 0);
		coins = prefs.getInt("Coins", 0);
		setLivesInfo(prefs.getInt("HealthLevel", 0));
		setEggCooldown(prefs.getInt("EggLevel", 0));
		setDashInfo(prefs.getInt("DashLevel", 0));
		setCoinsPerEnemy(prefs.getInt("CoinsLevel", 0));
		setWheatInfo(prefs.getInt("PowerupLevel", 0));
		showFPS = prefs.getBoolean("FPS", false);
		WASD  = prefs.getBoolean("WASD", true);
		IS_MULTIPLAYER = prefs.getBoolean("Multiplayer", false);
	}
	
	private void setLivesInfo(int level) {
		lives.setTotalLives(10 + (level * 10));
		healthLevel = level;
	}
	
	private void setDashInfo(int level) {
		dashLevel = level;
		switch (level) {
			case 0:
				DASH_SPEED = 9;
				DASH_USAGE = 0.5;
				DASH_REGEN = 0.25;
				break;
			case 1:
				DASH_SPEED = 9;
				DASH_USAGE = 0.45;
				DASH_REGEN = 0.275;
				break;
			case 2:
				DASH_SPEED = 10;
				DASH_USAGE = 0.45;
				DASH_REGEN = 0.275;
				break;
			case 3:
				DASH_SPEED = 10;
				DASH_USAGE = 0.4;
				DASH_REGEN = 0.3;
				break;
			case 4:
				DASH_SPEED = 11;
				DASH_USAGE = 0.4;
				DASH_REGEN = 0.3;
				break;
			case 5:
				DASH_SPEED = 11;
				DASH_USAGE = 0.35;
				DASH_REGEN = 0.325;
				break;
		}
	}
	
	private void setEggCooldown(int level) {
		eggLevel = level;
		EGG_COOLDOWN = 1000 - (level * 100);
	}
	
	private void setCoinsPerEnemy(int level) {
		coinsLevel = level;
		COINS_PER_ENEMY = 10 + (5*level);
	}
	
	private void setWheatInfo(int level) {
		powerupLevel = level;
		switch (level) {
			case 0:
				WHEAT_ALIVE_TIME = 5000;
				WHEAT_LIVES_GAINED = 2;
				LOW_WHEAT_COOLDOWN = 10000;
				HIGH_WHEAT_COOLDOWN = 25000;
				break;
			case 1:
				WHEAT_ALIVE_TIME = 5000;
				WHEAT_LIVES_GAINED = 3;
				LOW_WHEAT_COOLDOWN = 10000;
				HIGH_WHEAT_COOLDOWN = 25000;
				break;
			case 2:
				WHEAT_ALIVE_TIME = 7500;
				WHEAT_LIVES_GAINED = 3;
				LOW_WHEAT_COOLDOWN = 10000;
				HIGH_WHEAT_COOLDOWN = 20000;
				break;
			case 3:
				WHEAT_ALIVE_TIME = 7500;
				WHEAT_LIVES_GAINED = 4;
				LOW_WHEAT_COOLDOWN = 10000;
				HIGH_WHEAT_COOLDOWN = 20000;
				break;
			case 4:
				WHEAT_ALIVE_TIME = 10000;
				WHEAT_LIVES_GAINED = 4;
				LOW_WHEAT_COOLDOWN = 7500;
				HIGH_WHEAT_COOLDOWN = 15000;
				break;
			case 5:
				WHEAT_ALIVE_TIME = 10000;
				WHEAT_LIVES_GAINED = 5;
				LOW_WHEAT_COOLDOWN = 7500;
				HIGH_WHEAT_COOLDOWN = 15000;
				break;
		}
		wheatCooldown = rand.nextInt(HIGH_WHEAT_COOLDOWN - LOW_WHEAT_COOLDOWN) + LOW_WHEAT_COOLDOWN;
	}

	private void writeFile() {
//		try {
//			FileWriter f = new FileWriter("C:\\Users\\andre\\eclipse-workspace\\EmuAttack\\src\\gamedata.txt");
//			String out = "";
//			out += "HighScore:" + highScore;
//			out += "\nCoins:" + coins;
//			out += "\nHealthLevel:" + healthLevel;
//			out += "\nEggLevel:" + eggLevel;
//			out += "\nDashLevel:" + dashLevel;
//			out += "\nCoinsLevel:" + coinsLevel;
//			out += "\nPowerupLevel:" + powerupLevel;
//			out += "\nFPS:" + showFPS;
//			out += "\nWASD:" + WASD;
//			out += "\nMultiplayer:" + IS_MULTIPLAYER;
//			f.write(out);
//			f.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		prefs.putInt("HighScore", highScore);
		prefs.putInt("Coins", coins);
		prefs.putInt("HealthLevel", healthLevel);
		prefs.putInt("EggLevel", eggLevel);
		prefs.putInt("DashLevel", dashLevel);
		prefs.putInt("CoinsLevel", coinsLevel);
		prefs.putInt("PowerupLevel", powerupLevel);
		prefs.putBoolean("FPS", showFPS);
		prefs.putBoolean("WASD", WASD);
		prefs.putBoolean("Multiplayer", IS_MULTIPLAYER);
	}
	
	
	public void paintComponent (Graphics window) {
		/*
		 * Author: @Mr. Remington
		 */
		
		//set up the double buffering to make the game animation nice and smooth
		Graphics2D twoDGraph = (Graphics2D)window;
		
		if(back==null) back = (BufferedImage)(createImage(getWidth(),getHeight()));
		
		Graphics graphToBack = back.createGraphics();		
		/*******End of Remington Code*******/
				
		findFPS();
				
		while (tick < System.currentTimeMillis()) {
			if (tick == 0) {
				tick = System.currentTimeMillis();
			}
			switch (currentScene) {
				case TitleScene:
					drawTitle(graphToBack);
					break;
				case UpgradeScene:
					drawUpgrade(graphToBack);
					break;
				case HowToPlayScene:
					drawHowToPlay(graphToBack);
					break;
				case OptionScene:
					drawOptions(graphToBack);
					break;
				case GameScene:
					drawGame(graphToBack);
					break;
				case EndScene:
					drawEnd(graphToBack);
					break;
				default:
					break;
			}
			tick += 15;
			
			if (showFPS) {
				graphToBack.setFont(smallFont);
				graphToBack.setColor(Color.BLACK);
				graphToBack.drawString("FPS: " + fps + "/" + fpsLow, 15, 25);
			}
		}
								
		// Author: Mr. Remington
		twoDGraph.drawImage(back, null, 0, 0);
		repaint();
	}
	
	private void findFPS() {
		if (System.currentTimeMillis() > fpsStart + 1000) {
			fps = frameCount;
			try {
				fpsLow = 1000/highestGap;
			} catch (Exception e) {
				
			}
			fpsStart = System.currentTimeMillis();
			frameCount = 0;
			highestGap = 0;
		} else {
			frameCount++;
			highestGap = Math.max(highestGap, (int)(System.currentTimeMillis() - lastFrame));
			lastFrame = System.currentTimeMillis();
		}
	}
	
	private void drawTitle (Graphics graphToBack) {
		graphToBack.setColor(Color.WHITE);
		graphToBack.fillRect(0, 0, WIDTH, HEIGHT);

		
		if (titleTexts.getSize() < 1) {
			titleTexts.add("EMU ATTACK!", 0, HEIGHT/5, bigFont, Color.BLACK, graphToBack, WIDTH);
			titleTexts.add("PLAY", 0, HEIGHT/3, mediumFont, Color.BLACK, graphToBack, WIDTH);
			titleTexts.add("UPGRADES", 0, HEIGHT/9 * 4, mediumFont, Color.BLACK, graphToBack, WIDTH);
			titleTexts.add("HOW TO PLAY", 0, HEIGHT/9 * 5, mediumFont, Color.BLACK, graphToBack, WIDTH);
			titleTexts.add("OPTIONS", 0, HEIGHT/3 * 2, mediumFont, Color.BLACK, graphToBack, WIDTH);
			titleTexts.add("QUIT", 0, HEIGHT/9 * 7, mediumFont, Color.BLACK, graphToBack, WIDTH);
			titleTexts.add("Use UP/DOWN arrows to navigate, ENTER to select", 
					0, 875, smallFont, Color.BLACK, graphToBack, WIDTH);
		}
		
		graphToBack.setColor(new Color(165, 168, 173));
		switch (currentTitleSelection) {
			case Play:
				Text play = titleTexts.getText("PLAY");
				graphToBack.fillRoundRect(play.getX() - 5, play.getY() - play.getHeight()/2 - 5,
						play.getWidth() + 10, play.getHeight()/2 + 12, 10, 10);
				break;
			case Upgrades:
				Text upgrades = titleTexts.getText("UPGRADES");
				graphToBack.fillRoundRect(upgrades.getX() - 5, upgrades.getY() - upgrades.getHeight()/2 - 5,
						upgrades.getWidth() + 10, upgrades.getHeight()/2 + 12, 10, 10);
				break;
			case HowToPlay:
				Text howtoplay = titleTexts.getText("HOW TO PLAY");
				graphToBack.fillRoundRect(howtoplay.getX() - 5, howtoplay.getY() - howtoplay.getHeight()/2 - 5,
						howtoplay.getWidth() + 10, howtoplay.getHeight()/2 + 12, 10, 10);
				break;
			case Options:
				Text options = titleTexts.getText("OPTIONS");
				graphToBack.fillRoundRect(options.getX() - 5, options.getY() - options.getHeight()/2 - 5,
						options.getWidth() + 10, options.getHeight()/2 + 12, 10, 10);
				break;
			case Quit:
				Text quit = titleTexts.getText("QUIT");
				graphToBack.fillRoundRect(quit.getX() - 5, quit.getY() - quit.getHeight()/2 - 5,
						quit.getWidth() + 10, quit.getHeight()/2 + 14, 10, 10);
				break;
		}
		
		titleTexts.drawAll(graphToBack);
		
		drawCoins(graphToBack);
		
		graphToBack.drawImage(emuImage, WIDTH/4 - (emuImage.getWidth(null)/2) - 150, 
				HEIGHT/7 * 4, emuImage.getWidth(null), emuImage.getHeight(null), null);
		
		graphToBack.drawImage(humanImage, 3 * WIDTH/4 - (humanImage.getWidth(null)/2) + 50, 
				HEIGHT/7 * 4, humanImage.getWidth(null), humanImage.getHeight(null), null);
		
		graphToBack.setFont(highScoreFont);
		graphToBack.setColor(Color.BLACK);
		graphToBack.drawString("High Score: " + highScore, 10, HEIGHT-10);

		
		if (keys.get("MENU UP")) {
			currentTitleSelection = currentTitleSelection.previous();
			keys.replace("MENU UP", false);
		}
		else if (keys.get("MENU DOWN")) {
			currentTitleSelection = currentTitleSelection.next();
			keys.replace("MENU DOWN", false);
		}
		else if (keys.get("ENTER")) {
			switch (currentTitleSelection) {
				case Play:
					startGame();
					break;
				case Upgrades:
					currentScene = Scene.UpgradeScene;
					break;
				case HowToPlay:
					currentScene = Scene.HowToPlayScene;
					break;
				case Options:
					currentScene = Scene.OptionScene;
					break;
				case Quit:
					System.exit(0);
					break;
				default:
					break;
			}
			keys.replace("ENTER", false);
		}
		if (keys.get("COINS") && hacking) {
			coins+=100;
		}
	}
	
	private void drawCoins(Graphics graphToBack) {
		graphToBack.setFont(mediumFont);
		int w = graphToBack.getFontMetrics().stringWidth("" + coins);
		graphToBack.drawImage(coinImage, WIDTH - w - 30 - coinImage.getWidth(null), 0, null);
		graphToBack.drawString(coins + "", WIDTH - w - 15, 43);
	}
	
	private void drawUpgrade(Graphics graphToBack) {
		graphToBack.setColor(Color.WHITE);
		graphToBack.fillRect(0, 0, WIDTH, HEIGHT);
		
		if (upgradeTexts.getSize() < 1) {
			upgradeTexts.add("UPGRADES", 0, HEIGHT/9, bigFont, Color.BLACK, graphToBack, WIDTH);
			upgradeTexts.add("HEALTH", 0, HEIGHT/9 * 2, mediumFont, Color.BLACK, graphToBack, WIDTH);
			upgradeTexts.add("EGGS", 0, HEIGHT/3, mediumFont, Color.BLACK, graphToBack, WIDTH);
			upgradeTexts.add("DASH", 0, HEIGHT/9 * 4, mediumFont, Color.BLACK, graphToBack, WIDTH);
			upgradeTexts.add("COINS", 0, HEIGHT/9 * 5, mediumFont, Color.BLACK, graphToBack, WIDTH);
			upgradeTexts.add("WHEAT", 0, HEIGHT/3 * 2, mediumFont, Color.BLACK, graphToBack, WIDTH);
			upgradeTexts.add("Use UP/DOWN arrows to navigate, ENTER to buy, ESCAPE to go back", 
					0, 875, smallFont, Color.BLACK, graphToBack, WIDTH);
		}
		
		String description = "";
		String cost = "25";
		
		graphToBack.setColor(new Color(165, 168, 173));
		Text t;
		switch (currentUpgradeSelection) {
			case Health:
				t = upgradeTexts.getText("HEALTH");
				cost = healthCosts[healthLevel] + "";
				description = "Increases your total health by 10 hearts. You currently \nhave " + lives.getMaxLives() + " total lives.";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 12, 10, 10);
				break;
			case Egg:
				t = upgradeTexts.getText("EGGS");
				cost = eggCosts[eggLevel] + "";
				description = "Decreases the cooldown between shooting eggs by 0.1 \nseconds. Current cooldown is " + 
						(double)EGG_COOLDOWN/1000 + " seconds.";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 12, 10, 10);
				break;
			case Dash:
				t = upgradeTexts.getText("DASH");
				cost = dashCosts[dashLevel] + "";
				description = "Makes you dash faster, be able to dash longer, and \nregain dash time faster. Current dash speed is " + 
						DASH_SPEED + ".";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 12, 10, 10);
				break;
			case CoinsPerKill:
				t = upgradeTexts.getText("COINS");
				cost = coinsCosts[coinsLevel];
				description = "Get more 5 more coins per enemy kill. You currently get \n" + COINS_PER_ENEMY + " coins per enemy kill.";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 14, 10, 10);
				break;
			case Powerup:
				t = upgradeTexts.getText("WHEAT");
				cost = powerupCosts[powerupLevel];
				description = "Gain more health when you pick up wheat, and make wheat \nspawn more often. You currently gain " + 
						WHEAT_LIVES_GAINED + " lives per wheat.";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 14, 10, 10);
				break;

		}
		
		upgradeTexts.drawAll(graphToBack);
		upgradeTexts.drawAllFading(graphToBack);
		
		/*
		 * Changing stroke size from https://stackoverflow.com/questions/4219511/draw-rectangle-border-thickness
		 */
		graphToBack.setColor(Color.DARK_GRAY);
		Graphics2D g2 = (Graphics2D) graphToBack;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(400, 650, 800, 175, 25, 25);
		g2.setStroke(new BasicStroke(3));
		g2.drawRoundRect(1040, 765, 150, 50, 15, 15);
		g2.setStroke(oldStroke);
		
		graphToBack.drawImage(coinImage2, 1048, 773, null);
		graphToBack.setFont(costFont);
		graphToBack.drawString(cost, 1090, 800);
		graphToBack.setFont(smallFont);
		drawStringNewline(graphToBack, description, 410, 645);
		
		drawCoins(graphToBack);

		
		if (keys.get("MENU UP")) {
			currentUpgradeSelection = currentUpgradeSelection.previous();
			keys.replace("MENU UP", false);
		}
		else if (keys.get("MENU DOWN")) {
			currentUpgradeSelection = currentUpgradeSelection.next();
			keys.replace("MENU DOWN", false);
		}
		else if (keys.get("ENTER")) {
			buyUpgrade(graphToBack);
			keys.replace("ENTER", false);
		}
		if (keys.get("ESCAPE")) {
			currentScene = Scene.TitleScene;
			currentUpgradeSelection = UpgradeSelection.Health;
			upgradeTexts.clearFading();
			keys.replace("ESCAPE", false);
		}
	}
	
	/*
	 * drawStringNewLine method from https://stackoverflow.com/questions/4413132/problems-with-newline-in-graphics2d-drawstring
	 */
	private void drawStringNewline(Graphics g, String text, int x, int y) {
	    for (String line : text.split("\n"))
	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}
	
	private void drawStringNewlineCentered(Graphics g, String text, int y) {
	    for (String line : text.split("\n")) {
	    	int w = g.getFontMetrics().stringWidth(line);
	        g.drawString(line, WIDTH/2 - w/2, y += g.getFontMetrics().getHeight());
	    }
	}
	
	private void buyUpgrade(Graphics graphToBack) {
		switch (currentUpgradeSelection) {
		case Health:
			if (healthLevel == 5) {
				upgradeTexts.addFading("You have already maxed this upgrade!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			else if (coins >= Integer.parseInt(healthCosts[healthLevel])) {
				upgradeTexts.addFading("Upgrade purchased!!", 0, 713, purchaseFont, new Color(232, 189, 49), graphToBack, WIDTH);
				coins -= Integer.parseInt(healthCosts[healthLevel]);
				healthLevel++;
				writeFile();
				setLivesInfo(healthLevel);
			}
			else {
				upgradeTexts.addFading("You do not have enough coins!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			break;
		case Egg:
			if (eggLevel == 5) {
				upgradeTexts.addFading("You have already maxed this upgrade!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			else if (coins >= Integer.parseInt(eggCosts[eggLevel])) {
				upgradeTexts.addFading("Upgrade purchased!!", 0, 713, purchaseFont, new Color(232, 189, 49), graphToBack, WIDTH);
				coins -= Integer.parseInt(eggCosts[eggLevel]);
				eggLevel++;
				writeFile();
				setEggCooldown(eggLevel);
			}
			else {
				upgradeTexts.addFading("You do not have enough coins!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			break;
		case Dash:
			if (dashLevel == 5) {
				upgradeTexts.addFading("You have already maxed this upgrade!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			else if (coins >= Integer.parseInt(dashCosts[dashLevel])) {
				upgradeTexts.addFading("Upgrade purchased!!", 0, 713, purchaseFont, new Color(232, 189, 49), graphToBack, WIDTH);
				coins -= Integer.parseInt(dashCosts[dashLevel]);
				dashLevel++;
				writeFile();
				setDashInfo(dashLevel);
			}
			else {
				upgradeTexts.addFading("You do not have enough coins!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			break;
		case CoinsPerKill:
			if (coinsLevel == 5) {
				upgradeTexts.addFading("You have already maxed this upgrade!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			else if (coins >= Integer.parseInt(coinsCosts[coinsLevel])) {
				upgradeTexts.addFading("Upgrade purchased!!", 0, 713, purchaseFont, new Color(232, 189, 49), graphToBack, WIDTH);
				coins -= Integer.parseInt(coinsCosts[coinsLevel]);
				coinsLevel++;
				writeFile();
				setCoinsPerEnemy(coinsLevel);
			}
			else {
				upgradeTexts.addFading("You do not have enough coins!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			break;
		case Powerup:
			if (powerupLevel == 5) {
				upgradeTexts.addFading("You have already maxed this upgrade!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			else if (coins >= Integer.parseInt(powerupCosts[powerupLevel])) {
				upgradeTexts.addFading("Upgrade purchased!!", 0, 713, purchaseFont, new Color(232, 189, 49), graphToBack, WIDTH);
				coins -= Integer.parseInt(powerupCosts[powerupLevel]);
				powerupLevel++;
				writeFile();
				setWheatInfo(powerupLevel);
			}
			else {
				upgradeTexts.addFading("You do not have enough coins!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
			}
			break;
		}
	}
	
	private void drawHowToPlay(Graphics graphToBack) {
		graphToBack.setColor(Color.WHITE);
		graphToBack.fillRect(0, 0, WIDTH, HEIGHT);

		graphToBack.setFont(bigFont);
		graphToBack.setColor(Color.BLACK);
		int w = graphToBack.getFontMetrics().stringWidth("HOW TO PLAY");
		graphToBack.drawString("HOW TO PLAY", WIDTH/2 - w/2, 75);
		
		graphToBack.setFont(howToPlayFont);
		String text;
		if (WASD) {
			text = "Use WASD to move the emu around the screen."
					+ "\nPress J to shoot eggs and kill the enemies."
					+ "\nPress K to dash and move faster."
					+ "\nThe amount of dash you have is indicated by the dash bar."
					+ "\nPress L to use your mega egg"
					+ "\nIt needs to be charged, and can only be used once the mega egg bar is full."
					+ "\nPress ESCAPE when in game to pause.\n"
					+ "\nThere are enemies who will shoot bullets and randomly move around the screen."
					+ "\nIf you get hit by a bullet, you will take damage."
					+ "\nYour health is indicated by the hearts you have.\n"
					+ "\nYou get 1 point per second you are alive, "
					+ "\nand 10 points for every enemy you kill."
					+ "\nYou get coins (depending on your coin upgrade) for every enemy you kill."
					+ "\nWheat spawns randomly throughout the game, and colelcting the wheat will heal you."
					+ "\nYou can buy upgrades in the UPGRADES tab on the title screen"
					+ "\n\nMade by Andrew Ma";
		}
		else {
			text = "Use ARROW KEYS to move the emu around the screen."
					+ "\nPress Z to shoot eggs and kill the enemies."
					+ "\nPress X to dash and move faster."
					+ "\nThe amount of dash you have is indicated by the dash bar."
					+ "\nPress C to use your mega egg"
					+ "\nIt needs to be charged, and can only be used once the mega egg bar is full."
					+ "\nPress ESCAPE when in game to pause.\n"
					+ "\nThere are enemies who will shoot bullets and randomly move around the screen."
					+ "\nIf you get hit by a bullet, you will take damage."
					+ "\nYour health is indicated by the hearts you have.\n"
					+ "\nYou get 1 point per second you are alive, "
					+ "\nand 10 points for every enemy you kill."
					+ "\nYou get coins (depending on your coin upgrade) for every enemy you kill."
					+ "\nWheat spawns randomly throughout the game, and colelcting the wheat will heal you."
					+ "\nYou can buy upgrades in the UPGRADES tab on the title screen"
					+ "\n\nMade by Andrew Ma";
		}
		drawStringNewlineCentered(graphToBack, text, 100);
		
		graphToBack.setFont(smallFont);
		w = graphToBack.getFontMetrics().stringWidth("Press ESCAPE to go back");
		graphToBack.drawString("Press ESCAPE to go back", WIDTH/2 - w/2, 875);
		
		if (keys.get("ESCAPE")) {
			currentScene = Scene.TitleScene;
			keys.replace("ESCAPE", false);
		}
	}
	
	private void drawOptions(Graphics graphToBack) {
		graphToBack.setColor(Color.WHITE);
		graphToBack.fillRect(0, 0, WIDTH, HEIGHT);

		if (optionTexts.getSize() < 1) {
			optionTexts.add("OPTIONS", 0, HEIGHT/9, bigFont, Color.BLACK, graphToBack, WIDTH);
			optionTexts.add("Show FPS", 0, HEIGHT/9 * 2, mediumFont, Color.BLACK, graphToBack, WIDTH);
			optionTexts.add("Controls", 0, HEIGHT/3, mediumFont, Color.BLACK, graphToBack, WIDTH);
			optionTexts.add("Multiplayer", 0, HEIGHT/9 * 4, mediumFont, Color.BLACK, graphToBack, WIDTH);
			optionTexts.add("Reset", 0, HEIGHT/9 * 5, mediumFont, Color.BLACK, graphToBack, WIDTH);
			optionTexts.add("Use UP/DOWN arrows to navigate, ENTER to select, ESCAPE to go back", 
					0, 875, smallFont, Color.BLACK, graphToBack, WIDTH);
		}
		
		String description = "";
		
		graphToBack.setColor(new Color(165, 168, 173));
		Text t;
		switch (currentOptionSelection) {
			case ShowFPS:
				t = optionTexts.getText("Show FPS");
				description = "Toggles whether or not there is an FPS counter in the\ntop right. The counter reads AVERAGE FPS/WORST FPS.";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 12, 10, 10);
				if (showFPS) {
					graphToBack.setColor(Color.DARK_GRAY);
					graphToBack.setFont(costFont);
					graphToBack.drawString("ON", 1090, 800);
				}
				else {
					graphToBack.setColor(Color.GRAY);
					graphToBack.setFont(costFont);
					graphToBack.drawString("OFF", 1090, 800);
				}
				break;
			case Controls:
				t = optionTexts.getText("Controls");
				description = "Switches the controls between WASD + JKL and ARROW KEYS\n+ ZXC.";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 12, 10, 10);
				if (WASD) {
					graphToBack.setFont(costFont);
					graphToBack.setColor(Color.DARK_GRAY);
					graphToBack.drawString("WASD", 1070, 800);
				}
				else {
					graphToBack.setFont(costFont);
					graphToBack.setColor(Color.GRAY);
					graphToBack.drawString("ARROWS", 1060, 800);
				}
				break;
			case Multiplayer:
				t = optionTexts.getText("Multiplayer");
				description = "Toggles Multiplayer. In multiplayer, the emu controls \nare the same. Another "
						+ "person will then use the mouse to\nclick and move the humans. Some upgrades will be "
						+ "enabled\nto balance the game, even if you have not\npurchased them.";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 12, 10, 10);
				if (IS_MULTIPLAYER) {
					graphToBack.setFont(costFont);
					graphToBack.setColor(Color.DARK_GRAY);
					graphToBack.drawString("ON", 1090, 800);
				}
				else {
					graphToBack.setFont(costFont);
					graphToBack.setColor(Color.GRAY);
					graphToBack.drawString("OFF", 1090, 800);
				}
				break;
			case Reset:
				t = optionTexts.getText("Reset");
				description = "Resets EVERYTHING. Your stats, coins, and upgrades will\nall be reset."
						+ " This is not reversible.";
				graphToBack.fillRoundRect(t.getX() - 5, t.getY() - t.getHeight()/2 - 5,
						t.getWidth() + 10, t.getHeight()/2 + 12, 10, 10);
				graphToBack.setFont(costFont);
				graphToBack.setColor(Color.RED);
				graphToBack.drawString("!", 1105, 800);
				break;
		}
		
		optionTexts.drawAll(graphToBack);
		optionTexts.drawAllFading(graphToBack);
		
		/*
		 * Changing stroke size from https://stackoverflow.com/questions/4219511/draw-rectangle-border-thickness
		 */
		graphToBack.setColor(Color.DARK_GRAY);
		Graphics2D g2 = (Graphics2D) graphToBack;
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(400, 650, 800, 175, 25, 25);
		g2.setStroke(new BasicStroke(3));
		g2.drawRoundRect(1040, 765, 150, 50, 15, 15);
		g2.setStroke(oldStroke);
		
		graphToBack.setFont(smallFont);
		drawStringNewline(graphToBack, description, 410, 645);
		
		drawCoins(graphToBack);

		
		if (keys.get("MENU UP")) {
			currentOptionSelection = currentOptionSelection.previous();
			keys.replace("MENU UP", false);
		}
		else if (keys.get("MENU DOWN")) {
			currentOptionSelection = currentOptionSelection.next();
			keys.replace("MENU DOWN", false);
		}
		else if (keys.get("ENTER")) {
			keys.replace("ENTER", false);
			selectOption(graphToBack);
		}
		if (keys.get("ESCAPE")) {
			currentScene = Scene.TitleScene;
			currentOptionSelection = OptionSelection.ShowFPS;
			optionTexts.clearFading();
			keys.replace("ESCAPE", false);
		}
	}
	
	private void selectOption(Graphics graphToBack) {
		switch (currentOptionSelection) {
			case ShowFPS:
				if (showFPS) {
					showFPS = false;
				}
				else {
					showFPS = true;
				}
				break;
			case Controls:
				if (WASD) {
					WASD = false;
				}
				else {
					WASD = true;
				}
				break;
			case Multiplayer:
				if (IS_MULTIPLAYER) {
					IS_MULTIPLAYER = false;
				}
				else {
					IS_MULTIPLAYER = true;
				}
				break;
			case Reset:
				coins = 0;
				highScore = 0;
				healthLevel = 0;
				eggLevel = 0;
				dashLevel = 0;
				coinsLevel = 0;
				powerupLevel = 0;
				setLivesInfo(healthLevel);
				setDashInfo(dashLevel);
				setEggCooldown(eggLevel);
				setCoinsPerEnemy(coinsLevel);
				setWheatInfo(powerupLevel);
				
				optionTexts.addFading("Successfully reset!", 0, 713, purchaseFont, Color.RED, graphToBack, WIDTH);
				
				break;
		}
		writeFile();
	}
	
	private void drawGame (Graphics graphToBack) {
		
		graphToBack.setColor(new Color(160, 235, 122));
		graphToBack.fillRect(0, 0, WIDTH, HEIGHT);
		
		graphToBack.drawImage(river, (int)(WIDTH * 0.55), 0, river.getWidth(null), 
				river.getHeight(null), null);
		
		if (!paused) {			
			checkInputsGame();
			
			updateBars(graphToBack);
								
			updateProjectilesGame(graphToBack);
			
			updateHumansGame(graphToBack);
			
			updateUIGame(graphToBack);
		
			updateWheatGame(graphToBack);
			
			updateEmuGame(graphToBack);
		}
		else {
			if (keys.get("ESCAPE")) {
				paused = false;
				keys.replace("ESCAPE", false);
			}
			graphToBack.setColor(new Color(130, 130, 130));
			graphToBack.fillRect(0, 0, WIDTH, HEIGHT);
			
			graphToBack.setColor(Color.BLACK);
			graphToBack.setFont(mediumFont);
			drawStringNewlineCentered(graphToBack, "Game paused, press ESC to resume", HEIGHT/2 - 50);
		}
	}
	
	private void checkInputsGame() {
		if (keys.get("DASH")) {
			emu.setXSpeed(DASH_SPEED);
			emu.setYSpeed(DASH_SPEED);
			emu.updateDiagonalSpeed();
			emu.setDashing(true);
			dashBar.decrease(DASH_USAGE);
			if (dashBar.isEmpty()) keys.replace("DASH", false);
		}
		else {
			emu.setXSpeed(START_SPEED);
			emu.setYSpeed(START_SPEED);
			emu.updateDiagonalSpeed();
			emu.setDashing(false);
			dashBar.increase(DASH_REGEN);
		}
		
		if (keys.get("UP") && keys.get("LEFT") && emuUp() && emuLeft()) {
			emu.move("UPLEFT");
		}
		else if (keys.get("UP") && keys.get("RIGHT") && emuUp() && emuRight()) {
			emu.move("UPRIGHT");
		}
		else if (keys.get("DOWN") && keys.get("LEFT") && emuDown() && emuLeft()) {
			emu.move("DOWNLEFT");
		}
		else if (keys.get("DOWN") && keys.get("RIGHT") && emuDown() && emuRight()) {
			emu.move("DOWNRIGHT");
		}
		else {
			if (keys.get("UP") && emuUp()) {
				emu.move("UP");
			}
			else if (keys.get("DOWN") && emuDown()) {
				emu.move("DOWN");
			}
			if (keys.get("LEFT") && emuLeft()) {
				emu.move("LEFT");
			}
			else if (keys.get("RIGHT") && emuRight()) {
				emu.move("RIGHT");
			}
		}
		if (keys.get("SHOOT")) {
			if (System.currentTimeMillis() > lastEgg + EGG_COOLDOWN ) {
				Egg e = new Egg(emu.getX() + emu.getWidth()/2, emu.getY() + emu.getWidth()/3);
				projectiles.add(e);
				lastEgg = System.currentTimeMillis();
			}
		}
		
		// MEGA EPIC OVERPOWERED EGG (CHARGED ATTACK)
		if (keys.get("SUPER") && megaBar.isFull()) {
			Egg e = new Egg(emu.getX() + emu.getWidth()/2, emu.getY() - emu.getHeight()/2, true);
			projectiles.add(e);
			keys.replace("SUPER", false);
			megaBar.setFilled(0);
		}
		
		// Pause
		if (keys.get("ESCAPE")) {
			paused = true;
			keys.replace("ESCAPE", false);
		}
	}
	
	private void updateBars(Graphics graphToBack) {
		dashBar.draw(graphToBack);
		megaBar.draw(graphToBack);
		megaBar.increase(0.02);
	}
	
	private void updateEmuGame(Graphics graphToBack) {
		emu.draw(graphToBack);
		boolean hit = emu.checkCollisionsProjectiles(projectiles.getArray());
		if (hit) {
			lives.loseLife();
			if (lives.getLives() == 0) {
				if (!IS_MULTIPLAYER) {
					coins += enemiesKilled * COINS_PER_ENEMY;
					if (score.getScore() > highScore) {
						newHighScore = true;
						highScore = score.getScore();
						writeFile();
					}
				}
				else {
					readFile();
				}
				currentScene = Scene.EndScene;
			}
		}
		boolean wheat = emu.checkCollisionsWheat(wheats.getArray());
		if (wheat) {
			lives.gainLives(WHEAT_LIVES_GAINED);
			dashBar.increase(10);
			megaBar.increase(25);
		}
	}

	private void updateProjectilesGame(Graphics graphToBack) {
		projectiles.moveAll(graphToBack);
		projectiles.cleanAll(WIDTH);
	}
	
	private void updateHumansGame(Graphics graphToBack) {
		humans.moveAll(graphToBack, projectiles, emu.getY());
		boolean hit = humans.checkCollides(projectiles.getArray());
		
		if (hit) {
			score.incScore(10);
			enemiesKilled++;
		}
				
		while ((humans.getSize() - 1 < (System.currentTimeMillis() - startTime)/10000 
				&& humans.getSize() <= 10 && System.currentTimeMillis() > lastSpawn + 2000 - 
				((System.currentTimeMillis() - startTime)/10000 * 250)) 
				|| (IS_MULTIPLAYER && System.currentTimeMillis() > lastSpawn + 2000 - 
				((System.currentTimeMillis() - startTime)/10000 * 250) && humans.getSize() <= 10)) {
			Human human = new Human(WIDTH+100, 0, 3, 3);
			human.setY(human.getDestination()[1] - human.getHeight());
			humans.add(human);
			lastSpawn = System.currentTimeMillis();
		}
	}
	
	private void updateUIGame(Graphics graphToBack) {
		lives.drawLives(graphToBack);
		score.passiveGainScore();
		score.draw(graphToBack);
	}
	
	private void updateWheatGame(Graphics graphToBack) {
		if (System.currentTimeMillis() > lastWheat + wheatCooldown) {
			Wheat w = new Wheat(0, 0, WHEAT_ALIVE_TIME);
			w.setX(rand.nextInt(900-w.getHeight()));
			w.setY(rand.nextInt(960-w.getWidth()));
			wheats.add(w);

			wheatCooldown = rand.nextInt(HIGH_WHEAT_COOLDOWN - LOW_WHEAT_COOLDOWN) + LOW_WHEAT_COOLDOWN;
			lastWheat = System.currentTimeMillis();
		}
		wheats.drawAll(graphToBack);
	}
	
	private boolean emuUp() {
		return emu.getY() - emu.getYSpeed() > 0;
	}
	
	private boolean emuDown() {
		return emu.getY() + emu.getYSpeed()  + emu.getHeight() < HEIGHT;
	}
	
	private boolean emuLeft() {
		return emu.getX() - emu.getXSpeed() > 0;
	}
	
	private boolean emuRight() {
		return emu.getX() + emu.getXSpeed() < WIDTH/5 * 3;
	}
	
	private void drawEnd (Graphics graphToBack) {
		graphToBack.setColor(Color.WHITE);
		graphToBack.fillRect(0, 0, WIDTH, HEIGHT);

		graphToBack.setFont(megaFont);
		graphToBack.setColor(Color.BLACK);
		int w = graphToBack.getFontMetrics().stringWidth("YOU LOSE!");
		graphToBack.drawString("YOU LOSE!", WIDTH/2 - w/2, HEIGHT/4);
		
		if (!newHighScore) {
			graphToBack.setFont(mediumFont);
			w = graphToBack.getFontMetrics().stringWidth("Score: " + score.getScore());
			graphToBack.drawString("Score: " + score.getScore(), WIDTH/2 - w/2, HEIGHT/2);
		}
		else {
			graphToBack.setFont(bigFont);
			w = graphToBack.getFontMetrics().stringWidth("NEW HIGH SCORE!");
			graphToBack.drawString("NEW HIGH SCORE!", WIDTH/2 - w/2, HEIGHT/5*2 + 25);

			graphToBack.setFont(mediumFont);
			w = graphToBack.getFontMetrics().stringWidth("Score: " + score.getScore());
			graphToBack.drawString("Score: " + score.getScore(), WIDTH/2 - w/2, HEIGHT/5*3);
		}
		
		w = graphToBack.getFontMetrics().stringWidth("Coins Earned: " + (enemiesKilled*COINS_PER_ENEMY));
		graphToBack.drawString("Coins Earned: " + (enemiesKilled*COINS_PER_ENEMY), WIDTH/2 - w/2, HEIGHT/4 * 3);
		
		graphToBack.setFont(smallFont);
		w = graphToBack.getFontMetrics().stringWidth("Press ESCAPE to return to title screen");
		graphToBack.drawString("Press ESCAPE to return to title screen", WIDTH/2 - w/2, HEIGHT/8 * 7);

		
		if (keys.get("ESCAPE")) {
			currentScene = Scene.TitleScene;
			newHighScore = false;
			keys.replace("ESCAPE", false);
		}
	}
	
	private void startGame () {
		currentScene = Scene.GameScene;
		startTime = System.currentTimeMillis();
		lastWheat = System.currentTimeMillis();
		score.setScore(0);
		setLivesInfo(healthLevel);
		dashBar.reset();
		megaBar.setFilled(0);
		enemiesKilled = 0;
		humans.reset();
		projectiles.reset();
		wheats.reset();
		emu.setPos(WIDTH/8, HEIGHT/2);
		
		if (IS_MULTIPLAYER) {
			setLivesInfo(5);
			setDashInfo(5);
			setEggCooldown(0);
			setCoinsPerEnemy(5);
			setWheatInfo(5);
		}
		else {
			setLivesInfo(healthLevel);
			setDashInfo(dashLevel);
			setEggCooldown(eggLevel);
			setCoinsPerEnemy(coinsLevel);
			setWheatInfo(powerupLevel);
		}
	}

	/*
	 * Author: Mr. Remington
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (WASD) {
			if (e.getKeyCode() == KeyEvent.VK_W)
			{
				keys.replace("UP", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_S)
			{
				keys.replace("DOWN", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_A)
			{
				keys.replace("LEFT", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_D)
			{
				keys.replace("RIGHT", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_J)
			{
				keys.replace("SHOOT", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_K) 
			{
				keys.replace("DASH", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_L) 
			{
				keys.replace("SUPER", true);
			}
		}
		else {
			if (e.getKeyCode() == KeyEvent.VK_UP) 
			{
				keys.replace("UP", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) 
			{
				keys.replace("DOWN", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) 
			{
				keys.replace("LEFT", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
			{
				keys.replace("RIGHT", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_Z) 
			{
				keys.replace("SHOOT", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_X) 
			{
				keys.replace("DASH", true);
			}
			if (e.getKeyCode() == KeyEvent.VK_C) 
			{
				keys.replace("SUPER", true);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) 
		{
			keys.replace("ENTER", true);
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			keys.replace("ESCAPE", true);;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) 
		{
			keys.replace("MENU UP", true);
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) 
		{
			keys.replace("MENU DOWN", true);
		}
		if (e.getKeyCode() == KeyEvent.VK_P)
		{
			keys.replace("COINS", true);
		}
	}

	/*
	 * Author: Mr. Remington
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (WASD) {
			if (e.getKeyCode() == KeyEvent.VK_W)
			{
				keys.replace("UP", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_S)
			{
				keys.replace("DOWN", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_A)
			{
				keys.replace("LEFT", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_D)
			{
				keys.replace("RIGHT", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_J)
			{
				keys.replace("SHOOT", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_K) 
			{
				keys.replace("DASH", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_L) 
			{
				keys.replace("SUPER", false);
			}
		}
		else {
			if (e.getKeyCode() == KeyEvent.VK_UP) 
			{
				keys.replace("UP", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) 
			{
				keys.replace("DOWN", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) 
			{
				keys.replace("LEFT", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
			{
				keys.replace("RIGHT", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_Z) 
			{
				keys.replace("SHOOT", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_X) 
			{
				keys.replace("DASH", false);
			}
			if (e.getKeyCode() == KeyEvent.VK_C) 
			{
				keys.replace("SUPER", false);
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) 
		{
			keys.replace("ENTER", false);
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			keys.replace("ESCAPE", false);;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP) 
		{
			keys.replace("MENU UP", false);
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) 
		{
			keys.replace("MENU DOWN", false);
		}
		if (e.getKeyCode() == KeyEvent.VK_P)
		{
			keys.replace("COINS", false);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// empty
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if (IS_MULTIPLAYER) {
			if (selectedHuman != null && humans.clickSameHuman(selectedHuman, e.getX(), e.getY())) {
				selectedHuman.setSelected(false);
				selectedHuman = null;
			}
			else if (selectedHuman != null) {
				selectedHuman.setDestination(e.getX(), e.getY());
				selectedHuman.setStandby(false);
				selectedHuman.setSelected(false);
				selectedHuman = null;
			}
			else {
				selectedHuman = humans.getHumanClick(e.getX(), e.getY());
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
