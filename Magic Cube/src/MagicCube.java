/**
 * @(#)MagicCube.java
 *
 *
 * @Kai Fleischman 
 * @version 1.00 2017/2/3
 */
 
 //MUST FIX NOW - For some reason when turning the cube, at one point some of the vertices are set to zero and that messes up everything
 
 
 ////////// Things I need to fix ///////////
 // 1. Change the indexes of the fixed vertex - I accidentally defined it as the first face instead of the third face
 // for the front four pieces
 // 2. Change the code for Z-Axis rotation to make it more variable and to fit the changes in fixed vertices
 // Next to work on: X-Axis rotation
 ///////////////////////////////////////////
 

//import java.applet.*;
//import java.awt.Cursor;
import java.util.*;
import java.io.*; 
//import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
//import java.net.*;
//import javax.imageio.ImageIO;

public class MagicCube
{
	public static void main(String args[]) throws IOException
	{
		Simulator cube = new Simulator();
		cube.addWindowListener(new WindowAdapter()
		{public void windowClosing(WindowEvent e)
		{System.exit(0);}});
		cube.setSize(1000,650);
		cube.setVisible(true);
	}
}

class Simulator extends Frame implements MouseListener, MouseMotionListener, KeyListener
{
	private boolean changeMade;
	private Cube magic;
	private boolean shifting;
	private double hypotenuse;
	final int appX = 1000;
	final int appY = 650;
    private Image backbuffer;
    private Graphics backg;
    private Long startT, elapsedT, dropT;
    private Color transparentGray, lightGray;
    private Font bigFont, smallFont;
    final private Color blue = new Color(51, 204, 255);
	final private Color green = new Color(0, 255, 0);
	final private Color orange = new Color(255, 153, 0);
	final private Color purple = new Color(153, 51, 255);
	final private Color red = new Color(255, 0, 0);
	final private Color yellow = new Color(255, 255, 0);
	private FontMetrics metrics;
	private int rotate, lastX, lastY;
	private boolean firstRun;
	
	
    public void init()
    {
    	changeMade = true;
    	firstRun = true;
		int cubeSize = 360;
		hypotenuse = Math.sqrt(2*Math.pow(cubeSize /2,2));
    	magic = new Cube();
    	lastX = 0;
    	lastY = 0;
    	rotate = 0;
    	shifting = false;
    	lightGray = new Color(217, 217, 217);
    	dropT = 1000L;
    	bigFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
    	smallFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
    	transparentGray = new Color(50,50,50,150);
    	startT = elapsedT = 0L;
    	//Listens to events from mouse and keyboard
    	addMouseListener(this);
		addMouseMotionListener(this);
        addKeyListener(this);
    }
    
    Simulator() throws IOException
	{
		init();
	}

    public void paint(Graphics g) 
    {
    	if(firstRun)
    	{
    		firstRun = false;
    		backbuffer = createImage(getWidth(), getHeight());
       		backg = backbuffer.getGraphics();
    	}
        update(g);
    }
    
    public void update(Graphics g)
    {
    	//System.out.println(shifting);
    	rotate++;
    	backg.setColor(Color.WHITE);
        backg.fillRect(0,0,getWidth(),getHeight());
        backg.setColor(Color.GREEN);
        //rotate(backg,rotate);
        //backg.drawString(""+magic.getCube()[0][0][0].getX()[0],10,50);
        backg.drawString(""+lastX,10,50);
        backg.drawString(""+lastY,10,100);
        magic.drawCube(backg);
        if(changeMade)
        	printPoints();
       	int[] testX = {100,150,150,100};
       	int[] testY = {100,100,150,150};
       	//int[] testX = {100,100,150,150};
       	//int[] testY = {100,150,150,100};
       	backg.setColor(Color.BLACK);
       	//backg.fillRect(100,100,50,50);
       	backg.fillPolygon(testX,testY,4);
    	g.drawImage(backbuffer, 0, 0, this);
    	repaint();
    }
    
    private void printPoints()
    {
    	changeMade = false;
    	Part[][][] toPrint = magic.getCube();
    	for(int a = 0; a < 2; a++)
    	{
    		for(int b = 0; b < 2; b++)
    		{
    			for(int c = 0; c < 2; c++)
    			{
    				toPrint[b][c][a].identity(); System.out.println();
    				System.out.println("x1 : "+Arrays.toString(toPrint[b][c][a].getX1()));
    				System.out.println("y1 : "+Arrays.toString(toPrint[b][c][a].getY1()));
    				System.out.println("x2 : "+Arrays.toString(toPrint[b][c][a].getX2()));
    				System.out.println("y2 : "+Arrays.toString(toPrint[b][c][a].getY2()));
    				System.out.println();
    				
    			}
    		}
    	}
    }
    
