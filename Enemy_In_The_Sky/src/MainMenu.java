import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class MainMenu extends JFrame {
	private static int gameTypeSelection;
	private int screenWidth = 624;
	private int screenHeight = 600;
	private JPanel panel;
	private boolean isLoggedIn;
	private static JTextField username;
	private UserController user; // user operations
	private JPasswordField password, password2; 
	private JButton registerButton; //loginButton;
	private String defaultFont = "Comic Sans MS";
	private JLabel background, userDetail;
	private JLabel usernameLabel, passwordLabel, passwordLabel2; 
	private JButton quitButton, playTheGameButton, authorButton;

	public MainMenu() {
		isLoggedIn = false;
		user = new UserController();
		InitializeMenuGUI();
	}

	private void InitializeMenuGUI() {
		setUndecorated(true); 	
		setSize(screenWidth, screenHeight);
		setLocationRelativeTo(null); // center screen

		background = new JLabel();
		background.setName("menuimage");
		background.setIcon(new ImageIcon("resources\\image\\menu.jpg"));
		Dimension backgroundsize = background.getPreferredSize(); 
		background.setBounds(0, 0, backgroundsize.width, backgroundsize.height);

		panel = new JPanel();
		panel.setLayout(null);

		quitButton = new JButton("Exit");
		quitButton.setLocation(420, 370);
		quitButton.setSize(140, 40);
		quitButton.setFont(new Font(defaultFont, Font.BOLD, 18));
		quitButton.addActionListener(ExitGame());

		registerButton = new JButton("Register");
		registerButton.setLocation(420, 270);
		registerButton.setSize(140, 40);
		registerButton.setFont(new Font(defaultFont, Font.BOLD, 18));
		registerButton.addActionListener(LoginOrRegisterListener("Register"));

		playTheGameButton = new JButton("Start Game");
		playTheGameButton.setLocation(420, 320);
		playTheGameButton.setSize(140, 40);
		playTheGameButton.setFont(new Font(defaultFont, Font.BOLD, 18));
		playTheGameButton.addActionListener(StartGame());

		authorButton = new JButton("Enemy in the sky");
		authorButton.setName("authorButtonLabel");
		authorButton.setFont(new Font(defaultFont, Font.BOLD, 18));
		authorButton.setLocation(400, 120);
		authorButton.setSize(200, 50);

		userDetail = new JLabel();
		userDetail.setName("userDetailLabel"); // welcome
		userDetail.setLocation(440, 200);
		userDetail.setSize(250, 100);
		userDetail.setFont(new Font(defaultFont, Font.BOLD, 22));
		userDetail.setForeground(Color.white);
		userDetail.setVisible(false);

		background.add(userDetail);
		background.add(authorButton);
		background.add(registerButton);
		background.add(quitButton);
		background.add(playTheGameButton);

		panel.add(background);
		add(panel);
		
		setVisible(true);
		validate(); 
	}

	private void ShowLoginRegisterPopUp(String type) {
		Object[] buttonNames = { type, "Cancel" };        // cancel button on 

		int result = JOptionPane.showOptionDialog(this, LoginRegisterPopUpPanel(type), type, JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, buttonNames, null);    // ????

		String actionResultMsg = " User Name or Password Wrong";

		if (result == JOptionPane.YES_OPTION)  // Login 
		{
			if (type.equals("Login")) {
				if (user.IsSuccessfulLogin(username.getText(), String.valueOf(password.getPassword()))) {
					InitializeSuccessfulLogin(username.getText());
				}
			}

			if (type.equals("Register")) {
				
				actionResultMsg = user.Insert(username.getText(), String.valueOf(password.getPassword()),
						String.valueOf(password2.getPassword()));
				if (actionResultMsg.equals("OK")) {
					InitializeSuccessfulLogin(username.getText());
				}
			}

			if (!isLoggedIn) {
				JOptionPane.showConfirmDialog(null, actionResultMsg, type + " Failed !!!", JOptionPane.DEFAULT_OPTION,
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			// result == JOptionPane.CANCEL_OPTION
		}
	}

	//creating a panel to insert our pop up which created by JOptionPane to show register / login popup
	private JPanel LoginRegisterPopUpPanel(String type) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		usernameLabel = new JLabel("              Username : ");
		passwordLabel = new JLabel("              Password : ");
		passwordLabel2 = new JLabel("Retype Password : ");
		username = new JTextField();
		username.setPreferredSize(new Dimension(100, 25));

		password = new JPasswordField();
		password.setPreferredSize(new Dimension(100, 25));

		panel.add(usernameLabel);
		panel.add(username);
		panel.add(passwordLabel);
		panel.add(password);

		if (type.equals("Register")) {
			password2 = new JPasswordField();
			password2.setPreferredSize(new Dimension(100, 25));
			panel.add(passwordLabel2);
			// retype password
			panel.add(password2);
		}

		return panel;
	}

	private ActionListener LoginOrRegisterListener(String type) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowLoginRegisterPopUp(type);
			}
		};
	}

	private ActionListener StartGame() {
		Object[] buttonNames = { "Mini Game", "Mid Game", "Long Game", "Back" };
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isLoggedIn) {
					gameTypeSelection = JOptionPane.showOptionDialog(panel,
							"Use Arrows to Move Jet Combat"
									+ "\n\nPlease Hold 'Space' Key to Attack" + "\n\nGood Luck!\n\n",
							"INSTRUCTIONS", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttonNames, null);
					// Start Mini Game
					if (gameTypeSelection == JOptionPane.YES_OPTION)
						gameTypeSelection = 0;
					// Start Mid Game
					else if (gameTypeSelection == JOptionPane.NO_OPTION)
						gameTypeSelection = 1;
					// Start Long Game
					else if (gameTypeSelection == JOptionPane.CANCEL_OPTION)
						gameTypeSelection = 2;
					// Return To Menu
					else
						return;

					RunGame();

				} else {
					ShowLoginRegisterPopUp("Login");
				}
			}
		};
	}

	private ActionListener ExitGame() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(panel, "Are you sure you want to quit the game?", "Quit Game?",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					dispose(); // 
					System.exit(0);
				}
			}
		};
	}

	private void InitializeSuccessfulLogin(String username) {
		isLoggedIn = true; 
		registerButton.setVisible(false); // register 
		background.validate();
		playTheGameButton.setLocation(420, 320);
		userDetail.setText("<html>Welcome<br/>" + username.toLowerCase() + "</html>"); 
		userDetail.setVisible(true);
	}

	public static void RunGame() {
		EnemyInTheSky jf = new EnemyInTheSky(gameTypeSelection, username.getText());
		Thread thread = new Thread(jf);
		jf.Play();
		thread.start();
	}
}
