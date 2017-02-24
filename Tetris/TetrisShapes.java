import java.awt.Cursor;
import java.util.*;
import java.io.*; 
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.*;
import javax.imageio.ImageIO;

abstract class SHAPE implements Cloneable
{
	protected int menuSize = 160;
	protected Rectangle[] areas;
	protected BOX[] boxes;
	
	protected String state, color, position;
	protected int x, y, inc;
	
	public SHAPE(String s, String c, String p, int x, int y)
	{
		state = s;
		color = c;
		position = p;
		this.x = x;
		this.y = y;
		areas = new Rectangle[4];
		boxes = new BOX[4];
		inc = 0;
	}
	
	public void setState(String s)
	{
		state = s;
	}
	
	public boolean isFire()
	{
		return state.equals("fire");
	}
	
	public void drawShape(Graphics g)
	{
		if(state.equals("fire"))
			color = "fire";
		switch(position)
		{
			case "one": orientOne(g); break;
			case "two": orientTwo(g); break;
			case "three": orientThree(g); break;
			case "four": orientFour(g); break;
			default: orientOne(g);
		}
	}
	
	public Rectangle[] getRectangles()
	{
		return areas;
	}
	
	public BOX[] getBoxes()
	{
		return boxes;
	}
	
	public void rotate()
	{
		switch(position)
		{
			case "one": position = "two"; 		break;
			case "two": position = "three"; 	break;
			case "three": position = "four"; 	break;
			case "four": position = "one"; 		break;
			default: position = "one";
		}
	}
	
	public void leftShift()
	{
		x-=24;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void rightShift()
	{
		x+=24;
	}
	
	public void drop()
	{
		y+=24;
	}
	
	public String getPosition()
	{
		return position;
	}
	
	abstract public void orientOne(Graphics g);
	abstract public void orientTwo(Graphics g);
	abstract public void orientThree(Graphics g);
	abstract public void orientFour(Graphics g);
}

class SquareShape extends SHAPE
{
	public SquareShape(String s, String c, String p, int x, int y)
	{
		super(s,c,p,x,y);
	}
	
	public void orientOne(Graphics g)
	{
		if(x>=216+menuSize)
			inc = -24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+24+inc,y,color);
		BOX thr = new BOX(x+inc,y+24,color);
		BOX fou = new BOX(x+24+inc,y+24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientTwo(Graphics g)
	{
		orientOne(g);
	}
	public void orientThree(Graphics g)
	{
		orientOne(g);
	}
	public void orientFour(Graphics g)
	{
		orientOne(g);
	}
	
}

class LShape extends SHAPE
{
	public LShape(String s, String c, String p, int x, int y)
	{
		super(s,c,p,x,y);
	}
	