    /*public void rotate(Graphics g, double degrees)
    {
    	double midX = appX/2;
    	double midY = appY/2;
    	double hyp = Math.sqrt(2*Math.pow(360,2))/2;
    	//g.drawString(""+rads,0,100);
    	int[] xPoints = new int[4];
    	int[] yPoints = new int[4];
    	for(int n = 0; n < 4; n++)
    	{
    		xPoints[n] = (int) (midX+(hyp*Math.cos(Math.toRadians(degrees+90*n))));
    	}
    	for(int n = 0; n < 4; n++)
    	{
    		yPoints[n] = (int) (midY+(hyp*Math.sin(Math.toRadians(degrees+90*n))));
    	}
    	g.fillPolygon(xPoints,yPoints,xPoints.length);
    }*/

    public void mouseClicked(MouseEvent e)
    {
    	lastX = e.getX();
    	lastY = e.getY();
    }
	public void mouseMoved(MouseEvent e)    { }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseDragged(MouseEvent e)
    {
    	changeMade = true;
    	int changeX = lastX - e.getX();
    	int changeY = e.getY() - lastY;
    	System.out.println(changeY);
    	//System.out.println("Change in X " + changeX);
    	//System.out.println("Change in Y " + changeX);
    	//System.out.println();
    	
    	////////////////////////////// Z-Axis Rotation /////////////////////////////
    	//////////////////////////////   In Progress   /////////////////////////////
    	if(true)//shifting)
    	{
    		/*
    		for(Part[][] atemp : magic.getCube())
    		{
    			for(Part[] btemp : atemp)
    			{
    				for(Part temp : btemp)
    				{
    					int[] tempX = temp.getX1();
    					int[] tempY = temp.getY1();
    					int[] returnX = new int[4];
    					int[] returnY = new int[4];
    					int[] neighbors = new int[2];
    					int nonNeighbor = 0;
    					int fixed = temp.getFixed();
    					switch(fixed)
    					{
    						case 0: nonNeighbor = 2; neighbors[0] = 1; neighbors[1] = 3; break;
    						case 1: nonNeighbor = 3; neighbors[0] = 0; neighbors[1] = 2; break;
    						case 2: nonNeighbor = 0; neighbors[0] = 1; neighbors[1] = 3; break;
    						case 3: nonNeighbor = 1; neighbors[0] = 0; neighbors[1] = 2; break;
    					}

    					//int pointX = tempX[i] - tempX[fixed];

    					//////////////////// Corner Angle, opposite of Fixed Angle //////////////////////
    					int noNeighX = tempX[nonNeighbor] - tempX[fixed];
    					int noNeighY = tempY[nonNeighbor] - tempY[fixed];
    					double nNX = noNeighX * 1.0; nNX/=hypotenuse;
    					double nNY = noNeighY * 1.0; nNY/=hypotenuse;
    					double noNeighAngle = Math.atan(nNY/nNX);
    					if(nNX < 0 && nNY < 0)
    						noNeighAngle+=Math.PI;
    					else if(nNX < 0)
    						noNeighAngle+=Math.PI;
    					else if(nNY < 0)
    						noNeighAngle+=2.0*Math.PI;
    					returnX[nonNeighbor] = (int) (Math.round(tempX[fixed] + hypotenuse * Math.cos(noNeighAngle + changeY*Math.PI/180.0)));
    					returnY[nonNeighbor] = (int) (Math.round(tempY[fixed] + hypotenuse * Math.sin(noNeighAngle + changeY*Math.PI/180.0)));

    					//////////////////// Adjacent Angles, neighbors of Fixed Angle //////////////////////
    					for(int i = 0; i < 2; i++)
    					{
    						int neighX = tempX[neighbors[i]] - tempX[fixed];
    						int neighY = tempY[neighbors[i]] - tempY[fixed];
    						double nX = neighX * 1.0; nX /= (cubeSize/2);
    						double nY = neighY * 1.0; nY /= (cubeSize/2);
    						double neighAngle = Math.atan(nY/nX);
    						if(nX < 0 && nY < 0)
	    						neighAngle+=Math.PI;
	    					else if(nX < 0)
	    						neighAngle+=Math.PI;
	    					else if(nY < 0)
	    						neighAngle+=2.0*Math.PI;
	    					returnX[neighbors[i]] = (int) (Math.round(tempX[fixed] + cubeSize/2 * Math.cos(neighAngle + changeY*Math.PI/180.0)));
    						returnY[neighbors[i]] = (int) (Math.round(tempY[fixed] + cubeSize/2 * Math.sin(neighAngle + changeY*Math.PI/180.0)));
    					}
    					returnX[fixed] = tempX[fixed];
    					returnY[fixed] = tempY[fixed];
    					temp.setX1(returnX);
    					temp.setY1(returnY);
    					temp.setX2(returnX);
    					temp.setY2(returnY);
    				}
    			}
    		}*/

    		for(Part[][] atemp : magic.getCube())
    		{
    			for(Part[] btemp : atemp)
    			{
    				for(Part temp : btemp)
    				{
    					int[] tempX1 = temp.getX1(); int[] tempX2 = temp.getX2();
    					int[] tempY1 = temp.getY1(); int[] tempY2 = temp.getY2();
    					int[] returnX1 = new int[4]; int[] returnX2 = new int[4];
    					int[] returnY1 = new int[4]; int[] returnY2 = new int[4];
    					int[] currentX = tempX1; int[] currentY = tempY1;
    					int fixed = temp.getFixed();
    					int fX;
    					int fY;
    					if(fixed<4)
    					{
    						fX = tempX1[fixed];
    						fY = tempY1[fixed];
    					}
    					else
    					{
    						fX = tempX2[fixed%4];
    						fY = tempY2[fixed%4];
    					}
    					for(int i = 0; i < 8; i++)
    					{
    						//System.out.println("fX: " + fX + " fY: " + fY);
    						int alt = i%4;
    						//System.out.println("Alt: " + alt + " I: " + i);
    						if(i>3)
    						{
    							currentX = tempX2;
    							currentY = tempY2;
    						}
    						int thisX = currentX[alt] - fX;
    						int thisY = fY - currentY[alt];
    						double thisHypotenuse = Math.sqrt(Math.pow(thisX,2)+Math.pow(thisY,2));
    						double dTX = thisX * 1.0; dTX /= thisHypotenuse;
    						double dTY = thisY * 1.0; dTY /= thisHypotenuse;
    						//System.out.println("Hypotenuse: " + thisHypotenuse);
    						double angle = Math.atan(dTY/dTX);
    						//double angle = Math.acos(dTX);
    						/*if(dTX < 0 && dTY < 0)
	    						angle+=Math.PI/2;
	    					else if(dTY < 0 && dTX > 0)
	    						angle-=Math.PI/2;//*/
	    					/**/if(dTX<0 && dTY<0)
	    						angle+=Math.PI;
	    					else if(dTX<0)
	    						angle+=Math.PI;//*/
	    					//temp.identity();
	    					//System.out.println("Angle - " + (180*angle/Math.PI));
	    					if(Double.isNaN(angle))
	    					{
	    						if(dTY<0)
	    							angle = 3.0*Math.PI/2.0;
	    						else if(dTY>0)
	    							angle = Math.PI/2.0;
	    						else
	    							thisHypotenuse = 0.0;
	    					}
	    					angle = angle + changeY*Math.PI/180.0;

	    					if(i<4)
	    					{
	    						//temp.identity();
	    						//System.out.println((int) (Math.round(fX*1.0 + hypotenuse * Math.cos(angle))));
	    						//System.out.print(" " + (int) (Math.round(fY*1.0 + hypotenuse * Math.sin(angle))));
	    						//System.out.println();
	    						//System.out.println(Math.round(fX*1.0 + hypotenuse * Math.cos(angle)));
	    						//System.out.println(fX*1.0 + thisHypotenuse * Math.cos(angle));
	    						returnX1[alt] = (int) (Math.round(fX*1.0 + thisHypotenuse * Math.cos(angle)));
    							returnY1[alt] = (int) (Math.round(fY*1.0 + thisHypotenuse * Math.sin(angle)));
    							if(i==fixed)
    							{
    								returnX1[alt] = fX;
    								returnY1[alt] = fY;
    							}
	    					}
	    					else
	    					{
	    						returnX2[alt] = (int) (Math.round(fX + thisHypotenuse * Math.cos(angle + changeY*Math.PI/180.0)));
    							returnY2[alt] = (int) (Math.round(fY + thisHypotenuse * Math.sin(angle + changeY*Math.PI/180.0)));
    							if(i==fixed)
    							{
    								returnX2[alt] = fX;
    								returnY2[alt] = fY;
    							}
	    					}
    					}
    					temp.setX1(returnX1); temp.setX2(returnX2);
    					temp.setY1(returnY1); temp.setY2(returnY2);
    					System.out.println();
    				}
    			}
    		}
    		System.out.println("//////////////////////////////////////\n");


    	}
    	/////////////////////////////////////////////////////////////////////////////////
    	/////////////////////////////////////////////////////////////////////////////////


    	////////////////////////////// X-Axis Rotation /////////////////////////////
    	//////////////////////////////   In Progress!  /////////////////////////////
    	else
    	{
    		for(Part[][] atemp : magic.getCube())
    		{
    			for(Part[] btemp : atemp)
    			{
    				for(Part temp : btemp)
    				{
    					int[] tempX1 = temp.getX1(); int[] tempX2 = temp.getX2();
    					int[] tempY1 = temp.getY1(); int[] tempY2 = temp.getY2();
    					int[] returnX1 = new int[4]; int[] returnX2 = new int[4];
    					int[] returnY1 = new int[4]; int[] returnY2 = new int[4];

    				}
    			}
    		}
    	}
    	lastX = e.getX();
    	lastY = e.getY();
    	repaint();
    }
    public void mouseExited(MouseEvent e) 	{ }
    public void mousePressed(MouseEvent e)	{ }
    public void mouseReleased(MouseEvent e)	{ }
    public void keyReleased(KeyEvent evt)   
    {
    	if(evt.getKeyCode() == KeyEvent.VK_SHIFT)
    		shifting = false;
    }
   	public void keyTyped(KeyEvent evt)      { }
	public void keyPressed(KeyEvent evt)
	{
		if(evt.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			shifting = true;
		}
		
		repaint();
	}
}

