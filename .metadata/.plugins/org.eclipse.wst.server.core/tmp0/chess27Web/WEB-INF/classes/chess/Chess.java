//chris lopresti -cpl6- kyle myers -kwm38- chess project

package chess;

import java.util.Scanner;

//import piece.piece;
//import piece.pawn;
import piece.board;

public class Chess {

	public static Scanner sc;

	public static void main(String[] args) {

		board board = new board();

		sc = new Scanner(System.in);

		String input = "";

		while (!board.gameOver) {

			input = sc.nextLine();
			
			System.out.println("");

			board.validMove(input);
			

		}
		
	if (board.checkMate == true) {
		System.out.println("Checkmake, " + board.winner + " is the winner.");
		
		return;
	}

		if (board.drawRequest) {

			System.out.println("draw");

			return;

		} else if (board.resign == true) {
			System.out.println(board.winner + " is the winner.");
			return;
		} else {

			System.out.println(board.winner + " is the winner.");
			return;
		}
	}

}
