package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import exceptions.InvalidPowerUseException;
import exceptions.OccupiedCellException;
import exceptions.UnallowedMovementException;
import exceptions.WrongTurnException;
import model.game.Game;
import model.game.Player;
import model.game.SingleplayerGame;
import model.pieces.Piece;
import model.pieces.sidekicks.SideKick;
import model.pieces.heroes.*;
import model.game.Computer;
import model.game.Direction;;

@SuppressWarnings("serial")
public class SuperHeroChess extends JFrame implements ActionListener, MouseListener, ItemListener {
	private Game game;
	private SingleplayerGame singleplayerGame;
	private ArrayList<Piece> pieces;
	private ArrayList<JButton> bc;
	private Background boardcells;
	private JPanel currentplayer;
	private JProgressBar payload1;
	private JProgressBar payload2;
	private JPanel Directions;
	private ArrayList<JButton> d;
	private JComboBox<Piece> DeadCharacters;
	private Piece currentPiece;
	private Piece target;
	private Point newPoint;
	private JLabel currentLabel;
	private JLabel selectedItems;
	private Direction dir;
	private Piece medicTarget;
	private ArrayList<Point> locations;
	/*
	 * private JLabel player1hero; private JLabel player2hero; private String
	 * player1P; private String player2P; private JButton swap;
	 */

	public SuperHeroChess() {
		setTitle("Super Hero Chess");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(750, 650);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		String name1;
		Player player1;
		name1 = JOptionPane.showInputDialog("Please enter player one");
		player1 = new Player(name1);
		int reply = JOptionPane.showConfirmDialog(null, "Do you want to play against the computer?", "New game?",
				JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.NO_OPTION) {
			Player player2;
			String name2 = JOptionPane.showInputDialog("Please enter player two");
			player2 = new Player(name2);
			game = new Game(player1, player2);
		}
		else{
			Computer player2;
			player2= new Computer();
			singleplayerGame=new SingleplayerGame(player1,player2);
		}
		currentplayer = new JPanel();
		currentplayer.setBackground(new Color(98, 134, 168));
		currentLabel = new JLabel("Current Player: " + ((game==null)?singleplayerGame:game).getCurrentPlayer().getName());
		currentLabel.setBounds(0, 0, 200, 100);
		currentLabel.setFont(new Font("Serif", Font.BOLD, 16));
		currentLabel.setForeground(Color.darkGray);
		currentplayer.add(currentLabel, BorderLayout.WEST);
		Directions = new JPanel();
		currentplayer.add(Directions, BorderLayout.CENTER);
		Directions.setLayout(new GridLayout(0, 3));
		d = new ArrayList<>();
		addDirections();
		selectedItems = new JLabel();
		updateSelectedItem();
		currentplayer.add(selectedItems, BorderLayout.AFTER_LINE_ENDS);
		JButton usepower = new JButton();
		usepower.setBackground(new Color(107, 138, 167));
		usepower.setBorder(new LineBorder(Color.gray));
		usepower.addActionListener(this);
		usepower.setText("Use Power");
		JButton unselectPiece = new JButton();
		unselectPiece.setText("undo");
		unselectPiece.setBackground(new Color(107, 138, 167));
		unselectPiece.setBorder(new LineBorder(Color.GRAY));
		unselectPiece.addActionListener(this);
		currentplayer.add(unselectPiece, BorderLayout.LINE_START);
		currentplayer.add(usepower, BorderLayout.EAST);
		currentplayer.setSize(950, 500);
		add(currentplayer, BorderLayout.SOUTH);
		payload1 = new JProgressBar(JProgressBar.VERTICAL, 0, 6);
		payload1.setValue(0);
		payload1.setForeground(Color.blue);
		add(payload1, BorderLayout.WEST);
		payload2 = new JProgressBar(JProgressBar.VERTICAL, 0, 6);
		payload2.setValue(0);
		payload2.setForeground(Color.RED);
		add(payload2, BorderLayout.EAST);
		DeadCharacters = new JComboBox<Piece>();
		boardcells = new Background();
		updateDeadCharacters();
		bc = new ArrayList<>();
		dir = null;
		showGame();
		/*
		 * player1her(game==null)?singleplayerGame:gamew JLabel(); player2hero=new JLabel();
		 * player1hero.setText(player1P); player2hero.setText(player2P);
		 * player1hero.setVisible(true); swap=new JButton();
		 * swap.setText("swap"); swap.setBackground(new Color(107, 138, 167));
		 * swap.setBorder(new LineBorder(Color.GRAY));
		 * swap.addActionListener(this); currentplayer.add(swap,
		 * BorderLayout.WEST); add(player1hero,BorderLayout.EAST);
		 * add(player2hero,BorderLayout.WEST);
		 */
		revalidate();

	}