class Cube
{
	private Part[][][] pieces;
	private final int cubeSize = 360;
	private final int appY = 650;
	
	public Cube()
	{
		pieces = new Part[2][2][2];
		int appX = 1000;
		pieces[0][0][0] = new Part("G","O","W",(appX -cubeSize)/2,(appY-cubeSize)/2,(cubeSize),6);
		pieces[0][1][0] = new Part("G","R","W", appX /2,(appY-cubeSize)/2,(cubeSize),7);
		pieces[1][0][0] = new Part("G","O","Y",(appX -cubeSize)/2,appY/2,(cubeSize),5);
		pieces[1][1][0] = new Part("G","R","Y", appX /2,appY/2,(cubeSize),4);
		pieces[0][0][1] = new Part("B","O","W",(appX -cubeSize)/2,(appY-cubeSize)/2,(cubeSize/2),2);
		pieces[0][1][1] = new Part("B","R","W", appX /2,(appY-cubeSize)/2,(cubeSize/2),3);
		pieces[1][0][1] = new Part("B","O","Y",(appX -cubeSize)/2,appY/2,(cubeSize/2),1);
		pieces[1][1][1] = new Part("B","R","Y", appX /2,appY/2,(cubeSize/2),0);
	}
	
	public void rotate(String letter)
	{
		Part keep;
		switch(letter)
		{
			case "F":
				keep = pieces[0][0][0];
				pieces[0][0][0] = pieces[1][0][0];
				pieces[1][0][0] = pieces[1][1][0];
				pieces[1][1][0] = pieces[0][1][0];
				pieces[0][1][0] = keep;
				pieces[0][0][0].subZ();
				pieces[1][0][0].subZ();
				pieces[1][1][0].subZ();
				pieces[0][1][0].subZ();
				break;
				
			case "B":
				keep = pieces[0][0][1];
				pieces[0][0][1] = pieces[0][1][1];
				pieces[0][1][1] = pieces[1][1][1];
				pieces[1][1][1] = pieces[1][0][1];
				pieces[1][0][1] = keep;
				pieces[0][0][1].subZ();
				pieces[0][1][1].subZ();
				pieces[1][1][1].subZ();
				pieces[1][0][1].subZ();
				break;
			
			case "L":
				keep = pieces[0][0][1];
				pieces[0][0][1] = pieces[1][0][1];
				pieces[1][0][1] = pieces[1][0][0];
				pieces[1][0][0] = pieces[0][0][0];
				pieces[0][0][0] = keep;
				pieces[0][0][1].subX();
				pieces[1][0][1].subX();
				pieces[1][0][0].subX();
				pieces[0][0][0].subX();
				break;
				
			case "R":
				keep = pieces[0][1][0];
				pieces[0][1][0] = pieces[1][1][0];
				pieces[1][1][0] = pieces[1][1][1];
				pieces[1][1][1] = pieces[0][1][1];
				pieces[0][1][1] = keep;
				pieces[0][1][0].subX();
				pieces[1][1][0].subX();
				pieces[1][1][1].subX();
				pieces[0][1][1].subX();
				break;
				
			case "D":
				keep = pieces[1][0][0];
				pieces[1][0][0] = pieces[1][0][1];
				pieces[1][0][1] = pieces[1][1][1];
				pieces[1][1][1] = pieces[1][1][0];
				pieces[1][1][0] = keep;
				pieces[1][0][0].subY();
				pieces[1][0][1].subY();
				pieces[1][1][1].subY();
				pieces[1][1][0].subY();
				break;
				
			case "U":
				keep = pieces[0][0][1];
				pieces[0][0][1] = pieces[0][0][0];
				pieces[0][0][0] = pieces[0][1][0];
				pieces[0][1][0] = pieces[0][1][1];
				pieces[0][1][1] = keep;
				pieces[0][0][1].subY();
				pieces[0][0][0].subY();
				pieces[0][1][0].subY();
				pieces[0][1][1].subY();
		}
		
		pieces[0][0][0].display();
		pieces[0][1][0].display();
		System.out.println();
		pieces[1][0][0].display();
		pieces[1][1][0].display();
		System.out.println("\n");
	}
	
