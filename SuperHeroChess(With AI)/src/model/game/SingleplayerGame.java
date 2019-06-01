package model.game;

import java.awt.Point;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

import exceptions.*;
import model.pieces.Piece;
import model.pieces.heroes.ActivatablePowerHero;
import model.pieces.heroes.Armored;
import model.pieces.heroes.Medic;
import model.pieces.heroes.Ranged;
import model.pieces.heroes.Speedster;
import model.pieces.heroes.Super;
import model.pieces.heroes.Tech;
import model.pieces.sidekicks.SideKick;
import model.pieces.sidekicks.SideKickP1;
import model.pieces.sidekicks.SideKickP2;

public class SingleplayerGame extends Game {
	// PriorityQueue<Piece> piecePrioritized;
	static int helpme;

	public SingleplayerGame(Player player1, Computer player2) {
		super(player1, player2);
		helpme=0;

	}

	/*
	 * public PriorityQueue<Piece> getPiecePrioritized() { return
	 * piecePrioritized; }
	 */

	@Override
	public void switchTurns() {
		boolean end=checkWinner();
		setCurrentPlayer((getCurrentPlayer() == getPlayer1()) ? getPlayer2() : getPlayer1());
		// updatePriority();
		while (getCurrentPlayer().equals(getPlayer2())&&!end) {
			System.out.println("play Turn");
			playTurn();
			System.out.println("turn Played");
		}
	}

	public void playTurn() {
		ArrayList<Piece> z = getPossiblePieces();
		Collections.shuffle(z);
		PriorityQueue<Piece> pieces = finalPriority(z);
		System.out.println(""+pieces);
		while (!(pieces.isEmpty()) && getCurrentPlayer().equals(getPlayer2())) {
			Piece piece = (Piece) pieces.remove();
			System.out.println("choose "+piece.getName()+ helpme++);
			int whatToDo = 0;
			// if (canMove(piece)) {
			if (piece instanceof ActivatablePowerHero) {
				if (((ActivatablePowerHero) piece).isPowerUsed() == false){
					whatToDo = (int) (Math.random() * 2);
					System.out.println("choose activatable hero"+ helpme++);
					}
				else{
					whatToDo = 0;
					System.out.println("choose activatable hero"+ helpme++);
				}
			} else {
				whatToDo = 0;
				System.out.println("choose non-activatable hero"+ helpme++);
			}
			/*
			 * } else { if (piece instanceof ActivatablePowerHero) { if
			 * (((ActivatablePowerHero) piece).isPowerUsed() == false) whatToDo
			 * = 1; } else whatToDo = 2; }
			 */
			if (whatToDo == 0) {
				System.out.println("choose move"+ helpme++);
				MoveComputer(piece);
				System.out.println("move successful"+ helpme++);
				return;
			} else {// if (whatToDo == 1) {
				System.out.println("choose power"+ helpme++);
				UsePowerComputer((ActivatablePowerHero) piece);
				System.out.println("power successful"+ helpme++);
				return;
			} /*
				 * else { int a = (int) Math.random() * (z.size() - 2); Piece
				 * pic = (Piece) z.get(a); MoveComputer(pic); }
				 */
		}
		/*
		 * if (pieces.isEmpty()) { int a = (int) Math.random() * (z.size() - 2);
		 * Piece piece = (Piece) z.get(a); int whatToDo; if (piece instanceof
		 * ActivatablePowerHero) { if (((ActivatablePowerHero)
		 * piece).isPowerUsed() == false) whatToDo = (int) (Math.random() * 2);
		 * else whatToDo = 0; } else { whatToDo = 0; } if (whatToDo == 0) {
		 * MoveComputer(piece); } else { UsePowerComputer((ActivatablePowerHero)
		 * piece); } }
		 */
	}

	public void MoveComputer(Piece piece) {
		int i = 0;
		ArrayList<Direction> directions = getPossibleMovementDirections(piece);
		Direction direction = canAttackDirection(piece);
		PriorityQueue<Piece> queue = finalPriority(getPossiblePieces());
		while (direction == null) {
			System.out.println("no possible directions"+ helpme++);
			piece = queue.poll();
			System.out.println("new piece"+piece.getName()+ helpme++);
			directions = getPossibleMovementDirections(piece);
			direction = canAttackDirection(piece);
		}
		try {
			piece.move(direction);
		} catch (UnallowedMovementException e) {
			Collections.shuffle(directions);
			direction = directions.get((++i) % directions.size());
		} catch (OccupiedCellException e) {
			Collections.shuffle(directions);
			direction = directions.get((++i) % directions.size());
		} catch (WrongTurnException e) {
			return;
		} catch (NullPointerException e) {
			Collections.shuffle(directions);
			direction = directions.get((++i) % directions.size());
		}
		return;
	}

