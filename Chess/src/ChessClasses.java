/**
 * Created by Kai W. Fleischman on 2/24/2017.
 */


class Player implements java.io.Serializable
{
    protected String name;
    protected Match match;

    Player(String name)
    {
        this.name = name;
    }

    String getName(){ return name; }
}

class Client extends Player implements java.io.Serializable
{

    Client(String name)
    {
        super(name);
    }

}

class Bot extends Player implements java.io.Serializable
{
    Bot(String name)
    {
        super(name);
    }
}

class Message implements java.io.Serializable
{
    private String message;
    private Client sender;

    Message(String message, Client sender)
    {
        this.message = message;
        this.sender = sender;
    }

    String getMessage(){return message;}
    String getSender(){return sender.getName();}
}

class Move implements java.io.Serializable
{
    private String player;
    private String move;
    Move(String color, String move)
    {
        player = color;
        this.move = move;
    }
    public String getPlayer() {
        return player;
    }
    public String getMove() {
        return move;
    }
}

abstract class Match implements java.io.Serializable, Comparable
{
    Player playerOne;
    private Player playerTwo;

    Match(Player one)
    {
        playerOne = one;
    }

    void addPlayerTwo(Client playerTwo) {this.playerTwo = playerTwo;}
    Player getPlayerOne(){return playerOne;}
    Player getPlayerTwo(){return playerTwo;}
    abstract String getType();

}

class PVP extends Match implements java.io.Serializable, Comparable
{
    PVP(Player one)
    {
        super(one);
    }

    String getType(){return "PVP";}

    @Override
    public int compareTo(Object o) {
        PVP curr = (PVP)o;
        return curr.getPlayerOne().getName().equals(playerOne.getName()) ? 0 : 1;
    }
}

class PVC extends Match implements java.io.Serializable, Comparable
{
    PVC(Player one)
    {
        super(one);
    }

    String getType(){return "PVC";}

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}

class Board implements java.io.Serializable
{
    private String[][] board;

    Board(String[][] board)
    {
        this.board = board;
    }

    String[][] getBoard() {return board;}
    void setBoard(String[][] m) {board=m;}
}

class Pieces
{
    Pieces() {}

    //Check if Bishop's move is valid
    public boolean validMoveB(int x1, int y1, int x2, int y2, String[][] board) {
        int dX = Math.abs(x2 - x1);
        int dY = Math.abs(y2 - y1);
        return pathIsClear(board, x1, y1, x2, y2) && dX == dY;
    }

    //Check if King's move is valid
    public boolean validMoveK(int x1, int y1, int x2, int y2, String[][] board)
    {
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);
        return !(dX > 1 || dY > 1);
    }

    //Check if Knight's move is valid
    public boolean validMoveN(int x1, int y1, int x2, int y2)
    {
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);
        return !((dX != 2 && dY != 1) && (dX != 1 && dY != 2));
    }

    //Check if Rook's move is valid
    public boolean validMoveR(int x1, int y1, int x2, int y2, String[][] board) {
        int dX = Math.abs(x2 - x1);
        int dY = Math.abs(y2 - y1);
        return pathIsClear(board, x1, y1, x2, y2) && !(dX != 0 && dY != 0);
    }

    //Check if Pawn's move is valid
    public boolean validMoveP(int x1, int y1, int x2, int y2, String[][] board)
    {
        boolean moved = true;
        if(( board[y1][x1].contains("B") && y1 == 1 ) || (board[y1][x1].contains("W") && y1 == 6))
            moved = false;
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);
        if( !moved && dY == 2 )
            return true;
        if(( board[y1][x1].contains("B") && y2-y1 < 0 ) || (board[y1][x1].contains("W") && y1-y2 < 0))
            return false;
        return !(dY > 1 || dX > 1);
    }

    //Check if Queen's move is valid
    public boolean validMoveQ(int x1, int y1, int x2, int y2, String[][] board)
    {
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);
        return pathIsClear(board, x1, y1, x2, y2) && ((dX == dY) || (dX > 0 && dY == 0) || (dX == 0 && dY > 0));
    }

    protected boolean pathIsClear(String[][] board, int x1, int y1, int x2, int y2)
    {
        boolean works = true;
        int changeX = x2-x1;
        int changeY = y2-y1;
        int max = Math.max(Math.abs(changeX),Math.abs(changeY));
        if(changeX!=0)
            changeX /= Math.abs(x2-x1);
        if(changeY!=0)
            changeY /= Math.abs(y2-y1);
        int currX = x1+changeX;
        int currY = y1+changeY;
        for(int i = 1; i < max; i++){
            if(!board[currX][currY].equals(""))
                works = false;
            currX+=changeX;
            currY+=changeY;
        }
        return works;
    }
}