	public static String getDir() {
		String s = System.getProperty("user.dir");
		String tmp = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '\\') {
				tmp = tmp + '/';
			} else
				tmp = tmp + s.charAt(i);
		}
		return tmp;
	}

	public static void startGame() {
		new SuperHeroChess();
	}

	private void updateDeadCharacters() {
		DeadCharacters.removeAllItems();
		for (int i = 0; i < ((game==null)?singleplayerGame:game).getCurrentPlayer().getDeadCharacters().size(); i++) {
			DeadCharacters.addItem(((game==null)?singleplayerGame:game).getCurrentPlayer().getDeadCharacters().get(i));
		}
		currentplayer.add(DeadCharacters, BorderLayout.EAST);
		DeadCharacters.addItemListener(this);
		DeadCharacters.setBackground(new Color(98, 134, 168));
		DeadCharacters.setBorder(new LineBorder(Color.gray));
		DeadCharacters.setVisible(true);
		revalidate();

	}

	private void addDirections() {
		for (int i = 0; i < 9; i++) {
			JButton b = new JButton();
			b.addActionListener(this);
			Directions.add(b);
			d.add(b);
		}
		ArrayList<JButton> temp = new ArrayList<>();
		Font t = new Font("Serif", Font.BOLD, 0);
		d.get(0).setText("UpLeft");
		d.get(0).setFont(t);
		d.get(0).setForeground(new Color(255, 255, 255, 0));
		d.get(0).setIcon(new ImageIcon(getDir() + "/upleft.png"));
		temp.add(d.remove(0));
		d.get(0).setText("Up");
		d.get(0).setFont(t);
		d.get(0).setForeground(new Color(255, 255, 255, 0));
		d.get(0).setIcon(new ImageIcon(getDir() + "/up.png"));
		temp.add(d.remove(0));
		d.get(0).setText("UpRight");
		d.get(0).setFont(t);
		d.get(0).setForeground(new Color(255, 255, 255, 0));
		d.get(0).setIcon(new ImageIcon(getDir() + "/upright.png"));
		temp.add(d.remove(0));
		d.get(0).setText("Left");
		d.get(0).setFont(t);
		d.get(0).setForeground(new Color(255, 255, 255, 0));
		d.get(0).setIcon(new ImageIcon(getDir() + "/left.png"));
		temp.add(d.remove(0));
		d.get(0).setText("Move");
		temp.add(d.remove(0));
		d.get(0).setText("Right");
		d.get(0).setFont(t);
		d.get(0).setForeground(new Color(255, 255, 255, 0));
		d.get(0).setIcon(new ImageIcon(getDir() + "/right.png"));
		temp.add(d.remove(0));
		d.get(0).setText("DownLeft");
		d.get(0).setFont(t);
		d.get(0).setForeground(new Color(255, 255, 255, 0));
		d.get(0).setIcon(new ImageIcon(getDir() + "/downleft.png"));
		temp.add(d.remove(0));
		d.get(0).setText("Down");
		d.get(0).setFont(t);
		d.get(0).setForeground(new Color(255, 255, 255, 0));
		d.get(0).setIcon(new ImageIcon(getDir() + "/down.png"));
		temp.add(d.remove(0));
		d.get(0).setText("DownRight");
		d.get(0).setFont(t);
		d.get(0).setForeground(new Color(255, 255, 255, 0));
		d.get(0).setIcon(new ImageIcon(getDir() + "/downright.png"));
		temp.add(d.remove(0));
		while (!temp.isEmpty()) {
			temp.get(0).setBackground(new Color(98, 134, 168));
			temp.get(0).setBorder(new LineBorder(Color.gray));
			d.add(temp.remove(0));
		}
		revalidate();
	}

	private void showGame() {
		boardcells.setLayout(new GridLayout(((game==null)?singleplayerGame:game).getBoardHeight(), ((game==null)?singleplayerGame:game).getBoardWidth()));
		boardcells.setBounds(600, 600, 650, 600);
		pieces = new ArrayList<>();
		bc = new ArrayList<>();
		locations = new ArrayList<>();
		for (int i = 0; i < ((game==null)?singleplayerGame:game).getBoardHeight(); i++) {
			for (int j = 0; j < ((game==null)?singleplayerGame:game).getBoardWidth(); j++) {
				JButton b = new JButton();
				b.setSize(40, 40);
				b.setBounds(600, 600, 40, 40);
				b.setOpaque(false);
				b.setContentAreaFilled(false);
				b.addMouseListener(this);
				boardcells.add(b);
				this.bc.add(b);
				pieces.add(((game==null)?singleplayerGame:game).getCellAt(i, j).getPiece());
				b.setLocation(i, j);
				locations.add(b.getLocation());
				addIcon(b);

			}
		}
		add(boardcells, BorderLayout.CENTER);
		// player1P=" "+game.getCellAt(5, 0).getPiece().getName()+" ";
		// player2P=" "+game.getCellAt(1, 0).getPiece().getName()+" ";
		revalidate();

	}

	private void addIcon(JButton b) {
		int index = bc.indexOf(b);
		Piece piece = pieces.get(index);
		if (piece != null) {
			String name = piece.getName();
			switch (name) {
			case "The Mountain":
				b.setIcon(new ImageIcon(getDir() + "/mountain.png"));
				break;
			case "Jon Snow":
				b.setIcon(new ImageIcon(getDir() + "/jonsnow.png"));
				break;
			case "Eleven":
				b.setIcon(new ImageIcon(getDir() + "/eleven.png"));
				break;
			case "Night King":
				b.setIcon(new ImageIcon(getDir() + "/nightking.png"));
				break;
			case "Pickle Rick":
				b.setIcon(new ImageIcon(getDir() + "/picklerick.png"));
				break;
			case "Demogorgon":
				b.setIcon(new ImageIcon(getDir() + "/demogorgon.png"));
				break;
			case "Jesse Pinkman":
				b.setIcon(new ImageIcon(getDir() + "/jessepinkman.png"));
				break;
			case "Scary Terry":
				b.setIcon(new ImageIcon(getDir() + "/scaryterry.png"));
				break;
			case "Dr Hannibal Lecter":
				b.setIcon(new ImageIcon(getDir() + "/hannibal.png"));
				break;
			case "Heisenberg":
				b.setIcon(new ImageIcon(getDir() + "/heisenberg.png"));
				break;
			case "Elliot Alderson":
				b.setIcon(new ImageIcon(getDir() + "/elliot.png"));
				break;
			case "Mr Robot":
				b.setIcon(new ImageIcon(getDir() + "/mrrobot.png"));
				break;
			case "Morty":
				b.setIcon(new ImageIcon(getDir() + "/morty.png"));
				break;
			case "Unsullied":
				b.setIcon(new ImageIcon(getDir() + "/unsullied.png"));
				break;
			}
		} else
			b.setIcon(null);

	}

	public void updatePayLoad1(JProgressBar pl) {
		pl.setValue(((game==null)?singleplayerGame:game).getPlayer1().getPayloadPos());
		revalidate();
	}

	public void updatePayLoad2(JProgressBar pl) {
		pl.setValue(((game==null)?singleplayerGame:game).getPlayer2().getPayloadPos());
		revalidate();
	}

	@SuppressWarnings("finally")
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton j = (JButton) e.getSource();
		switch (j.getText()) {
		case "Use Power":
			if (currentPiece instanceof Medic) {
				Medic p = (Medic) (currentPiece);
				try {
					p.usePower(dir, medicTarget, null);
				} catch (InvalidPowerUseException e1) {
					JOptionPane.showMessageDialog(null, "Invalid use of power", "Exception", JOptionPane.ERROR_MESSAGE);
				} catch (WrongTurnException e1) {
					JOptionPane.showMessageDialog(null, "Wrong turn", "Exception", JOptionPane.ERROR_MESSAGE);
				} finally {
					currentPiece = null;
					medicTarget = null;
					newPoint = null;
					updateboard();
					break;
				}
			} else if (currentPiece instanceof ActivatablePowerHero) {
				ActivatablePowerHero p = (ActivatablePowerHero) (currentPiece);
				try {
					p.usePower(dir, target, newPoint);
				} catch (InvalidPowerUseException e1) {
					JOptionPane.showMessageDialog(null, "Invalid use of power", "Exception", JOptionPane.ERROR_MESSAGE);
				} catch (WrongTurnException e1) {
					JOptionPane.showMessageDialog(null, "Wrong turn", "Exception", JOptionPane.ERROR_MESSAGE);
				} finally {
					currentPiece = null;
					target = null;
					newPoint = null;
					updateboard();
					break;
				}
			}
		case "undo":
			currentPiece = null;
			target = null;
			newPoint = null;
			updateboard();
			break;
		/*
		 * case "swap": String temp=player1P; player1P=player2P; player2P=temp;
		 * updateboard(); break;
		 */
		case "Move":
			try {
				currentPiece.move(dir);
				currentPiece = null;
				target = null;
			} catch (UnallowedMovementException e1) {
				JOptionPane.showMessageDialog(null, "Unallowed move", "Exception", JOptionPane.ERROR_MESSAGE);
			} catch (OccupiedCellException e1) {
				JOptionPane.showMessageDialog(null, "Occupied cell", "Exception", JOptionPane.ERROR_MESSAGE);
			} catch (WrongTurnException e1) {
				JOptionPane.showMessageDialog(null, "Wrong turn", "Exception", JOptionPane.ERROR_MESSAGE);
			} finally {
				currentPiece = null;
				target = null;
				dir = null;
				updateboard();
				break;
			}

		case "Up":
			dir = Direction.UP;
			break;
		case "Down":
			dir = Direction.DOWN;
			break;
		case "Left":
			dir = Direction.LEFT;
			break;
		case "Right":
			dir = Direction.RIGHT;
			break;
		case "UpLeft":
			dir = Direction.UPLEFT;
			break;
		case "UpRight":
			dir = Direction.UPRIGHT;
			break;
		case "DownLeft":
			dir = Direction.DOWNLEFT;
			break;
		case "DownRight":
			dir = Direction.DOWNRIGHT;
			break;
		}
		updateboard();
		revalidate();
		

	}

	private void updateboard() {
		pieces = new ArrayList<>();
		for (int i = 0; i < ((game==null)?singleplayerGame:game).getBoardHeight(); i++) {
			for (int j = 0; j < ((game==null)?singleplayerGame:game).getBoardWidth(); j++) {
				pieces.add(((game==null)?singleplayerGame:game).getCellAt(i, j).getPiece());
			}
		}
		for (int i = 0; i < bc.size(); i++) {
			addIcon(bc.get(i));
		}
		currentLabel.setText("Current Player: " + ((game==null)?singleplayerGame:game).getCurrentPlayer().getName());
		// player1hero.setText(player1P);
		// player2hero.setText(player2P);
		updatePayLoad1(payload1);
		updatePayLoad2(payload2);
		updateSelectedItem();
		updateDeadCharacters();
		endGame();
		revalidate();

	}

	private void updateSelectedItem() {
		String p = "";
		String t = "";
		String n = "";
		if (currentPiece != null)
			p = currentPiece.getName();
		if (target != null)
			t = target.getName();
		if (newPoint != null) {
			n = "" + newPoint.getLocation();
			n = n.substring(14, n.length());
		}
		selectedItems.setText(
				"<html> current piece: " + p + "<br>" + "target: " + t + "<br>" + "Teleport Position:" + n + "</html>");
		revalidate();
	}

	public static void main(String[] args) {
		startGame();
	}

	private String details(Piece piece) {
		if (piece != null) {
			String s = "";
			String name = piece.getName();
			switch (name) {
			case "The Mountain":
				s = "Some dead man";
				break;
			case "Jon Snow":
				s = "My watch has ended";
				break;
			case "Eleven":
				s = "Friends don't lie";
				break;
			case "Night King":
				s = "....";
				break;
			case "Pickle Rick":
				s = "*Burps*";
				break;
			case "Demogorgon":
				s = "*SCREAMS*";
				break;
			case "Jesse Pinkman":
				s = "This is my own private domicile and I will not be harassed";
				break;
			case "Scary Terry":
				s = "You can run but you can't hide";
				break;
			case "Dr Hannibal Lecter":
				s = "Nothing here is vegetarian";
				break;
			case "Heisenberg":
				s = "This is not meth";
				break;
			case "Elliot Alderson":
				s = "Hello friend";
				break;
			case "Mr Robot":
				s = "I am the architect";
				break;
			case "Morty":
				s = "Aw Jeez!";
				break;
			case "Unsullied":
				s = "Unsullied fear nothing";
				break;
			}
			if (piece instanceof SideKick) {
				return s = "<html>" + "Owner: " + piece.getOwner().getName() + "<br>" + "Name: " + piece.getName()
						+ "<br>" + "This piece has no activatable power" + "<br>" + "Type: "
						+ piece.getClass().getName().substring(23, piece.getClass().getName().length() - 2) + "<br>" + s
						+ "</html>";
			} else if (piece instanceof Speedster) {
				return s = "<html>" + "Owner: " + piece.getOwner().getName() + "<br>" + "Name: " + piece.getName()
						+ "<br>" + "This piece has no activatable power" + "<br>" + "Type: "
						+ piece.getClass().getName().substring(20, piece.getClass().getName().length()) + "<br>" + s
						+ "</html>";
			} else if (piece instanceof Armored) {
				return s = "<html>" + "Owner: " + piece.getOwner().getName() + "<br>" + "Name: " + piece.getName()
						+ "<br>" + "Armored Up: " + ((Armored) piece).isArmorUp() + "<br>" + "Type: "
						+ piece.getClass().getName().substring(20, piece.getClass().getName().length()) + "<br>" + s
						+ "</html>";
			} else {
				return s = "<html>" + "Owner: " + piece.getOwner().getName() + "<br>" + "Name: " + piece.getName()
						+ "<br>" + "Power Used: " + ((ActivatablePowerHero) piece).isPowerUsed() + "<br>" + "Type: "
						+ piece.getClass().getName().substring(20, piece.getClass().getName().length()) + "<br>" + s
						+ "</html>";
			}

		}
		return "No info to show";
	}

	private void endGame() {
		if (((game==null)?singleplayerGame:game).getCurrentPlayer() == ((game==null)?singleplayerGame:game).getPlayer1()) {
			if (((game==null)?singleplayerGame:game).getPlayer2().getPayloadPos() == ((game==null)?singleplayerGame:game).getPayloadPosTarget()) {
				JOptionPane.showMessageDialog(null, ((game==null)?singleplayerGame:game).getPlayer2().getName() + " Wins!");
				System.exit(0);
			}
		} else {
			if (((game==null)?singleplayerGame:game).getPlayer1().getPayloadPos() == ((game==null)?singleplayerGame:game).getPayloadPosTarget()) {
				JOptionPane.showMessageDialog(null, ((game==null)?singleplayerGame:game).getPlayer1().getName() + " Wins!");
				System.exit(0);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			JButton j = (JButton) e.getSource();
			int index = bc.indexOf(j);
			Piece piece = pieces.get(index);
			if (piece != null) {
				if (currentPiece == null) {
					int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to choose this piece?", "",
							JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION) {
						currentPiece = piece;
					}

				} else {
					int reply1 = JOptionPane.showConfirmDialog(null, "Are you sure you want to choose this target?", "",
							JOptionPane.YES_NO_OPTION);
					if (reply1 == JOptionPane.YES_OPTION) {
						target = piece;

					}
				}
			} else {
				int reply1 = JOptionPane.showConfirmDialog(null, "Are you sure you want to choose this point?", "",
						JOptionPane.YES_NO_OPTION);
				if (reply1 == JOptionPane.YES_OPTION)
					newPoint = locations.get(index);
			}
			updateboard();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JButton j = (JButton) e.getSource();
		int index = bc.indexOf(j);
		Piece piece = pieces.get(index);
		String s = details(piece);
		j.setToolTipText(s);

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			int index = DeadCharacters.getSelectedIndex();
			medicTarget = ((game==null)?singleplayerGame:game).getCurrentPlayer().getDeadCharacters().get(index);
		}

	}

	public class Background extends JPanel {
		Image bg = new ImageIcon(getDir() + "/background14.png").getImage();

		public void paintComponent(Graphics g) {
			int x = (this.getWidth() - bg.getWidth(null)) / 2;
			int y = (this.getHeight() - bg.getHeight(null)) / 2;
			g.drawImage(bg, x, y, null);
		}
	}

}
