//This program will graph functions simply take in an equation such as 1/2(x)+x^3 and then prompt the user to define the variable
//Variable: x
//Once this happens, there will be an arraylist of points stored which will then be displayed left to right


/**
 * @(#)Graphine.java
 *
 *
 * @author 
 * @version 1.00 2016/10/26
 */

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

public class Graphine extends Applet implements MouseListener, MouseMotionListener, KeyListener{
    
    Cursor hand = new Cursor(Cursor.HAND_CURSOR);
    Cursor DefaultC = new Cursor(Cursor.DEFAULT_CURSOR);
    private ArrayList<point> points;
    private point Origin, prevPoint;
    private Function function;
    private boolean wordEntered, functionOn, refresh;
    private String localInput;
    private Image backbuffer;
    private Graphics backg;
    private Font Default, bigFont, mediumFont, smallFont;
    private int getWidth, getHeight, currentWidth, inputLength, tempOriginX, tempOriginY;
    private double increment, actualX, actualY, lastIncChange;
    private Rectangle homeButton;
    private String before, after;
    private Long startT, elapsedT;
    private Color transparentGray, babyBlue, lightGrey, grey;
    private final char[] typableCharacters = {'a','b','c','d','e','f','g','h','i','j','k','l',
	'm','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H',
	'I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3',
	'4','5','6','7','8','9','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[',
	'}',']',':',';','"','<',',','>','.','?','/','\\','|','\'',' '};
	
    public void init()
    {
    	startT = elapsedT = 0L;
    	before = after = "";
    	lastIncChange = 1000;
    	increment = 1.0;
    	refresh = true;
    	prevPoint = new point(0,0);
    	points = new ArrayList<point>();
    	functionOn = false;
    	function = null;
    	inputLength = 40;
        bigFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
    	mediumFont = new Font(Font.MONOSPACED, Font.BOLD, 20);
    	smallFont = new Font(Font.MONOSPACED, Font.BOLD, 12);	
		Default = new Font("Monospaced",Font.PLAIN,40);
    	localInput = "";
    	addMouseListener(this);
		addMouseMotionListener(this);
        addKeyListener(this);
    	transparentGray = new Color(50,50,50,150);
    	babyBlue = new Color(0,191,255);
    	wordEntered = false;
		currentWidth = 0;
		getWidth = 1000;
		getHeight = 650;
		lightGrey = new Color(188,184,184,200);
		grey = new Color(215,215,215);
		//create the backbuffer image that will later be swapped to the screen
       	backbuffer = createImage(getWidth, getHeight);
       	//get the backbuffer's graphics (canvas) so that we can draw on it
       	backg = backbuffer.getGraphics();
       	homeButton = new Rectangle(900,15,24,20);
				
		//chatRoomOneButton = new Rectangle((getWidth/2-borderLeft)-200,borderTop+39+spacing,400,50);
    	//chatRoomTwoButton = new Rectangle((getWidth/2-borderLeft)-200,borderTop+89+2*spacing,400,50);
    	//chatRoomThreeButton = new Rectangle((getWidth/2-borderLeft)-200,borderTop+139+3*spacing,400,50);
    	//backButton = new Rectangle(borderLeft,borderTop,120,30);
    	
        ArrayList<point> points = new ArrayList<point>();
        Function function = null;
        Origin = new point(getWidth/2,getHeight/2);
        tempOriginX = (int)Origin.getXValue();
        tempOriginY = (int)Origin.getYValue();
        actualX = getWidth;
        actualY = getHeight;
    }

    public void paint(Graphics g)
    {
        getWidth = getWidth();
        getHeight = getHeight();
        update(g);
    }
    
