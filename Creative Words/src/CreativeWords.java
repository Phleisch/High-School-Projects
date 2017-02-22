/**
 * @(#)CreativeWords.java
 *
 *
 * @author 
 * @version 1.00 2016/8/26
 */

import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;


public class CreativeWords extends java.applet.Applet implements MouseListener, MouseMotionListener, KeyListener{
	ArrayList<String> wordFixes,results,popWords,topFixes;
	final String[] letters = 	{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	final String[][] keySwaps = { {"a","q","w","s","z"},{"b","v","g","h","n"},{"c","x","d","f","v"},{"d","s","e","r","f","c","x"},
									   {"e","r","f","d","s","w"},{"f","r","t","g","v","c","d"},{"g","f","t","y","h","b","v"},
									   {"h","y","u","j","n","b","g"},{"i","u","o","k","j"},{"j","h","u","i","k","m","n"},
									   {"k","j","i","o","l","m"},{"l","k","o","p"},{"m","n","j","k"},{"n","b","h","j","m"},
									   {"o","i","p","l","k"},{"p","l","o"},{"q","w","a"},{"r","t","f","d","e"},{"s","w","e","d","x","z","a"},
									   {"t","y","g","f","r"},{"u","i","j","h","y"},{"v","c","f","g","b"},{"w","e","s","a","q"},
									   {"x","z","s","d","c"},{"y","u","h","g","t"},{"z","a","s","x"} };
	FontMetrics metrics;
    Image backbuffer;
    Graphics backg;
    Font Default;
    String localInput;
    Color transparentGray, exist, nonexist, babyBlue;
    int getWidth, getHeight, ratio, area, random, currentWidth,best,next;
   	ArrayList<String> words = new ArrayList<String>();
   	Scanner in;	
   	ArrayList<word> wordObjects = new ArrayList<word>();
   	boolean wordExists, wordEntered, paused, noMatch;
    
    public void init() {
    	
    	noMatch = false;
    	
    	topFixes = new ArrayList<String>();
    	popWords = new ArrayList<String>();
    	wordFixes = new ArrayList<String>();
    	results = new ArrayList<String>();
    	
    	Default = new Font("Monospaced",Font.PLAIN,60);
    	
    	localInput = "";
    	
    	addMouseListener(this);
		addMouseMotionListener(this);
        addKeyListener(this);
    	
    	transparentGray = new Color(50,50,50,200);
    	exist = new Color(0,255,0,100);
    	nonexist = new Color(255,0,0,100);
    	babyBlue = new Color(0,191,255);
    	
    	wordEntered = false;
    	wordExists = true;
    	paused = false;
    	
    	getWidth = getWidth();
    	getHeight = getHeight();
    	
    	area = getWidth * getHeight;
    	ratio = (int) (area/400);
    	
    	//create the backbuffer image that will later be swapped to the screen
        backbuffer = createImage(getSize().width, getSize().height);
        //get the backbuffer's graphics (canvas) so that we can draw on it
        backg = backbuffer.getGraphics();
    	
    	try
    	{
    		in = new Scanner(new File("C:\\Users\\KaiFl\\IdeaProjects\\High-School-Projects\\Creative Words\\src\\words.txt"));
    		while(in.hasNext())
	    	{
	    		words.add(in.next());
	    	}
	    	
	    	in = new Scanner(new File("C:\\Users\\KaiFl\\IdeaProjects\\High-School-Projects\\Creative Words\\src\\1000First.txt"));
	    	while(in.hasNext())
	    	{
	    		popWords.add(in.next());
	    	}
    		
    	} catch(IOException e)
    	{
    		
    	}
    }

    public void paint(Graphics g) {
    	
    	update(g);
    	
    	
    	
    }
    
    public void update(Graphics g)
    {
    	delay(100);
    	backg.setColor(Color.WHITE);
    	backg.fillRect(0,0,getWidth,getHeight);
    	
    	backg.setColor(Color.BLACK);
    	
    	random = (int) (Math.random() * 0) + 1;
    	
    	if( !paused )
    	{
	    	if( random == 1 )
	    	{
	    		
	    		word Next = new word(words,getWidth,getHeight);
	    		wordObjects.add(Next);
	    		
	    	}
	    	
	    	for(int x = 0; x < wordObjects.size(); x++)
	    	{
	    		backg.setFont(wordObjects.get(x).getFont());
	    		backg.drawString(wordObjects.get(x).getWord(),wordObjects.get(x).getX(),wordObjects.get(x).getY());
	    		wordObjects.get(x).changeX();
	    		wordObjects.get(x).changeY();
	    	}
	    	checkInBounds();
    	}
    	
    	entryBox(backg);
    	
    	g.drawImage(backbuffer, 0, 0, this);
    	repaint();
    	
    }
    
    public boolean binarySearch(String key)
	{
		key = key.toLowerCase();
		
		int lo = 0;
		int hi = words.size();
		int mid = 0;
		
		while( lo <= hi && mid < words.size() && lo >= 0 )
		{
			
			mid = lo + (hi - lo)/2;
			if(key.compareTo(words.get(mid)) < 0) 
				hi = mid - 1; //middle was too high
            else if(key.compareTo(words.get(mid)) > 0) 
            	lo = mid + 1; //middle was too low
            else
            	return true;
            mid = lo + (hi - lo)/2;
		}
		
		return false;
		
	}
	
	public void linearSearch(String key, int p)
	{
		
		String cur = "";
		int len = 0;
		
		for(int x = 0; x < words.size(); x++)
		{
			cur = words.get(x);
			len = cur.length();
			
			if( cur.contains(key) && len >= p-1 && len <= p+1 )
				wordFixes.add(cur);
		}
		
		//return false;
		
	}
	
	public void correctWord(String word)
	{
		
		if(binarySearch(reverse(word)) && wordFixes.size() == 0)
		{	
			wordFixes.add(reverse(word));	
		}
		if(wordFixes.size() == 0)
		{
			deletion(word);
			swap(word);
			exchange(word);
			insertion(word);
			results.remove(0);
		}
		
		for(int x = results.size()-1; x > results.size()/2; x--)
			results.remove(x);
		if(wordFixes.size() == 0 && results.size() < 100000)	
			correctWord(results.get(0));
		
	}
	
	public int checkUsage(String word)
	{
		
		for( int x = 0; x < popWords.size(); x++ )
		{
			if(word.equalsIgnoreCase(popWords.get(x)))
				return x;
		}
		return popWords.size();
		
	}
	
	/////////////Insertion/////////////
	
	public void insertion(String word)
	{
		String newWord = "";
		for(int x = 0; x < word.length(); x++)
		{
			for(int n = 0; n < 26; n++)
			{
			if( x == 0 )
				newWord = letters[n] + word;
				else if( x == word.length()-1 )
				newWord = word + letters[n];
				else
				newWord = word.substring(0,x) + letters[n] + word.substring(x,word.length());
				if( binarySearch(newWord) )
				wordFixes.add(newWord);
				else
				results.add(newWord);
			}
		}
	}
	
	///////////Deletion///////////
	
	public void deletion(String word)
	{
		String newWord = "";
		for(int x = 0; x < word.length(); x++)
		{
			if( x == 0 )
			newWord = word.substring(1,word.length());
			else if( x == word.length()-1 )
			newWord = word.substring(0,word.length()-1);
			else
			newWord = word.substring(0,x) + word.substring(x+1,word.length());
			if( binarySearch(newWord) )
			wordFixes.add(newWord);
			else
			results.add(newWord);
		}	
	}
	
	//////////////Swap//////////////
	
	public void swap(String word)
	{
		String newWord = "";
		for(int x = 0; x < word.length(); x++)
		{
			if(word.length() <= 1)
			{
				x = 10;
				break;
			}
			
			
			if( x == word.length()-1 )
				newWord = word.substring(0,x-1) + word.substring(x) + word.substring(x-1,x);	
			else if( x == 0)
				newWord = word.substring(x+1,x+2) + word.substring(x,x+1) + word.substring(x+2,word.length());
			else
				newWord = word.substring(0,x) + word.substring(x+1,x+2) + word.substring(x,x+1) + word.substring(x+2,word.length());
			if( binarySearch(newWord) )
				wordFixes.add(newWord);
			else
				results.add(newWord);
		}
	}
	
	/////////////Reverse////////////
	
	public String reverse(String word)
	{
		String newWord = "";
		
		for(int x = word.length()-1; x >= 0; x--)
		{
			
			newWord = newWord + String.valueOf(word.charAt(x));
			
		}
		
		return newWord;
	}
	
	/////////////Exchange///////////
	
	public void exchange(String word)
	{
		String newWord = "";
		for(int x = 0; x < word.length(); x++)
		{
			
			for(int n = 0; n < 26; n++)
			{
				if( x == 0 )
					newWord = letters[n] + word.substring(1,word.length());
				else if( x == word.length()-1 )
					newWord = word.substring(0,word.length()-1) + letters[n];
				else
					newWord = word.substring(0,x) + letters[n] + word.substring(x+1,word.length());
				if( binarySearch(newWord) )
				wordFixes.add(newWord);
				else
					results.add(newWord);	
			}
		}	
	}
	
	////////////keySwitch//////////
	
	public void keySwitch(String word)
	{
		String newWord = "";
		int index = 0;
		for(int x = 0; x < word.length(); x++)
		{
			index = (int) (word.charAt(x)) - 97;
			for(int n = 1; n < keySwaps[index].length; n++)
			{
				if( x == 0 )
					newWord = keySwaps[index][n] + word.substring(1,word.length());
				else if( x == word.length()-1 )
					newWord = word.substring(0,word.length()-1) + keySwaps[index][n];
				else
					newWord = word.substring(0,x) + keySwaps[index][n] + word.substring(x+1,word.length());
				if( binarySearch(newWord) )
				wordFixes.add(newWord);
				else
				results.add(newWord);	
			}
		}	
	}
	
	/*///////////chunking///////////
	
	public void chunking(String word)
	{
		int len = word.length();
		String newWord = "";
		for(int x = 4; x < word.length(); x++)
		{
			for(int n = 0; n <= word.length()-x; n++)
			{
				System.out.println(newWord);
				linearSearch(newWord,len);
			}
		}
	}												*/
	
    
    public void entryBox(Graphics g)
    {
    	g.setFont(Default);
    	metrics = g.getFontMetrics();
    	best = popWords.size();
    	currentWidth = metrics.stringWidth(localInput)/2;
    	int x = (int) (getWidth/2) - 300;
    	int y = (int) (getHeight/2) - 30;
    	if(wordEntered)
    	{
    		if(wordExists)
    			g.setColor(exist);
    		else
    		{
    			g.setColor(nonexist);
    			correctWord(localInput);
    			if(wordFixes.size() != 0)
    			{
    				
    				for(int a = 0; a < wordFixes.size(); a++)
    				{
    					next = checkUsage(wordFixes.get(a));
    					if(best>next)
    					{
    						best = next;
    						if(popWords.get(best).length() == localInput.length())
    							topFixes.add(popWords.get(best));
    					}
    				}
    				if(topFixes.size() != 0)
    					localInput = topFixes.get(0);
    				else if( best < popWords.size() )
    					localInput = popWords.get(best);
    				else
    				{
    					boolean matchLength = false;
    					for(int b = 0; b < wordFixes.size(); b++)
    					{
    						for(int c = 0; c < localInput.length()-2; c++)
    						{
    							if(wordFixes.get(b).length() == localInput.length()-c)
    						{
    							localInput = wordFixes.get(b);
    							b = wordFixes.size();
    							c = 100000;
    							matchLength = true;
    						}
    						}
    					}
    					if(!matchLength)
    						localInput = wordFixes.get(0);	
    				}
    				noMatch = false;
    			}
    			else
    			{
    				localInput = localInput.toUpperCase();
    				noMatch = true;
    			}
    			wordFixes.clear();
    			results.clear();
    			topFixes.clear();
    			wordExists = true;
    		}
    	}
    	else	
    	g.setColor(transparentGray);
    	
    	if(noMatch)
    	g.setColor(nonexist);
    	g.fillRoundRect(x,y,600,60,24,24);	
    	g.setColor(Color.BLACK);
    	g.drawRoundRect(x,y,600,60,24,24);
    	g.setColor(babyBlue);
    	g.drawString(localInput,(getWidth/2)-currentWidth,y+47);
    	
    }
    
    
    public void delay(int n)
	{
		try {
		    Thread.sleep(n);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
	
	public void checkInBounds()
	{
		
		for(int x = 0; x < wordObjects.size(); x++)
		{
			
			if( wordObjects.get(x).getX() >= -getWidth && wordObjects.get(x).getX() <= getWidth*2 
				&& wordObjects.get(x).getY() >= -100 && wordObjects.get(x).getY() <= getHeight + 100)
				{
					//nothing
				}
			
			else
			{
				wordObjects.remove(x);
				x--;
			}
			
		}
		
	}
    
    
    
    
    public void mouseClicked(MouseEvent e)  { }
    public void mouseMoved(MouseEvent e)    { }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseDragged(MouseEvent e)	{ }
    public void mouseExited(MouseEvent e) 	{ }
    public void mousePressed(MouseEvent e)	{ }
    public void mouseReleased(MouseEvent e) { }
    public void keyReleased(KeyEvent evt)   { }
   	public void keyTyped(KeyEvent evt)      { }
	public void keyPressed(KeyEvent evt)
	{
		if(true){
			if(true){
	   			if(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE){
	   				if(localInput.length() != 0){
	   					localInput = localInput.substring(0,localInput.length()-1);
	   				}
	   				wordEntered = false;
	   				noMatch = false;
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_ENTER){
	   				if(localInput.length() != 0)
	   				{
	   					wordEntered = true;
	   				}
	   				
	   				if(binarySearch(localInput))
	   				{
	   					wordExists = true;
	   				}
	   				else
	   					wordExists = false;
	   				

			    	//currentWidth = metrics.stringWidth(localInput)/2;
			    	
			    	int x = (int) (getWidth/2) - 300;
			    	int y = (int) (getHeight/2) - 30;
	   				
	   				
	   				word Next = new word(words,getWidth,getHeight);
	   				Next.setWord(localInput);
	   				Next.setFont(Default);
	   				Next.setStart((getWidth/2)-currentWidth,y+47);
    				wordObjects.add(Next);
	   					
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_CONTROL){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_PAUSE){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_CAPS_LOCK){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
	   				paused = !paused;
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_PAGE_UP){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_TAB){
	   			
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_END){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_HOME){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_LEFT){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_UP){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_RIGHT){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_DOWN){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F12){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F11){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F10){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F9){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F8){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F7){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F6){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F5){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F4){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F3){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F2){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_F1){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD9){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD8){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD7){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD6){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD5){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD4){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD3){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD2){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD1){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_NUMPAD0){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_SHIFT){
	   				
	   			}
	   			else if(evt.getKeyCode() == KeyEvent.VK_CONTROL){
	   				
	   			}
	   			else{
	   			
	   			char key = evt.getKeyChar();
	   				localInput = localInput + key;
			}
			repaint();	
		}
   	}
	}
   	
   	
    
}

class word{
	
	private int speedX, speedY, fontSize, x, y, appX, appY;
	private String word;
	Font font;
	
	public word( ArrayList<String> e, int width, int height )
	{
		appX = width;
		appY = height;
		randSlope();
		randFont();
		randWord(e);
	}
	
	public void randSlope()
	{
		
		while(speedX == 0)
			speedX = (int) (Math.random() * 81) - 40;
		
		while(speedY == 0)
			speedY = (int) (Math.random() * 81) - 40;
			
		x = (int) (Math.random() * appX);
		y = (int) (Math.random() * appY);
			
	}
	
	public void randFont()
    {
    	
    	fontSize = (int) (Math.random() * 70 ) + 11;
    	int choice = (int) (Math.random() * 4);
    	String fontName;
    	int fontChoice = (int) (Math.random() * 4);
    	
    	switch( fontChoice )
    	{
    		case 1: fontName = "TimesNewRoman"; break;
    		case 2: fontName = "Arial"; break;
    		case 3: fontName = "SansSerif"; break;
    		case 4: fontName = "Monospaced"; break;
    		default: fontName = "Arial";
    	}
    	
    	if( choice == 0 )
    		font = new Font(fontName,Font.PLAIN,fontSize);
    	else if( choice == 1 )
    		font = new Font(fontName,Font.BOLD,fontSize);
    	else if( choice == 2 )
    		font = new Font(fontName,Font.ITALIC,fontSize);
    	
    }
    
    public void randWord( ArrayList<String> e )
    {
    	
    	int num = e.size();
    	int index = (int) (Math.random() * num );
    	word = e.get(index);
    	
    }
    
    public Font getFont()
    {
    	return font;
    }
    
    public void setFont(Font funt)
    {
    	font = funt;
    }
    
    public void setStart(int a, int b)
    {
    	x = a;
    	y = b;
    }
    
    public void setWord(String a)
    {
    	word = a;
    }
    
    public String getWord()
    {
    	return word;
    }
    
    public int getXSpeed()
    {
    	return speedX;
    }
    
    public int getYSpeed()
    {
    	return speedY;
    }
    
    public int getX()
    {
    	return x;
    }
    
    public int getY()
    {
    	return y;
    }
    
    public void changeX()
    {
    	x += speedX;	
    }
    
    public void changeY()
    {
    	y += speedY;	
    }
	
}