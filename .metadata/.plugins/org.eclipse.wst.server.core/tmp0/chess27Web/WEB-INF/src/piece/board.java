//chris lopresti -cpl6- kyle myers -kwm38- chess project
package piece;

import java.util.HashMap;
import java.util.Map;

public class board {
	public final Map<Character, Integer> map;
	public String turn;
	public static piece[][] board = new piece[9][9];
	public int blackPieces;
	public int whitePieces;
	public boolean gameOver;
	public String winner;
	public boolean putInCheck;
	public boolean drawRequest;
	public boolean resign;
	public char promotionPiece;
	public piece blackKing;
	public piece whiteKing;
	public boolean checkMate;

	public static final String[][] displayBoard = { { "##", "  ", "##", "  ", "##", "  ", "##", "  " },
			{ "  ", "##", "  ", "##", "  ", "##", "  ", "##" }, { "##", "  ", "##", "  ", "##", "  ", "##", "  " },
			{ "  ", "##", "  ", "##", "  ", "##", "  ", "##" }, { "##", "  ", "##", "  ", "##", "  ", "##", "  " },
			{ "  ", "##", "  ", "##", "  ", "##", "  ", "##" }, { "##", "  ", "##", "  ", "##", "  ", "##", "  " },
			{ "  ", "##", "  ", "##", "  ", "##", "  ", "##" }

	};

