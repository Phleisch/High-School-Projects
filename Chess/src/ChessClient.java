/**
 * Created by Kai W. Fleischman on 2/24/2017.
 */
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.net.*;
import java.util.Date;
import javax.imageio.ImageIO;

public class ChessClient
{
    public static void main(String args[]) throws IOException
    {
        ClientTestServer tester = new ClientTestServer();
        tester.addWindowListener(new WindowAdapter()
        {public void windowClosing(WindowEvent e)
        {System.exit(0);}});
        tester.setSize(1440,900);
        tester.setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("In shutdown hook");
            }
        }, "Shutdown-thread"));
    }
}

class ClientTestServer extends Frame implements MouseListener, MouseMotionListener, KeyListener
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
    private boolean wordEntered,firstGraphicsWindow,userEstablished,validUsername,usernameTaken,inAChatRoom,muted;
    protected Image cat;
    private Font bigFont,mediumFont,smallFont;
    private boolean[] errorTypes;
    ArrayList<String> Lines;
    protected Rectangle chatRoomOneButton, chatRoomTwoButton, chatRoomThreeButton, backButton;
    private final char[] typableCharacters = {'a','b','c','d','e','f','g','h','i','j','k','l',
            'm','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H',
            'I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3',
            '4','5','6','7','8','9','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[',
            '}',']',':',';','"','<',',','>','.','?','/','\\','|','\'',' '};

    private final String[] invalidWords = new String[]{"test"};

    public void init()
    {
        try{cat = ImageIO.read(new File("C:\\Users\\KaiFl\\IdeaProjects\\High-School-Projects\\Chapp\\src\\cat.jpg"));}catch(IOException e){}
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
        getWidth = 600;
        getHeight = 300;
        firstGraphicsWindow = true;
        userEstablished = false;
        borderLeft = 8;
        borderRight = 9;
        borderTop = 31;
        borderBottom = 9;
        inputLength = 14;
        validUsername = false;
        usernameTaken = false;
        lightGrey = new Color(188,184,184,200);
        grey = new Color(215,215,215);
        messageStartIndex = 0;
        textBoxYValue = 0;
        Lines = new ArrayList<String>();
        cycle = 0;
        muted = false;
        spacing = (getHeight-borderTop-borderBottom-(50*3)-40)/4;

        chatRoomOneButton = new Rectangle((getWidth/2-borderLeft)-200,borderTop+39+spacing,400,50);
        chatRoomTwoButton = new Rectangle((getWidth/2-borderLeft)-200,borderTop+89+2*spacing,400,50);
        chatRoomThreeButton = new Rectangle((getWidth/2-borderLeft)-200,borderTop+139+3*spacing,400,50);
        backButton = new Rectangle(borderLeft,borderTop,120,30);

        inAChatRoom = false;

        //errorTypes: 0 = no connection, 1 = unchecked Problem, 2 = unsupported version
        //3 = connection lost, 4 = banned, 5 = unknown error
        errorTypes = new boolean[] {false,false,false,false,false,false};

    }

    public ClientTestServer() throws IOException
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
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try{

                }catch(Exception e){}
            }
        }, "Shutdown-thread"));

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

            }

        }
        g.setFont(Default);
        metrics = g.getFontMetrics();
        charactersPerLine = (getWidth-borderLeft*2)/metrics.stringWidth("A");
        //Rectangle r = Frame.getBounds();
        getWidth = getWidth();
        getHeight = getHeight();

        chatRoomOneButton.setBounds((getWidth/2-borderLeft)-200,borderTop+39+spacing,400,50);
        chatRoomTwoButton.setBounds((getWidth/2-borderLeft)-200,borderTop+89+2*spacing,400,50);
        chatRoomThreeButton.setBounds((getWidth/2-borderLeft)-200,borderTop+139+3*spacing,400,50);
        backButton.setBounds(borderLeft,borderTop,120,30);

        firstGraphicsWindow = false;
        update(g);
    }

    public void update(Graphics g)
    {
        backg.setColor(Color.WHITE);

        backg.fillRect(0,0,getWidth,getHeight);
        if(errorTypes[0])
            errorPage(backg,"We couldn't connect to the server!");
        else if(errorTypes[3])
            errorPage(backg,"Your connection with our server was lost!");
        else if(errorTypes[5])
            errorPage(backg,"An unexpected error occured! Please restart Chapp.");
        else if(!userEstablished)
        {
            usernamePage(backg);
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
            textBoxYValue = longMessage(g,charactersPerLine,(getHeight-borderBottom-(42*Lines.size())),true,false);
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


        g.setColor(Color.BLACK);
        g.fillRect(borderLeft,borderTop,100,30);
        g.setColor(Color.WHITE);
        g.drawString("Back",borderLeft+2,borderTop+28);
    }

    public int longMessage(Graphics g, int charPerLine, int startY, boolean thisUser, boolean admin)
    {
        int startPoint = startY;

        //System.out.println(admin);
        //System.out.println(false);

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
            else if(admin)
                g.setColor(new Color(255,215,0));
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
        g.setFont(bigFont);
        metrics = g.getFontMetrics();
        currentWidth = metrics.stringWidth("Chapp")/2;
        g.drawString("Chapp",borderLeft+getWidth/2-currentWidth,borderTop+30);

        spacing = (getHeight-borderTop-borderBottom-(50*3)-40)/4;
        //ChatRoom 1
        metrics = g.getFontMetrics();
        currentWidth = metrics.stringWidth("Chat Room One")/2;
        g.setColor(lightGrey);
        g.fillRoundRect((getWidth/2-borderLeft)-200,borderTop+39+spacing,400,50,10,10);
        g.setColor(babyBlue);
        g.drawString("Chat Room One",(getWidth/2-borderLeft)-currentWidth,borderTop+39+spacing+35);

        //ChatRoom 2
        metrics = g.getFontMetrics();
        currentWidth = metrics.stringWidth("Chat Room Two")/2;
        g.setColor(lightGrey);
        g.fillRoundRect((getWidth/2-borderLeft)-200,borderTop+89+2*spacing,400,50,10,10);
        g.setColor(babyBlue);
        g.drawString("Chat Room Two",(getWidth/2-borderLeft)-currentWidth,borderTop+89+2*spacing+35);
        //ChatRoom 3
        metrics = g.getFontMetrics();
        currentWidth = metrics.stringWidth("Chat Room Three")/2;
        g.setColor(lightGrey);
        g.fillRoundRect((getWidth/2-borderLeft)-200,borderTop+139+3*spacing,400,50,10,10);
        g.setColor(babyBlue);
        g.drawString("Chat Room Three",(getWidth/2-borderLeft)-currentWidth,borderTop+139+3*spacing+35);
    }

    public void usernamePage(Graphics g)
    {

        int ranX;



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

    public void errorPage(Graphics g, String Message)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth,getHeight);
        int y = getHeight-borderTop-borderBottom;
        int x = getWidth-borderLeft-borderRight;
        int radiX = 0;
        int radiY = 0;
        int Try = 100;
        while(Try > 10)
        {
            if(x%Try == 0)
            {
                radiX = x/Try;
            }

            Try--;
        }
        Try = 100;
        while(Try > 6)
        {
            if(y%Try == 0)
            {
                radiY = y/Try;
            }
            Try--;
        }
        if(radiX == 0)
            radiX = x/10;
        if(radiY == 0)
            radiY = y/6;
        int eex, ey, ez;
        for(int r = borderLeft; r <= x+radiX; r += radiX)
        {
            for(int l = borderTop; l <= y+radiY; l += radiY)
            {
                if((l-borderTop)/radiY == 4 && (r-borderLeft)/radiX == 1)
                {

                }
                else
                {
                    eex = (int) (Math.random() * 256);
                    ey = (int) (Math.random() * 256);
                    ez = (int) (Math.random() * 256);
                    backg.setColor(new Color(eex,ey,ez));
                    g.fillRect(r,l,radiX,radiY);
                }

            }
        }

        g.setColor(transparentGray);
        g.fillRect(0,0,getWidth,getHeight);

        g.setFont(Default);
        DrawMessage(g,"Something's out of place...",Message);
    }

    public void DrawMessage(Graphics g, String one, String two)
    {

        metrics = g.getFontMetrics();
        int limit = (getWidth-borderLeft-borderRight-100);
        int Space = (getHeight-borderTop-borderBottom-50-((limit)/metrics.stringWidth(two))*50)/2;

        int lines = 1 + (1+(metrics.stringWidth(two)/limit));
        if(metrics.stringWidth(two)>metrics.stringWidth(one))
        {
            if(metrics.stringWidth(two)>(limit))
                currentWidth = limit;
            else
                currentWidth = metrics.stringWidth(two);
        }
        else
            currentWidth = metrics.stringWidth(one);

        g.setColor(lightGrey);
        g.fillRoundRect((getWidth/2)-5-currentWidth/2,borderTop+Space,currentWidth+10,50*lines,10,10);
        g.setColor(Color.WHITE);
        g.drawString(one,(getWidth/2)-metrics.stringWidth(one)/2,borderTop+Space+35);
        int factor = 1;
        int charAllowed = limit/metrics.stringWidth("W");
        for(int x = 0; x < two.length(); x+= (charAllowed))
        {
            if(two.length() <= x+charAllowed)
            {
                //System.out.println("X: "+x+", Limit: "+limit+", Length: "+two.length());
                g.drawString(two.substring(x),(getWidth/2)-metrics.stringWidth(two.substring(x))/2,borderTop+Space+35+50*factor);

            }

            else
                g.drawString(two.substring(x,charAllowed),(getWidth/2)-metrics.stringWidth(two.substring(x,charAllowed))/2,borderTop+Space+35+50*factor);
            factor++;
        }


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

        if(inArea(pressX,pressY,chatRoomOneButton) && userEstablished && inAChatRoom == false)
        {
            inAChatRoom = true;

            try{objectToServer.writeObject("ChatRoom1");}
            catch(UnknownHostException ex){errorTypes[5] = true;}
            catch(IOException ex){errorTypes[5] = true;}
            catch(NullPointerException ex){ errorTypes[5] = true; }
            //catch(ClassNotFoundException ex){ localInput = "Error"; }


        }
        else if(inArea(pressX,pressY,chatRoomTwoButton) && userEstablished && inAChatRoom == false)
        {
            inAChatRoom = true;

            try{objectToServer.writeObject("ChatRoom2");}
            catch(UnknownHostException ex){errorTypes[5] = true;}
            catch(IOException ex){errorTypes[5] = true;}
            catch(NullPointerException ex){ errorTypes[5] = true; }
            //catch(ClassNotFoundException ex){ localInput = "Error"; }


        }

        else if(inArea(pressX,pressY,chatRoomThreeButton) && userEstablished && inAChatRoom == false)
        {
            inAChatRoom = true;

            try{objectToServer.writeObject("ChatRoom3");}
            catch(UnknownHostException ex){errorTypes[5] = true;}
            catch(IOException ex){errorTypes[5] = true;}
            catch(NullPointerException ex){ errorTypes[5] = true; }
            //catch(ClassNotFoundException ex){ localInput = "Error"; }


        }

        else if(inArea(pressX,pressY,backButton) && userEstablished && inAChatRoom == true)
        {
            inAChatRoom = false;

            try{objectToServer.writeObject("Left");}
            catch(UnknownHostException ex){errorTypes[5] = true;}
            catch(IOException ex){errorTypes[5] = true;}
            catch(NullPointerException ex){ errorTypes[5] = true; }
            //catch(ClassNotFoundException ex){ localInput = "Error"; }


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
            if(!userEstablished&&checkUsername()&&!localInput.equalsIgnoreCase("admin"))
            {}


            else if(userEstablished )
                try{

                    //correctMessage();
                    if(localInput.equals("/exit"))
                    {
                        inAChatRoom = false;
                        try{objectToServer.writeObject("Left");}
                        catch(UnknownHostException ex){errorTypes[5] = true;}
                        catch(IOException ex){errorTypes[5] = true;}
                        catch(NullPointerException ex){ errorTypes[5] = true; }
                        //catch(ClassNotFoundException ex){ localInput = "Error"; }


                    }
                    else if(!muted)
                    {}
                    localInput = "";
                }


                catch(NullPointerException e){ errorTypes[5] = true; }


        }

        else if(evt.getKeyCode() == KeyEvent.VK_UP)
        {


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


                }

            }
        }
    }
}