    public void update(Graphics g)
    {
    	backg.setColor(Color.WHITE);
    	backg.fillRect(0,0,getWidth,getHeight);
    	backg.setFont(Default);
    	backg.setColor(Color.BLACK);
    	int[] xPoints = {900,912,924};
    	int[] yPoints = {25,15,25};
    	backg.fillPolygon(xPoints,yPoints,3);
    	backg.fillRect(904,25,16,10);
    	backg.setColor(Color.WHITE);
    	backg.fillRect(909,27,6,8);
    	backg.setColor(Color.BLACK);
    	/*if(actualX>500 && lastIncChange < 500)
    	{
    		increment = .1;
    		if(functionOn)
    		points = function.populateArray(-1*(actualX/2),actualX/2,increment);
    		lastIncChange = actualX;
    	}
    	else if((actualX<500 && actualX>250) && (lastIncChange >= 500 || lastIncChange <= 250))
    	{
    		increment = .01;
    		if(functionOn)
    		points = function.populateArray(-1*(actualX/2),actualX/2,increment);
    		lastIncChange = actualX;
    	}
    	else if((actualX<250 && actualX>100) && (lastIncChange >= 250 || lastIncChange <= 100))
    	{
    		increment = .005;
    		if(functionOn)
    		points = function.populateArray(-1*(actualX/2),actualX/2,increment);
    		lastIncChange = actualX;
    	}
    	else if((actualX<100 && actualX>50) && (lastIncChange >= 100 || lastIncChange <= 50))
    	{
    		increment = .0005;
    		if(functionOn)
    		points = function.populateArray(-1*(actualX/2),actualX/2,increment);
    		lastIncChange = actualX;
    	}
    	else if(actualX<50 && lastIncChange >= 50)
    	{
    		increment = .0001;
    		if(functionOn)
    		points = function.populateArray(-1*(actualX/2),actualX/2,increment);
    		lastIncChange = actualX;
    	}
    	
    	if(actualX <= 5)
    		increment = .000001;*/
    	//increment = .1;
    	
    	backg.drawString("Increment: "+increment,10,600);
    	gridLines(backg);
    	//backg.drawString("Hello World",10,50);
    	if(functionOn && refresh)
    	{
    		refresh = false;
    	//	points = function.populateArray(tempOriginX-1500,tempOriginX+750,1);
    		startT = System.currentTimeMillis();
    		
    		points = function.populateArray(-1*(actualX/2),actualX/2,increment);
    		elapsedT = ((new Date()).getTime() - startT);
    	}
    	backg.drawString("Ms Elapsed: "+elapsedT,500,500);
    	//if(!refresh)
    	//	backg.drawString("Process Completed",10,500);
    	backg.drawString("ActualX: "+actualX,10,500);
    	backg.drawString("ActualY: "+actualY,10,550);
    	graph(backg);
    	backg.drawString("Origin X: "+tempOriginX,10,400);
    	backg.drawString("Origin Y: "+tempOriginY,10,450);
    	backg.drawOval(tempOriginX-10,tempOriginY-10,20,20);
    	backg.drawString(localInput,10,100);
    	g.drawImage(backbuffer, 0, 0, this);
    	repaint();
    }
    
    public void gridLines(Graphics g)
    {
    	int xOff = tempOriginX%25;
    	int yOff = tempOriginY%25;
    	int factorX = ((int) Math.ceil(tempOriginX/1000))+2;
    	int factorY = ((int) Math.ceil(tempOriginY/650))+2;
    	int displacement = -(500-tempOriginX);
    	
    	g.setFont(smallFont);
    	for(int x = tempOriginX-1500*factorX; x<= tempOriginX+1500*factorX; x+=25)
    	{
    		if((x+xOff)%100 == 0 || (x-xOff)%100 == 0)
    		{
    			double ate = x;
    			double aX = actualX;
    			double gW = getWidth;
    			double ratio = gW/aX;
    			double output = formatNum((ate-500.0)/ratio);
    			g.drawString(""+output,x,35);
    		}
    		
    		g.drawLine(x,-1500,x,1500);
    	}
    	
    	for(int y = tempOriginY-1500*factorY; y<= tempOriginY+1500*factorY; y+=25)
    	{
    		g.drawLine(-1500,y,1500,y);
    	}
    }
    
    /*public void gridLines(Graphics g)
    {
    	
    	for(int x = -1000; x<= 1000; x+=10)
    	{
    		g.drawLine((int)Origin.getXValue()+x,-1000,0,1000);
    		g.drawLine(-1000,(int)Origin.getYValue()+x,1000,0);
    	}
    	
    }*/
    
