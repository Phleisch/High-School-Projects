import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.util.Date;
import java.applet.Applet;
import javax.imageio.ImageIO;

public class ChappAdmin
{
    public static void main(String args[]) throws IOException
    {
        TestServer tester = new TestServer();
        tester.addWindowListener(new WindowAdapter()
        {public void windowClosing(WindowEvent e)
        {System.exit(0);}});
        tester.setSize(1300,650);
        tester.setVisible(true);
    }
}

class TestServer extends Frame implements MouseListener, MouseMotionListener, KeyListener
{
    private DataOutputStream toServer;
    private DataInputStream fromServer;
    private ObjectOutputStream objectToServer;
    private ObjectInputStream objectFromServer;
    private Socket socket;
    private String localInput;
    private FontMetrics metrics;
    private Image backbuffer;
    private Graphics backg;
    private Font Default;
    private Color transparentGray, babyBlue, lightGrey, grey;
    private int getWidth, getHeight, currentWidth, borderLeft, borderRight, borderTop, borderBottom,
            inputLength, messageStartIndex, textBoxYValue, charactersPerLine, cycle, spacing;
    private boolean wordEntered,firstGraphicsWindow,userEstablished,validUsername,usernameTaken,inAChatRoom;
    private User Local;
    private ArrayList<Message> messageCache;
    private ArrayList<colorfulLine> lines;
    protected Image cat;
    private Font bigFont,mediumFont,smallFont;
    private boolean[] errorTypes;
    ArrayList<String> Lines;
    protected Rectangle chatRoomOneButton, backButton;

    private final char[] typableCharacters = {'a','b','c','d','e','f','g','h','i','j','k','l',
            'm','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H',
            'I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3',
            '4','5','6','7','8','9','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[',
            '}',']',':',';','"','<',',','>','.','?','/','\\','|','\'',' '};

    private final String[] invalidWords = new String[]{"test"};

    public void init()
    {
        try{cat = ImageIO.read(new File("cat.jpg"));}catch(IOException e){}
        bigFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
        mediumFont = new Font(Font.MONOSPACED, Font.BOLD, 20);
        smallFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
        messageCache = new ArrayList<Message>();
        Default = new Font("Monospaced",Font.PLAIN,40);
        localInput = "";
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        transparentGray = new Color(50,50,50,150);
        babyBlue = new Color(0,191,255);
        wordEntered = false;
        currentWidth = 0;
        getWidth = 1300;
        getHeight = 650;
        firstGraphicsWindow = true;
        userEstablished = false;
        borderLeft = 8;
        borderRight = 9;
        borderTop = 31;
        borderBottom = 9;
        inputLength = 15;
        validUsername = false;
        usernameTaken = false;
        lines = new ArrayList<colorfulLine>();
        lightGrey = new Color(188,184,184,200);
        grey = new Color(215,215,215);
        messageStartIndex = 0;
        textBoxYValue = 0;
        Lines = new ArrayList<String>();
        cycle = 0;

        spacing = (getHeight-borderTop-borderBottom-(50*3)-40)/4;

        chatRoomOneButton = new Rectangle((getWidth/2-borderLeft)-200,borderTop+39+spacing,400,50);
        backButton = new Rectangle(borderLeft,borderTop,120,30);

        inAChatRoom = false;

        //errorTypes: 0 = no connection, 1 = unchecked Problem, 2 = unsupported version
        //3 = connection lost, 4 = banned, 5 = unknown error
        errorTypes = new boolean[] {false,false,false,false,false,false};

    }