	public void initial()
	{
		pieces[0][0][0].display();
		pieces[0][1][0].display();
		System.out.println();
		pieces[1][0][0].display();
		pieces[1][1][0].display();
		System.out.println("\n");
	}
	
	public void drawCube(Graphics g)
	{
		Stack<Part> priority = new Stack<Part>();
		ArrayList<Part> transfer = new ArrayList<Part>();
		for(int a = 0; a < 2; a++)
			for(int b = 0; b < 2; b++)
				for(int c = 0; c < 2; c++)
					transfer.add(pieces[b][c][a]);
		while(transfer.size()>0)
		{
			int highPriority = Integer.MIN_VALUE;
			int index = 0;
			for(int i = 0; i < transfer.size(); i++)
			{
				int curr = transfer.get(i).getMaxZ();
				if(curr>highPriority)
				{
					highPriority = curr;
					index = i;
				}
			}
			priority.push(transfer.get(index));
			transfer.remove(index);
		}
		while(!priority.empty())
			priority.pop().draw(g);
	}
	
	public Part[][][] getCube()
	{
		return pieces;
	}	
}

class Part
{
	private String p1, p2, p3, front, out, up;
	private Color c1, c2, c3;
	private int[] x1, y1, z1;
	private int[] x2, y2, z2;
	private final int cubeSize = 360;
	private final int appX = 1000;
	private final int appY = 650;
	private int fixedIndex;
	private boolean backBotRight;
	
