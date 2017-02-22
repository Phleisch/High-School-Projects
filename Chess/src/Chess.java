import java.util.*;
//import java.io.*;

public class Chess
{
	
	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		Game chess = new Game();
		boolean gameOver = false;
		while(!gameOver)
		{
			boolean goodMove = false;
			chess.display();
			while(!goodMove)
			{
				System.out.print("\nPiece to move: ");
				String piece = in.nextLine();
				if(piece.equals("quit"))
					System.exit(0);
				System.out.print("Destination: ");
				String dest = in.nextLine();
				goodMove = chess.turn(piece,dest);
			}
			gameOver = chess.checkWin();
		}
	}
		
}

class Game
{
	private char[][] board;
	private boolean capTurn;
	final private char[][] blank = new char[][]
				{ 	{'.',' ','.',' ','.',' ','.',' '},
					{' ','.',' ','.',' ','.',' ','.'},
					{'.',' ','.',' ','.',' ','.',' '},
					{' ','.',' ','.',' ','.',' ','.'},
					{'.',' ','.',' ','.',' ','.',' '},
					{' ','.',' ','.',' ','.',' ','.'},
					{'.',' ','.',' ','.',' ','.',' '},
					{' ','.',' ','.',' ','.',' ','.'}
				};
	private HashMap<String,Piece> pieces;
	
	Game()
	{
		board = new char[][]
				{ 	{'r','n','b','q','k','b','n','r'},
					{'p','p','p','p','p','p','p','p'},
					{'.',' ','.',' ','.',' ','.',' '},
					{' ','.',' ','.',' ','.',' ','.'},
					{'.',' ','.',' ','.',' ','.',' '},
					{' ','.',' ','.',' ','.',' ','.'},
					{'P','P','P','P','P','P','P','P'},
					{'R','N','B','Q','K','B','N','R'}
				};
		capTurn = true;
		pieces = new HashMap<>();
		
		//        LowerCase Pieces						      UpperCase Pieces
		
		pieces.put("a8",new Rook("a8","nCap")); 	pieces.put("a1",new Rook("a1","cap"));
		pieces.put("b8",new Knight("b8","nCap")); 	pieces.put("b1",new Knight("b1","cap"));
		pieces.put("c8",new Bishop("c8","nCap")); 	pieces.put("c1",new Bishop("c1","cap"));
		pieces.put("d8",new Queen("d8","nCap")); 	pieces.put("d1",new Queen("d1","cap"));
		pieces.put("e8",new King("e8","nCap")); 	pieces.put("e1",new King("e1","cap"));
		pieces.put("f8",new Bishop("f8","nCap")); 	pieces.put("f1",new Bishop("f1","cap"));
		pieces.put("g8",new Knight("g8","nCap")); 	pieces.put("g1",new Knight("g1","cap"));
		pieces.put("h8",new Rook("h8","nCap")); 	pieces.put("h1",new Rook("h1","cap"));
		pieces.put("a7",new Pawn("a7","nCap")); 	pieces.put("a2",new Pawn("a2","cap"));
		pieces.put("b7",new Pawn("b7","nCap")); 	pieces.put("b2",new Pawn("b2","cap"));
		pieces.put("c7",new Pawn("c7","nCap")); 	pieces.put("c2",new Pawn("c2","cap"));
		pieces.put("d7",new Pawn("d7","nCap")); 	pieces.put("d2",new Pawn("d2","cap"));
		pieces.put("e7",new Pawn("e7","nCap")); 	pieces.put("e2",new Pawn("e2","cap"));
		pieces.put("f7",new Pawn("f7","nCap")); 	pieces.put("f2",new Pawn("f2","cap"));
		pieces.put("g7",new Pawn("g7","nCap")); 	pieces.put("g2",new Pawn("g2","cap"));
		pieces.put("h7",new Pawn("h7","nCap")); 	pieces.put("h2",new Pawn("h2","cap"));
	}
	
	void display()
	{
		System.out.println("   a  b  c  d  e  f  g  h");
		System.out.println("  O----------------------O");
		for(int r = 0; r < board.length; r++)
		{
			System.out.print((8-r)+" |");
			for(int c = 0; c < board[0].length; c++)
			{
				System.out.print(board[r][c]);
				if(c<board[0].length-1)
					System.out.print("  ");
			}
			System.out.print("| "+(8-r));
			if(r<board.length-1)
				System.out.println("\n");
			else
			{
				System.out.println("\n  O----------------------O");
				System.out.println("   a  b  c  d  e  f  g  h\n");
			}
		}
		String turn = capTurn ? "Uppercase's Move" : "Lowercase's Move";
		System.out.println(turn+"\n");
	}
	
	boolean turn(String piece, String toMove)
	{
		Piece moving = pieces.get(piece);
		piece = piece.toLowerCase();
		toMove = toMove.toLowerCase();
		int row, col, col2, row2;
		row=col=row2=col2=0;
		try
		{
			row = 8-Integer.parseInt(""+piece.charAt(1));
			col = piece.charAt(0)-97;
			row2 = 8-Integer.parseInt(""+toMove.charAt(1));
			col2 = toMove.charAt(0)-97;
		}
		catch(Exception e)
		{
			System.out.println("That's not a move. Try Again.");
			return false;
		}
		if(row>7 || row<0 || col>7 || col<0 || row2>7 || row2<0 || col2>7 || col2<0)
		{
			System.out.println("Keep your pieces on the board.");
			return false;
		}
		//System.out.println(row+", "+col+" -> "+row2+", "+col2);
		if(board[row][col] == ' ' || board[row][col] == '.')
		{
			System.out.println("Invalid Move.\n");
			return false;
		}
		if((board[row][col] > 91 && capTurn) || (board[row][col] < 91 && !capTurn))
		{
			System.out.println("That's not your piece. Invalid move.\n");
			return false;
		}
		if(!moving.validMove(row,col,row2,col2,board))
		{
			System.out.println("Your piece cannot move there.\n");
			return false;
		}
		Piece take = pieces.get(toMove);
		if(take!=null)
			pieces.remove(toMove);
		board[row2][col2] = board[row][col];
		board[row][col] = blank[row][col];
		pieces.remove(piece);
		pieces.put(toMove,moving);
		capTurn = !capTurn;
		System.out.println("\n\n");
		return true;
	}
	