    public TestServer() throws IOException
    {
        Scanner user = new Scanner(System.in);
        System.out.print("IP address of your server: ");
        String ip = user.nextLine();
        init();
        try
        {
            socket = new Socket(InetAddress.getByName(ip), 8000);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());
            objectFromServer = new ObjectInputStream(socket.getInputStream());
            objectToServer = new ObjectOutputStream(socket.getOutputStream());
        }catch(IOException err)	{ errorTypes[0] = true; }

    }

    public void paint(Graphics g)
    {
        if( firstGraphicsWindow )
        {
            //create the backbuffer image that will later be swapped to the screen
            backbuffer = createImage(getWidth, getHeight);
            //get the backbuffer's graphics (canvas) so that we can draw on it
            backg = backbuffer.getGraphics();

            listenForEvents task = new listenForEvents();
            new Thread(task).start();


            //////////////////////////////////////////
            //     ArrayList of colorful Lines      //
            //////////////////////////////////////////	


            int ranX;
            int ranY;
            int ranXLen;
            int ranYLen;
            int ranColor;
            Color PURPLE = new Color(128,0,128);

            Color color;
            for(int x = 0; x < 1000; x++)
            {
                ranColor = (int) (Math.random() * 255);
                ranX = borderLeft;
                ranY = getHeight-borderBottom;
                ranXLen = (int) (Math.random() * getWidth);
                ranYLen = (int) (Math.random() * getHeight);

                color = new Color(ranColor,255,255);

                lines.add(new colorfulLine(ranX,ranY,ranXLen,ranYLen,color));
            }

        }
        g.setFont(Default);
        metrics = g.getFontMetrics();
        charactersPerLine = (getWidth-borderLeft*2)/metrics.stringWidth("A");
        //Rectangle r = Frame.getBounds();
        getWidth = getWidth();
        getHeight = getHeight();

        chatRoomOneButton.setBounds((getWidth/2-borderLeft)-200,borderTop+39+spacing,400,50);
        backButton.setBounds(borderLeft,borderTop,120,30);

        firstGraphicsWindow = false;
        update(g);
    }

    public void update(Graphics g)
    {
        backg.setColor(Color.WHITE);

        backg.fillRect(0,0,getWidth,getHeight);
        if(errorTypes[0])
            errorPage(backg);
        else if(!userEstablished)
        {
            usernamePage(backg);
            enterUsername(backg);
        }

        else if(!inAChatRoom && userEstablished)
        {
            backg.drawImage(cat,0,0,getWidth,getHeight,this);
            homePage(backg);
        }
        else
        {
            backg.drawImage(cat,0,0,getWidth,getHeight,this);
            entryBox(backg);
            drawMessages(backg);
        }

        g.drawImage(backbuffer, 0, 0, this);
        repaint();

    }

    public int validChar(char a)
    {

        for( char check : typableCharacters )
            if( a == check )
                return 0;
        return -1;

    }

    ////////////////////////////////////////////////
    //             Essential Methods              //
    ////////////////////////////////////////////////

    public void enterUsername(Graphics g)
    {
        String blank = "Enter Username";
        int x = (int) (getWidth/2) - 300;
        int y = (int) (getHeight/2) - 30;

        g.setFont(Default);
        metrics = g.getFontMetrics();
        if(localInput.length() <= 0)
        {
            currentWidth = metrics.stringWidth(blank)/2;
            g.setColor(Color.GRAY);
            g.drawString(blank,(getWidth/2)-currentWidth,y+47);
        }
        currentWidth = metrics.stringWidth(localInput)/2;
        g.setColor(transparentGray);
        g.fillRoundRect(x,y,600,60,24,24);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x,y,600,60,24,24);
        g.setColor(babyBlue);
        g.drawString(localInput,(getWidth/2)-currentWidth,y+47);

    }

    public void entryBox(Graphics g)
    {
        charactersPerLine = (getWidth-borderLeft*2)/metrics.stringWidth("A");

        if(localInput.length() < charactersPerLine)
        {
            textBoxYValue = getHeight-borderBottom-42;
            g.setColor(transparentGray);
            g.fillRect(borderLeft,textBoxYValue,getWidth-17,42);
            g.setColor(Color.BLACK);
            g.drawRect(borderLeft,textBoxYValue,getWidth-17,42);
            g.setColor(Color.WHITE);
            g.drawString(localInput,10,getHeight-20);
            textBoxYValue = getHeight-borderBottom-84;
        }
        else
        {
            for(int m = 0; m < localInput.length(); m+=charactersPerLine)
            {
                if(m+charactersPerLine >= localInput.length())
                    Lines.add(localInput.substring(m,localInput.length()));
                else
                    Lines.add(localInput.substring(m,m+charactersPerLine));
            }
            textBoxYValue = longMessage(g,charactersPerLine,(getHeight-borderBottom-(42*Lines.size())),true);
        }

    }

    public boolean checkUsername()
    {

        for( String a : invalidWords )
        {
            localInput = " "+localInput;
            if(localInput.toLowerCase().indexOf(a) != -1)
            {
                localInput = "";
                return false;
            }
        }

        localInput = localInput.trim();
    	/*if(localInput.length()==inputLength)
    	{
    		//g.drawString("Character Limit Reached",100,200);
    		validUsername = false;
    	}*/
    	
    	/*if(localInput.length()<=0)
    	{
    		g.drawString("Username Too Short",100,300);
    		validUsername = false;
    	}*/
        return true;


    }

    public void drawMessages(Graphics g)
    {
        Lines.clear();
        String current = "";
        g.setFont(Default);
        metrics = g.getFontMetrics();
        charactersPerLine = (getWidth-borderLeft*2)/metrics.stringWidth("A");
        int startPoint = textBoxYValue;
        if(messageCache.size() > 0)
            for( int x = messageStartIndex; x < messageCache.size(); x++ )
            {
                current = messageCache.get(x).getMessage();
                g.setFont(Default);

                if(messageCache.get(x).getType().equals("Notice"))
                {
                    g.setColor(grey);
                    if(current.length() >= charactersPerLine)
                    {

                        for(int m = 0; m < current.length(); m+=charactersPerLine)
                        {
                            if(m+charactersPerLine >= current.length())
                                Lines.add(current.substring(m,current.length()));
                            else
                                Lines.add(current.substring(m,m+charactersPerLine));
                        }
                        startPoint = longMessage(g,charactersPerLine,(startPoint+42-(Lines.size()*42)),false);
                    }

                    else
                    {
                        g.setColor(transparentGray);
                        g.fillRect(borderLeft,startPoint,getWidth-17,42);
                        g.setColor(Color.BLACK);
                        g.drawRect(borderLeft,startPoint,getWidth-17,42);
                        g.setColor(grey);
                        g.drawString(messageCache.get(x).getMessage(),10,startPoint+31);
                        startPoint -= 42;
                    }

                }

                else if(messageCache.get(x).getSender().getName().equals(Local.getName()))
                {
                    current = "<You> " + current;
                    g.setColor(Color.WHITE);
                    if(current.length() >= charactersPerLine)
                    {

                        for(int m = 0; m < current.length(); m+=charactersPerLine)
                        {
                            if(m+charactersPerLine >= current.length())
                                Lines.add(current.substring(m,current.length()));
                            else
                                Lines.add(current.substring(m,m+charactersPerLine));
                        }


                        startPoint = longMessage(g,charactersPerLine,(startPoint+42-(Lines.size()*42)),true);

                    }

                    else
                    {
                        g.setColor(transparentGray);
                        g.fillRect(borderLeft,startPoint,getWidth-17,42);
                        g.setColor(Color.BLACK);
                        g.drawRect(borderLeft,startPoint,getWidth-17,42);
                        g.setColor(Color.WHITE);
                        g.drawString("<You> " + messageCache.get(x).getMessage(),10,startPoint+31);
                        startPoint -= 42;
                    }

                }
                else
                {
                    current = "<"+messageCache.get(x).getSender().getName()+"> " + current;

                    g.setColor(Color.GREEN);
                    if(current.length() >= charactersPerLine)
                    {

                        for(int m = 0; m < current.length(); m+=charactersPerLine)
                        {
                            if(m+charactersPerLine >= current.length())
                                Lines.add(current.substring(m,current.length()));
                            else
                                Lines.add(current.substring(m,m+charactersPerLine));
                        }



                        startPoint = longMessage(g,charactersPerLine,(startPoint+42-(Lines.size()*42)),false);

                        //startPoint = longMessage(g,charactersPerLine,(startPoint+42-(Lines.size()*42)));

                    }
                    else
                    {
                        g.setColor(transparentGray);
                        g.fillRect(borderLeft,startPoint,getWidth-17,42);
                        g.setColor(Color.BLACK);
                        g.drawRect(borderLeft,startPoint,getWidth-17,42);
                        g.setColor(Color.GREEN);
                        g.drawString(current,10,startPoint+31);
                        startPoint -= 42;
                    }
                }
                Lines.clear();
            }
        g.setColor(Color.BLACK);
        g.fillRect(borderLeft,borderTop,100,30);
        g.setColor(Color.WHITE);
        g.drawString("Back",borderLeft+2,borderTop+28);
    }

    public int longMessage(Graphics g, int charPerLine, int startY, boolean thisUser)
    {
        int startPoint = startY;

        g.setFont(Default);
        g.setColor(transparentGray);
        g.fillRect(borderLeft,startPoint,getWidth-17,Lines.size()*42);
        g.drawRect(borderLeft,startPoint,getWidth-17,Lines.size()*42);

        for( int x = 0; x < Lines.size(); x++ )
        {
            if(thisUser)
                g.setColor(Color.WHITE);
            else if(Lines.get(0).substring(0,1).equals("*"))
                g.setColor(grey);
            else
                g.setColor(Color.GREEN);
            g.drawString(Lines.get(x),10,startPoint+31);
            startPoint += 42;
        }

        return startY-42;

    }

    public void correctMessage()
    {
        localInput = " " + localInput + " ";
        String temp = localInput.toLowerCase();
        System.out.println(localInput);
        //String[] words = localInput.split(" ");
        //for( int x = 0; x < words.length; x++ )
        //{
        for( String e : invalidWords )
        {
            if(temp.indexOf(e) != -1)
            {
                if(temp.indexOf(e) != 0)
                {
                    localInput = localInput.substring(0,temp.indexOf(e)) + toAsterics(localInput.substring(localInput.indexOf(e),localInput.indexOf(e)+e.length()).trim()) +
                            localInput.substring(localInput.indexOf(e)+e.length(),localInput.length());
                }
                else
                {
                    localInput = toAsterics(localInput.substring(temp.indexOf(e),temp.indexOf(e)+e.length()).trim()) +
                            localInput.substring(temp.indexOf(e)+e.length(),temp.length());
                }
            }
            System.out.println(localInput);
        }
        //}

        //for(int x = 0; x < words.length; x++)
        //{
        //temp = temp + words[x] + " ";
        //}
        //temp = temp.trim();
        System.out.println(localInput);
        localInput = localInput.trim();

    }

    public String toAsterics(String a)
    {

        String toSend = "";
        for(int x = 0; x < a.length(); x++)
            toSend = toSend + "*";
        return toSend;

    }

    ////////////////////////////////////////////////
    //              Graphics Methods              //
    ////////////////////////////////////////////////


    public void homePage(Graphics g)
    {
        g.setColor(lightGrey);
        g.fillRect(borderLeft,borderTop-1,getWidth-borderLeft*2,40);
        g.setFont(mediumFont);
        g.setColor(Color.WHITE);
        g.drawString(timeMessage()+", "+Local.getName(),15+borderLeft,35+borderTop);
        g.setFont(bigFont);
        metrics = g.getFontMetrics();
        currentWidth = metrics.stringWidth("ChappAdmin")/2;
        g.drawString("ChappAdmin",borderLeft+getWidth/2-currentWidth,borderTop+30);

        spacing = (getHeight-borderTop-borderBottom-(50*3)-40)/4;
        //ChatRoom 1
        metrics = g.getFontMetrics();
        currentWidth = metrics.stringWidth("Universal Chat Room")/2;
        g.setColor(lightGrey);
        g.fillRoundRect((getWidth/2-borderLeft)-200,borderTop+39+spacing,400,50,10,10);
        g.setColor(babyBlue);
        g.drawString("Universal Chat Room",(getWidth/2-borderLeft)-currentWidth,borderTop+39+spacing+35);

    }

    public void usernamePage(Graphics g)
    {

        int ranX;
        for( colorfulLine line : lines )
        {
            g.setColor(line.getColor());
            g.drawLine(line.getX(),line.getY(),line.getLenX(),line.getLenY());
        }

    }

    public void determineError(String e)
    {
        switch(e)
        {
            case "No Connection": 													errorTypes[0] = true; break;
            case "Unchecked Problem Occurred; We will fix it right away": 			errorTypes[1] = true; break;
            case "Your version may not support some functionality; contact Kai": 	errorTypes[2] = true; break;
            case "Connection Lost - the server was closed": 						errorTypes[3] = true; break;
            case "You were expunged for inappropriate behavior": 					errorTypes[4] = true; break;
            case "Something went wrong - restart Chapp": 							errorTypes[5] = true; break;
            default: /*Nothing*/;
        }
    }

    public void errorPage(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth,getHeight);
        g.setColor(Color.GREEN);
        g.drawString("We couldn't connect to the server! Either we're down or you mistyped the IP Address.",10,100);

    }

    public String timeMessage()
    {
        String timeOfDay = ""+ new Date();
        timeOfDay = timeOfDay.substring(timeOfDay.indexOf(":") -2, timeOfDay.indexOf(":"));

        switch( timeOfDay )
        {
            case "12": timeOfDay = "Good Day"; break;
            case "13": timeOfDay = "Good Afternoon"; break;
            case "14": timeOfDay = "Good Afternoon"; break;
            case "15": timeOfDay = "Good Afternoon"; break;
            case "16": timeOfDay = "Good Afternoon"; break;
            case "17": timeOfDay = "Good Afternoon"; break;
            case "18": timeOfDay = "Good Evening"; break;
            case "19": timeOfDay = "Good Evening"; break;
            case "20": timeOfDay = "Good Evening"; break;
            case "21": timeOfDay = "Good Evening"; break;
            case "22": timeOfDay = "Good Evening"; break;
            case "23": timeOfDay = "Good Evening"; break;
            default: timeOfDay = "Good Morning";
        }
        return timeOfDay;
    }

    public boolean inArea( int x, int y, Rectangle f)
    {
        if(( x >= f.getMinX() && x <= f.getMaxX() )
                && ( y >= f.getMinY() && y <= f.getMaxY()))
            return true;
        else
            return false;
    }

    ////////////////////////////////////////////////
    //            Java Event Methods              //
    ////////////////////////////////////////////////

    public void mouseClicked(MouseEvent e)
    {

        int pressX = e.getX();
        int pressY = e.getY();

        if(inArea(pressX,pressY,chatRoomOneButton) && userEstablished)
        {
            inAChatRoom = true;

            try{objectToServer.writeObject("Universal");}
            catch(UnknownHostException ex){}
            catch(IOException ex){}
            catch(NullPointerException ex){ localInput = "Error - Bad Connection"; }
            //catch(ClassNotFoundException ex){ localInput = "Error"; }
            //messageCache.clear();
        }
        else if(inArea(pressX,pressY,backButton) && userEstablished)
        {
            inAChatRoom = false;
            try{objectToServer.writeObject("UniversalLeave");}
            catch(UnknownHostException ex){}
            catch(IOException ex){}
            catch(NullPointerException ex){ localInput = "Error - Bad Connection"; }
            //messageCache.clear();
        }

    }


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

        if(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE){
            if(localInput.length() != 0){
                localInput = localInput.substring(0,localInput.length()-1);
            }
        }

        else if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            Object object;
            //System.out.println(userEstablished);
            if(!userEstablished&&checkUsername())
                try
                {
                    Local = new User("Admin's Version", InetAddress.getLocalHost());
                    objectToServer.writeObject(Local);
                    object = objectFromServer.readObject();
                    if( object instanceof User )
                    {
                        userEstablished = true;
                        inputLength = 400;
                        localInput = "";
                    }
                    //System.out.println(userEstablished);
                    else
                    {
                        userEstablished = false;
                        Local = null;
                        localInput = "";
                    }
                }
                catch(UnknownHostException e){userEstablished = false;}
                catch(IOException e){}
                catch(NullPointerException e){ localInput = "Error - Bad Connection"; }
                catch(ClassNotFoundException e){ localInput = "Error"; }

            else if(userEstablished)
                try{
                    //correctMessage();
                    objectToServer.writeObject(new Message(localInput,null,Local));
                    localInput = "";
                }
                catch(IOException e){}
                catch(NullPointerException e){ localInput = "Error - Bad Connection"; }


        }

        else if(evt.getKeyCode() == KeyEvent.VK_UP)
        {
            if( messageStartIndex < messageCache.size()-1 && userEstablished )
                messageStartIndex++;
        }
        else if(evt.getKeyCode() == KeyEvent.VK_DOWN)
        {
            if( messageStartIndex > 0 && userEstablished )
                messageStartIndex--;
        }

        else{
            if( validChar(evt.getKeyChar()) != -1 && localInput.length() < inputLength )
                localInput = localInput + evt.getKeyChar();
        }

        repaint();

    }

    class listenForEvents implements Runnable
    {

        public listenForEvents() { }

        public void run()
        {
            System.out.print("");
            while(true)
            {
                System.out.print("");
                if(userEstablished)
                {
                    try{
                        Object object = objectFromServer.readObject();
                        if(object instanceof ArrayList)
                        {
                            messageCache = (ArrayList<Message>) object;
                        }
                        //objectFromServer.reset();

                    }catch(IOException e){System.out.println("error... Line 713"); errorTypes[0] = true;}
                    catch(ClassNotFoundException e){ System.out.println("error... Line 714"); }
                    catch(NullPointerException e){System.out.println("error... Line 715");}
                }

            }
        }
    }
}