	public Part(String a, String b, String c, int x, int y, int z, int fixed)
	{
		fixedIndex = fixed;
		p1 = a;
		p2 = b;
		p3 = c;
		front = p1;
		out = p2;
		up = p3;
		if(z==cubeSize)
			backBotRight = true;
		else
			backBotRight = false;
		x1 = new int[]{x,x+cubeSize/2,x+cubeSize/2,x};
		x2 = new int[]{x,x+cubeSize/2,x+cubeSize/2,x};
		y1 = new int[]{y,y,y+cubeSize/2,y+cubeSize/2};
		y2 = new int[]{y,y,y+cubeSize/2,y+cubeSize/2};
		z1 = new int[]{z,z,z,z};
		z2 = new int[]{z-cubeSize/2,z-cubeSize/2,z-cubeSize/2,z-cubeSize/2};
		setColors();
	}
	
	private void setColors()
	{
		switch(p1)
		{
			case "G": c1 = Color.GREEN; break;
			case "B": c1 = Color.BLUE;
		}
		switch(p2)
		{
			case "O": c2 = Color.ORANGE; break;
			case "R": c2 = Color.RED;
		}
		switch(p3)
		{
			case "W": c3 = Color.GRAY; break;
			case "Y": c3 = Color.YELLOW;
		}
	}
	
