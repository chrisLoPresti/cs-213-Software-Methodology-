//chris lopresti -cpl6- kyle myers -kwm38- chess project
package piece;

public class bishop extends piece {

	public String id;

	public bishop(int col, int row, String color, String name, String id) {
		super(col, row, color, name, id);

	}

	public boolean move(int col, int row, piece[][] board) {

		// check to make sure the column and row have changed
		if (col == column || row == rows) {
			return false;
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

		// check down to the left diagonal
		int place = 0;

		if (rows - 1 != 0) {
			place = column - 1;

			for (int i = rows - 1; i > 0; i--) {

				if (place >0 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place--;

					continue;

				} else if (place >0 &&board[i][place].id.equals("king") && !(board[i][place].color.equals(this.color))) {

					board[0][0].name.equals("false");
					
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

				if (place <9 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place++;

					continue;

				} else if (place <9 && board[i][place].id.equals("king") && !(board[i][place].color.equals(this.color))) {

					board[0][0].name.equals("false");
					
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

				if ( place >0 && (board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place--;

					continue;

				} else if (place >0 &&board[i][place].id.equals("king") && !(board[i][place].color.equals(this.color))) {

					board[0][0].name.equals("false");
					
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

				if (place <9 &&(board[i][place].id.equals("hash") || board[i][place].id.equals("space"))) {

					place++;

					continue;

				} else if (place <9 && board[i][place].id.equals("king") && !(board[i][place].color.equals(this.color))) {

					board[0][0].name.equals("false");
					
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
