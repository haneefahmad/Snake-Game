package snakegame;

import javax.swing.*;

public class SnakeGame extends JFrame {
	SnakeGame() {
		super("Snake Game");

		// ✅ Use relative classpath to set the window icon
		setIconImage(new ImageIcon(getClass().getResource("/snakegame/icons/snakeGameSymbol.png")).getImage());

		add(new Board());
		pack();

		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new SnakeGame().setVisible(true);
	}
}
