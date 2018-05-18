//chris lopresti -cpl6- kyle myers -kwm38- chess project

package piece;

public class queen extends piece {

	public String id;

	public queen(int col, int row, String color, String name, String id) {
		super(col, row, color, name, id);

	}

	public boolean move(int col, int row, piece[][] board) {
		
	


		// check to see if the piece is trying to be moved to its same location
		if (col == column && row == rows) {
			return false;
		}

		if ((col != column && row == rows) || (col == column && row != rows)) {

			this.column = col;
			this.rows = row;
			this.moved = true;
			
			board[0][0].name.equals("false");

			return true;
		}

		if ((col != column && row != rows)) {

			int colDiff;
			int rowDiff;

			// check for move down to the left
			if ((col < column) && (row < rows)) {
				colDiff = column - col;
				rowDiff = rows - row;

				if (colDiff != rowDiff) {
					return false;
				}
				this.rows = row;
				this.column = col;
				this.moved = true;
				
				board[0][0].name.equals("false");
				return true;
			}

			// check for move down to the right
			if ((col > column) && (row < rows)) {
				colDiff = col - column;
				rowDiff = rows - row;

				if (colDiff != rowDiff) {
					return false;
				}
				this.rows = row;
				this.column = col;
				this.moved = true;
				
				board[0][0].name.equals("false");
				return true;
			}

			// check for move up to the right
			if ((col > column) && (row > rows)) {
				colDiff = col - column;
				rowDiff = row - rows;

				if (colDiff != rowDiff) {
					return false;
				}
				this.rows = row;
				this.column = col;
				this.moved = true;
				
				board[0][0].name.equals("false");
				return true;
			}

			// check for move up to the left
			if ((col < column) && (row > rows)) {
				colDiff = column - col;
				rowDiff = row - rows;

				if (colDiff != rowDiff) {
					return false;
				}
				this.rows = row;
				this.column = col;
				this.moved = true;
				
				board[0][0].name.equals("false");
				return true;
			}

		}

		return false;

	}

	public boolean check(piece[][] board) {

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

		// check down to the left diagonal
		int place = 0;

		if (rows - 1 != 0) {
			place = column - 1;

			for (int i = rows - 1; i > 0; i--) {

				if (place > 0 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place--;

					continue;

				} else if (place > 0 && board[i][place].id.equals("king")
						&& !(board[i][place].color.equals(this.color))) {

					return true;

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

				} else if (place < 9 && board[i][place].id.equals("king")
						&& !(board[i][place].color.equals(this.color))) {

					return true;

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

				} else if (place > 0 && board[i][place].id.equals("king")
						&& !(board[i][place].color.equals(this.color))) {

					return true;

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

				} else if (place < 9 && board[i][place].id.equals("king")
						&& !(board[i][place].color.equals(this.color))) {

					return true;

				} else {

					break;

				}

			}
		}
		// System.out.println("valid move");

		return false;

	}
}