	public static final String[] columns = { "", "a", "b", "c", "d", "e", "f", "g", "h" };
	public static final int[] rows = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };

	public board() {
		map = new HashMap<>();

		map.put('a', 1);
		map.put('b', 2);
		map.put('c', 3);
		map.put('d', 4);
		map.put('e', 5);
		map.put('f', 6);
		map.put('g', 7);
		map.put('h', 8);

		for (int i = 1; i < 9; i++) {
			pawn pawnb = new pawn(i, 7, "black", "bp", "pawn");
			board[7][i] = pawnb;
			pawn pawnw = new pawn(i, 2, "white", "wp", "pawn");
			board[2][i] = pawnw;
		}

		piece rookb1 = new rook(8, 8, "black", "bR", "rook");
		board[8][8] = rookb1;
		piece rookb2 = new rook(1, 8, "black", "bR", "rook");
		board[8][1] = rookb2;
		piece rookw1 = new rook(1, 1, "white", "wR", "rook");
		board[1][1] = rookw1;
		piece rookw2 = new rook(8, 1, "white", "wR", "rook");
		board[1][8] = rookw2;

		piece bishopb1 = new bishop(3, 8, "black", "bB", "bishop");
		board[8][3] = bishopb1;
		piece bishopb2 = new bishop(6, 8, "black", "bB", "bishop");
		board[8][6] = bishopb2;
		piece bishopw1 = new bishop(3, 1, "white", "wB", "bishop");
		board[1][3] = bishopw1;
		piece bishopw2 = new bishop(6, 1, "white", "wB", "bishop");
		board[1][6] = bishopw2;

		piece knightb1 = new knight(2, 8, "black", "bN", "knight");
		board[8][2] = knightb1;
		piece knightb2 = new knight(7, 8, "black", "bN", "knight");
		board[8][7] = knightb2;
		piece knightw1 = new knight(2, 1, "white", "wN", "knight");
		board[1][2] = knightw1;
		piece knightw2 = new knight(7, 1, "white", "wN", "knight");
		board[1][7] = knightw2;

		piece queenb = new queen(4, 8, "black", "bQ", "queen");
		board[8][4] = queenb;
		piece queenw = new queen(4, 1, "white", "wQ", "queen");
		board[1][4] = queenw;

		piece kingb = new king(5, 8, "black", "bK", "king");
		board[8][5] = kingb;
		piece kingw = new king(5, 1, "white", "wK", "king");
		board[1][5] = kingw;

		for (int i = 3; i < 7; i++) {
			for (int j = 1; j < 9; j++) {
				if (i % 2 != 0) {
					if (j % 2 == 0) {
						space space = new space(j, i, "", "  ", "space");

						board[i][j] = space;

					} else {

						hash hash = new hash(j, i, "", "##", "hash");

						board[i][j] = hash;

					}
				} else {

					if (j % 2 == 0) {

						hash hash = new hash(j, i, "", "##", "hash");

						board[i][j] = hash;

					} else {

						space space = new space(j, i, "", "  ", "space");

						board[i][j] = space;

					}

				}

			}

		}

		Enpassant enpassant = new Enpassant(0, 0, "", "false", "");

		board[0][0] = enpassant;
		this.blackPieces = 16;
		this.resign = false;
		this.whitePieces = 16;
		this.gameOver = false;
		this.promotionPiece = 'a';
		this.blackKing = board[8][5];
		this.whiteKing = board[1][5];
		this.turn = "White";
		this.checkMate = false;

		display();

	}

	public void display() {

		for (int i = 8; i > 0; i--) {

			for (int j = 1; j < 9; j++) {
				if (board[i][j] != null) {
					System.out.print(board[i][j].name + " ");
				}
			}
			System.out.print(rows[i] + "\n");
		}

		for (int j = 1; j < 9; j++) {

			System.out.print(" " + columns[j] + " ");
		}
		System.out.println("");
		System.out.println("");

		if (gameOver) {

			return;

		} else {

			if (putInCheck) {

				System.out.println("Check, " + turn + "'s" + " move:");

			} else {

				System.out.print(turn + "'s" + " move:");
			}

		}
	}// end of display

	public void validMove(String input) {

		try {

			// check for valid input

			// check if someone resigns

			if (input.equals("resign")) {

				this.resign = true;

				if (this.turn.equals("White")) {

					this.winner = "Black";
				} else {

					this.winner = "White";
				}

				this.gameOver = true;

				return;
			}

			// check if someone accepted the draw previously made or else remove
			// the draw
			// request

			if (drawRequest && input.equals("draw")) {

				this.gameOver = true;

				return;
			} else {

				this.drawRequest = false;
			}

			// input can not be length 6
			if (input.length() == 6) {

				System.out.println("Illegal move, try again");
				System.out.print(turn + "'s" + " move:");

				return;

			}

			// if input grater then 7 it must be 11
			if (input.length() > 7 && input.length() != 11) {

				System.out.println("Illegal move, try again");
				System.out.print(turn + "'s" + " move:");

				return;

			}

			// if length ==11 make sure it says "filerank filerank draw?"
			if (input.length() == 11) {
				if (input.charAt(2) == ' ' && input.charAt(5) == ' ' && input.substring(6).equals("draw?")) {

					this.drawRequest = true;

				} else {
					System.out.println("Illegal move, try again");
					System.out.print(turn + "'s" + " move:");

					return;
				}
			}

			// if input is 7 they must be promoting a pawn
			if (input.length() == 7) {
				if (input.charAt(2) == ' ' && input.charAt(5) == ' ') {

					if (input.charAt(6) == 'Q') {

						this.promotionPiece = 'Q';

					} else if (input.charAt(6) == 'R') {

						this.promotionPiece = 'R';

					} else if (input.charAt(6) == 'B') {

						this.promotionPiece = 'B';

					} else if (input.charAt(6) == 'N') {

						this.promotionPiece = 'N';

					} else {
						System.out.println("Illegal move, try again");
						System.out.print(turn + "'s" + " move:");

						return;
					}

				} else {
					System.out.println("Illegal move, try again");
					System.out.print(turn + "'s" + " move:");

					return;
				}
			}

			// if length is less then 5, check the format

			if (input.length() < 5 || input.charAt(2) != ' ') {

				System.out.println("Illegal move, try again");
				System.out.print(turn + "'s" + " move:");

				return;

			}

			// parse input for array indices
			int col1 = map.get(input.charAt(0));
			int col2 = map.get(input.charAt(3));
			int row1 = Character.getNumericValue(input.charAt(1));
			int row2 = Character.getNumericValue(input.charAt(4));

			// check bounds
			if (col1 < 1 || col1 > 8 || col2 < 1 || col2 > 8 || row1 < 1 || row1 > 8 || row2 < 1 || row2 > 8) {

				System.out.println("Illegal move, try again");
				System.out.print(turn + "'s" + " move:");

				return;

			}

			// make sure current space is not a space or hash

			if (board[row1][col1].name.equals("  ") || board[row1][col1].name.equals("##")) {

				System.out.println("Illegal move, try again");
				System.out.print(turn + "'s" + " move:");

				return;

			}

			// make sure the turn is correct
			if (!(turn.toLowerCase().equals(board[row1][col1].color))) {

				System.out.println("Illegal move, try again");
				System.out.print(turn + "'s" + " move:");

				return;

			}

			// check if desired space has piece of same color
			String colorCurrent = board[row1][col1].color;
			String colorDesired = board[row2][col2].color;

			if (colorCurrent.equals(colorDesired)) {

				System.out.println("Illegal move, try again");
				System.out.print(turn + "'s" + " move:");

				return;

			}

			// if promoting make sure the piece is a pawn, and make sure its
			// approaching the 8th or 1st row depending on color

			if (!(this.promotionPiece == 'a') && !(board[row1][col1].id.equals("pawn"))) {

				System.out.println("Illegal move, try again");
				System.out.print(turn + "'s" + " move:");
				this.promotionPiece = 'a';
				return;

			} else {

				if (!(this.promotionPiece == 'a')) {
					if (board[row1][col1].color.equals("white") && row2 != 8 && row1 != 7) {
						System.out.println("Illegal move, try again");
						System.out.print(turn + "'s" + " move:");
						this.promotionPiece = 'a';

						return;
					} else if (board[row1][col1].color.equals("black") && row2 != 1 && row1 != 2) {

						System.out.println("Illegal move, try again");
						System.out.print(turn + "'s" + " move:");
						this.promotionPiece = 'a';

						return;

					}
				}
			}

			// check if path is clear(if not a knight)

			if (!(board[row1][col1].name.equals("bN") || board[row1][col1].name.equals("wN"))) {

				// check vertical movement
				if (col1 == col2) {
					// check if no one is in front of you vertically
					if (row1 < row2) {
						for (int i = row1 + 1; i < row2; i++) {

							if (!(board[i][col1].name.equals("##") || board[i][col1].name.equals("  "))) {

								System.out.println("Illegal move, try again");
								System.out.print(turn + "'s" + " move:");

								return;

							}

						}
						// check if no one below you vertically

					} else {

						for (int i = row1 - 1; i > row2; i--) {

							if (!(board[i][col1].name.equals("##") || board[i][col1].name.equals("  "))) {

								System.out.println("Illegal move, try again");
								System.out.print(turn + "'s" + " move:");

								return;

							}

						}

					}
					// System.out.println("valid move");

				} else if (row1 == row2) {

					// check if no one is blocking lateral movement
					if (col1 > col2) { // check to the left
						for (int i = col1 - 1; i > col2; i--) {

							if (!(board[row1][i].name.equals("##") || board[row1][i].name.equals("  "))) {

								System.out.println("Illegal move, try again");
								System.out.print(turn + "'s" + " move:");

								return;

							}

						}

					} else {// check to the right
						for (int i = col1 + 1; i < col2; i++) {

							if (!(board[row1][i].name.equals("##") || board[row1][i].name.equals("  "))) {

								System.out.println("Illegal move, try again");
								System.out.print(turn + "'s" + " move:");

								return;

							}

						}

					}
					// System.out.println("valid move");
				}

				// check diagonal line

				// check down to the left diagonal
				if (row1 > row2 && col1 > col2) {

					int place = col1 - 1;

					for (int i = row1 - 1; i > row2; i--) {

						if (!(board[i][place].name.equals("##") || board[i][place].name.equals("  "))) {

							System.out.println("Illegal move, try again");
							System.out.print(turn + "'s" + " move:");

							return;

						}

						place--;

					}
					// System.out.println("valid move");
					// checks for bottom right diagonal
				} else if (row1 > row2 && col1 < col2) {

					int place = col1 + 1;

					for (int i = row1 - 1; i > row2; i--) {

						if (!(board[i][place].name.equals("##") || board[i][place].name.equals("  "))) {

							System.out.println("Illegal move, try again");
							System.out.print(turn + "'s" + " move:");

							return;

						}

						place++;

					}
					// System.out.println("valid move");
					// check upper left diagonal
				} else if (row1 < row2 && col1 > col2) {

					int place = col1 - 1;

					for (int i = row1 + 1; i < row2; i++) {
						if (!(board[i][place].name.equals("##") || board[i][place].name.equals("  "))) {

							System.out.println("Illegal move, try again");
							System.out.print(turn + "'s" + " move:");

							return;

						}

						place--;

					}
					// System.out.println("valid move");
					// check upper right diagonal
				} else if (row1 < row2 && col1 < col2) {

					int place = col1 + 1;

					for (int i = row1 + 1; i < row2; i++) {

						if (!(board[i][place].name.equals("##") || board[i][place].name.equals("  "))) {

							System.out.println("Illegal move, try again");
							System.out.print(turn + "'s" + " move:");

							return;

						}

						place++;

					}
					// System.out.println("valid move");
				}

			} // end of clear path checking for not a knight

			move(col1, row1, col2, row2);

		} catch (Exception e) {

			System.out.println("Illegal move, try again");
			System.out.print(turn + "'s" + " move:");

			return;

		}

	}

	public void move(int col1, int row1, int col2, int row2) {

		// proceed to move forward obvious paths are clear

		boolean valid = board[row1][col1].move(col2, row2, board);

		if (valid == false) {

			System.out.println("Illegal move, try again");
			System.out.print(turn + "'s" + " move:");
			return;

		} else {

			if (turn.equals("White")) {

				turn = "Black";

			} else {

				turn = "White";

			}

		}

		// fill in the gap the moved piece left behind

		piece temp = board[row2][col2];

		board[row2][col2] = board[row1][col1];


		// keep track of the kings
		if (board[row2][col2].id.equals("king")) {

			if (board[row2][col2].color.equals("white")) {

				this.whiteKing = board[row2][col2];
			} else {

				this.blackKing = board[row2][col2];
			}

		}

		if (displayBoard[row1 - 1][col1 - 1].equals("  ")) {

			board[row1][col1] = new space(col1, row1, "", "  ", "space");

		} else if (displayBoard[row1 - 1][col1 - 1].equals("##")) {

			board[row1][col1] = new hash(col1, row1, "", "##", "hash");

		}

		// if a pawn just performed an Enpassant , we have to remove the
		// initially adjacent pawn

		if (board[row2][col2].id.equals("pawn")) {

			if (board[row2][col2].justEnpassanted == true) {

				if (displayBoard[row2 - 2][col2 - 1].equals("  ")) {

					board[row2 - 1][col2] = new space(col2, row2 - 1, "", "  ", "space");

					board[row2][col2].justEnpassanted = false;

				} else if (displayBoard[row2 - 2][col2 - 1].equals("##")) {

					board[row2 - 1][col2] = new hash(col2, row2 - 1, "", "##", "hash");

					board[row2][col2].justEnpassanted = false;

				}

			}

		}

		// decrement pieces if one was taken
		if (temp.color.equals("black")) {

			this.blackPieces--;
		} else if (temp.color.equals("white")) {

			this.whitePieces--;
		}

		// promote a pawn being promoted

		if (board[row2][col2].id.equals("pawn") && (row2 == 8 || row2 == 1)) {

			promote(board[row2][col2]);

		}

		// check if a king was taken
		gameOver(temp);

		// check to see if king is in check

		putInCheck = board[row2][col2].check(board);

		// check to see if by moving your piece the king is in check
		if (!putInCheck) {
			if (board[row2][col2].color.equals("white")) {
				putInCheck = whiteKing.check(board);
			} else {
				putInCheck = blackKing.check(board);
			}
		}

		boolean bcheckmate = false;
		boolean wcheckmate = false;

		if (putInCheck) {

			bcheckmate = checkMate(blackKing);
			wcheckmate = checkMate(whiteKing);

			if (bcheckmate) {

				this.gameOver = true;
				this.checkMate = true;
				gameOver(blackKing);

			} else if (wcheckmate) {
				this.gameOver = true;
				this.checkMate = true;

				gameOver(whiteKing);

			}
		}

		// print the new board
		display();

		return;

	}// end of move

	public void gameOver(piece temp) {

		// check if king was taken
		if (temp.name.equals("wK")) {

			this.gameOver = true;
			this.winner = "Black";

		} else if (temp.name.equals("bK")) {

			this.gameOver = true;
			this.winner = "White";

		}

	}

	public void promote(piece piece) {

		if (this.promotionPiece == 'a' || this.promotionPiece == 'Q') {

			if (piece.color.equals("white")) {
				board[piece.rows][piece.column] = new queen(piece.column, piece.rows, "white", "wQ", "queen");
				this.promotionPiece = 'a';
			} else {
				board[piece.rows][piece.column] = new queen(piece.column, piece.rows, "black", "bQ", "queen");
				this.promotionPiece = 'a';
			}

		} else if (this.promotionPiece == 'R') {

			if (piece.color.equals("white")) {
				board[piece.rows][piece.column] = new rook(piece.column, piece.rows, "white", "wR", "rook");
				this.promotionPiece = 'a';
			} else {
				board[piece.rows][piece.column] = new rook(piece.column, piece.rows, "black", "bR", "rook");
				this.promotionPiece = 'a';
			}

		} else if (this.promotionPiece == 'B') {

			if (piece.color.equals("white")) {
				board[piece.rows][piece.column] = new bishop(piece.column, piece.rows, "white", "wB", "bishop");
				this.promotionPiece = 'a';
			} else {
				board[piece.rows][piece.column] = new bishop(piece.column, piece.rows, "black", "bB", "bishop");
				this.promotionPiece = 'a';
			}

		} else if (this.promotionPiece == 'N') {

			if (piece.color.equals("white")) {
				board[piece.rows][piece.column] = new knight(piece.column, piece.rows, "white", "wN", "knight");
				this.promotionPiece = 'a';
			} else {
				board[piece.rows][piece.column] = new knight(piece.column, piece.rows, "black", "bN", "knight");
				this.promotionPiece = 'a';
			}

		}

	}// end of promote

	public boolean checkMate(piece king) {

		int checkCount = 0;
		int freeSpaces = 8;
		int column = king.column;

		int rows = king.rows;

		boolean r = false;
		boolean l = false;
		boolean u = false;
		boolean d = false;
		boolean ur = false;
		boolean ul = false;
		boolean dr = false;
		boolean dl = false;

		if (rows == 1 || rows == 8 || column == 1 || column == 8) {

			if (rows == 1 && column == 1) {

				freeSpaces -= 5;

			} else if (rows == 1 && column == 8) {

				freeSpaces -= 5;

			} else if (rows == 8 && column == 1) {

				freeSpaces -= 5;

			} else if (rows == 8 && column == 8) {

				freeSpaces -= 5;

			} else {

				freeSpaces -= 3;

			}

		}

		piece checker = new piece(9, 9, "", "", "");

		// check right

		for (int i = column + 1; i < 9; i++) {

			if (board[rows][i].name.equals("##") || board[rows][i].name.equals("  ")) {

				continue;
			} else {

				if (board[rows][i].name.equals("bR") || board[rows][i].name.equals("bQ")
						|| (board[rows][i].name.equals("bK") && i == column + 1)) {

					if (board[rows][i].equals("king")) {

						return false;
					}

					if (king.color.equals("white")) {

						checker = board[rows][i];
						checkCount++;
						freeSpaces--;

						break;

					}

				} else if (board[rows][i].name.equals("wR") || board[rows][i].name.equals("wQ")
						|| (board[rows][i].name.equals("wK") && i == column + 1)) {

					if (board[rows][i].equals("king")) {

						return false;
					}

					if (king.color.equals("black")) {
						checker = board[rows][i];
						checkCount++;
						freeSpaces--;

						break;
					} else {

						freeSpaces--;
						break;

					}

				}

				if (i == column + 1) {
					// System.out.println("right");
					freeSpaces--;
					break;

				}
				r = true;
				break;
			}

		} // end of check right

		// check left

		for (int i = column - 1; i > 0; i--) {

			if (board[rows][i].name.equals("##") || board[rows][i].name.equals("  ")) {
				continue;
			} else {

				if (board[rows][i].name.equals("bR") || board[rows][i].name.equals("bQ")
						|| (board[rows][i].name.equals("bK") && 1 == column - 1)) {

					if (board[rows][i].equals("king")) {

						return false;
					}

					if (king.color.equals("white")) {
						checker = board[rows][i];
						checkCount++;
						freeSpaces--;

						break;

					}

				} else if (board[rows][i].name.equals("wR") || board[rows][i].name.equals("wQ")
						|| (board[rows][i].name.equals("wK") && 1 == column - 1)) {

					if (board[rows][i].equals("king")) {

						return false;
					}

					if (king.color.equals("black")) {
						checker = board[rows][i];
						checkCount++;
						freeSpaces--;

						checker = board[rows][i];

						break;

					}

				}

				if (i == column - 1) {
					// System.out.println("left");
					freeSpaces--;
					break;

				}
				l = true;
				break;
			}

		}
		// end of check left

		// if the right checker is a king, and the left has no checker, we
		// have a free space to the left

		// check up

		for (int i = rows + 1; i < 9; i++) {

			if (board[i][column].name.equals("##") || board[i][column].name.equals("  ")) {
				continue;
			} else {

				if (board[i][column].name.equals("bR") || board[i][column].name.equals("bQ")
						|| (board[i][column].name.equals("bK") && i == column + 1)) {

					if (board[i][column].equals("king")) {

						return false;
					}

					if (king.color.equals("white")) {
						checker = board[i][column];
						checkCount++;
						freeSpaces--;

						checker = board[i][column];

						break;

					}

				} else if (board[i][column].name.equals("wR") || board[i][column].name.equals("wQ")
						|| (board[i][column].name.equals("wK") && i == column + 1)) {

					if (board[i][column].equals("king")) {

						return false;
					}

					if (king.color.equals("black")) {
						checker = board[i][column];
						checkCount++;
						freeSpaces--;

						break;

					}

				}

				if (i == rows + 1) {
					// System.out.println("up");
					freeSpaces--;
					break;

				}
				u = true;
				break;
			}

		} // end of check up

		// check down

		for (int i = rows - 1; i > 0; i--) {

			if (board[i][column].name.equals("##") || board[i][column].name.equals("  ")) {
				continue;
			} else {

				if (board[i][column].name.equals("bR") || board[i][column].name.equals("bQ")
						|| (board[i][column].name.equals("bK") && i == column - 1)) {

					if (board[i][column].equals("king")) {

						return false;
					}

					if (king.color.equals("white")) {
						checker = board[i][column];
						checkCount++;
						freeSpaces--;

						checker = board[i][column];

						break;

					}

				} else if (board[i][column].name.equals("wR") || board[i][column].name.equals("wQ")
						|| (board[i][column].name.equals("wK") && i == column - 1)) {

					if (board[i][column].equals("king")) {

						return false;
					}

					if (king.color.equals("black")) {
						checker = board[i][column];
						checkCount++;
						freeSpaces--;

						checker = board[i][column];

						break;

					}

				}

				if (i == rows - 1) {
					// System.out.println("down");
					freeSpaces--;
					break;

				}
				d = true;
				break;
			}

		}
		// end of check down, must be a free space below

		// check down to the left diagonal

		int place = 0;

		if (rows - 1 != 0) {
			place = column - 1;

			for (int i = rows - 1; i > 0; i--) {

				if (place > 0 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place--;

					continue;

				} else if (place > 0
						&& (board[i][place].id.equals("king") || board[i][place].id.equals("queen")
								|| board[i][place].id.equals("bishop") || board[i][place].id.equals("pawn"))
						&& !(board[i][place].color.equals(king.color))) {

					if ((board[i][place].id.equals("king") || board[i][place].id.equals("pawn")) && i == rows - 1) {

						if (board[i][place].equals("king")) {

							return false;
						}

						checkCount++;
						freeSpaces--;

						break;
					}

					if (!(board[i][place].id.equals("king") || board[i][place].id.equals("pawn"))) {
						checker = board[i][place];
						checkCount++;
						freeSpaces--;

						break;
					}
				} else {

					if (i == rows - 1) {
						// System.out.println("dl");
						freeSpaces--;
						break;

					}
					dl = true;
					break;

				}

			}
		}

		// check upper right diagonal only if lower left was a king or a pawn

		if (rows + 1 != 9) {
			place = column + 1;

			for (int i = rows + 1; i < 9; i++) {

				if (place < 9 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place++;

					continue;

				} else if (place < 9
						&& (board[i][place].id.equals("king") || board[i][place].id.equals("queen")
								|| board[i][place].id.equals("bishop") || board[i][place].id.equals("pawn"))
						&& !(board[i][place].color.equals(king.color))) {

					if ((board[i][place].id.equals("king") || board[i][place].id.equals("pawn")) && i == rows + 1) {

						if (board[i][place].equals("king")) {

							return false;
						}

						checkCount++;
						freeSpaces--;

						break;
					}
					if (!(board[i][place].id.equals("king") || board[i][place].id.equals("pawn"))) {
						checker = board[i][place];
						checkCount++;
						freeSpaces--;
						// System.out.println("Ur");
						break;
					}
				} else {
					if (i == rows + 1) {

						freeSpaces--;
						break;

					}
					ur = true;
					break;

				}

			}
		}
		// must be a free space upper right

		// checks for bottom right diagonal

		if (rows - 1 != 0) {
			place = column + 1;

			for (int i = rows - 1; i > 0; i--) {

				if (place < 9 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place++;

					continue;

				} else if (place < 9
						&& (board[i][place].id.equals("king") || board[i][place].id.equals("queen")
								|| board[i][place].id.equals("bishop") || board[i][place].id.equals("pawn"))
						&& !(board[i][place].color.equals(king.color))) {

					if ((board[i][place].id.equals("king") || board[i][place].id.equals("pawn") && i == rows - 1)) {

						if (board[i][place].equals("king")) {

							return false;
						}

						checkCount++;
						freeSpaces--;

						break;
					}
					if (!(board[i][place].id.equals("king") || board[i][place].id.equals("pawn"))) {
						checker = board[i][place];
						checkCount++;
						freeSpaces--;

						break;
					}
				} else {

					if (i == rows + 1) {
						// System.out.println("BR");
						freeSpaces--;
						break;

					}
					dr = true;
					break;

				}

			}
		}

		// check upper left diagonal

		if (rows + 1 != 9) {
			place = column - 1;
			// System.out.println("rows +1 " + rows +1);

			for (int i = rows + 1; i < 9; i++) {

				if (place > 0 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place--;

					continue;

				} else if (place > 0
						&& (board[i][place].id.equals("king") || board[i][place].id.equals("queen")
								|| board[i][place].id.equals("bishop") || board[i][place].id.equals("pawn"))
						&& !(board[i][place].color.equals(king.color))) {

					if ((board[i][place].id.equals("king") || board[i][place].id.equals("pawn") && i == rows + 1)) {

						if (board[i][place].equals("king")) {

							return false;
						}

						checkCount++;
						freeSpaces--;

						break;
					}
					if (!(board[i][place].id.equals("king") || board[i][place].id.equals("pawn"))) {
						checker = board[i][place];
						checkCount++;
						freeSpaces--;

						break;
					}
				} else {

					if (i == rows + 1) {
						// System.out.println("ul");
						freeSpaces--;
						break;

					}
					ul = true;
					break;

				}

			}
		}

		// if you have only 0 free sapces, check if you can take the checker

		// if you have one free space left, make sure you wont be taken if you
		// move there
		// boolean knightCheck = false;

		boolean notSafeSpace = false;
		if (freeSpaces == 1) {
			if (r) {

				// knightCheck = knightSpace(king, rows, column + 1);
				piece temp = new king(king.column + 1, king.rows, king.color, king.name, king.id);
				notSafeSpace = temp.check(board);

				if (notSafeSpace) {

					freeSpaces--;
				}
			}
			if (l) {

				// knightCheck = knightSpace(king, rows, column - 1);
				piece temp = new king(king.column - 1, king.rows, king.color, king.name, king.id);
				notSafeSpace = temp.check(board);

				if (notSafeSpace) {

					freeSpaces--;
				}

			}
			if (u) {

				// knightCheck = knightSpace(king, rows + 1, column);
				piece temp = new king(king.column, king.rows + 1, king.color, king.name, king.id);
				notSafeSpace = temp.check(board);

				if (notSafeSpace) {

					freeSpaces--;
				}

			}
			if (d) {

				// knightCheck = knightSpace(king, rows - 1, column);
				piece temp = new king(king.column, king.rows - 1, king.color, king.name, king.id);
				notSafeSpace = temp.check(board);

				if (notSafeSpace) {

					freeSpaces--;
				}

			}
			if (ur) {

				// knightCheck = knightSpace(king, rows + 1, column + 1);
				piece temp = new king(king.column + 1, king.rows + 1, king.color, king.name, king.id);
				notSafeSpace = temp.check(board);

				if (notSafeSpace) {

					freeSpaces--;
				}

			}
			if (ul) {

				// knightCheck = knightSpace(king, rows + 1, column - 1);
				piece temp = new king(king.column - 1, king.rows + 1, king.color, king.name, king.id);
				notSafeSpace = temp.check(board);

				if (notSafeSpace) {

					freeSpaces--;
				}

			}
			if (dr) {

				// knightCheck = knightSpace(king, rows - 1, column + 1);
				piece temp = new king(king.column + 1, king.rows - 1, king.color, king.name, king.id);
				notSafeSpace = temp.check(board);

				if (notSafeSpace) {

					freeSpaces--;
				}

			}
			if (dl) {

				// knightCheck = knightSpace(king, rows - 1, column - 1);
				piece temp = new king(king.column - 1, king.rows - 1, king.color, king.name, king.id);
				notSafeSpace = temp.check(board);

				if (notSafeSpace) {

					freeSpaces--;
				}

			}
		}

		if (freeSpaces == 0 && checkCount == 1) {

			if (Math.abs(checker.column - column) == 1 && Math.abs(checker.rows - rows) == 1) {

				return false;

			}

		}

		if (checkCount == 1 && freeSpaces == 0) {

			boolean saved = save(checker);

			if (saved) {

				return false;

			} else {

				return true;
			}

		}

		// if another piece can get you in the safe spacey your in checkmate
		if (freeSpaces == 1 && notSafeSpace) {

			return true;
		}

		if (freeSpaces == 0) {

			return true;
		}

		return false;
	}

	/*
	 * public boolean knightSpace(piece king, int rows, int column) {
	 * 
	 * // check up one left 2
	 * 
	 * if (rows + 1 <= 8 && column - 2 >= 1 && board[rows + 1][column -
	 * 2].id.equals("knight")) {
	 * 
	 * if (!(board[rows + 1][column - 2].color.equals(king.color))) {
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // check up one right 2
	 * 
	 * if (rows + 1 <= 8 && column + 2 <= 8 && board[rows + 1][column +
	 * 2].id.equals("knight")) {
	 * 
	 * if (!(board[rows + 1][column + 2].color.equals(king.color))) {
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // check down one left 2
	 * 
	 * if (rows - 1 >= 1 && column - 2 >= 1 && board[rows - 1][column -
	 * 2].id.equals("knight")) {
	 * 
	 * if (!(board[rows - 1][column - 2].color.equals(king.color))) {
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // check down one right 2
	 * 
	 * if (rows - 1 >= 1 && column + 2 <= 8 && board[rows - 1][column +
	 * 2].id.equals("knight")) {
	 * 
	 * if (!(board[rows - 1][column + 2].color.equals(king.color))) {
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // check up two left 1
	 * 
	 * if (rows + 2 <= 8 && column - 1 >= 1 && board[rows + 2][column -
	 * 1].id.equals("knight")) {
	 * 
	 * if (!(board[rows + 2][column - 1].color.equals(king.color))) {
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // check up two right 1
	 * 
	 * if (rows + 2 <= 8 && column + 1 <= 8 && board[rows + 2][column +
	 * 1].id.equals("knight")) {
	 * 
	 * if (!(board[rows + 2][column + 1].color.equals(king.color))) {
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // check down two left 1
	 * 
	 * if (rows - 2 >= 1 && column - 1 >= 1 && board[rows - 2][column -
	 * 1].id.equals("knight")) {
	 * 
	 * if (!(board[rows - 2][column - 1].color.equals(king.color))) {
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * // check down two right 1
	 * 
	 * if (rows - 2 >= 1 && column + 1 <= 8 && board[rows - 2][column +
	 * 1].id.equals("knight")) { if (!(board[rows - 2][column +
	 * 1].color.equals(king.color))) {
	 * 
	 * return true;
	 * 
	 * }
	 * 
	 * }
	 * 
	 * return false;
	 * 
	 * }
	 */
	public boolean save(piece piece) {

		boolean saved = false;

		if (piece.color.equals("black")) {

			piece newPiece = new king(piece.column, piece.rows, "black", "bK", "king");

			saved = newPiece.check(board);

		} else {

			piece newPiece = new king(piece.column, piece.rows, "white", "wK", "king");

			saved = newPiece.check(board);
		}

		if (saved) {

			return true;

		} else {

			return false;
		}

	}

}// end of class
