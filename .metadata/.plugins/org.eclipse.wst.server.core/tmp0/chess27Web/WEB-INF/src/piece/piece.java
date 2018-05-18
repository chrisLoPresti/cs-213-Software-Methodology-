//chris lopresti -cpl6- kyle myers -kwm38- chess project

package piece;


public class piece implements move, check  {
	
	public String id;
	
	public String name;
	public int column;
	public int rows;
	public String color;
	public boolean moved;
	public boolean justEnpassanted;


	
	public piece(int col, int row, String color, String name, String id) {
		this.column = col;
		this. rows = row;
		this.color = color;
		this.moved = false;
		this.name = name;
		this.id = id;
		
		
	}


	@Override
	public boolean move(int col, int row, piece[][] board) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean check(piece[][] board) {
		// TODO Auto-generated method stub
		
		return false;
		
	}


	
	
	


}
