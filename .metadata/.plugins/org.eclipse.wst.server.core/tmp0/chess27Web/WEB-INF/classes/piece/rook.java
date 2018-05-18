//chris lopresti -cpl6- kyle myers -kwm38- chess project

package piece;

public class rook extends piece {

	public String id;

	public rook(int col, int row, String color, String name, String id) {
		super(col, row, color, name, id);

	}

	public boolean move(int col, int row, piece[][] board) {

		// check to make sure the piece is moved to a different location
		if (col == column && row == rows) {
			return false;
		}

		// check for column change row consistent or column consistent row
		// change
		if ((col != column && row == rows) || (col == column && row != rows)) {

			this.column = col;
			this.rows = row;
			this.moved = true;
			board[0][0].name.equals("false");
			return true;
		}

		return false;

	}

	public boolean check(piece[][] board) {

		// check if rook puts other player in check

		// check right

		for (int i = column + 1; i < 9; i++) {

			if (board[rows][i].name.equals("##") || board[rows][i].name.equals("  ")) {

				continue;
			} else {

				if (board[rows][i].name.equals("bK")) {

					if (this.color.equals("white")) {

						return true;

					}

				} else if (board[rows][i].name.equals("wK")) {

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

				if (board[rows][i].name.equals("bK")) {

					if (this.color.equals("white")) {

						return true;

					}

				} else if (board[rows][i].name.equals("wK")) {

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

				if (board[i][column].name.equals("bK")) {

					if (this.color.equals("white")) {

						return true;

					}

				} else if (board[i][column].name.equals("wK")) {

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

				if (board[i][column].name.equals("bK")) {

					if (this.color.equals("white")) {

						return true;

					}

				} else if (board[i][column].name.equals("wK")) {

					if (this.color.equals("black")) {

						return true;

					}

				}

				break;
			}

		} // end of check down

		return false;

	}// end of check

}