    public double formatNum(Double dub)
    {
    	String a = ""+dub;
    	a = a.substring(0,a.indexOf(".")+2);
    	return Double.parseDouble(a);
    }
    
    public void graph(Graphics g)
    {
    	for(point p : points)
    	{
    		//System.out.println("X: "+transpose(p.getXValue(),true)+", Y: "+transpose(p.getYValue(),false));
    		g.drawRect(transpose(p.getXValue(),true),transpose(p.getYValue(),false),1,1);
    	}
    }
    
    public int transpose(double a, boolean x)
    {
    	double gW = getWidth;
    	double gH = getHeight;
    	double aX = actualX;
    	double aY = actualY;
    	double ratioX = gW/aX;
    	double ratioY = gH/aY;
    	
    	if(x)
    		return (int)(a*ratioX+tempOriginX);
    	else
    		return (int) (tempOriginY+(-a*ratioY));
    }
    
    public boolean inArea( int x, int y, Rectangle f)
    {
    	if(( x >= f.getMinX() && x <= f.getMaxX() )
   		&& ( y >= f.getMinY() && y <= f.getMaxY()))
   			return true;
   		else
   			return false;
    }
    
   	public int validChar(char a)
	{
		for( char check : typableCharacters )
			if( a == check )
				return 0;
		return -1;		
	}
    
    public void mouseClicked(MouseEvent e)  
	{	
		//refresh = true;
		int pressX = e.getX();
    	int pressY = e.getY();
    	if(inArea(pressX,pressY,homeButton))
    	{
    		actualX = 30;
    		actualY = 20;
    		tempOriginX = 500;
    		tempOriginY = 325;
    	}
    	repaint();
	}
	
	
    public void mouseMoved(MouseEvent e)    { }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseDragged(MouseEvent e)
    {
    	//refresh = true;
    	int pressX = e.getX();
    	int pressY = e.getY();
    	int diffX = (int) (pressX - prevPoint.getXValue());
    	int diffY = (int) (pressY - prevPoint.getYValue());
    	tempOriginX = (int) Origin.getXValue()+(diffX);
    	tempOriginY = (int) Origin.getYValue()+(diffY);
    	//Origin.setXValue(Origin.getXValue()+(diffX));
    	//Origin.setYValue(Origin.getYValue()+(diffY));
    	
    }
    public void mouseExited(MouseEvent e) 	{ }
    public void mousePressed(MouseEvent e)
    {
    	//refresh = true;
    	setCursor(hand);
    	prevPoint.setXValue(e.getX());
    	prevPoint.setYValue(e.getY());
    }
    public void mouseReleased(MouseEvent e)
    {
    	//refresh = true;
    	setCursor(DefaultC);
    	Origin.setXValue(tempOriginX);
    	Origin.setYValue(tempOriginY);
    }
    public void keyReleased(KeyEvent evt)   { }
   	public void keyTyped(KeyEvent evt)      { }
	public void keyPressed(KeyEvent evt)
	{
	   	//localInput = "WHY WON'T THIS WORK?";
	   	
	   	if(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE)
	   	{
	   		if(localInput.length() != 0)
	   			localInput = localInput.substring(0,localInput.length()-1);
	   	}
	   		
	   	else if(evt.getKeyCode() == KeyEvent.VK_LEFT/* && actualX > 30 && actualY > 30*/)
	   	{
	   		if(actualX > 5)
	   			actualX -= 5;
	   		else if(actualX > 1)
	   			actualX -= 1;
	   		else if(actualX > .1)
	   			actualX -= .1;
	   		else if(actualX > .01)
	   			actualX -= .01;
	   	}
	   	else if(evt.getKeyCode() == KeyEvent.VK_RIGHT)
	   	{
	   		actualX += 5;
	   	}
	   	else if(evt.getKeyCode() == KeyEvent.VK_UP)
	   	{
	   		actualY += 5;
	   	}
	   	else if(evt.getKeyCode() == KeyEvent.VK_DOWN)
	   	{
	   		if(actualY > 5)
	   			actualY -= 5;
	   		else if(actualY > 1)
	   			actualY -= 1;
	   		else if(actualY > .1)
	   			actualY -= .1;
	   		else if(actualY > .01)
	   			actualY -= .01;
	   	}
	   	else if(evt.getKeyCode() == KeyEvent.VK_BACK_SLASH)
	   	{
	   		increment /= 10;
	   		refresh = true;
	   	}
	   	else if(evt.getKeyCode() == KeyEvent.VK_P)
	   	{
	   		increment *= 10;
	   		refresh = true;
	   	}
	   	else if(evt.getKeyCode() == KeyEvent.VK_ENTER)
	   	{
	   		if(localInput.indexOf("x") == -1 && localInput.indexOf(">")!= -1)
	   		{
	   			String variable = "" + localInput.charAt(localInput.indexOf(">")+1);
	   			String func = localInput.substring(0,localInput.length()-3);
	   			function = new Function(func,variable);
	   		}
	   		else
	   			function = new Function(localInput,"x");
	   		functionOn = true;
	   		refresh = true;
	   	}	
	   		
	   	
		else
		{
			if( validChar(evt.getKeyChar()) != -1 )
	   			localInput = localInput + evt.getKeyChar();
	   		functionOn = false;
	   		//if(points.size() > 0)
	   		//	points.clear();
		}
			
		
		repaint();
			
	}
    
}

