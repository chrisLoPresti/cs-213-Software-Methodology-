
//chris lopresti -cpl6- kyle myers -kwm38- chess project
package piece;

public class king extends piece {

	public String id;

	public king(int col, int row, String color, String name, String id) {
		super(col, row, color, name, id);

	}

	public boolean move(int col, int row, piece[][] board) {

		// castle
		if (this.moved == false && (col ==7 || col ==3)) {

			// right castle
			if (col == 7) {
				if ((board[1][8].moved == false) || (board[8][8].moved == false)) {
					this.rows = row;
					this.column = col;
					this.moved = true;
					board[rows][column - 1] = board[rows][8];
					board[rows][8].moved = true;
					if (rows == 1) {
						board[rows][8] = new space(1, 8, "", "  ", "space");
					}
					if (rows == 8) {
						board[rows][8] = new hash(8, 8, "", "##", "hash");
					}
					board[0][0].name.equals("false");
					return true;
				}
			}
			// left castle
			if (col ==3) {
				if ((board[1][1].moved == false) || (board[8][1].moved == false)) {
					this.rows = row;
					this.column = col;
					this.moved = true;
					board[rows][column + 1] = board[rows][8];
					board[rows][8].moved = true;
					if (rows == 1) {

						board[rows][1] = new hash(1, 8, "", "##", "hash");

					}
					if (rows == 8) {

						board[rows][1] = new space(1, 1, "", "  ", "space");

					}
					board[0][0].name.equals("false");
					return true;
				}
			}

		}

		if (col == column && row == rows) {
			return false;
		}

		if (col != column && row == rows) {
			if ((col >= column + 2) || (col <= column - 2)) {
				return false;
			}
			this.column = col;
			this.moved = true;
			board[0][0].name.equals("false");
			return true;
		}

		if (col == column && row != rows) {
			if ((row >= rows + 2) || (row <= rows - 2)) {
				return false;
			}
			this.rows = row;
			this.moved = true;
			board[0][0].name.equals("false");
			return true;
		}
		if (col != column && row != rows) {
			if (((col >= column + 2) || (col <= column - 2)) || ((row >= rows + 2) || (row <= rows - 2))) {
				return false;
			}

			this.rows = row;
			this.column = col;
			this.moved = true;
			board[0][0].name.equals("false");

			return true;

		}
		return false;

	}

	public boolean check(piece[][] board) {

		// check right

		for (int i = column + 1; i < 9; i++) {

			if (board[rows][i].name.equals("##") || board[rows][i].name.equals("  ")) {

				continue;
			} else {

				if (board[rows][i].name.equals("bR") || board[rows][i].name.equals("bQ")
						|| (board[rows][i].name.equals("bK") && i == column + 1)) {

					if (this.color.equals("white")) {

						return true;

					}

				} else if (board[rows][i].name.equals("wR") || board[rows][i].name.equals("wQ")
						|| (board[rows][i].name.equals("wK") && i == column + 1)) {

					if (this.color.equals("black")) {

						return true;

					}

				}

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

					if (this.color.equals("white")) {

						return true;

					}

				} else if (board[rows][i].name.equals("wR") || board[rows][i].name.equals("wQ")
						|| (board[rows][i].name.equals("wK") && 1 == column - 1)) {

					if (this.color.equals("black")) {

						return true;

					}

				}
				break;
			}

		} // end of check left

