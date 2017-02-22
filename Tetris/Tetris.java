/**
 * @(#)Tetris.java
 *
 *
 * @Kai Fleischman
 * @version 1.00 2016/11/2
 */

import java.applet.*;
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

public class Tetris extends Applet implements MouseListener, MouseMotionListener, KeyListener
{
    
    /** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */

	private AudioClip pointz = null; 
	private AudioClip rekt = null;
	private Scanner in;
    String[] colors = {"blue","green","orange","purple","red","yellow"};
    String[] shapes = {"Square","L","MirrorL","S","MirrorS","T","Long"};
    private Image backbuffer;
    private Graphics backg;
    private SHAPE current;
    private boolean moveLeft, moveRight, inPlay, gameOver;
    private ArrayList<BOX> boxes;
    private Long startT, elapsedT, dropT;
    private Color transparentGray, lightGray;
    private int levelsBroken, score, menuSize, currentWidth, powerUps, multiplier, pUCount;
    private int destroyedBoxes;
    private Font bigFont, smallFont;
    final private Color blue = new Color(51, 204, 255);
	final private Color green = new Color(0, 255, 0);
	final private Color orange = new Color(255, 153, 0);
	final private Color purple = new Color(153, 51, 255);
	final private Color red = new Color(255, 0, 0);
	final private Color yellow = new Color(255, 255, 0);
	private FontMetrics metrics;
    LongShape second;
    
    boolean control, shift, q;
    public void init()
    {
    	in = null;
    	try {
    		//in = new Scanner(new File("GameData.txt"));
			pointz = Applet.newAudioClip(new URL("HITMARKER.wav"));
			rekt = Applet.newAudioClip(new URL("HITMARKER.wav"));
			
		} catch (MalformedURLException murle) {
			System.out.println(murle);
		}
    	destroyedBoxes = 0;
    	pUCount = 0;
    	powerUps = 0;
    	lightGray = new Color(217, 217, 217);
    	menuSize = getWidth()-240;
    	second = new LongShape("?","green","one",120+menuSize,0);
    	score = 0;
    	control = shift = q = false;
    	gameOver = false;
    	levelsBroken = 0;
    	dropT = 1000L;
    	bigFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
    	smallFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
    	transparentGray = new Color(50,50,50,150);
    	startT = elapsedT = 0L;
    	//Listens to events from mouse and keyboard
    	addMouseListener(this);
		addMouseMotionListener(this);
        addKeyListener(this);
    	inPlay = false;
    	boxes = new ArrayList<BOX>();
    	
    	backbuffer = createImage(getWidth(), getHeight());
       	backg = backbuffer.getGraphics();
    	current = second;
    	
    }

    public void paint(Graphics g) 
    {
        update(g);
    }
    
    public void update(Graphics g)
    {
    	multiplier = (levelsBroken/5)+1;
    	if(pUCount>=4000+500*(destroyedBoxes/12) && powerUps == 0)
    	{
    		pUCount -= 4000+500*(destroyedBoxes/12);
    		powerUps++;
    	}
    	if(control && shift && q)
    	{
    		control = shift = q = false;
    		boxes.clear();
			current = randomShape();
			levelsBroken = 0;
			score = 0;
			pUCount = 0;
			gameOver = false;
			destroyedBoxes = 0;
    	}
    	
    	if(levelsBroken >= 32)
    	{
    			dropT = 250L - (levelsBroken-32)*5L;
    	}
    	else
    		dropT = 1000L - (levelsBroken/8)*250L;
    	moveLeft = moveRight = true;
    	for(Rectangle a : current.getRectangles())
    	{
    		if(a != null)
    			if(a.getX() <= menuSize)
    				moveLeft = false;
    	}
    	for(Rectangle a : current.getRectangles())
    	{
    		if(a != null)
    			if(a.getX()+24 >= getWidth())
    				moveRight = false;
    	}
    	backg.setColor(Color.WHITE);
        backg.fillRect(0,0,getWidth(),getHeight());
        gridLines(backg);
        if(startT == 0 && inPlay)
    		startT = System.currentTimeMillis();
    	else if(elapsedT<dropT && inPlay)
    		elapsedT = System.currentTimeMillis()-startT;	
    	else if(elapsedT >= dropT && inPlay)
    	{
    		score++;
    		pUCount++;
    		current.drop();
    		startT = elapsedT = 0L;
    	}
    	for(BOX a : boxes)
    		a.drawBox(backg);
    	current.drawShape(backg);
    	if(inPlay)
    		checkBounds();
    	if(gameOver)
    	{
    		inPlay = false;
    		gameOver(backg);
    	}
    	else if(!inPlay)
    		paused(backg);
    	drawMenu(backg);
    	g.drawImage(backbuffer, 0, 0, this);
    	repaint();
    }
    