	public void UsePowerComputer(ActivatablePowerHero piece) {
		ArrayList<Direction> directions = usePowerDirection(piece);
		Collections.shuffle(directions);
		if (piece instanceof Ranged) {
			System.out.println("choose Ranged"+ helpme++);
			boolean possible=false;
			int a=piece.getPosI();
			int b=piece.getPosJ();
			for (Direction d : directions) {
				Piece hit = null;
				if (d == Direction.RIGHT) {
					for (int j = b + 1; j < getBoardWidth(); j++) {
						hit = getCellAt(a, j).getPiece();
						if (hit != null) {
							break;
						}
					}
				} else if (d == Direction.LEFT) {
					for (int j = b - 1; j >= 0; j--) {
						hit = getCellAt(a, j).getPiece();
						if (hit != null) {
							break;
						}
					}
				} else if (d == Direction.UP) {
					for (int i = a - 1; i >= 0; i--) {
						hit = getCellAt(i, b).getPiece();
						if (hit != null) {
							break;
						}
					}
				} else if (d == Direction.DOWN) {
					for (int i = a + 1; i < getBoardHeight(); i++) {
						hit = getCellAt(i, b).getPiece();
						if (hit != null) {
							break;
						}
					}
				}
				if (hit != null) {
					if (hit.getOwner() != getPlayer2()) {
						try {
							piece.usePower(d, null, null);
						} catch (InvalidPowerUseException | WrongTurnException e) {
							continue;
						}
						possible=true;
						break;

					}
				}
			}
			if(possible==false){
				MoveComputer(piece);
			}
		} else if (piece instanceof Super) {
			System.out.println("choose super power"+ helpme++);
			int a=piece.getPosI();
			int b=piece.getPosJ();
			Point c=new Point(a,b);
			ArrayList<Direction> superTarget=new ArrayList<Direction>();
			for(Direction direc:directions){
				Point d=getDestination(direc, c);
				int f=(int)d.getX();
				int g=(int)d.getY();
				Point h=getDestinationSpeedster(direc, c);
				int i=(int)h.getX();
				int j=(int)h.getY();
				if((getCellAt(f, g).getPiece()!=null&&getCellAt(f, g).getPiece().getOwner()==getPlayer1())||(getCellAt(i, j).getPiece()!=null&&getCellAt(i, j).getPiece().getOwner()==getPlayer1())){
					superTarget.add(direc);
				}
			}if(superTarget.size()==0){
				MoveComputer(piece);
				return;
			}
			
			for (Direction dir : superTarget) {
				try {
					piece.usePower(dir, null, null);
					break;
				} catch (InvalidPowerUseException e) {
					continue;
				} catch (WrongTurnException e) {
					continue;
				}
			}
		} else if (piece instanceof Tech) {
			System.out.println("choose tech"+ helpme++);
			int whichPower = (int) (Math.random() * 3);
			// teleport=0,hack=1,restore=2
			if (whichPower == 0) {
				System.out.println("choose tech power teleport"+ helpme++);
				Piece target = getPossiblePieces().get(0);
				Point newPos = bestPoint(target);
				try {
					piece.usePower(null, target, newPos);
				} catch (InvalidPowerUseException e) {
					return;
				} catch (WrongTurnException e) {
					return;
				} catch (NullPointerException e) {
					return;
				}

			} else if (whichPower == 1) {
				System.out.println("choose tech power hack"+ helpme++);
				ArrayList<Piece> hackable=getPossibleHacks();
				for (Piece pic : hackable) {
					try {
						piece.usePower(null, pic, null);
						break;
					} catch (InvalidPowerUseException | WrongTurnException e) {
						continue;
					} catch (NullPointerException e) {
						return;
					}
				}
			} else if (whichPower == 2) {
				System.out.println("choose tech power restore"+ helpme++);
				ArrayList<Piece> restorable = getPossibleRestore();
				for(Piece target:restorable) {
					try {
						piece.usePower(null, target, null);
						break;
					} catch (InvalidPowerUseException | WrongTurnException e) {
						continue;
					} catch (NullPointerException e) {
						return;
					}
				}
			}
		} else if (piece instanceof Medic) {
			System.out.println("choose medic"+ helpme++);
			ArrayList<Direction> safeRevive = new ArrayList<Direction>();
			ArrayList<Direction> gettingDir=getDirections();
			int c=piece.getPosI();
			int d=piece.getPosJ();
			Point p=new Point(c,d);
			for (Direction safedir : gettingDir) {
				Point q=getDestination(safedir, p);
				int g= (int)q.getX();
				int h= (int)q.getY();
				if (isSafe(q)&&getCellAt(g, h).getPiece()==null)
					safeRevive.add(safedir);
			}
			if (safeRevive.size() == 0) {
				gettingDir = new ArrayList<Direction>(getDirections());
				for (Direction safedir : gettingDir) {
					Point q=getDestination(safedir, p);
					int g= (int)q.getX();
					int h= (int)q.getY();
					if (getCellAt(g, h).getPiece()==null)
						safeRevive.add(safedir);
				}
			}
			boolean containArmored = false;
			boolean containRanged = false;
			boolean containSuper = false;
			boolean containMedic = false;
			boolean containTech = false;
			boolean containSpeedster = false;
			boolean containSidekick = false;
			for (int i = 0; i < getPlayer2().getDeadCharacters().size(); i++) {
				if (getPlayer2().getDeadCharacters().get(i) instanceof Armored)
					containArmored = true;
				if (getPlayer2().getDeadCharacters().get(i) instanceof Ranged)
					containRanged = true;
				if (getPlayer2().getDeadCharacters().get(i) instanceof Super)
					containSuper = true;
				if (getPlayer2().getDeadCharacters().get(i) instanceof Medic)
					containMedic = true;
				if (getPlayer2().getDeadCharacters().get(i) instanceof Tech)
					containTech = true;
				if (getPlayer2().getDeadCharacters().get(i) instanceof Speedster)
					containSpeedster = true;
				if (getPlayer2().getDeadCharacters().get(i) instanceof SideKick)
					containSidekick = true;
			}
			for (int i = 0; i < getPlayer2().getDeadCharacters().size() && containArmored; i++) {
				if (getPlayer2().getDeadCharacters().get(i) instanceof Armored) {
					try {
						piece.usePower(safeRevive.get(0), getPlayer2().getDeadCharacters().get(i), null);
						break;
					} catch (InvalidPowerUseException e) {
						continue;
					} catch (WrongTurnException e) {
						continue;
					}
				}

			}
			for (int i = 0; i < getPlayer2().getDeadCharacters().size() && containRanged; i++) {
				if (getPlayer2().getDeadCharacters().get(i) instanceof Ranged) {
					try {
						piece.usePower(safeRevive.get(0), getPlayer2().getDeadCharacters().get(i), null);
						break;
					} catch (InvalidPowerUseException e) {
						continue;
					} catch (WrongTurnException e) {
						continue;
					}
				}
			}
			for (int i = 0; i < getPlayer2().getDeadCharacters().size() && containSuper; i++) {
				if (getPlayer2().getDeadCharacters().get(i) instanceof Super) {
					try {
						piece.usePower(safeRevive.get(0), getPlayer2().getDeadCharacters().get(i), null);
						break;
					} catch (InvalidPowerUseException e) {
						continue;
					} catch (WrongTurnException e) {
						continue;
					}
				}
			}
			for (int i = 0; i < getPlayer2().getDeadCharacters().size() && containMedic; i++) {
				if (getPlayer2().getDeadCharacters().get(i) instanceof Medic) {
					try {
						piece.usePower(safeRevive.get(0), getPlayer2().getDeadCharacters().get(i), null);
						break;
					} catch (InvalidPowerUseException e) {
						continue;
					} catch (WrongTurnException e) {
						continue;
					}
				}
			}
			for (int i = 0; i < getPlayer2().getDeadCharacters().size() && containTech; i++) {
				if (getPlayer2().getDeadCharacters().get(i) instanceof Tech) {
					try {
						piece.usePower(safeRevive.get(0), getPlayer2().getDeadCharacters().get(i), null);
						break;
					} catch (InvalidPowerUseException e) {
						continue;
					} catch (WrongTurnException e) {
						continue;
					}
				}
			}
			for (int i = 0; i < getPlayer2().getDeadCharacters().size() && containSpeedster; i++) {
				if (getPlayer2().getDeadCharacters().get(i) instanceof Speedster) {
					try {
						piece.usePower(safeRevive.get(0), getPlayer2().getDeadCharacters().get(i), null);
						break;
					} catch (InvalidPowerUseException e) {
						continue;
					} catch (WrongTurnException e) {
						continue;
					}
				}
			}
			for (int i = 0; i < getPlayer2().getDeadCharacters().size() && containSidekick; i++) {
				if (getPlayer2().getDeadCharacters().get(i) instanceof SideKick) {
					try {
						piece.usePower(safeRevive.get(0), getPlayer2().getDeadCharacters().get(i), null);
						break;
					} catch (InvalidPowerUseException e) {
						continue;
					} catch (WrongTurnException e) {
						continue;
					}
				}
			}

		}
		if (getCurrentPlayer().equals(getPlayer2()))
			System.out.println("power unavailable so move"+ helpme++);
			MoveComputer(piece);
	}