	public void orientOne(Graphics g)
	{
		if(x>=216+menuSize)
			inc = -24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+inc,y+24,color);
		BOX thr = new BOX(x+inc,y+48,color);
		BOX fou = new BOX(x+24+inc,y+48,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientTwo(Graphics g)
	{
		if(x==menuSize)
			inc = 48;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+inc,y-24,color);
		BOX thr = new BOX(x-24+inc,y,color);
		BOX fou = new BOX(x-48+inc,y,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientThree(Graphics g)
	{
		if(x==menuSize)
			inc = 24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x-24+inc,y,color);
		BOX thr = new BOX(x+inc,y+24,color);
		BOX fou = new BOX(x+inc,y+48,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientFour(Graphics g)
	{
		if(x>=192+menuSize)
			inc = -48;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+inc,y+24,color);
		BOX thr = new BOX(x+24+inc,y,color);
		BOX fou = new BOX(x+48+inc,y,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	
}

class MirrorLShape extends SHAPE
{

	public MirrorLShape(String s, String c, String p, int x, int y)
	{
		super(s,c,p,x,y);
	}
	
	public void orientOne(Graphics g)
	{
		if(x==menuSize)
			inc = 24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+inc,y+24,color);
		BOX thr = new BOX(x+inc,y+48,color);
		BOX fou = new BOX(x-24+inc,y+48,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientTwo(Graphics g)
	{
		if(x==menuSize)
			inc = 48;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+inc,y-24,color);
		BOX thr = new BOX(x-24+inc,y-24,color);
		BOX fou = new BOX(x-48+inc,y-24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientThree(Graphics g)
	{
		if(x>=216+menuSize)
			inc = -24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+inc,y+24,color);
		BOX thr = new BOX(x+inc,y+48,color);
		BOX fou = new BOX(x+24+inc,y,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientFour(Graphics g)
	{
		if(x>=192+menuSize)
			inc = -48;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+inc,y-24,color);
		BOX thr = new BOX(x+24+inc,y,color);
		BOX fou = new BOX(x+48+inc,y,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	
}

class TShape extends SHAPE
{
	
	public TShape(String s, String c, String p, int x, int y)
	{
		super(s,c,p,x,y);
	}
	
	public void orientOne(Graphics g)
	{
		if(x>=192+menuSize)
			inc = -48;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+24+inc,y,color);
		BOX thr = new BOX(x+48+inc,y,color);
		BOX fou = new BOX(x+24+inc,y+24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientTwo(Graphics g)
	{
		if(x>=216+menuSize)
			inc = -24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+24+inc,y,color);
		BOX thr = new BOX(x+inc,y-24,color);
		BOX fou = new BOX(x+inc,y+24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientThree(Graphics g)
	{
		if(x>=192+menuSize)
			inc = -48;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+24+inc,y,color);
		BOX thr = new BOX(x+48+inc,y,color);
		BOX fou = new BOX(x+24+inc,y-24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientFour(Graphics g)
	{
		if(x==menuSize)
			inc = 24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x-24+inc,y,color);
		BOX thr = new BOX(x+inc,y+24,color);
		BOX fou = new BOX(x+inc,y-24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	
}

class SShape extends SHAPE
{
	
	public SShape(String s, String c, String p, int x, int y)
	{
		super(s,c,p,x,y);
	}
	
	public void orientOne(Graphics g)
	{
		if(x>=192+menuSize)
			inc = -48;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+24+inc,y,color);
		BOX thr = new BOX(x+24+inc,y-24,color);
		BOX fou = new BOX(x+48+inc,y-24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientTwo(Graphics g)
	{
		if(x>=216+menuSize)
			inc = -24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+24+inc,y,color);
		BOX thr = new BOX(x+24+inc,y+24,color);
		BOX fou = new BOX(x+inc,y-24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientThree(Graphics g)
	{
		orientOne(g);
	}
	public void orientFour(Graphics g)
	{
		orientTwo(g);
	}
	
}

class MirrorSShape extends SHAPE
{
	
	public MirrorSShape(String s, String c, String p, int x, int y)
	{
		super(s,c,p,x,y);
	}
	
	public void orientOne(Graphics g)
	{
		if(x>=192+menuSize)
			inc = -48;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x+24+inc,y,color);
		BOX thr = new BOX(x+24+inc,y+24,color);
		BOX fou = new BOX(x+48+inc,y+24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientTwo(Graphics g)
	{
		if(x>=216+menuSize)
			inc = -24;
		else
			inc = 0;
		
		BOX one = new BOX(x,y,color);
		BOX two = new BOX(x,y+24,color);
		BOX thr = new BOX(x+24+inc,y,color);
		BOX fou = new BOX(x+24+inc,y-24,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientThree(Graphics g)
	{
		orientOne(g);
	}
	public void orientFour(Graphics g)
	{
		orientTwo(g);
	}
	
}

class LongShape extends SHAPE
{
	
	public LongShape(String s, String c, String p, int x, int y)
	{
		super(s,c,p,x,y);
	}
	
	public void orientOne(Graphics g)
	{
		BOX one = new BOX(x,y,color);
		BOX two = new BOX(x,y+24,color);
		BOX thr = new BOX(x,y+48,color);
		BOX fou = new BOX(x,y+72,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientTwo(Graphics g)
	{
		if(x>=192+menuSize)
			inc = -48;
		else if(x==0+120)
			inc = 24;
		else
			inc = 0;
		
		BOX one = new BOX(x+inc,y,color);
		BOX two = new BOX(x-24+inc,y,color);
		BOX thr = new BOX(x+24+inc,y,color);
		BOX fou = new BOX(x+48+inc,y,color);
		boxes[0] = one; boxes[1] = two; boxes[2] = thr; boxes[3] = fou;
		one.drawBox(g);
		two.drawBox(g);
		thr.drawBox(g);
		fou.drawBox(g);
		areas[0] = one.getRect();
		areas[1] = two.getRect();
		areas[2] = thr.getRect();
		areas[3] = fou.getRect();
	}
	public void orientThree(Graphics g)
	{
		orientOne(g);
	}
	public void orientFour(Graphics g)
	{
		orientTwo(g);
	}
	
}


class BOX implements Cloneable
{
	final private Color blue = new Color(51, 204, 255);
	final private Color green = new Color(0, 255, 0);
	final private Color orange = new Color(255, 153, 0);
	final private Color purple = new Color(153, 51, 255);
	final private Color red = new Color(255, 0, 0);
	final private Color yellow = new Color(255, 255, 0);
	final private Color flameOne = new Color(255, 204, 0);
	final private Color flameTwo = new Color(255, 157, 0);
	final private Color flameThree = new Color(255, 77, 0);
	final private Color flameFour = new Color(255, 100, 0);
	private Rectangle area;
	private Color current;
	private String color;
	private int xCoord, yCoord;
	
	public BOX(int x, int y, String color)
	{
		xCoord = x;
		yCoord = y;
		this.color = color;
		switch(color)
		{
			case "blue": current = blue; break;
			case "green": current = green; break;
			case "orange": current = orange; break;
			case "purple": current = purple; break;
			case "red": current = red; break;
			case "yellow": current = yellow; break;
			case "fire": current = randomFire(); break;
			default: current = blue;
		}
		area = new Rectangle(xCoord,yCoord,24,24);
	}
	
	private Color randomFire()
	{
		int rand = (int) (Math.random()*4);
		switch(rand)
		{
			case 0: return flameOne;
			case 1: return flameTwo;
			case 2: return flameThree;
			case 3: return flameFour;
			default: return flameFour;
		}
	}
	
	protected BOX clone()
	{
		BOX clone = new BOX(xCoord,yCoord,color);
		return clone;
	}
	
	public void drawBox(Graphics g)
	{
		int scale = 24;
		g.setColor(Color.BLACK);
		g.fillRect(xCoord,yCoord,scale,scale);
		
		g.setColor(current.darker());
		g.drawLine(xCoord+2,yCoord+3,xCoord+2,yCoord+scale-3);
		g.drawLine(xCoord+2,yCoord+scale-3,xCoord+scale-3,yCoord+scale-3);
		g.drawLine(xCoord+3,yCoord+4,xCoord+3,yCoord+scale-4);
		g.drawLine(xCoord+4,yCoord+scale-4,xCoord+scale-4,yCoord+scale-4);
		
		g.setColor(current.brighter());
		g.drawLine(xCoord+2,yCoord+2,xCoord+scale-3,yCoord+2);
		g.drawLine(xCoord+scale-3,yCoord+2,xCoord+scale-3,yCoord+scale-3);
		g.drawLine(xCoord+3,yCoord+3,xCoord+scale-4,yCoord+3);
		g.drawLine(xCoord+scale-4,yCoord+3,xCoord+scale-4,yCoord+scale-4);
		
		g.setColor(current);
		g.fillRect(xCoord+4,yCoord+4,scale-8,scale-8);
	}
	
	public Rectangle getRect()
	{
		return area;
	}
	
	public void drop()
	{
		yCoord += 24;
		area.setBounds(xCoord, yCoord, 24, 24);
	}
	
	public boolean inArea(int x, int y)
	{
		Rectangle f = area;
		if(( x >= f.getMinX() && x <= f.getMaxX() )
   		&& ( y >= f.getMinY() && y <= f.getMaxY()))
   			return true;
   		else
   			return false;
	}
}