    public void drawMenu(Graphics g)
    {
    	g.setFont(bigFont);
    	metrics = g.getFontMetrics();
    	g.setColor(Color.lightGray);
    	g.fillRect(0,0,menuSize,getHeight());
    	
    	g.setColor(blue);
    	g.drawString("T",(menuSize/2)-66,40);
    	g.setColor(green);
    	g.drawString("e",(menuSize/2)-44,40);
    	g.setColor(orange);
    	g.drawString("t",(menuSize/2)-22,40);
    	g.setColor(purple);
    	g.drawString("r",(menuSize/2),40);
    	g.setColor(red);
    	g.drawString("i",(menuSize/2)+22,40);
    	g.setColor(yellow);
    	g.drawString("s",(menuSize/2)+44,40);
    	
    	g.setColor(Color.BLACK);
    	g.drawString("Score",(menuSize/2)-(metrics.stringWidth("Score")/2),100);
    	g.setColor(Color.WHITE);
    	g.drawString(""+score,(menuSize/2)-(metrics.stringWidth(""+score)/2),130);
    	
    	g.setColor(Color.BLACK);
    	g.drawString("Boost",(menuSize/2)-(metrics.stringWidth("Boost")/2),190);
    	g.setColor(Color.WHITE);
    	if(powerUps >0)
    		g.drawString("Press F",(menuSize/2)-(metrics.stringWidth("Press F")/2),220);
    	else
    		g.drawString("None",(menuSize/2)-(metrics.stringWidth("None")/2),220);
    	
    	g.setColor(Color.BLACK);
    	g.drawString("Levels",(menuSize/2)-(metrics.stringWidth("Levels")/2),280);
    	g.setColor(Color.WHITE);
    	g.drawString(""+levelsBroken,(menuSize/2)-(metrics.stringWidth(""+levelsBroken)/2),310);
    	
    	
    }
    
    public void paused(Graphics g)
    {
    	g.setColor(transparentGray);
    	g.fillRect(menuSize,0,getWidth(),getHeight());
    	g.setColor(Color.WHITE);
    	g.setFont(bigFont);
    	g.drawString("Paused",54+menuSize,250);
    }
    
    public void gameOver(Graphics g)
    {
    	g.setColor(transparentGray);
    	g.fillRect(menuSize,0,getWidth(),getHeight());
    	g.setColor(Color.WHITE);
    	g.setFont(bigFont);
    	g.drawString("Game Over",21+menuSize,250);
    	g.setFont(smallFont);
    	g.drawString("Completed "+levelsBroken+" layers",10+menuSize,320);
    	g.setFont(smallFont);
    	g.drawString("Score "+score,10+menuSize,360);
    }
    
    public void gridLines(Graphics g)
    {
    	g.setColor(Color.BLACK);
    	for(int a = menuSize; a < getWidth(); a+=24)
    	{
   			g.drawLine(a,0,a,getHeight());
    	}
    	for(int b = 0; b < getHeight(); b+=24)
	   	{
	   		g.drawLine(menuSize,b,getWidth(),b);	
	   	}
    }
    
    public void checkBounds()
    {
    	boolean outOfBounds = false;
    	for(Rectangle a : current.getRectangles())
    	{
    		if(a.getY() >= getHeight() || getHeight() <= a.getY()+a.getHeight())
    		{
    			for(BOX b : current.getBoxes())
    			{
    				BOX clone = b.clone();
    				boxes.add(clone);
    			}
    			current = randomShape();
    			outOfBounds = true;
    			break;
    		}
    	}
    	
    	if(!outOfBounds)
    	{
    		ArrayList<BOX> toRemove = new ArrayList<BOX>();
    		for(Rectangle a : current.getRectangles())
    		{
    			for(BOX b : boxes)
    			{
    				if(overlap(a,b.getRect()) && !current.isFire())
    				{
    					for(BOX d : current.getBoxes())
		    			{
		    				if(d != null)
		    				{
		    					BOX clone = d.clone();
		    					boxes.add(clone);
		    				}
		    			}
		    			current = randomShape();
		    			outOfBounds = true;
		    			break;
    				}
    				else if(overlap(a,b.getRect()) && current.isFire())
    				{
    					toRemove.add(b);
    					destroyedBoxes++;
    					score += 25*multiplier;
    					pUCount += 25*multiplier;
    				}
    			}
    		}
    		if(toRemove.size()>0)
    		{
    			for(BOX a : toRemove)
    			{
    				boxes.remove(boxes.indexOf(a));
    			}
    		}
    	}
    	if(outOfBounds)
    		checkLevelComplete();
    }
    