	public void display()
	{
		System.out.print(front);
	}
	
	public void subX()
	{
		String hold = front;
		front = up;
		up = hold;
	}
	
	public void subY()
	{
		String hold = out;
		out = front;
		front = hold; 
	}
	
	public void subZ()
	{
		String hold = up;
		up = out;
		out = hold;
	}
	
	public int getMaxZ()
	{
		return findHighest(z1) > findHighest(z2) ? findHighest(z1) : findHighest(z2);
	}
	
	public void identity()
	{
		System.out.print(p1+p2+p3+": ");
	}
	
	public void draw(Graphics g)
	{
		
		// 1 = Front, 2 = Top, 3 = Back, 4 = Bottom, 5 = Left, 6 = Right //
		
		int[][] Face1 = {{x1[0],x1[1],x1[2],x1[3]},
						{y1[0],y1[1],y1[2],y1[3]},
						{z1[0],z1[1],z1[2],z1[3]}};
		
		int[][] Face2 = {{x1[0],x1[1],x2[1],x2[0]},
						{y1[0],y1[1],y2[1],y2[0]},
						{z1[0],z1[1],z2[1],z2[0]}};
		
		int[][] Face3 = {{x2[0],x2[1],x2[2],x2[3]},
						{y2[0],y2[1],y2[2],y2[3]},
						{z2[0],z2[1],z2[2],z2[3]}};
		
		int[][] Face4 = {{x1[3],x1[2],x2[2],x2[3]},
						{y1[3],y1[2],y2[2],y2[3]},
						{z1[3],z1[2],z2[2],z2[3]}};
		
		int[][] Face5 = {{x1[0],x1[3],x2[3],x2[0]},
						{y1[0],y1[3],y2[3],y2[0]},
						{z1[0],z1[3],z2[3],z2[0]}};
		
		int[][] Face6 = {{x1[1],x1[2],x2[2],x2[1]},
						{y1[1],y1[2],y2[2],y2[1]},
						{z1[1],z1[2],z2[2],z2[1]}};
		
		ArrayList<int[][]> transfer = new ArrayList<int[][]>();
		transfer.add(Face1); transfer.add(Face2); transfer.add(Face3);
		transfer.add(Face4); transfer.add(Face5); transfer.add(Face6);
		Stack<int[][]> priority = new Stack<int[][]>();
		int closest;
		int highest;
		while(transfer.size()>0)
		{
			closest = Integer.MIN_VALUE;
			highest = 0;
			for(int x = 0; x < transfer.size(); x++)
			{
				int curr = findHighest(transfer.get(x)[2]);
				if(curr>closest)
				{
					closest = curr;
					highest = x;
				}
			}
			priority.push(transfer.get(highest));
			transfer.remove(highest);
		}
		for(int x = 0; x < 6; x++)
		{
			int[][] curr = priority.pop();
			if(curr==Face1)
				g.setColor(c1);
			else if(curr==Face2)
				g.setColor(c2);
			else if(curr==Face5)
				g.setColor(c3);
			else
				g.setColor(Color.BLACK);
			int[] currX = curr[0];
			g.fillPolygon(curr[0],curr[1],4);
			g.setColor(Color.BLACK);
			g.drawPolygon(curr[0],curr[1],4);
		}
	}
	
	private int findHighest(int[] search)
	{
		int returnVal = Integer.MIN_VALUE;
		for(int a : search)
			if(a > returnVal)
				returnVal = a;
		return returnVal;
	}
	
	public int[] getX1(){return x1;}
	public int[] getY1(){return y1;}
	public int[] getZ1(){return z1;}
	public int[] getX2(){return x2;}
	public int[] getY2(){return y2;}
	public int[] getZ2(){return z2;}
	public int getFixed(){return fixedIndex;}
	public boolean getBackBottomRight(){return backBotRight;}
	public void setX1(int[] a){x1=a;}
	public void setY1(int[] a){y1=a;}
	public void setZ1(int[] a){z1=a;}
	public void setX2(int[] a){x2=a;}
	public void setY2(int[] a){y2=a;}
	public void setZ2(int[] a){z2=a;}
			
}