import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.Date;
import java.awt.*;

class User implements java.io.Serializable
{
	
	private String name, designation;
	InetAddress ip;
	
	public User( String name, InetAddress ip )
	{
		
		this.name = name;
		this.ip = ip;
		designation = null;
		
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDesignation()
	{
		return designation;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDesignation(String designation)
	{
		this.designation = designation;
	}
	
	public String getType()
	{
		return "User";
	}
	
	public InetAddress getIP()
	{
		return ip;
	}
	
}


//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

class specialUser extends User
{
	
	private String name, designation;
	private Color textColor;
	private Font font;
	InetAddress ip;
	
	public specialUser( String name, InetAddress ip )
	{
		
		super(name, ip);
		
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDesignation()
	{
		return designation;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDesignation(String designation)
	{
		this.designation = designation;
	}
	
	public String getType()
	{
		return "User";
	}
	
}

//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

class Admin extends User
{
	
	private String name, designation;
	private Color textColor;
	private Font font;
	InetAddress ip;
	
	public Admin( String name, InetAddress ip )
	{
		
		super(name, ip);
		
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDesignation()
	{
		return designation;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setDesignation(String designation)
	{
		this.designation = designation;
	}
	
	public String getType()
	{
		return "User";
	}
	
}

//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

class Message implements java.io.Serializable
{
	
	private String message, type;
	private ChatRoom designation;
	private User sender;
	
	public Message( String message, ChatRoom designation, User sender )
	{
		
		this.message = message;
		this.designation = designation;
		this.sender = sender;
		type = "Message";
		
	}
	
	public Message( String message, ChatRoom designation, User sender, String type )
	{
		
		this.message = message;
		this.designation = designation;
		this.sender = sender;
		this.type = type;
		
	}
	
	public String getMessage()
	{
		
		return message;
		
	}
	
	public ChatRoom getDesignation()
	{
		
		return designation;
		
	}
	
	public User getSender()
	{
		
		return sender;
		
	}
	
	public String getType()
	{
		return type;
	}
	
}

//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

class ChatRoom implements java.io.Serializable
{
	
	String title;
	ArrayList<User> users;
	ArrayList<Message> messages;
	
	public ChatRoom( String title )
	{
		
		this.title = title;
		users = new ArrayList<User>();
		messages = new ArrayList<Message>();
		
	}
	
	public boolean addUser( User user )
	{
		
		if( users.indexOf(user) != -1 )
			return false;
		else
			users.add(user);
		return true;
		
	}
	
	public boolean removeUser( User user )
	{
		
		if( users.indexOf(user) == -1 )
			return false;
		else
			users.remove(users.indexOf(user));
		return true;
		
	}
	
	public boolean addMessage( Message message )
	{
		
		if( users.indexOf(message.getSender()) == -1 )
			return false;
		else
			messages.add(0,message);
		return true;
		
	}
	
	public boolean removeMessage( Message message )
	{
		
		if( users.indexOf(message.getSender()) != -1 )
			return false;
		else
			messages.remove(messages.indexOf(message));
		return true;
		
	}
	
	public String getType()
	{
		return "ChatRoom";
	}
	
	public ArrayList<Message> getMessages()
	{
		return messages;
	}
	
}

//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

interface messageSentListener
{
	
	void messageSent();
	
}

class colorfulLine
{

	private int startX,startY,lenX,lenY;
	Color color;

	public colorfulLine(int x, int y, int x1, int y1, Color color)
	{

		startX = x;
		startY = y;
		lenX = x1;
		lenY = y1;
		this.color = color;

	}

	public int getX()
	{
		return startX;
	}

	public int getY()
	{
		return startY;
	}

	public int getLenX()
	{
		return lenX;
	}

	public int getLenY()
	{
		return lenY;
	}

	public Color getColor()
	{
		return color;
	}

}
