// import java.awt.*;
// import java.util.*;
// import javax.sound.sampled.Clip;
// import javax.swing.*;
// import java.awt.event.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.sound.sampled.Clip;
import javax.swing.SwingConstants;

public class EnemyInTheSky extends JFrame implements Runnable {
	private JPanel panel;
	private Set<Integer> keyInput;
	private KeyListener myKeyListener;
	private JFrame frame;
	private Sounds mySound;
	private Clip gameClip; // to stop background game music when game ends or user exits to main menu.
	private Image healthImage, AirCombatImage, collideImage, gameEndImage, successImage, hitImage, exitImage, enemyImage,
			enemyBulletImage, bulletImage;
	private boolean spacePressed = false, isThreadRunning = true;
	private int enemyPeriod, bulletPeriod, healthLimit, healthBarDecreaseSizeX, randomX, mainJetSpeed,
			enemySpeed, bulletSpeed, enemyBulletSpeed, enemyBulletSize, gameDuration, hitPoint, planeSize, screenWidth,
			screenHeight, backgroundPositionY, jetFighterBulletSize, Menu_ObjHeight, upLimit, score, downLimit,
			blastDustPeriod, counter, jet_X, jet_Y, healthBarSizeX;
	private String userName, lastHitEnemy, defaultFont, backgroundImagePath;
	private JLabel collideLabel, exitLabel, jetFighter, healthBar, background, scoreLabel, scoreIntLabel, enemyHitLabel;

	public EnemyInTheSky(int duration, String name) {
		userName = name;
		gameDuration = duration;
		InitializeFixedParameters();
	}

	public void Play() {
		InitializeParameters();
		InitializeGuiElements();
	}

	private void InitializeImages() {
		collideImage = new ImageIcon("resources\\image\\collide.png").getImage(); //retrieves the Image object associated with the ImageIcon using the getImage() method and assigns it to the collideImage variable.

		collideImage = collideImage.getScaledInstance(96, 96, java.awt.Image.SCALE_SMOOTH); // getScaledInstance() method is called on the collideImage object to resize the image to a width and height of 96 pixels each, using smooth scaling mode. The resized image replaces the original image assigned to collideImage.

		gameEndImage = new ImageIcon("resources\\image\\rip.png").getImage();
		gameEndImage = gameEndImage.getScaledInstance(planeSize * 2, planeSize * 2, java.awt.Image.SCALE_SMOOTH); //SCALE_SMOOTH argument indicates that smooth scaling should be used for resizing the image.

		successImage = new ImageIcon("resources\\image\\done.png").getImage();
		successImage = successImage.getScaledInstance(planeSize , planeSize , java.awt.Image.SCALE_SMOOTH);

		AirCombatImage = new ImageIcon("resources\\image\\jetfighter.png").getImage();
		AirCombatImage = AirCombatImage.getScaledInstance(planeSize, planeSize, java.awt.Image.SCALE_SMOOTH);

		exitImage = new ImageIcon("resources\\image\\exit.png").getImage();
		exitImage = exitImage.getScaledInstance(140, 45, java.awt.Image.SCALE_SMOOTH);

		hitImage = new ImageIcon("resources\\image\\enemyHit.png").getImage();
		hitImage = hitImage.getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH);

		bulletImage = new ImageIcon("resources\\image\\bullet.png").getImage();
		bulletImage = bulletImage.getScaledInstance(jetFighterBulletSize, jetFighterBulletSize * 2,
				java.awt.Image.SCALE_SMOOTH);

		enemyBulletImage = new ImageIcon("resources\\image\\enemyBullet.png").getImage();
		enemyBulletImage = enemyBulletImage.getScaledInstance(enemyBulletSize, enemyBulletSize * 2,
				java.awt.Image.SCALE_SMOOTH);

		enemyImage = new ImageIcon("resources\\image\\enemy.png").getImage();
		enemyImage = enemyImage.getScaledInstance(planeSize, planeSize, java.awt.Image.SCALE_SMOOTH);