	boolean checkWin()
	{
		boolean capKing = false;
		boolean lowKing = false;
		for(String key : pieces.keySet())
		{
			Piece p = pieces.get(key);
			if(p instanceof King)
			{
				if(p.getOwner().equals("cap"))
					capKing = true;
				else
					lowKing = true;
			}
		}
		if(!capKing || !lowKing)
		{
			String out = !capKing ? "Lowercase Wins!" : "Uppercase Wins";
			System.out.println(out);
		}
		
		return (!capKing || !lowKing);
	}
}

abstract class Piece
{
	protected String location;
	protected String owner;
	
	Piece(String l, String o)
	{
		location = l;
		owner = o;
	}
	
	abstract public boolean validMove(int x1, int y1, int x2, int y2, char[][] board);
	String getOwner(){return owner;}
	
}

class Rook extends Piece
{
	Rook(String l, String o)
	{
		super(l,o);
	}
	
	public boolean validMove(int x1, int y1, int x2, int y2, char[][] board)
	{
		int dX = Math.abs(x2-x1);
		int dY = Math.abs(y2-y1);
		char key = owner.equals("cap") ? 'A' : 'a';
		if( Math.abs(board[x2][y2]-key) <= 26 && !(board[x2][y2] != ' ' || board[x2][y2] != '.' ) )
			return false;
        return !(dX != 0 && dY != 0);
    }
}

class Knight extends Piece
{
	Knight(String l, String o)
	{
		super(l,o);
	}
	
	public boolean validMove(int x1, int y1, int x2, int y2, char[][] board)
	{
		int dX = Math.abs(x2-x1);
		int dY = Math.abs(y2-y1);
		char key = owner.equals("cap") ? 'A' : 'a';
		if( Math.abs(board[x2][y2]-key) <= 26 && !(board[x2][y2] != ' ' || board[x2][y2] != '.' ) )
			return false;
		//System.out.println("a");
        return !((dX != 2 && dY != 1) && (dX != 1 && dY != 2));
    }
}

class Bishop extends Piece
{
	Bishop(String l, String o)
	{
		super(l,o);
	}
	
	public boolean validMove(int x1, int y1, int x2, int y2, char[][] board)
	{
		int dX = Math.abs(x2-x1);
		int dY = Math.abs(y2-y1);
		char key = owner.equals("cap") ? 'A' : 'a';
		if( Math.abs(board[x2][y2]-key) <= 26 && !(board[x2][y2] != ' ' || board[x2][y2] != '.' ) )
			return false;
		if( dX != dY )
			return false;
		return true;
	}
}

class King extends Piece
{
	private boolean moved;
	King(String l, String o)
	{
		super(l,o);
		moved = false;
	}
	
	public boolean validMove(int x1, int y1, int x2, int y2, char[][] board)
	{
		int dX = Math.abs(x2-x1);
		int dY = Math.abs(y2-y1);
		char key = owner.equals("cap") ? 'A' : 'a';
		if( Math.abs(board[x2][y2]-key) <= 26 && !(board[x2][y2] != ' ' || board[x2][y2] != '.' ) )
			return false;
		if( !moved && dX == 2 && dY == 0 && (board[x2+1][y2] == key+17 || board[x2-1][y2] == key+17) )
		{
			moved = true;
			return true;
		}
		if( dX > 1 || dY > 1 )
			return false;
		moved = true;
		return true;
	}
}

class Queen extends Piece
{
	Queen(String l, String o)
	{
		super(l,o);
	}
	
	public boolean validMove(int x1, int y1, int x2, int y2, char[][] board)
	{
		int dX = Math.abs(x2-x1);
		int dY = Math.abs(y2-y1);
		char key = owner.equals("cap") ? 'A' : 'a';
		if( Math.abs(board[x2][y2]-key) <= 26 && !(board[x2][y2] != ' ' || board[x2][y2] != '.' ) )
			return false;
        return (dX == dY) || (dX > 0 && dY == 0) || (dX == 0 && dY > 0);
    }
}

class Pawn extends Piece
{
	private boolean moved;
	Pawn(String l, String o)
	{
		super(l,o);
		moved = false;
	}
	
	public boolean validMove(int x1, int y1, int x2, int y2, char[][] board)
	{
		int dX = Math.abs(x2-x1);
		int dY = Math.abs(y2-y1);
		char key = owner.equals("cap") ? 'A' : 'a';
		if( Math.abs(board[x2][y2]-key) <= 26 && !(board[x2][y2] != ' ' || board[x2][y2] != '.' ) )
			return false;
		if( !moved && dY == 2 )
		{
			moved = true;
			return true;
		}
		if( dY > 1 || dX > 1 )
			return false;
		moved = true;
		return true;
	}
}