    public SHAPE randomShape()
    {
    	int color = (int) (Math.random()*6);
    	int shape = (int) (Math.random()*7);
    	SHAPE next;
    	switch(shape)
    	{
    		case 0: next = new SquareShape("?",colors[color],"one",120+menuSize,0); break;
    		case 1: next = new LShape("?",colors[color],"one",120+menuSize,0); break;
    		case 2: next = new MirrorLShape("?",colors[color],"one",120+menuSize,0); break;
    		case 3: next = new SShape("?",colors[color],"one",120+menuSize,0); break;
    		case 4: next = new MirrorSShape("?",colors[color],"one",120+menuSize,0); break;
    		case 5: next = new TShape("?",colors[color],"one",120+menuSize,0); break;
    		case 6: next = new LongShape("?",colors[color],"one",120+menuSize,0); break;
    		default: next = new SquareShape("?",colors[color],"one",120+menuSize,0);
    	}
    	return next;
    }
    
    public boolean overlap(Rectangle a, Rectangle b)
    {
    	return (a.getX() == b.getX() && a.getY()+a.getHeight() == b.getY())
    	&& ((a.getX()+a.getWidth() == b.getX()+b.getWidth() && a.getY()+a.getHeight() == b.getY()));
    }
    
    public void checkLevelComplete()
    {
    	int highest = getHeight();
    	for(BOX a : boxes)
    	{
    		if(a.getRect().getY() <0)
    			gameOver = true;
    		if(a.getRect().getY() < highest)
    			highest = (int)a.getRect().getY();
    	}
    	int levels = (getHeight()-highest)/24;
    	BOX[][] filled = new BOX[levels][10];
    	for(BOX a : boxes)
    	{
    		filled[(int)(a.getRect().getY()-highest)/24][(int)(a.getRect().getX()-menuSize)/24] = a;
    	}
    	boolean complete = true;
    	for(int a = filled.length-1; a >= 0; a--)
    	{
    		for(int b = 0; b < filled[a].length; b++)
    		{
    			if(filled[a][b] == null)
    				complete = false;
    		}
    		if(complete)
    		{
    			destroyedBoxes+=10;
    			pUCount += 225*multiplier;
    			score += 225*multiplier;
    			levelsBroken++;
    			//int levelCompleted = a;
    			for(int b = 0; b < filled[a].length; b++)
	    		{
	    			boxes.remove(filled[a][b]);
	    		}
	    		if(a>0)
	    		{
	    			for(int c = a-1; c >= 0; c--)
    				{
		    			for(int b = 0; b < filled[a].length; b++)
	    				{
	    					if(filled[c][b] != null)
	    						filled[c][b].drop();
	    				}
    				}
	    		}
	    		
    		}
    		complete = true;
    	}
    	
    	
    }
    
    public void mouseClicked(MouseEvent e)  { }
	public void mouseMoved(MouseEvent e)    { }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseDragged(MouseEvent e)	{ }
    public void mouseExited(MouseEvent e) 	{ }
    public void mousePressed(MouseEvent e)	{ }
    public void mouseReleased(MouseEvent e)	{ }
    public void keyReleased(KeyEvent evt)   { }
   	public void keyTyped(KeyEvent evt)      { }
	public void keyPressed(KeyEvent evt)
	{
		if(evt.getKeyCode() == KeyEvent.VK_UP && inPlay)
		{
			current.rotate();
		}
		
		else if(evt.getKeyCode() == KeyEvent.VK_RIGHT && moveRight && inPlay)
		{
			current.rightShift();
		}
		
		else if(evt.getKeyCode() == KeyEvent.VK_LEFT && moveLeft && inPlay)
		{
			current.leftShift();
		}
		
		else if(evt.getKeyCode() == KeyEvent.VK_DOWN && inPlay)
		{
			current.drop();
			score++;
			pUCount++;
			startT = 0L;
		}
		
		else if(evt.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			control = true;
		}
		else if(evt.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			shift = true;
		}
		
		else if(evt.getKeyCode() == KeyEvent.VK_Q)
		{
			q = true;
		}
		
		else if(evt.getKeyCode() == KeyEvent.VK_SPACE)
		{
			inPlay = !inPlay;
		}
		
		else if(evt.getKeyCode() == KeyEvent.VK_F /*&& powerUps > 0*/)
		{
			current.setState("fire");
			powerUps--;
		}
		
		repaint();
	}
}