class point
{
	
	private double xValue, yValue;
	
	public point(double x, double y)
	{
		xValue = x;
		yValue = y;
	}
	
	public double getXValue()
	{
		return xValue;
	}
	
	public double getYValue()
	{
		return yValue;
	}
	
	public void setXValue(double x)
	{
		xValue = x;
	}
	
	public void setYValue(double y)
	{
		yValue = y;
	}
}

class Function
{
	
	private String variable, function;
	
	public Function(String f, String v)
	{
		function = f;
		variable = v;
	}
	
	public ArrayList<point> populateArray(double xStart, double xFin, double increment)
	{
		ArrayList<point> temp = new ArrayList<point>();
		for(double x = xStart; x <= xFin; x+=increment)
		{
			double y = x;
			String tryThis = ""+y;
			if(tryThis.indexOf("E") != -1)
				y = 0;
			double u = calculateValue(y);
			if(u<600 && u >-600)
				temp.add(new point(y,u));
		}
		return temp;
	}
	
	public double calculateValue(double x)
	{
		String tempFunc = function;
		tempFunc = dumbDown(function,x);
		return parseValue(tempFunc);
	}
	
	/*public double realValue(ArrayList<String> parts)
	{
		double current = 0.0;
		for(int x = 0; x<parts.size(); x++)
		{
			if(x==0)
				current = parseValue(parts.get(0));
			else
				current = parseValue(current+""+parts.get(x));
		}
		return current;
	}*/
	
	public String dumbDown(String expression, double xValue)
	{
		String temp = expression;
		temp = temp.replaceAll(" ", "");
		temp = temp.replaceAll(variable,""+xValue);
		temp = temp.replaceAll("e",""+Math.E);
		temp = temp.replaceAll("pi",""+Math.PI);
		return temp;
	}
	
	/*public ArrayList<String> splitFunction(String expression)
	{
		ArrayList<String> functionParts = new ArrayList<String>();
		int begPart = 0;
		int closeIndex = 0;
		for(int x = 0; x < expression.length(); x++)
		{
			if(x!=0 && expression.charAt(x) == '(')
			{
				if(expression.charAt(x-1) != '(')
				{
					closeIndex = x;
					functionParts.add(expression.substring(begPart,closeIndex));
				}
			}
			else if(expression.charAt(x) == '(')	{}
			else if(expression.charAt(x) == ')' && expression.charAt(x-1) != ')')
			{
				closeIndex = x;
				functionParts.add(expression.substring(begPart,closeIndex));
			}
			else if(expression.charAt(x) == ')')	{}
			else
			{
				if(expression.charAt(x-1) == '(' || expression.charAt(x-1) == ')')
				{
					begPart = x;
				}
			}
		}
		System.out.println(functionParts);
		return functionParts;
	}*/
	