		// check up
		for (int i = rows + 1; i < 9; i++) {

			if (board[i][column].name.equals("##") || board[i][column].name.equals("  ")) {
				continue;
			} else {

				if (board[i][column].name.equals("bR") || board[i][column].name.equals("bQ")
						|| (board[i][column].name.equals("bK") && i == column + 1)) {

					if (this.color.equals("white")) {

						return true;

					}

				} else if (board[i][column].name.equals("wR") || board[i][column].name.equals("wQ")
						|| (board[i][column].name.equals("wK") && i == column + 1)) {

					if (this.color.equals("black")) {

						return true;

					}

				}

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

					if (this.color.equals("white")) {

						return true;

					}

				} else if (board[i][column].name.equals("wR") || board[i][column].name.equals("wQ")
						|| (board[i][column].name.equals("wK") && i == column - 1)) {

					if (this.color.equals("black")) {

						return true;

					}

				}

				break;
			}

		} // end of check down

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
						&& !(board[i][place].color.equals(this.color))) {

					if ((board[i][place].id.equals("king") || board[i][place].id.equals("pawn")) && i == rows - 1) {

						return true;
					} else if (!(board[i][place].id.equals("king") || board[i][place].id.equals("pawn"))) {

						return true;
					}
				} else {

					break;

				}

			}
		}
		// System.out.println("valid move");
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
						&& !(board[i][place].color.equals(this.color))) {

					if ((board[i][place].id.equals("king") || board[i][place].id.equals("pawn")) && i == rows - 1) {

						return true;
					}
					if (!(board[i][place].id.equals("king") || board[i][place].id.equals("pawn"))) {

						return true;
					}
				} else {

					break;

				}

			}
		}
		// System.out.println("valid move");
		// check upper left diagonal
		if (rows + 1 != 9) {
			place = column - 1;

			for (int i = rows + 1; i < 9; i++) {

				if (place > 0 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place--;

					continue;

				} else if (place > 0
						&& (board[i][place].id.equals("king") || board[i][place].id.equals("queen")
								|| board[i][place].id.equals("bishop") || board[i][place].id.equals("pawn"))
						&& !(board[i][place].color.equals(this.color))) {

					if ((board[i][place].id.equals("king") || board[i][place].id.equals("pawn")) && i == rows + 1) {

						return true;
					}
					if (!(board[i][place].id.equals("king") || board[i][place].id.equals("pawn"))) {

						return true;
					}
				} else {

					break;

				}

			}
		}
		// System.out.println("valid move");
		// check upper right diagonal
		if (rows + 1 != 9) {
			place = column + 1;

			for (int i = rows + 1; i < 9; i++) {

				if (place < 9 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place++;

					continue;

				} else if (place < 9
						&& (board[i][place].id.equals("king") || board[i][place].id.equals("queen")
								|| board[i][place].id.equals("bishop") || board[i][place].id.equals("pawn"))
						&& !(board[i][place].color.equals(this.color))) {

					if ((board[i][place].id.equals("king") || board[i][place].id.equals("pawn")) && i == rows + 1) {

						return true;
					}
					if (!(board[i][place].id.equals("king") || board[i][place].id.equals("pawn"))) {

						return true;
					}
				} else {

					break;

				}

			}
		}

		// check up one left 2

		if (rows + 1 <= 8 && column - 2 >= 1 && board[rows + 1][column - 2].id.equals("knight")) {

			if (!(board[rows + 1][column - 2].color.equals(this.color))) {

				return true;

			}

		}

		// check up one right 2

		if (rows + 1 <= 8 && column + 2 <= 8 && board[rows + 1][column + 2].id.equals("knight")) {

			if (!(board[rows + 1][column + 2].color.equals(this.color))) {

				return true;

			}

		}

		// check down one left 2

		if (rows - 1 >= 1 && column - 2 >= 1 && board[rows - 1][column - 2].id.equals("knight")) {

			if (!(board[rows - 1][column - 2].color.equals(this.color))) {

				return true;

			}

		}

		// check down one right 2

		if (rows - 1 >= 1 && column + 2 <= 8 && board[rows - 1][column + 2].id.equals("knight")) {

			if (!(board[rows - 1][column + 2].color.equals(this.color))) {

				return true;

			}

		}

		// check up two left 1

		if (rows + 2 <= 8 && column - 1 >= 1 && board[rows + 2][column - 1].id.equals("knight")) {

			if (!(board[rows + 2][column - 1].color.equals(this.color))) {

				return true;

			}

		}

		// check up two right 1

		if (rows + 2 <= 8 && column + 1 <= 8 && board[rows + 2][column + 1].id.equals("knight")) {

			if (!(board[rows + 2][column + 1].color.equals(this.color))) {

				return true;

			}

		}

		// check down two left 1

		if (rows - 2 >= 1 && column - 1 >= 1 && board[rows - 2][column - 1].id.equals("knight")) {

			if (!(board[rows - 2][column - 1].color.equals(this.color))) {

				return true;

			}

		}

		// check down two right 1

		if (rows - 2 >= 1 && column + 1 <= 8 && board[rows - 2][column + 1].id.equals("knight")) {
			if (!(board[rows - 2][column + 1].color.equals(this.color))) {

				return true;

			}

		}
		// System.out.println("valid move");

		return false;

	}

}
