//chris lopresti -cpl6- kyle myers -kwm38- chess project

package piece;

public class knight extends piece {

	public String id;

	public knight(int col, int row, String color, String name , String id) {
		super(col, row, color, name, id);

	}

	public boolean move(int col, int row, piece[][] board) {

	
		// check out of limit horizontally
		if (col > column + 2 || col < column - 2) {

			return false;

		}

		// check out of limit vertically
		if (row > rows + 2 || row < rows - 2) {

			return false;
		}

		// moving right or left two - up or down one
		if ((col == column + 2 || col == column - 2) && (row == rows + 1 || row == rows - 1)) {

			this.rows = row;

			this.column = col;

			board[0][0].name.equals("false");
			
			this.moved = true;

			return true;

		}

		// moving right or left one - up or down two
		if ((col == column + 1 || col == column - 1) && (row == rows + 2 || row == rows - 2)) {

			this.rows = row;

			this.column = col;

			this.moved = true;
			
			board[0][0].name.equals("false");

			return true;

		}

		return false;
	}

	public boolean check(piece[][] board) {
		

	

		// check up one left 2

		if (rows + 1 <=8  && column - 2 >=1 && board[rows + 1][column - 2].id.equals("king")) {
			System.out.println("here");

			if (!(board[rows + 1][column - 2].color.equals(this.color))) {

				return true;

			}

		}
		
		// check up one right 2

		if (rows + 1 <= 8 && column + 2 <= 8 && board[rows + 1][column + 2].id.equals("king")) {

			if (!(board[rows + 1][column + 2].color.equals(this.color))) {

				return true;

			}

		}
	
		// check down one left 2

		if (rows - 1 >= 1 && column - 2 >= 1 && board[rows - 1][column - 2].id.equals("king")) {

			if (!(board[rows - 1][column - 2].color.equals(this.color))) {

				return true;

			}

		}

		// check down one right 2

		if (rows - 1 >= 1 && column + 2 <= 8 && board[rows - 1][column + 2].id.equals("king")) {

			if (!(board[rows - 1][column + 2].color.equals(this.color))) {

				return true;

			}

		}

		// check up two left 1

		if (rows + 2 <= 8 && column - 1 >= 1 && board[rows + 2][column - 1].id.equals("king")) {

			if (!(board[rows + 2][column - 1].color.equals(this.color))) {

				return true;

			}

		}	
		
		// check up two right 1

		if (rows + 2 <= 8 && column + 1 <= 8 && board[rows + 2][column + 1].id.equals("king")) {

			if (!(board[rows + 2][column + 1].color.equals(this.color))) {

				return true;

			}

		}

		// check down two left 1

		if (rows - 2 >= 1 && column - 1 >= 1 && board[rows - 2][column - 1].id.equals("king")) {

			if (!(board[rows - 2][column - 1].color.equals(this.color))) {

				return true;

			}

		}
	
		// check down two right 1

		if (rows - 2 >= 1 && column + 1 <= 8 && board[rows - 2][column + 1].id.equals("king")) {
			if (!(board[rows - 2][column + 1].color.equals(this.color))) {

				return true;

			}

		}

		return false;
	}

}