	/*public ArrayList<String> splitFunction(String expression)
	{
		ArrayList<String> functionParts = new ArrayList<String>();
		int begPart = 0;
		int closeIndex = 0;
		for(int x = 0; x < expression.length(); x++)
		{
			if(expression.charAt(x) == '('){}
			else if(expression.charAt(x) == ')' && expression.charAt(x-1) != ')')
			{
				closeIndex = x;
				functionParts.add(expression.substring(begPart,closeIndex));
			}
			else
			{
				if(expression.charAt(x-1) == '(' || expression.charAt(x-1) == ')')
				{
					begPart = x;
				}
			}
		}
		//System.out.println(functionParts);
		return functionParts;
	}*/
	private String[] findIndOper(String expression)
	{
		String temp = expression;
		String indOpr = "";
		int firstOpenParen, lastCloseParen, totalOpenParen;
		firstOpenParen = lastCloseParen = -1;
		totalOpenParen = 0;
		String[] parts = new String[3];
		parts[0] = parts[1] = parts[2] = "";
		for(int x = 0; x < expression.length(); x++)
		{
			if(firstOpenParen == -1 && expression.charAt(x) == '(')
			{
				firstOpenParen = x;
				totalOpenParen++;
			}
			else if(expression.charAt(x) == '(')
				totalOpenParen++;
			else if(expression.charAt(x) == ')' && totalOpenParen > 1)
				totalOpenParen--;
			else if(expression.charAt(x) == ')' && totalOpenParen == 1)
			{
				lastCloseParen = x;
				x = expression.length()+1;
			}
		}
		if(firstOpenParen ==0)
			parts[0] = expression.substring(firstOpenParen,lastCloseParen+1);
		else if(lastCloseParen == expression.length()-1)
			parts[2] = expression.substring(firstOpenParen,lastCloseParen+1);
		
		temp = expression.replace(expression.substring(firstOpenParen,lastCloseParen+1),"");
		if(temp.indexOf("(") != -1)
		{
			firstOpenParen = lastCloseParen = -1;
			totalOpenParen = 0;
			for(int x = 0; x < temp.length(); x++)
			{
				if(firstOpenParen == -1 && temp.charAt(x) == '(')
				{
					firstOpenParen = x;
					totalOpenParen++;
				}
				else if(temp.charAt(x) == '(')
					totalOpenParen++;
				else if(temp.charAt(x) == ')' && totalOpenParen > 1)
					totalOpenParen--;
				else if(temp.charAt(x) == ')' && totalOpenParen == 1)
				{
					lastCloseParen = x;
					x = temp.length()+1;
				}
			}
			if(firstOpenParen == 0)
				parts[0] = temp.substring(firstOpenParen,lastCloseParen+1);
			else if(lastCloseParen == temp.length()-1)
				parts[2] = temp.substring(firstOpenParen,lastCloseParen+1);
			temp = temp.replace(temp.substring(firstOpenParen,lastCloseParen+1),"");
		}
		//System.out.println("Temp: "+temp);
		indOpr = temp;
		if(indOpr.length() == 1)
			parts[1] = indOpr;
		else if(parts[0].equals(""))
		{
			parts[0] = indOpr.substring(0,indOpr.length()-1);
			parts[1] = ""+indOpr.charAt(indOpr.length()-1);
		}
		else if(parts[2].equals(""))
		{
			parts[1] = ""+indOpr.charAt(0);
			parts[2] = indOpr.substring(1);
		}
		//for(String a : parts)
			//System.out.println(a);
		//indOpr = "#"+indOpr+"#";
		//System.out.println(temp);
		return parts;
	}
	private double parseValue(String expression)
	{
		//expression = expression.replaceAll("E","");
		//System.out.println(expression);
		String[] parts = new String[3];
		if(expression.indexOf("(") != -1)
			expression = expression.substring(1,expression.length()-1); //gets rid of the packaging parentheses
		
		if(expression.indexOf("(") == -1)
		{
			//System.out.println(expression);
			if(expression.contains("+"))
			{
				parts = expression.split("\\+");
				return Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
			}
			else if(expression.contains("*"))
			{
				parts = expression.split("\\*");
				return Double.parseDouble(parts[0]) * Double.parseDouble(parts[1]);
			}
			else if(expression.contains("/"))
			{
			//	System.out.println(expression);
				parts = expression.split("/");
				return Double.parseDouble(parts[0]) / Double.parseDouble(parts[1]);
			}
			else if(expression.contains("^"))
			{
				parts = expression.split("\\^");
				return Math.pow(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
			}
			else if(expression.contains("-"))
			{
				if(expression.charAt(0) != '-' && expression.indexOf("E") == -1)
				{
					parts = expression.split("-");
					return Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
				}
				else if(expression.indexOf("-",1) != -1 && expression.indexOf("E") == -1)
				{
					int removeIndex = expression.indexOf("-",1);
					parts[0] = expression.substring(0,removeIndex);
					parts[1] = expression.substring(removeIndex+1,expression.length());
					return Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
				}
				else if(expression.indexOf("E") != -1)
				{
					int firstNegative = -1;
					for(int x = expression.length()-1; x >= 0; x--)
					{
						if(expression.charAt(x) == '-')
						{
							firstNegative = x;
							x = -1;
						}
					}
					if(expression.charAt(firstNegative-1) == '-')
					{
						parts[1] = expression.substring(firstNegative);
						parts[0] = expression.substring(0,firstNegative-1);
					}
					else
					{
						parts[0] = expression.substring(0,firstNegative);
						parts[1] = expression.substring(firstNegative+1);
					}
					return Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
				}
				else
				{
					expression = expression.replaceAll("E","");
					return Double.parseDouble(expression);
				}
			}
			else if(expression.indexOf("abs") != -1)
			{
				//parts[0] = expression.substring(0,4) +expression.substring(expression.length()-1);
				return Math.abs(Double.parseDouble(expression.substring(4,expression.length()-1)));
			}
			/*else if(expression.indexOf("abs") != -1)
			{
				//parts[0] = expression.substring(0,4) +expression.substring(expression.length()-1);
				return Math.abs(Double.parseDouble(expression.substring(4,expression.length()-1)));
			}*/
			else
				return Double.parseDouble(expression);
		}
		
		else
		{
			parts = findIndOper(expression);
			if(parts[1].equals("+"))
			{
				return parseValue(parts[0]) + parseValue(parts[2]);
			}
			else if(parts[1].equals("*"))
			{
				return parseValue(parts[0]) * parseValue(parts[2]);
			}
			else if(parts[1].equals("/"))
			{
				return parseValue(parts[0]) / parseValue(parts[2]);
			}
			else if(parts[1].equals("^"))
			{
				return Math.pow(parseValue(parts[0]), parseValue(parts[2]));
			}
			else if(parts[1].equals("-"))
			{
				return parseValue(parts[0]) - parseValue(parts[2]);
			}
			else if(expression.indexOf("abs") != -1)
			{
				return Math.abs(parseValue(expression.substring(expression.indexOf("("),expression.length()-1)));
			}
			else if(expression.indexOf("acos") != -1)
			{
				return Math.acos(parseValue(expression.substring(expression.indexOf("("),expression.length()-1)));
			}
			else if(expression.indexOf("asin") != -1)
			{
				return Math.asin(parseValue(expression.substring(expression.indexOf("("),expression.length()-1)));
			}
			else if(expression.indexOf("log") != -1)
			{
				return Math.log(parseValue(expression.substring(expression.indexOf("("),expression.length()-1)));
			}
			else if(expression.indexOf("atan") != -1)
			{
				return Math.atan(parseValue(expression.substring(expression.indexOf("("),expression.length()-1)));
			}
			else if(expression.indexOf("tan") != -1)
			{
				return Math.tan(parseValue(expression.substring(expression.indexOf("("),expression.length()-1)));
			}
			else if(expression.indexOf("cos") != -1)
			{
				return Math.cos(parseValue(expression.substring(expression.indexOf("("),expression.length()-1)));
			}
			else if(expression.indexOf("sin") != -1)
			{
				return Math.sin(parseValue(expression.substring(expression.indexOf("("),expression.length()-1)));
			}
			else
				return 0.0;
		}
	}
}