	private ArrayList<Piece> getPossibleRestore() {
		ArrayList<Piece> possibleTargets=getPossiblePieces();
		ArrayList<Piece> actualTargets=new ArrayList<Piece>();
		for(Piece piece:possibleTargets){
			if(piece instanceof Armored && (!((Armored)piece).isArmorUp())){
				actualTargets.add(piece);
			}
			else if(piece instanceof ActivatablePowerHero&&((ActivatablePowerHero)piece).isPowerUsed()){
				actualTargets.add(piece);
			}
		}
		return actualTargets;
	}

	private ArrayList<Piece> getPossibleHacks() {
		ArrayList<Piece> possibletargets=getEnemyPieces();
		ArrayList<Piece> actualTargets=new ArrayList<Piece>();
		for(Piece piece:possibletargets){
			if(piece instanceof Armored && ((Armored)piece).isArmorUp()){
				actualTargets.add(piece);
			}
			else if(piece instanceof ActivatablePowerHero&&(!((ActivatablePowerHero)piece).isPowerUsed())){
				actualTargets.add(piece);
			}
		}
		return actualTargets;
	}

	/*
	 * public ArrayList<Armored> getArmoredUP() { ArrayList<Armored> armored =
	 * new ArrayList<Armored>(); for (int i = 0; i < getBoardHeight(); i++) {
	 * for (int j = 0; j < getBoardWidth(); j++) { if (getCellAt(i,
	 * j).getPiece() != null) { if (getCellAt(i, j).getPiece().getOwner() !=
	 * getPlayer2()) { if (getCellAt(i, j).getPiece() instanceof Armored) { if
	 * (((Armored) getCellAt(i, j).getPiece()).isArmorUp()) {
	 * armored.add((Armored) getCellAt(i, j).getPiece()); } } } } } } return
	 * armored; }
	 */
	public ArrayList<Direction> getPossibleMovementDirections(Piece piece) {
		ArrayList<Direction> directions = new ArrayList<Direction>();
		if (piece instanceof SideKickP2) {
			directions.add(Direction.DOWN);
			directions.add(Direction.DOWNLEFT);
			directions.add(Direction.DOWNRIGHT);
			directions.add(Direction.LEFT);
			directions.add(Direction.RIGHT);
		} else if (piece instanceof Super || piece instanceof Medic) {
			directions.add(Direction.LEFT);
			directions.add(Direction.RIGHT);
			directions.add(Direction.UP);
			directions.add(Direction.DOWN);
		} else if (piece instanceof Ranged || piece instanceof Armored || piece instanceof Speedster) {
			directions = getDirections();
		} else if (piece instanceof Tech) {
			directions.add(Direction.UPLEFT);
			directions.add(Direction.UPRIGHT);
			directions.add(Direction.DOWNLEFT);
			directions.add(Direction.DOWNRIGHT);
		}
		return directions;
	}