		healthImage = new ImageIcon("resources\\image\\healthBar.png").getImage();
		healthImage = healthImage.getScaledInstance(healthBarSizeX, 48, java.awt.Image.SCALE_SMOOTH);
	}

	private void InitializeFixedParameters() {
		mySound = new Sounds();
		defaultFont = "Comic Sans MS"; // use in the graphical interface
		planeSize = 64;  //airCombat 
		screenWidth = 624;
		screenHeight = 600;
		backgroundPositionY = 0;// initial vertical position of the background 
		Menu_ObjHeight = 65; // if windows default bar is active then 100 else 65
		randomX = 0; // enemy positions
		mainJetSpeed = 20;
		enemySpeed = 25;
		bulletSpeed = 80;
		enemyBulletSpeed = 45;
		downLimit = 0; // bottom edge of screen 
		hitPoint = 100;
		upLimit = 0; // upper edge of screen 
		enemyBulletSize = 33;
		jetFighterBulletSize = 15;
		healthBarSizeX = 180; //initialize the value 
		enemyPeriod = 500; //interval between appearances of enemy Jet
		bulletPeriod = 225; //the firing rate of the player's bullets.
		blastDustPeriod = 2500;//removing  dust effects after an explosion 
		InitializeImages();

		if (gameDuration == 0) //mini
			backgroundImagePath = "resources\\image\\Mini.jpg";
		if (gameDuration == 1) // mid
			backgroundImagePath = "resources\\image\\Middle.jpg";
		if (gameDuration == 2) // long
			backgroundImagePath = "resources\\image\\LongGame.jpg";
	}

	private void InitializeParameters() {
		isThreadRunning = true;
		gameClip = mySound.GameBackgroundSound();
		gameClip.start();
		healthLimit = 6;
		healthBarDecreaseSizeX = (healthBarSizeX - 40) / healthLimit; 
		score = 0;
		counter = 0;
		jet_X = 250;
		jet_Y = 0;
		keyInput = new HashSet<>(); 
	}

	private void InitializeGuiElements() {
		setUndecorated(true); //This removes the title bar from the game window
		setDefaultCloseOperation(EXIT_ON_CLOSE);//When the window is closed, the program terminates
		setVisible(true); // makes the game window visible to the player.
		panel = new JPanel();//creates a new JPanel to serve as the container for GUI components.
		panel.setLayout(null);// makes the game window visible to the player.
		background = new JLabel();// creates a JLabel component to display the background image of the game.
		background.setName("backgroundimage");
		background.setIcon(new ImageIcon(backgroundImagePath));
		Dimension backgroundsize = background.getPreferredSize();

		jet_Y = (int) backgroundsize.getHeight() - Menu_ObjHeight; //Menu_Objheight = 65 ; sets initial vertical position of the jet fighter
		//System.out.println(jet_Y);

		downLimit = (int) backgroundsize.getHeight() - Menu_ObjHeight; //sets the lower limit or boundary for the game 

		backgroundPositionY = -1 * ((int) backgroundsize.getHeight() - screenHeight); //to ensure it is centered on the screen vertically.

		upLimit = downLimit - screenHeight + Menu_ObjHeight;

		background.setBounds(0, backgroundPositionY, backgroundsize.width, backgroundsize.height); //bounds of the background image JLabel to position it correctly within the game window.

		exitLabel = new JLabel();//JLabel to represent the exit button in the game.
		exitLabel.setIcon(new ImageIcon(exitImage));
		exitLabel.setName("exitButton");
		exitLabel.setSize(140, 45);
		exitLabel.setFont(new Font(defaultFont, Font.BOLD, 18));
		exitLabel.addMouseListener(ExitGame());
		background.add(exitLabel);

		collideLabel = new JLabel();
		collideLabel.setIcon(new ImageIcon(collideImage));
		collideLabel.setName("expCollision");
		collideLabel.setSize(96, 96);
		collideLabel.setVisible(false);
		background.add(collideLabel);

		enemyHitLabel = new JLabel();
		enemyHitLabel.setIcon(new ImageIcon(hitImage));
		enemyHitLabel.setName("expHitDown");
		enemyHitLabel.setSize(64, 64);
		enemyHitLabel.setVisible(false);
		background.add(enemyHitLabel);

		InitializeJetFighter();
		InitializeScoreBar();
		InitializeHealthBar();

		panel.add(background); //adds the background JLabel to the panel.
		add(panel);
		setSize(screenWidth, screenHeight);
		setLocationRelativeTo(null); // centers the game window on the screen.
		myKeyListener = MyMultiKeyListener();//adds a key listener to the game window to handle keyboard input.
		addKeyListener(myKeyListener);
		validate();//validates the layout of the components in the game window.
	}

	private void InitializeJetFighter() {
		jetFighter = new JLabel();
		jetFighter.setName("mainJet");
		jetFighter.setIcon(new ImageIcon(AirCombatImage));
		Dimension size = jetFighter.getPreferredSize();
		jetFighter.setBounds(jet_X, jet_Y, size.width, size.height); // set player jet position
		background.add(jetFighter);
	}

	private void InitializeScoreBar() {
		scoreLabel = new JLabel();
		scoreLabel.setName("score");
		scoreLabel.setText("SCORE: ");
		scoreLabel.setFont(new Font(defaultFont, Font.BOLD, 24));
		scoreLabel.setForeground(Color.DARK_GRAY);
		scoreLabel.setSize(111, 30);
		scoreLabel.setBackground(Color.LIGHT_GRAY);
		scoreLabel.setOpaque(true);
		background.add(scoreLabel);

		scoreIntLabel = new JLabel();
		scoreIntLabel.setName("scoreint");
		scoreIntLabel.setText(String.valueOf(score));
		scoreIntLabel.setFont(new Font(defaultFont, Font.BOLD, 24));
		scoreIntLabel.setForeground(Color.DARK_GRAY);
		scoreIntLabel.setSize(150, 30);
		scoreIntLabel.setBackground(Color.LIGHT_GRAY);
		scoreIntLabel.setOpaque(true);
		background.add(scoreIntLabel);
	}

	private void InitializeHealthBar() {
		healthBar = new JLabel();
		healthBar.setName("expHealthBar");
		healthBar.setIcon(new ImageIcon(healthImage));
		healthBar.setSize(healthBarSizeX, 48);
		background.add(healthBar);
	}

	private void CreateNewEnemy() {
		if (isThreadRunning) {
			randomX = GetRandomNumberUsingNextInt(randomX, 0, 550);
			background.add(AddEnemy(randomX));
			panel.validate();
		}
	}

	private JLabel AddEnemy(int newx) {
		counter++;
		JLabel jetEnemy = new JLabel();
		jetEnemy.setName("enemy" + counter); // enemy+ 
		jetEnemy.setIcon(new ImageIcon(enemyImage));
		Dimension sizeEnemy = jetEnemy.getPreferredSize();
		jetEnemy.setBounds(newx, upLimit, sizeEnemy.width, sizeEnemy.height);

		counter++;
		JLabel enemyBullet = new JLabel();
		enemyBullet.setName("BulletFire" + counter);
		enemyBullet.setIcon(new ImageIcon(enemyBulletImage));
		enemyBullet.setSize(enemyBulletSize, enemyBulletSize * 2);
		enemyBullet.setLocation(newx + (planeSize / 2) - enemyBulletSize / 2, upLimit + planeSize);
		background.add(enemyBullet);

		return jetEnemy;
	}

	private void MoveBackground() {
		upLimit = downLimit  + Menu_ObjHeight - screenHeight;
		//System.out.println(upLimit); 
	
		if (upLimit <= 200) {
			GameOver(true);
		} else {
			backgroundPositionY = backgroundPositionY + 1;
			downLimit = downLimit - 1;

			healthBar.setLocation(0, upLimit);// set at starting 
			exitLabel.setLocation(500, upLimit); // set at the end of page

			scoreLabel.setLocation(200, upLimit + 5);
			scoreIntLabel.setLocation(310, upLimit + 5);

			if (jet_Y >= downLimit) {
				jet_Y = downLimit;
				jetFighter.setLocation(jetFighter.getX(), downLimit);
			}

			if (jet_Y <= upLimit) {
				jet_Y = upLimit;
				jetFighter.setLocation(jetFighter.getX(), upLimit);
			}

			background.setLocation(background.getX(), backgroundPositionY);
		}
	}

	private void Addbullet() {
		// left side bullet
		counter++;
		JLabel bullet = new JLabel();
		bullet.setName("bullet" + counter);
		bullet.setIcon(new ImageIcon(bulletImage));
		bullet.setSize(jetFighterBulletSize, jetFighterBulletSize * 2);
		bullet.setLocation(jet_X, jet_Y + (jetFighterBulletSize / 2));
		background.add(bullet);

		// right side bullet
		counter++;
		JLabel bullet2 = new JLabel();
		bullet2.setName("bullet" + counter);
		bullet2.setIcon(new ImageIcon(bulletImage));
		bullet2.setSize(jetFighterBulletSize, jetFighterBulletSize * 2);
		bullet2.setLocation(jet_X + 50, jet_Y + (jetFighterBulletSize / 2)); //fighter jet have dimension of 50
		background.add(bullet2);

		mySound.BulletSound();
	}

	private void CompareLocations(Container parent) {
		Component[] all = parent.getComponents();  //parent class --> component ; Sub class --> Container 

		for (Component enemy : all) { //getcomponents() se sare components aa jayenge , lekin hme sirf enemy wala chaiye 
			if (enemy.getName().contains("enemy")) {
				// when enemy got out from screen remove the related component
				if (enemy.getY() > downLimit+30) {
					parent.remove(enemy);
					parent.validate();
				} else if (Math.abs(enemy.getX() - jet_X) < 50
						&& Math.abs(enemy.getY() - jet_Y) < 50) {
					System.out.println("Collide JetFighter Position : " + jet_X + " , " + jet_Y
							+ " ---- Enemy Position : " + enemy.getX() + " , " + enemy.getY() );

					collideLabel.setVisible(true);
					collideLabel.setLocation(jet_X, jet_Y);

					if (!enemy.getName().contains("BulletFire")) {
						score = score + hitPoint;
						scoreIntLabel.setText(String.valueOf(score));
					}
					mySound.Collision();
					healthLimit--;  // Collision with Enemy: The score is incremented by (hitPoint), The health bar decreases ,If the health bar ==0, the game ends.
					healthBarSizeX = healthBarSizeX - healthBarDecreaseSizeX;
					healthBar.setSize(healthBarSizeX, 48);

					parent.remove(enemy);
					parent.validate();

					if (healthLimit == 0)
						GameOver(false);

				} else {
					for (Component bullet : all) {
						if (bullet.getName().contains("bullet")) {
							if (bullet.getY() < (upLimit)) {
								parent.remove(bullet);
								parent.validate();
							} else if (Math.abs(bullet.getY() - enemy.getY()) < 50
									&& Math.abs(bullet.getX() - enemy.getX()) < 50) {

								if (lastHitEnemy != enemy.getName()) {
									System.out.println("Hit " + bullet.getName() + " vertical coordiante: " + bullet.getY()
											+ " , " + enemy.getName() + " vertical coordiante: " + enemy.getY() + " " + upLimit);

									if (!enemy.getName().contains("BulletFire")) {
										score = score + hitPoint; //JetFighterBullet's Bullet Hits Enemy , The score is incremented by (hitPoint).
										scoreIntLabel.setText(String.valueOf(score));
										lastHitEnemy = enemy.getName();
										mySound.EnemyDown();
										enemyHitLabel.setVisible(true);
										enemyHitLabel.setLocation(enemy.getX(), enemy.getY());
									}
								}

								parent.remove(bullet);
								parent.remove(enemy);
								parent.validate();
							}
						}
					}
				}
			}
		}
		parent.repaint();
		parent.validate();// any changes made to the container's components (such as adding or removing an enemy) will be reflected in the visual layout of the container
	}

	private void UpdateEnemyLocations(Container parent) { 
		//UpdateEnemyLocations() is a method that iterates over all components in a container, identifies enemy components, and updates their locations to simulate their movement in the game.
		Component[] all = parent.getComponents();

		for (Component c : all) {
			if (c.getName().contains("enemy") && isThreadRunning) // enemy ki speed 
				c.setLocation(c.getX(), c.getY() + enemySpeed);
		}
	}

	private void UpdateBulletLocations(Container parent) {  
		Component[] all = parent.getComponents(); // getComponents se sare elements aa jate hai !!!

		for (Component c : all) { //list of component se required components nikal rhe !!! c variable can be anything (bullet, jet ,....)
			if (c.getName().contains("bullet") && isThreadRunning)  // c.getName().bullets se bullet wala components nikal rhe
				c.setLocation(c.getX(), c.getY() - bulletSpeed);   //Set the bulletSpeed of AirCombat 

			if (c.getName().contains("BulletFire") && isThreadRunning)  //c.getName().bulletFire se bulletFire wala components nikal rhe
				c.setLocation(c.getX(), c.getY() + enemyBulletSpeed); // Set the bulletFire Speed of enemy
		}
	}

	private KeyListener MyMultiKeyListener() { // return a new instance of KeyListner interface 
		return new KeyListener() {
			@Override   //override keyPressed() method 
			public synchronized void keyPressed(KeyEvent e) {
				keyInput.add(e.getKeyCode());

				jet_X = jetFighter.getX();
				jet_Y = jetFighter.getY();

				if (!keyInput.isEmpty()) {
					for (Iterator<Integer> it = keyInput.iterator(); it.hasNext();) {
						switch (it.next()) {
						case KeyEvent.VK_SPACE:
							spacePressed = true;
							break;
						
						case KeyEvent.VK_UP:
							if (jet_Y != upLimit)
								jetFighter.setLocation(jetFighter.getX(), jetFighter.getY() - mainJetSpeed);
							break;
						
						case KeyEvent.VK_LEFT:
							if (jet_X >= 0)
								jetFighter.setLocation(jetFighter.getX() - mainJetSpeed, jetFighter.getY());
							break;
						
						case KeyEvent.VK_DOWN:
							if (jet_Y != downLimit)
								jetFighter.setLocation(jetFighter.getX(), jetFighter.getY() + mainJetSpeed);
							break;
						
						case KeyEvent.VK_RIGHT:
							if (jet_X != (screenWidth-75))
								jetFighter.setLocation(jetFighter.getX() + mainJetSpeed, jetFighter.getY());
							break;
						}
					}
					jet_X = jetFighter.getX();
					jet_Y = jetFighter.getY();
				}
			}

			@Override
			public synchronized void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 32) {
					spacePressed = false;
				}
				keyInput.remove(e.getKeyCode());

			}

			@Override  // whenever we implement an interface , we must define all the methods declare in that interface whether it is  required or not  !!! 
			public void keyTyped(KeyEvent e) {
				 }

		};
	}
	private int GetRandomNumberUsingNextInt(int lastGenerated, int min, int max) {
		Random random = new Random();
		int created = random.nextInt(max - min) + min;

		while (Math.abs(lastGenerated - created) < planeSize)
			created = random.nextInt(max - min) + min;

		return created;
	}

	private void GameOver(boolean success) {
		StopThread();

		if (!success) {
			mySound.GameOverFail();
			System.out.println("Game Over :(");
			jetFighter.setIcon(new ImageIcon(gameEndImage));
		} else {
			mySound.LevelComplete();
			System.out.println("Level Completed :)");
			jetFighter.setIcon(new ImageIcon(successImage));
		}

		jetFighter.setLocation(250, downLimit - 300);
		jetFighter.setSize(256, 256);

		scoreLabel.setLocation(185, downLimit - 500 + Menu_ObjHeight);
		scoreLabel.setText("Your Final Score");
		scoreLabel.setSize(300, 25);
		scoreLabel.setBackground(Color.LIGHT_GRAY);
		scoreLabel.setOpaque(true);
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

		scoreIntLabel.setLocation(185, downLimit - 475 + Menu_ObjHeight);
		scoreIntLabel.setText(String.valueOf(score));
		scoreIntLabel.setSize(300, 25);
		scoreIntLabel.setBackground(Color.LIGHT_GRAY);
		scoreIntLabel.setOpaque(true);
		scoreIntLabel.setHorizontalAlignment(SwingConstants.CENTER);

		removeKeyListener(myKeyListener);

		JButton playAgainButton = new JButton("Play Again");
		playAgainButton.setLocation(250, downLimit - 450 + Menu_ObjHeight);
		playAgainButton.setSize(140, 50);
		playAgainButton.setName("playAgainButton");
		playAgainButton.setFont(new Font(defaultFont, Font.BOLD, 18));
		playAgainButton.addActionListener(PlayAgainListener());

		background.add(playAgainButton);
		ClearComponents();
		background.repaint();
		background.validate();
	}

	private ActionListener PlayAgainListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); 
				MainMenu.RunGame();
			}
		};
	}

	private MouseAdapter ExitGame() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit the game?", "Quit Game?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					StopThread();
					dispose();
				} else { // option "no" selected
					// do nothing, return game menu
				}
			}
		};
	}

	@Override
	public void run() {
		isThreadRunning = true;

		long current = System.currentTimeMillis();
		long end = current + enemyPeriod;

		long currentForBullet = System.currentTimeMillis();
		long endForBullet = current + bulletPeriod;

		long currentForEnemyHit = System.currentTimeMillis();
		long endForEnemyHit = currentForEnemyHit + blastDustPeriod;

		while (isThreadRunning) {
			try {
				Thread.sleep(100);
				if (isThreadRunning) {
					MoveBackground();
					UpdateBulletLocations(background);
					UpdateEnemyLocations(background);
					CompareLocations(background);

					// Remove Blast and dust images in Every 2500 milliseconds
					if (System.currentTimeMillis() > endForEnemyHit) {
						currentForEnemyHit = System.currentTimeMillis();
						endForEnemyHit = currentForEnemyHit + blastDustPeriod;

						if (enemyHitLabel.isVisible())
							enemyHitLabel.setVisible(false);

						if (collideLabel.isVisible())
							collideLabel.setVisible(false);
					}
					// Create New Enemy Every 500 milliseconds
					if (System.currentTimeMillis() > end) {
						CreateNewEnemy();
						current = System.currentTimeMillis();
						end = current + enemyPeriod;
					}
					// if space hold then auto attack every 225 milliseconds
					if (System.currentTimeMillis() > endForBullet && spacePressed == true) {
						Addbullet();
						currentForBullet = System.currentTimeMillis();
						endForBullet = currentForBullet + bulletPeriod;
					}
				}
			} catch (Exception e) {
				Thread.currentThread().interrupt();
				System.out.println("Thread was interrupted, Failed to complete operation");
			}
		}
		System.out.println("Thread Dead");
	}

	private void StopThread() {
		gameClip.close();
		isThreadRunning = false;
	}

	private void ClearComponents() {
		Component[] all = background.getComponents();

		try {
			for (Component c : all) {
				if (c.getName() != null) {
					if (c.getName().contains("enemy") || c.getName().contains("bullet")
							|| c.getName().contains("exp")) {
						background.remove(c);
					}
				}
			}
		} catch (Exception e) {

		}

		System.out.println("Components Removed");
	}
}