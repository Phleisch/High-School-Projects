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

abstract class Match implements java.io.Serializable
{
    protected Player playerOne;
    protected Player playerTwo;

    Match(Player one)
    {
        playerOne = one;
    }

    Match(Player one, Player two)
    {
        playerOne = one;
        playerTwo = two;
    }

    Player getPlayerOne(){return playerOne;}
    Player getPlayerTwo(){return playerTwo;}
    abstract String getType();

}

class PVP extends Match implements java.io.Serializable
{
    PVP(Player one)
    {
        super(one);
    }

    PVP(Player one, Player two)
    {
        super(one, two);
    }

    String getType(){return "PVP";}
}

class PVC extends Match implements java.io.Serializable
{
    PVC(Player one)
    {
        super(one);
    }

    PVC(Player one, Player two)
    {
        super(one, two);
    }

    String getType(){return "PVC";}
}