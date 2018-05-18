//chris lopresti -cpl6- kyle myers -kwm38- chess project

package piece;

public class pawn extends piece {

	public String id;

	public pawn(int col, int row, String color, String name, String id) {
		super(col, row, color, name, id);

	}

	public boolean move(int col, int row, piece[][] board) {

		// special check for diagonal pawn movement and pawn taking a piece
		// in front of it

		// can not take a piece in front of pawn
		if (column == col && !(board[row][col].name.equals("  ") || board[row][col].name.equals("##"))) {

			return false;
			// can not move diagonal unless the space is occupied by a
			// piece of the opposite color
		}

		// diagonal only if not a space or if ep initated or if diagonal piece
		// is of different color

		if (column != col) {
			if (this.color.equals("white")) {
				if ((board[row][col].name.equals("  ") || board[row][col].name.equals("##"))
						&& board[row - 1][col].id.equals("pawn") && !(board[row - 1][col].color.equals(this.color))) {

					if (board[row - 1][col].id.equals("pawn") && board[0][0].name.equals("true")) {

						this.column = col;
						this.rows = row;
						this.moved = true;
						board[0][0].name.equals("false");
						this.justEnpassanted = true;
						return true;
					}
					return false;
				} else if (col != column && row != rows
						&& !(board[row][col].name.equals("  ") || board[row][col].name.equals("##"))
						&& !(board[row][col].color.equals(this.color))) {

					this.column = col;
					this.rows = row;
					this.moved = true;
					board[0][0].name.equals("false");
					return true;

				}
				return false;
			} else if (this.color.equals("black")) {

				if ((board[row][col].name.equals("  ") || board[row][col].name.equals("##"))
						&& board[row + 1][col].id.equals("pawn") && !(board[row + 1][col].color.equals(this.color))) {

					if (board[row + 1][col].id.equals("pawn") && board[0][0].name.equals("true")) {

						this.column = col;
						this.rows = row;
						this.moved = true;
						board[0][0].name.equals("false");
						this.justEnpassanted = true;
						return true;
					}
					return false;
				} else // diagonal was previously checked before hand so ok

				if (col != column && row != rows
						&& !(board[row][col].name.equals("  ") || board[row][col].name.equals("##"))
						&& !(board[row][col].color.equals(this.color))) {

					this.column = col;
					this.rows = row;
					this.moved = true;
					board[0][0].name.equals("false");
					return true;

				}
				return false;
			}

		}

		if (this.color.equals("white")) {

			// can't go backwards
			if (row < rows) {

				return false;

			}

			// move up

			if (row == rows + 2) {

				if (moved == false) {

					this.rows = rows + 2;

					this.moved = true;

					board[0][0].name = "true";

					return true;
				}

			} else if (row == rows + 1) {

				this.rows = rows + 1;

				this.moved = true;
				board[0][0].name.equals("false");

				return true;
			}

			// move sideways check

			if ((col == column + 1 || col == column - 1) && row == rows + 1) {

				this.column = col;

				this.moved = true;

				board[0][0].name.equals("false");

				return true;
			}

		} else if (this.color.equals("black")) {

			// can't go backwards
			if (row > rows) {

				return false;

			}

			// move down

			if (row == rows - 2) {

				if (moved == false) {

					this.rows = rows - 2;

					this.moved = true;

					board[0][0].name = "true";

					return true;
				}

			} else if (row == rows - 1) {

				this.rows = rows - 1;

				this.moved = true;

				board[0][0].name.equals("false");

				return true;
			}

			// move sideways check

			if ((col == column + 1 || col == column - 1) && row == rows) {

				this.column = col;

				this.moved = true;

				board[0][0].name.equals("false");

				return true;
			}

		}

		return false;

	}

	public boolean check(piece[][] board) {

		// check if pawn puts other player in check
		// black pawn
		if (this.color.equals("black")) {

			if (rows - 1 != 0 && column + 1 != 9 && column - 1 != 0 && (board[rows - 1][column + 1].name.equals("wK")
					|| board[rows - 1][column - 1].name.equals("wK"))) {

				return true;

			}
			// white pawn
		} else if (this.color.equals("white")) {

			if (rows + 1 != 9 && column + 1 != 9 && column - 1 != 0 && (board[rows + 1][column + 1].name.equals("bK")
					|| board[rows + 1][column - 1].name.equals("bK"))) {

				return true;

			}

		} // end of pawn check

		return false;
	}

}
