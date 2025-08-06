package snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.Scanner;

public class Board extends JPanel implements ActionListener {
	private Image apple;
	private Image dot;
	private Image head;

	private final int ALL_DOTS = 900;
	private final int DOT_SIZE = 10;
	private final int RANDOM_POSITION = 29;

	private int apple_x;
	private int apple_y;

	private final int x[] = new int[ALL_DOTS];
	private final int y[] = new int[ALL_DOTS];

	private boolean leftDirection = false;
	private boolean rightDirection = true;
	private boolean upDirection = false;
	private boolean downDirection = false;

	private boolean inGame = true;

	private int dots;
	private Timer timer;

	private JButton restartButton;

	private int score = 0;
	private int highScore = 0;
	private final String HIGHSCORE_FILE = "highscore.txt";

	private int delay = 140; // initial delay (in milliseconds)

	Board() {

		setLayout(null);

		addKeyListener(new TAdapter());

		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(300, 300));
		setFocusable(true);

		loadImages();
		loadHighScore();
		initGame();
	}

	public void loadImages() {
		try {
			apple = new ImageIcon(getClass().getResource("/snakegame/icons/apple.png")).getImage();
			dot = new ImageIcon(getClass().getResource("/snakegame/icons/dot.png")).getImage();
			head = new ImageIcon(getClass().getResource("/snakegame/icons/head.png")).getImage();
		} catch (Exception e) {
			System.out.println("Image loading failed: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void initGame() {
		dots = 3;

		for (int i = 0; i < dots; i++) {
			y[i] = 50;
			x[i] = 50 - i * DOT_SIZE;
		}
		locateApple();

		timer = new Timer(delay, this);
		timer.start();

		restartButton = new JButton("Restart");
		restartButton.setBounds(100, 160, 100, 30);
		restartButton.setFocusable(false);
		restartButton.addActionListener(e -> restartGame());
		restartButton.setVisible(false); // Hide initially
		this.add(restartButton); // Add button to panel
	}

	public void locateApple() {
		int r = (int) (Math.random() * RANDOM_POSITION);
		apple_x = r * DOT_SIZE;

		r = (int) (Math.random() * RANDOM_POSITION);
		apple_y = r * DOT_SIZE;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		draw(g);
	}

	public void draw(Graphics g) {
		if (inGame) {
			g.drawImage(apple, apple_x, apple_y, this);
			for (int i = 0; i < dots; i++) {
				if (i == 0) {
					g.drawImage(head, x[i], y[i], this);
				} else {
					g.drawImage(dot, x[i], y[i], this);
				}
			}

			g.setColor(Color.WHITE);
			g.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
			g.drawString("Score: " + score, 10, 20);
			g.drawString("HS: " + highScore, 170, 20);

			Toolkit.getDefaultToolkit().sync();
		} else {
			gameOver(g);
		}

	}

	public void gameOver(Graphics g) {
		restartButton.setVisible(true);

		String msg = "Game Over!";
		String scoreMsg = "Score: " + score;
		String highScoreMsg = "High Score: " + highScore;
		Font font = new Font("SAN_SERIF", Font.BOLD, 14);
		FontMetrics metrices = getFontMetrics(font);

		g.setColor(Color.WHITE);
		g.setFont(font);
		g.drawString(msg, (300 - metrices.stringWidth(msg)) / 2, 300 / 2);
		g.drawString(scoreMsg, 10, 20);
		g.drawString(highScoreMsg, 10, 40);

	}

	public void move() {
		for (int i = dots; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}

		if (leftDirection) {
			x[0] -= DOT_SIZE;
		}
		if (rightDirection) {
			x[0] += DOT_SIZE;
		}
		if (upDirection) {
			y[0] -= DOT_SIZE;
		}
		if (downDirection) {
			y[0] += DOT_SIZE;
		}

		if (x[0] >= 300 || x[0] < 0 || y[0] >= 300 || y[0] < 0) {
			inGame = false;
			timer.stop();
		}
	}

	public void checkApple() {
		if ((x[0] == apple_x) && (y[0] == apple_y)) {
			dots++;
			score++;
			locateApple();

			if (score > highScore) {
				highScore = score;
				saveHighScore();
			}

			if (delay > 40) {
				delay -= 10;
				timer.setDelay(delay);
			}
		}
	}

	public void checkCollision() {
		for (int i = dots; i > 0; i--) {
			if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
				inGame = false;
				timer.stop();
			}
		}

		if (x[0] >= 300 || x[0] < 0 || y[0] >= 300 || y[0] < 0) {
			inGame = false;
			timer.stop();
		}
	}

	public void restartGame() {
		if (score > highScore) {
			highScore = score;
			saveHighScore();
		}
		dots = 3;
		score = 0;
		inGame = true;

		leftDirection = false;
		rightDirection = true;
		upDirection = false;
		downDirection = false;

		for (int i = 0; i < dots; i++) {
			y[i] = 50;
			x[i] = 50 - i * DOT_SIZE;
		}

		locateApple();
		restartButton.setVisible(false);
		timer.restart();
		repaint();
	}

	public void saveHighScore() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(HIGHSCORE_FILE));
			writer.println(highScore);
			writer.close();
		} catch (IOException e) {
			System.out.println("Error saving high score:");
		}
	}

	public void actionPerformed(ActionEvent ae) {
		if (inGame) {
			checkApple();
			checkCollision();
			move();
		}
		repaint();
	}

	public void loadHighScore() {
		try {
			File file = new File(HIGHSCORE_FILE);
			if (file.exists()) {
				Scanner scanner = new Scanner(file);
				if (scanner.hasNextInt()) {
					highScore = scanner.nextInt();
				}
				scanner.close();
			}
		} catch (FileNotFoundException e) {
			System.out.println("High score file not found.");
		}

	}

	public class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			if (key == KeyEvent.VK_LEFT && (!rightDirection)) {
				leftDirection = true;
				upDirection = false;
				downDirection = false;
			}

			if (key == KeyEvent.VK_RIGHT && (!leftDirection)) {
				rightDirection = true;
				upDirection = false;
				downDirection = false;
			}

			if (key == KeyEvent.VK_UP && (!downDirection)) {
				upDirection = true;
				leftDirection = false;
				rightDirection = false;
			}

			if (key == KeyEvent.VK_DOWN && (!upDirection)) {
				downDirection = true;
				leftDirection = false;
				rightDirection = false;
			}

			if (!inGame && key == KeyEvent.VK_ENTER) {
				restartGame();
			}
		}
	}

}
