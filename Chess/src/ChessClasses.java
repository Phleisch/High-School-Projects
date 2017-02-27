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

class Match implements java.io.Serializable
{
    protected Player playerOne;
    protected Player playerTwo;
    protected String type;

    Match(Player one, String type)
    {
        playerOne = one;
        this.type = type;
    }

    Match(Player one, Player two, String type)
    {
        playerOne = one;
        playerTwo = two;
        this.type = type;
    }

    Player getPlayerOne(){return playerOne;}
    Player getPlayerTwo(){return playerTwo;}
    String getType(){return type;}

}

class PVP extends Match implements java.io.Serializable
{
    PVP(Player one, String type)
    {
        super(one, type);
    }

    PVP(Player one, Player two, String type)
    {
        super(one, two, type);
    }
}

class PVC extends Match implements java.io.Serializable
{
    PVC(Player one, String type)
    {
        super(one, type);
    }

    PVC(Player one, Player two, String type)
    {
        super(one, two, type);
    }
}