	public ArrayList<Piece> getPossiblePieces() {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int i = 0; i < getBoardHeight(); i++) {
			for (int j = 0; j < getBoardWidth(); j++) {
				if (getCellAt(i, j).getPiece() != null) {
					if (getCellAt(i, j).getPiece().getOwner() == getPlayer2()) {
						pieces.add(getCellAt(i, j).getPiece());
					}
				}
			}
		}
		return pieces;
	}

	public ArrayList<Point> getPossiblePoints() {
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < getBoardHeight(); i++) {
			for (int j = 0; j < getBoardWidth(); j++) {
				if (getCellAt(i, j).getPiece() == null) {
					points.add(new Point(i, j));
				}
			}
		}
		return points;
	}

	public Point bestPoint(Piece piece) {
		ArrayList<Point> points = getPossiblePoints();
		Collections.shuffle(points);
		ArrayList<Direction> directions = getPossibleMovementDirections(piece);
		for (Point point : points) {
			int x = (int) point.getX();
			int y = (int) point.getY();
			if (!(piece instanceof Speedster)) {
				if (isSafe(point)) {
					for (Direction dir : directions) {
						switch (dir) {
						case UPRIGHT:
							if (getCellAt(x == 0 ? getBoardHeight() - 1 : x - 1, (y + 1) % getBoardWidth())
									.getPiece() != null
									&& getCellAt(x == 0 ? getBoardHeight() - 1 : x - 1, (y + 1) % getBoardWidth())
											.getPiece().getOwner() != getPlayer2()) {
								return point;
							}
						case UPLEFT:
							if (getCellAt(x == 0 ? getBoardHeight() - 1 : x - 1, y == 0 ? getBoardWidth() - 1 : y - 1)
									.getPiece() != null
									&& getCellAt(x == 0 ? getBoardHeight() - 1 : x - 1,
											y == 0 ? getBoardWidth() - 1 : y - 1).getPiece()
													.getOwner() != getPlayer2()) {
								return point;
							}
						case UP:
							if (getCellAt(x == 0 ? getBoardHeight() - 1 : x - 1, y).getPiece() != null
									&& getCellAt(x == 0 ? getBoardHeight() - 1 : x - 1, y).getPiece()
											.getOwner() != getPlayer2()) {
								return point;
							}
						case RIGHT:
							if (getCellAt(x, (y + 1) % getBoardWidth()).getPiece() != null
									&& getCellAt(x, (y + 1) % getBoardWidth()).getPiece().getOwner() != getPlayer2()) {
								return point;
							}
						case LEFT:
							if (getCellAt(x, y == 0 ? getBoardWidth() - 1 : y - 1).getPiece() != null
									&& getCellAt(x, y == 0 ? getBoardWidth() - 1 : y - 1).getPiece()
											.getOwner() != getPlayer2()) {
								return point;
							}
						case DOWN:
							if (getCellAt((x + 1) % getBoardHeight(), y).getPiece() != null
									&& getCellAt((x + 1) % getBoardHeight(), y).getPiece().getOwner() != getPlayer1()) {
								return point;
							}
						case DOWNRIGHT:
							if (getCellAt((x + 1) % getBoardHeight(), (y + 1) % getBoardWidth()).getPiece() != null
									&& getCellAt((x + 1) % getBoardHeight(), (y + 1) % getBoardWidth()).getPiece()
											.getOwner() != getPlayer2()) {
								return point;
							}
						case DOWNLEFT:
							if (getCellAt((x + 1) % getBoardHeight(), y == 0 ? getBoardWidth() - 1 : y - 1)
									.getPiece() != null
									&& getCellAt((x + 1) % getBoardHeight(), y == 0 ? getBoardWidth() - 1 : y - 1)
											.getPiece().getOwner() != getPlayer2()) {
								return point;
							}
						}
					}
				} else {
					if (isSafe(point)) {
						for (Direction dir : directions) {
							switch (dir) {
							case UPRIGHT:
								if (getCellAt(
										x >= 2 ? x - 2 : x == 1 ? getBoardHeight() - 1 : getBoardHeight() - 2, (y + 2)
												% getBoardWidth())
														.getPiece() != null
										&& getCellAt(
												x >= 2 ? x - 2 : x == 1 ? getBoardHeight() - 1 : getBoardHeight() - 2,
												(y + 2) % getBoardWidth()).getPiece().getOwner() != getPlayer2()) {
									return point;
								}
							case UPLEFT:
								if (getCellAt(x >= 2 ? x - 2 : x == 1 ? getBoardHeight() - 1 : getBoardHeight() - 2,
										y >= 2 ? y - 2 : y == 1 ? getBoardWidth() - 1 : getBoardWidth() - 2)
												.getPiece() != null
										&& getCellAt(
												x >= 2 ? x - 2 : x == 1 ? getBoardHeight() - 1 : getBoardHeight() - 2,
												y >= 2 ? y - 2 : y == 1 ? getBoardWidth() - 1 : getBoardWidth() - 2)
														.getPiece().getOwner() != getPlayer2()) {
									return point;
								}
							case UP:
								if (getCellAt(x >= 2 ? x - 2 : x == 1 ? getBoardHeight() - 1 : getBoardHeight() - 2, y)
										.getPiece() != null
										&& getCellAt(
												x >= 2 ? x - 2 : x == 1 ? getBoardHeight() - 1 : getBoardHeight() - 2,
												y).getPiece().getOwner() != getPlayer2()) {
									return point;
								}
							case RIGHT:
								if (getCellAt(x, (y + 2) % getBoardWidth()).getPiece() != null
										&& getCellAt(x, (y + 2) % getBoardWidth()).getPiece()
												.getOwner() != getPlayer2()) {
									return point;
								}
							case LEFT:
								if (getCellAt(x, y >= 2 ? y - 2 : y == 1 ? getBoardWidth() - 1 : getBoardWidth() - 2)
										.getPiece() != null
										&& getCellAt(x,
												y >= 2 ? y - 2 : y == 1 ? getBoardWidth() - 1 : getBoardWidth() - 2)
														.getPiece().getOwner() != getPlayer2()) {
									return point;
								}
							case DOWN:
								if (getCellAt((x + 2) % getBoardHeight(), y).getPiece() != null
										&& getCellAt((x + 2) % getBoardHeight(), y).getPiece()
												.getOwner() != getPlayer2()) {
									return point;
								}
							case DOWNRIGHT:
								if (getCellAt((x + 2) % getBoardHeight(), (y + 2) % getBoardWidth()).getPiece() != null
										&& getCellAt((x + 2) % getBoardHeight(), (y + 2) % getBoardWidth()).getPiece()
												.getOwner() != getPlayer2()) {
									return point;
								}
							case DOWNLEFT:
								if (getCellAt((x + 2) % getBoardHeight(),
										y >= 2 ? y - 2 : y == 1 ? getBoardWidth() - 1 : getBoardWidth() - 2)
												.getPiece() != null
										&& getCellAt((x + 2) % getBoardHeight(),
												y >= 2 ? y - 2 : y == 1 ? getBoardWidth() - 1 : getBoardWidth() - 2)
														.getPiece().getOwner() != getPlayer2()) {
									return point;
								}
							}
						}
					}
				}
			}
			for (Point pint : points) {
				if (isSafe(pint)) {
					return pint;
				}
			}
		}
		return points.get(0);
	}

	public boolean isGonnaDie(Piece piece) {
		int x = piece.getPosI();
		int y = piece.getPosJ();
		if (isSafe(new Point(x, y))) {
			return false;
		}
		return true;
	}

	/*
	 * public ArrayList<Piece> piecesMightDie() { ArrayList<Piece> pieces =
	 * getPossiblePieces(); ArrayList<Piece> piecesMightDie = new
	 * ArrayList<Piece>(); for (int i = 0; i < pieces.size(); i++) { if
	 * (isGonnaDie(pieces.get(i))) { piecesMightDie.add(pieces.get(i)); } }
	 * return piecesMightDie;
	 * 
	 * }
	 */

	/*
	 * public String getTypeOfPiece(Piece piece) { if (piece instanceof Medic)
	 * return "Medic"; if (piece instanceof Ranged) return "Ranged"; if (piece
	 * instanceof Super) return "Super"; if (piece instanceof Armored) return
	 * "Armored"; if (piece instanceof Speedster) return "Speedster"; if (piece
	 * instanceof Tech) return "Tech"; return "Sidekick"; }
	 */

	/*
	 * public ArrayList<Piece> piecesMightNotDie() { ArrayList<Piece> pieces =
	 * getPossiblePieces(); ArrayList<Piece> piecesMightNotDieOrKill = new
	 * ArrayList<Piece>(); for (int i = 0; i < pieces.size(); i++) { if
	 * (!(isGonnaDie(pieces.get(i)))) {
	 * piecesMightNotDieOrKill.add(pieces.get(i)); } } return
	 * piecesMightNotDieOrKill;
	 * 
	 * }
	 */

	public boolean isSafe(Point x) {
		int i = (int) x.getX();
		int j = (int) x.getY();
		if (i - 1 >= 0) {
			if (getCellAt(i - 1, j).getPiece() != null && (!(getCellAt(i - 1, j).getPiece() instanceof Tech))
					&& (!(getCellAt(i - 1, j).getPiece() instanceof SideKickP1))
					&& getCellAt(i - 1, j).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(i - 1, j).getPiece() instanceof Speedster))) {
				return false;
			}
		} else {
			if (getCellAt(getBoardHeight() - 2, j).getPiece() != null
					&& (!(getCellAt(getBoardHeight() - 2, j).getPiece() instanceof Tech))
					&& (!(getCellAt(getBoardHeight() - 2, j).getPiece() instanceof SideKickP1))
					&& getCellAt(getBoardHeight() - 2, j).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(getBoardHeight() - 2, j).getPiece() instanceof Speedster))) {
				return false;
			}
		}
		if (getCellAt((i + 1) % getBoardHeight(), j).getPiece() != null
				&& (!(getCellAt((i + 1) % getBoardHeight(), j).getPiece() instanceof Tech))
				&& getCellAt((i + 1) % getBoardHeight(), j).getPiece().getOwner() == getPlayer1()
				&& (!(getCellAt((i + 1) % getBoardHeight(), j).getPiece() instanceof Speedster))) {
			return false;
		}
		if (j - 1 >= 0) {
			if (getCellAt(i, j - 1).getPiece() != null && (!(getCellAt(i, j - 1).getPiece() instanceof Tech))
					&& getCellAt(i, j - 1).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(i, j - 1).getPiece() instanceof Speedster))) {
				return false;
			}
		} else {
			if (getCellAt(i, getBoardWidth() - 2).getPiece() != null
					&& (!(getCellAt(i, getBoardWidth() - 2).getPiece() instanceof Tech))
					&& getCellAt(i, getBoardWidth() - 2).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(i, getBoardWidth() - 2).getPiece() instanceof Speedster))) {
				return false;
			}
		}
		if (getCellAt(i, (j + 1) % getBoardWidth()).getPiece() != null
				&& (!(getCellAt(i, (j + 1) % getBoardWidth()).getPiece() instanceof Tech))
				&& getCellAt(i, (j + 1) % getBoardWidth()).getPiece().getOwner() == getPlayer1()
				&& (!(getCellAt(i, (j + 1) % getBoardWidth()).getPiece() instanceof Speedster))) {
			return false;
		}
		if (j - 1 >= 0 && i - 1 >= 0) {
			if (getCellAt(i - 1, j - 1).getPiece() != null
					&& (!(getCellAt(i - 1, j - 1).getPiece() instanceof SideKick))
					&& getCellAt(i - 1, j - 1).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(i - 1, j - 1).getPiece() instanceof Super))
					&& (!(getCellAt(i - 1, j - 1).getPiece() instanceof Medic))
					&& (!(getCellAt(i - 1, j - 1).getPiece() instanceof Speedster))) {
				return false;
			}
		} else if (j - 1 < 0) {

			if (getCellAt(i - 1, getBoardWidth() - 2).getPiece() != null
					&& (!(getCellAt(i - 1, getBoardWidth() - 2).getPiece() instanceof SideKick))
					&& getCellAt(i - 1, getBoardWidth() - 2).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(i - 1, getBoardWidth() - 2).getPiece() instanceof Super))
					&& (!(getCellAt(i - 1, getBoardWidth() - 2).getPiece() instanceof Medic))
					&& (!(getCellAt(i - 1, getBoardWidth() - 2).getPiece() instanceof Speedster))) {
				return false;
			}
		} else if (i - 1 < 0) {
			if (getCellAt(getBoardHeight() - 2, j - 1).getPiece() != null
					&& (!(getCellAt(getBoardHeight() - 2, j - 1).getPiece() instanceof SideKick))
					&& getCellAt(getBoardHeight() - 2, j - 1).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(getBoardHeight() - 2, j - 1).getPiece() instanceof Super))
					&& (!(getCellAt(getBoardHeight() - 2, j - 1).getPiece() instanceof Medic))
					&& (!(getCellAt(getBoardHeight() - 2, j - 1).getPiece() instanceof Speedster))) {
				return false;
			}
		} else {
			if (getCellAt(getBoardHeight() - 2, getBoardWidth() - 2).getPiece() != null
					&& (!(getCellAt(getBoardHeight() - 2, getBoardWidth() - 2).getPiece() instanceof SideKick))
					&& getCellAt(getBoardHeight() - 2, getBoardWidth() - 2).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(getBoardHeight() - 2, getBoardWidth() - 2).getPiece() instanceof Super))
					&& (!(getCellAt(getBoardHeight() - 2, getBoardWidth() - 2).getPiece() instanceof Medic))
					&& (!(getCellAt(getBoardHeight() - 2, getBoardWidth() - 2).getPiece() instanceof Speedster))) {
				return false;
			}
		}
		if (i - 1 >= 0) {
			if (getCellAt(i - 1, (j + 1) % getBoardWidth()).getPiece() != null
					&& (!(getCellAt(i - 1, (j + 1) % getBoardWidth()).getPiece() instanceof SideKick))
					&& getCellAt(i - 1, (j + 1) % getBoardWidth()).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(i - 1, (j + 1) % getBoardWidth()).getPiece() instanceof Super))
					&& (!(getCellAt(i - 1, (j + 1) % getBoardWidth()).getPiece() instanceof Medic))
					&& (!(getCellAt(i - 1, (j + 1) % getBoardWidth()).getPiece() instanceof Speedster))) {
				return false;
			}
		} else {
			if (getCellAt(getBoardHeight() - 2, (j + 1) % getBoardWidth()).getPiece() != null
					&& (!(getCellAt(getBoardHeight() - 2, (j + 1) % getBoardWidth()).getPiece() instanceof SideKick))
					&& getCellAt(getBoardHeight() - 2, (j + 1) % getBoardWidth()).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt(getBoardHeight() - 2, (j + 1) % getBoardWidth()).getPiece() instanceof Super))
					&& (!(getCellAt(getBoardHeight() - 2, (j + 1) % getBoardWidth()).getPiece() instanceof Medic))
					&& (!(getCellAt(getBoardHeight() - 2, (j + 1) % getBoardWidth())
							.getPiece() instanceof Speedster))) {
				return false;
			}
		}

		if (j - 1 >= 0) {
			if (getCellAt((i + 1) % getBoardHeight(), j - 1).getPiece() != null
					&& getCellAt((i + 1) % getBoardHeight(), j - 1).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt((i + 1) % getBoardHeight(), j - 1).getPiece() instanceof Super))
					&& (!(getCellAt((i + 1) % getBoardHeight(), j - 1).getPiece() instanceof Medic))
					&& (!(getCellAt((i + 1) % getBoardHeight(), j - 1).getPiece() instanceof Speedster))) {
				return false;
			}
		} else {
			if (getCellAt((i + 1) % getBoardHeight(), getBoardWidth() - 2).getPiece() != null
					&& getCellAt((i + 1) % getBoardHeight(), getBoardWidth() - 2).getPiece().getOwner() == getPlayer1()
					&& (!(getCellAt((i + 1) % getBoardHeight(), getBoardWidth() - 2).getPiece() instanceof Super))
					&& (!(getCellAt((i + 1) % getBoardHeight(), getBoardWidth() - 2).getPiece() instanceof Medic))
					&& (!(getCellAt((i + 1) % getBoardHeight(), getBoardWidth() - 2)
							.getPiece() instanceof Speedster))) {
				return false;
			}
		}
		if (getCellAt((i + 1) % getBoardHeight(), (j + 1) % getBoardWidth()).getPiece() != null
				&& getCellAt((i + 1) % getBoardHeight(), (j + 1) % getBoardWidth()).getPiece()
						.getOwner() == getPlayer1()
				&& (!(getCellAt((i + 1) % getBoardHeight(), (j + 1) % getBoardWidth()).getPiece() instanceof Super))
				&& (!(getCellAt((i + 1) % getBoardHeight(), (j + 1) % getBoardWidth()).getPiece() instanceof Medic))
				&& (!(getCellAt((i + 1) % getBoardHeight(), (j + 1) % getBoardWidth())
						.getPiece() instanceof Speedster))) {
			return false;
		}
		// SPEEDSTER
		if (i - 2 >= 0) {
			if (getCellAt(i - 2, j).getPiece() != null && getCellAt(i - 2, j).getPiece().getOwner() == getPlayer1()
					&& (getCellAt(i - 2, j).getPiece() instanceof Speedster)) {
				return false;
			}
		} else {
			if (i == 1) {
				if (getCellAt(6, j).getPiece() != null && getCellAt(6, j).getPiece().getOwner() == getPlayer1()
						&& (getCellAt(6, j).getPiece() instanceof Speedster)) {
					return false;
				}
			} else {
				if (getCellAt(5, j).getPiece() != null && getCellAt(5, j).getPiece().getOwner() == getPlayer1()
						&& (getCellAt(5, j).getPiece() instanceof Speedster)) {
					return false;
				}
			}
		}
		if (getCellAt((i + 2) % getBoardHeight(), j).getPiece() != null
				&& getCellAt((i + 2) % getBoardHeight(), j).getPiece().getOwner() == getPlayer1()
				&& (getCellAt((i + 2) % getBoardHeight(), j).getPiece() instanceof Speedster)) {
			return false;
		}
		if (getCellAt(i, (j + 2) % getBoardWidth()).getPiece() != null
				&& getCellAt(i, (j + 2) % getBoardWidth()).getPiece().getOwner() == getPlayer1()
				&& (getCellAt(i, (j + 2) % getBoardWidth()).getPiece() instanceof Speedster)) {
			return false;
		}
		if (j - 2 >= 0) {
			if (getCellAt(i, j - 2).getPiece() != null && getCellAt(i, j - 2).getPiece().getOwner() == getPlayer1()
					&& (getCellAt(i, j - 2).getPiece() instanceof Speedster)) {
				return false;
			}
		} else if (j == 1) {
			if (getCellAt(i, 5).getPiece() != null && getCellAt(i, 5).getPiece().getOwner() == getPlayer1()
					&& (getCellAt(i, 5).getPiece() instanceof Speedster)) {
				return false;
			}
		} else {
			if (getCellAt(i, 4).getPiece() != null && getCellAt(i, 4).getPiece().getOwner() == getPlayer1()
					&& (getCellAt(i, 4).getPiece() instanceof Speedster)) {
				return false;
			}
		}
		if (getCellAt((i + 2) % getBoardHeight(), (j + 2) % getBoardWidth()).getPiece() != null
				&& getCellAt((i + 2) % getBoardHeight(), (j + 2) % getBoardWidth()).getPiece()
						.getOwner() == getPlayer1()
				&& (getCellAt((i + 2) % getBoardHeight(), (j + 2) % getBoardWidth()).getPiece() instanceof Speedster)) {
			return false;
		}
		if (getCellAt(i == 1 ? 6 : i == 0 ? 5 : i - 2, j == 1 ? 5 : j == 0 ? 4 : j - 2).getPiece() != null
				&& getCellAt(i == 1 ? 6 : i == 0 ? 5 : i - 2, j == 1 ? 5 : j == 0 ? 4 : j - 2).getPiece()
						.getOwner() == getPlayer1()
				&& (getCellAt(i == 1 ? 6 : i == 0 ? 5 : i - 2, j == 1 ? 5 : j == 0 ? 4 : j - 2)
						.getPiece() instanceof Speedster)) {
			return false;
		}
		if (getCellAt(i == 1 ? 6 : i == 0 ? 5 : i - 2, (j + 2) % getBoardWidth()).getPiece() != null
				&& getCellAt(i == 1 ? 6 : i == 0 ? 5 : i - 2, (i + 2) % getBoardHeight()).getPiece()
						.getOwner() == getPlayer1()
				&& (getCellAt(i == 1 ? 6 : i == 0 ? 5 : i - 2, (j + 2) % getBoardWidth())
						.getPiece() instanceof Speedster)) {
			return false;
		}
		if (getCellAt((i + 2) % getBoardHeight(), j == 1 ? 5 : j == 0 ? 4 : j - 2).getPiece() != null
				&& getCellAt((i + 2) % getBoardHeight(), j == 1 ? 5 : j == 0 ? 4 : j - 2).getPiece()
						.getOwner() == getPlayer1()
				&& (getCellAt((i + 2) % getBoardHeight(), j == 1 ? 5 : j == 0 ? 4 : j - 2)
						.getPiece() instanceof Speedster)) {
			return false;
		}
		return true;
	}

	public Point getDestination(Direction d, Point pos) {
		Point p = new Point();
		p.x = pos.x;
		p.y = pos.y;

		switch (d) {

		case DOWN:
			p.x = ((p.x + 1) % getBoardHeight());
			break;

		case DOWNLEFT:
			p.x = ((p.x + 1) % getBoardHeight());
			if (p.y >= 1) {
				p.y--;
			} else {
				p.y = getBoardWidth() - 1;
			}
			break;

		case DOWNRIGHT:
			p.x = ((p.x + 1) % getBoardHeight());
			p.y = ((p.y + 1) % getBoardWidth());
			break;

		case LEFT:
			if (p.y >= 1) {
				p.y--;
			} else {
				p.y = getBoardWidth() - 1;
			}
			break;

		case RIGHT:
			p.y = ((p.y + 1) % getBoardWidth());
			break;

		case UP:
			if (p.x >= 1) {
				p.x--;
			} else {
				p.x = getBoardHeight() - 1;
			}
			break;

		case UPLEFT:
			if (p.x >= 1) {
				p.x--;
			} else {
				p.x = getBoardHeight() - 1;
			}
			if (p.y >= 1) {
				p.y--;
			} else {
				p.y = getBoardWidth() - 1;
			}
			break;

		case UPRIGHT:
			if (p.x >= 1) {
				p.x--;
			} else {
				p.x = getBoardHeight() - 1;
			}
			p.y = ((p.y + 1) % getBoardWidth());
			break;

		}

		return p;

	}

	public Point getDestinationSpeedster(Direction d, Point pos) {
		Point p = new Point();
		p.x = pos.x;
		p.y = pos.y;

		switch (d) {

		case DOWN:
			p.x = ((p.x + 2) % getBoardHeight());
			break;

		case DOWNLEFT:
			p.x = ((p.x + 2) % getBoardHeight());
			if (p.y >= 2) {
				p.y -= 2;
			} else if (p.y == 1) {
				p.y = getBoardWidth() - 1;
			} else {
				p.y = getBoardWidth() - 2;
			}
			break;

		case DOWNRIGHT:
			p.x = ((p.x + 1) % getBoardHeight());
			p.y = ((p.y + 1) % getBoardWidth());
			break;

		case LEFT:
			if (p.y >= 2) {
				p.y -= 2;
			} else if (p.y == 1) {
				p.y = getBoardWidth() - 1;
			} else {
				p.y = getBoardWidth() - 2;
			}
			break;

		case RIGHT:
			p.y = ((p.y + 1) % getBoardWidth());
			break;

		case UP:
			if (p.x >= 2) {
				p.x -= 2;
			} else if (p.x == 1) {
				p.x = getBoardHeight() - 1;
			} else {
				p.x = getBoardHeight() - 2;
			}
			break;

		case UPLEFT:
			if (p.x >= 2) {
				p.x -= 2;
			} else if (p.x == 1) {
				p.x = getBoardHeight() - 1;
			} else {
				p.x = getBoardHeight() - 2;
			}
			if (p.y >= 2) {
				p.y -= 2;
			} else if (p.y == 1) {
				p.y = getBoardWidth() - 1;
			} else {
				p.y = getBoardWidth() - 2;
			}
			break;

		case UPRIGHT:
			if (p.x >= 2) {
				p.x -= 2;
			} else if (p.x == 1) {
				p.x = getBoardHeight() - 1;
			} else {
				p.x = getBoardHeight() - 2;
			}
			p.y = ((p.y + 1) % getBoardWidth());
			break;

		}

		return p;
	}

	public ArrayList<Direction> usePowerDirection(ActivatablePowerHero piece) {
		if (piece instanceof Medic)
			return new ArrayList<Direction>(getDirections());
		else
			return getPossibleMovementDirections(piece);
	}

	private ArrayList<Direction> getDirections() {
		ArrayList<Direction> directions = new ArrayList<Direction>();
		directions.add(Direction.DOWN);
		directions.add(Direction.DOWNLEFT);
		directions.add(Direction.DOWNRIGHT);
		directions.add(Direction.RIGHT);
		directions.add(Direction.LEFT);
		directions.add(Direction.UP);
		directions.add(Direction.UPLEFT);
		directions.add(Direction.UPRIGHT);
		return directions;
	}

	public boolean canAttack(Piece piece) {
		ArrayList<Direction> directions = getPossibleMovementDirections(piece);
		int x = piece.getPosI();
		int y = piece.getPosJ();
		Point position = new Point(x, y);
		if (!(piece instanceof Speedster)) {
			for (Direction dir : directions) {
				Point newpos = (getDestination(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() != null
						&& getCellAt(newX, newY).getPiece().getOwner() != getPlayer2()) {
					return true;
				}
			}
		} else {
			for (Direction dir : directions) {
				Point newpos = (getDestinationSpeedster(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() != null
						&& getCellAt(newX, newY).getPiece().getOwner() != getPlayer2()) {
					return true;
				}
			}
		}
		return false;
	}

	public PriorityQueue<Piece> finalPriority(ArrayList<Piece> pieces) {
		PriorityQueue<Piece> x = new PriorityQueue<Piece>();
		for (Piece piece : pieces) {
			piece.setPriority(0);
			if (!(piece instanceof SideKick))
				piece.setPriority(piece.getPriority() + 1);
			if (isGonnaDie(piece))
				piece.setPriority(piece.getPriority() + 2);
			// if (canAttack(piece))
			// piece.setPriority(piece.getPriority() + 1);
			x.add(piece);
		}
		return x;
	}
	/*
	 * public void updatePriority(){ getPiecePrioritized().clear(); for (Piece
	 * pic:getPossiblePieces()){ getPiecePrioritized().add(pic);
	 * pic.setPriority(0); if (!(pic instanceof SideKick))
	 * pic.setPriority(pic.getPriority() + 1); if (isGonnaDie(pic))
	 * pic.setPriority(pic.getPriority() + 2);
	 * 
	 * getPiecePrioritized().add(pic); }
	 */
	/*
	 * for(Piece piece:getPiecePrioritized()){ piece.setPriority(0); if (!(piece
	 * instanceof SideKick)) piece.setPriority(piece.getPriority() + 1); if
	 * (isGonnaDie(piece)) piece.setPriority(piece.getPriority() + 2); //if
	 * (canAttack(piece)) // piece.setPriority(piece.getPriority() + 1); } }
	 */

	public Direction canAttackDirection(Piece piece) throws NullPointerException {
		ArrayList<Direction> directions = getPossibleMovementDirections(piece);
		Collections.shuffle(directions);
		int x = piece.getPosI();
		int y = piece.getPosJ();
		Point position = new Point(x, y);
		if (!(piece instanceof Speedster)) {
			for (Direction dir : directions) {
				Point newpos = (getDestination(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() != null
						&& getCellAt(newX, newY).getPiece().getOwner() != getPlayer2() && isSafe(newpos)) {
					return dir;
				}
			}
			for (Direction dir : directions) {
				Point newpos = (getDestination(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() != null
						&& getCellAt(newX, newY).getPiece().getOwner() != getPlayer2()) {
					return dir;
				}
			}
			for (Direction dir : directions) {
				Point newpos = (getDestination(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() == null && isSafe(newpos)) {
					return dir;
				}
			}
			for (Direction dir : directions) {
				Point newpos = (getDestination(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() == null) {
					return dir;
				}
			}
		} else {
			for (Direction dir : directions) {
				Point newpos = (getDestinationSpeedster(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() != null
						&& getCellAt(newX, newY).getPiece().getOwner() != getPlayer2() && isSafe(newpos)) {
					return dir;
				}
			}
			for (Direction dir : directions) {
				Point newpos = (getDestinationSpeedster(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() != null
						& getCellAt(newX, newY).getPiece().getOwner() != getPlayer2()) {
					return dir;
				}
			}
			for (Direction dir : directions) {
				Point newpos = (getDestinationSpeedster(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() == null & isSafe(newpos)) {
					return dir;
				}
			}
			for (Direction dir : directions) {
				Point newpos = (getDestinationSpeedster(dir, position));
				int newX = (int) newpos.getX();
				int newY = (int) newpos.getY();
				if (getCellAt(newX, newY).getPiece() == null) {
					return dir;
				}
			}
		}
		return null;
	}

	public ArrayList<Piece> getEnemyPieces() {
		ArrayList<Piece> pieces = new ArrayList<Piece>();
		for (int i = 0; i < getBoardHeight(); i++) {
			for (int j = 0; j < getBoardWidth(); j++) {
				if (getCellAt(i, j).getPiece() != null) {
					if (getCellAt(i, j).getPiece().getOwner() == getPlayer1()) {
						pieces.add(getCellAt(i, j).getPiece());
					}
				}
			}
		}
		return pieces;
	}

	/*
	 * public boolean canMove(Piece piece) { Point pos = new
	 * Point(piece.getPosI(), piece.getPosJ()); ArrayList<Direction> directions
	 * = getPossibleMovementDirections(piece); for (Direction dir : directions)
	 * { if (piece instanceof Speedster) { Point newPos =
	 * getDestinationSpeedster(dir, pos); int newX = (int) newPos.getX(); int
	 * newY = (int) newPos.getY(); if (getCellAt(newX, newY).getPiece() != null
	 * && getCellAt(newX, newY).getPiece().getOwner() != piece.getOwner()) {
	 * return true; } } else { Point newPos = getDestination(dir, pos); int newX
	 * = (int) newPos.getX(); int newY = (int) newPos.getY(); if
	 * (getCellAt(newX, newY).getPiece() != null && getCellAt(newX,
	 * newY).getPiece().getOwner() != piece.getOwner()) { return true; } } }
	 * return false; }
	 */

}
