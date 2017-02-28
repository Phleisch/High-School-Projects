/**
 * Created by Kai W. Fleischman on 2/24/2017.
 * This program is the client side of computer
 * chess.
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
        tester.setSize(1366,728);
        tester.setVisible(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("In shutdown hook");
        }, "Shutdown-thread"));
    }
}

class ClientTestServer extends Frame implements MouseListener, MouseMotionListener, KeyListener
{
    private ArrayList<Message> messageCache;
    private ArrayList<Match> matchCache;
    private Rectangle chatToggle, chatArea, newGame;
    private boolean chatVisible;
    private Client local;
    private ObjectOutputStream objectToServer;
    private ObjectInputStream objectFromServer;
    private String localInput;
    private FontMetrics metrics;
    private Image backbuffer;
    private Graphics backg;
    private Color transparentGray, babyBlue, lightGrey, lightGreyT;
    private int getWidth, getHeight, borderLeft, borderRight, borderTop, borderBottom, chatLeft,
            messageStartIndex, textBoxYValue, charactersPerLine, boxSize, messStart,
            gameButtonHeight, buttonStartHeight;
    private boolean firstGraphicsWindow, chatting;
    private Image chessBackground;
    private Font bigFont,mediumFont, Default;
    private ArrayList<String> Lines;
    private final char[] typableCharacters = {'a','b','c','d','e','f','g','h','i','j','k','l',
            'm','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H',
            'I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3',
            '4','5','6','7','8','9','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[',
            '}',']',':',';','"','<',',','>','.','?','/','\\','|','\'',' '};
    private void init()
    {
        matchCache = new ArrayList<>();
        chatLeft = 345;
        boxSize = 23;
        gameButtonHeight = 100;
        buttonStartHeight = borderTop+39;
        messageCache = new ArrayList<>();
        try {/*cat*/chessBackground = ImageIO.read(new File("C:\\Users\\KaiFl\\IdeaProjects\\High-School-Projects\\Chapp\\src\\cat.jpg"));
            //chessBackground = ImageIO.read(new File("C:\\Users\\s690016\\Pictures\\images.jpg"));
        } catch(IOException e) {System.out.println("Could not load images.");}
        chatVisible = true;
        chatting = false;
        bigFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
        mediumFont = new Font(Font.MONOSPACED, Font.BOLD, 20);
        Default = new Font("Monospaced",Font.PLAIN,40);
        localInput = "";
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        transparentGray = new Color(50,50,50,150);
        babyBlue = new Color(0,191,255);
        firstGraphicsWindow = true;
        borderLeft = 8;
        borderRight = 9;
        borderTop = 31;
        borderBottom = 9;
        lightGrey = new Color(188,184,184);
        lightGreyT = new Color(188,184,184,200);
        messageStartIndex = 0;
        textBoxYValue = 0;
        Lines = new ArrayList<>();
    }

    ClientTestServer() throws IOException
    {
        local = null;
        Scanner user = new Scanner(System.in);
        String ip;
        int maxNameLength = 14;
        boolean connected = false;
        while(!connected)
        {
            connected = true;
            System.out.print("IP address of your server: ");
            ip = user.nextLine();
            if(ip.equals("quit"))
                System.exit(0);
            System.out.println("Attempting connection...");
            try
            {
                Socket socket = new Socket(InetAddress.getByName(ip), 8000);
                objectFromServer = new ObjectInputStream(socket.getInputStream());
                objectToServer = new ObjectOutputStream(socket.getOutputStream());
            } catch(Exception err) {
                System.out.println("Bad connection or bad IP. Try again or abort (\"quit\").\n");
                connected = false;
            }
        }
        System.out.println("Connected!");

        boolean validName = false;
        String name;
        while(!validName)
        {
            validName = true;
            System.out.print("\nDeclare a unique username: ");
            name = user.nextLine();
            objectToServer.writeObject(name);
            try{Object obj = objectFromServer.readObject();
                if(obj == null || name.length()>maxNameLength || name.length()==0)
                {
                    System.out.println("Something went wrong.");
                    validName = false;
                }
                local = (Client)obj;}
            catch(Exception e)
            {
                System.out.println("Something went wrong.");
                validName = false;
            }
        }
        init();

    }

    public void paint(Graphics g)
    {
        if( firstGraphicsWindow )
        {
            getWidth = getWidth();
            getHeight = getHeight();
            backbuffer = createImage(getWidth, getHeight);
            backg = backbuffer.getGraphics();
            listenForEvents task = new listenForEvents();
            new Thread(task).start();
            try{
                objectToServer.writeObject(2);
                objectToServer.reset();
                objectToServer.writeObject(3);
                objectToServer.reset();
            }
            catch(Exception e)
            {
                System.out.println("Something went wrong.");
            }
            chatToggle = new Rectangle(getWidth-(chatLeft+30),borderTop+39,30,getHeight);
            chatArea = new Rectangle(getWidth-chatLeft,borderTop+39,chatLeft-borderRight,getHeight);
            newGame = new Rectangle(getWidth-150,borderTop,150,38);
            messStart = getWidth-chatLeft+5;
        }
        g.setFont(mediumFont);
        firstGraphicsWindow = false;
        update(g);
    }

    public void update(Graphics g)
    {
        backg.setColor(Color.WHITE);
        backg.fillRect(0,0,getWidth,getHeight);
        backg.drawImage(chessBackground,0,0,getWidth,getHeight,this);
        chatArea(backg);
        homePage(backg);
        g.drawImage(backbuffer, 0, 0, this);
        repaint();
    }

    private int validChar(char a)
    {
        for( char check : typableCharacters )
            if( a == check )
                return 0;
        return -1;
    }

    private void chatArea(Graphics g)
    {
        if(chatVisible)
        {
            int locX = 30+chatLeft;
            g.setColor(lightGreyT);
            g.fillRoundRect(getWidth-locX,borderTop+39,locX,getHeight-borderTop-38-borderBottom-1,2,2);
            g.setColor(Color.black);
            g.drawRoundRect(getWidth-locX,borderTop+39,locX-chatLeft,getHeight-borderTop-38-borderBottom-1,2,2);
            g.setFont(mediumFont);
            g.setColor(Color.WHITE);
            g.drawString(">>",(getWidth-locX)+3,borderTop+362);
            g.setColor(Color.black);
            g.drawRect(getWidth-chatLeft,borderTop+39,chatLeft-borderRight,getHeight-borderTop-38-borderBottom-1);
            entryBox(g);
            drawMessages(g);
        }
        else
        {
            int locX = 30;
            g.setColor(lightGreyT);
            g.fillRoundRect(getWidth-(locX+borderRight),borderTop+39,locX,getHeight-borderTop-38-borderBottom-1,2,2);
            g.setColor(Color.black);
            g.drawRoundRect(getWidth-(locX+borderRight),borderTop+39,locX,getHeight-borderTop-38-borderBottom-1,2,2);
            g.setFont(mediumFont);
            g.setColor(Color.WHITE);
            g.drawString("<<",getWidth-(locX+borderRight)+3,borderTop+362);
        }
    }

    private void entryBox(Graphics g) {
        Lines.clear();
        g.setFont(mediumFont);
        metrics = g.getFontMetrics();
        charactersPerLine = (chatLeft - borderRight) / metrics.stringWidth("A");
        charactersPerLine--;
        if (localInput.length() <= charactersPerLine) {
            textBoxYValue = getHeight - borderBottom - boxSize;
            g.setColor(transparentGray);
            g.fillRect(getWidth-chatLeft, textBoxYValue, chatLeft, boxSize);
            g.fillRect(getWidth-chatLeft, textBoxYValue, chatLeft, boxSize);
            g.setColor(Color.BLACK);
            g.drawRect(getWidth-chatLeft, textBoxYValue, chatLeft, boxSize);
            g.setColor(Color.WHITE);
            g.drawString(localInput, messStart, getHeight - 15);
            textBoxYValue = getHeight-borderBottom-boxSize*2;
        } else {
            for (int m = 0; m < localInput.length(); m += charactersPerLine) {
                if (m + charactersPerLine >= localInput.length() && m!=0)
                    Lines.add(localInput.substring(m, localInput.length()));
                else
                    Lines.add(localInput.substring(m, m + charactersPerLine));
            }
            longMessage(g, (getHeight - borderBottom - (boxSize * Lines.size())), true);
            textBoxYValue = (getHeight - borderBottom - (boxSize * (Lines.size()+1)));
        }
    }

    private void drawMatches(Graphics g)
    {
        g.setFont(Default);
        metrics = g.getFontMetrics();
        buttonStartHeight = borderTop+39;
        int vis = chatVisible ? 1 : 0;
        int nVis = !chatVisible ? 1 : 0;
        int curr = (getWidth-chatLeft*vis-borderRight*nVis-(30+borderLeft)) / metrics.stringWidth("A");
        String display;
        for(Match a : matchCache)
        {
            if(a.getPlayerTwo()==null)
                display = a.getPlayerOne().getName() + " vs. ???";
            else
                display = a.getPlayerOne().getName() + " vs. " + a.getPlayerTwo().getName();
            curr = metrics.stringWidth(display);
            g.setColor(transparentGray);
            g.fillRect(borderLeft,buttonStartHeight,getWidth-chatLeft*vis-borderRight*nVis-(30+borderLeft),gameButtonHeight);
            g.setColor(Color.BLACK);
            g.drawRect(borderLeft,buttonStartHeight,getWidth-chatLeft*vis-borderRight*nVis-(30+borderLeft),gameButtonHeight);
            g.setColor(Color.WHITE);
            g.drawString(display,((getWidth-chatLeft*vis-borderRight*nVis-(30+borderLeft)-curr)/2)+borderLeft,buttonStartHeight+70);
            buttonStartHeight+=gameButtonHeight;
        }
        g.setFont(mediumFont);
    }

    ////////////////////////////////////////////////
    //             Essential Methods              //
    ////////////////////////////////////////////////

    private void drawMessages(Graphics g)
    {
        Lines.clear();
        String current;
        g.setFont(mediumFont);
        metrics = g.getFontMetrics();
        int startPoint = textBoxYValue;
        if(messageCache.size() > 0) {
            for (int x = messageStartIndex; x < messageCache.size(); x++) {
                current = messageCache.get(x).getMessage();
                g.setFont(mediumFont);

                if (messageCache.get(x).getSender().equals(local.getName())) {
                    current = "<You> " + current;
                    g.setColor(Color.WHITE);
                    if (current.length() > charactersPerLine) {

                        for (int m = 0; m < current.length(); m += charactersPerLine) {
                            if (m + charactersPerLine >= current.length() && m!=0)
                                Lines.add(current.substring(m, current.length()));
                            else
                                Lines.add(current.substring(m, m + charactersPerLine));
                        }


                        startPoint = longMessage(g, (startPoint + boxSize - (Lines.size() * boxSize)), true);

                    } else {
                        g.setColor(transparentGray);
                        g.fillRect(getWidth-chatLeft, startPoint, chatLeft, boxSize);
                        g.setColor(Color.BLACK);
                        g.drawRect(getWidth-chatLeft, startPoint, chatLeft, boxSize);
                        g.setColor(Color.WHITE);
                        g.drawString("<You> " + messageCache.get(x).getMessage(), messStart, startPoint + 17);
                        startPoint -= boxSize;
                    }

                } else {
                    current = "<" + messageCache.get(x).getSender() + "> " + current;
                    if (current.length() > charactersPerLine) {
                        for (int m = 0; m < current.length(); m += charactersPerLine) {
                            if (m + charactersPerLine >= current.length() && m!=0)
                                Lines.add(current.substring(m, current.length()));
                            else
                                Lines.add(current.substring(m, m + charactersPerLine));
                        }
                        startPoint = longMessage(g, (startPoint + boxSize - (Lines.size() * boxSize)), false);
                    } else {
                        g.setColor(transparentGray);
                        g.fillRect(getWidth-chatLeft, startPoint, chatLeft, boxSize);
                        g.setColor(Color.BLACK);
                        g.drawRect(getWidth-chatLeft, startPoint, chatLeft, boxSize);
                        g.setColor(babyBlue);
                        g.drawString(current, messStart, startPoint + 17);
                        startPoint -= boxSize;
                    }
                }
                Lines.clear();
            }
        }
    }

    private int longMessage(Graphics g, int startY, boolean thisUser)
    {
        int startPoint = startY;
        g.setFont(mediumFont);
        metrics = g.getFontMetrics();
        g.setColor(transparentGray);
        g.fillRect(getWidth-chatLeft, startPoint, chatLeft, Lines.size()*boxSize);
        g.setColor(Color.BLACK);
        g.drawRect(getWidth-chatLeft, startPoint, chatLeft, Lines.size()*boxSize);
        for (String Line : Lines) {
            if (thisUser)
                g.setColor(Color.WHITE);
            else
                g.setColor(babyBlue);
            g.drawString(Line, messStart, startPoint + 17);
            startPoint += boxSize;
        }
        return startY-boxSize;
    }

    ////////////////////////////////////////////////
    //              Graphics Methods              //
    ////////////////////////////////////////////////


    private void homePage(Graphics g)
    {
        drawMatches(g);
        metrics = g.getFontMetrics();
        g.setColor(lightGrey);
        g.fillRect(borderLeft,borderTop-1,getWidth-borderLeft*2,40);
        g.setFont(mediumFont);
        g.setColor(Color.WHITE);
        g.drawString(timeMessage()+", "+local.getName(),15+borderLeft,30+borderTop);
        int currentWidth = metrics.stringWidth("New Game");
        g.drawString("New Game",getWidth-(borderRight+15+ currentWidth),30+borderTop);
        g.setColor(babyBlue);
        currentWidth = metrics.stringWidth("+ New Game");
        g.drawString("+",getWidth-borderRight-15- currentWidth,30+borderTop);
        g.setColor(Color.WHITE);
        g.setFont(bigFont);
        metrics = g.getFontMetrics();
        currentWidth = metrics.stringWidth("Chess")/2;
        g.drawString("Chess",borderLeft+getWidth/2- currentWidth,borderTop+30);
    }

    private String timeMessage()
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

    ////////////////////////////////////////////////
    //            Java Event Methods              //
    ////////////////////////////////////////////////

    public void mouseClicked(MouseEvent e)
    {
        int pressX = e.getX();
        int pressY = e.getY();
        /*
        System.out.println("Click detected.");
        System.out.println("Press X: "+pressX+" Press Y: "+pressY);
        System.out.println("X: "+chatToggle.getX()+" Y: "+chatToggle.getY()+" Width: "+chatToggle.getWidth()+" Height"+chatToggle.getHeight());
        */
        if(chatToggle.contains(pressX,pressY))
        {
            //System.out.println("Contained.");
            if(chatVisible)
            {
                chatToggle.setBounds(getWidth-30,borderTop+39,30,getHeight);
                chatArea.setBounds(0,0,0,0);
                chatting = false;
            }
            else
            {
                chatToggle.setBounds(getWidth-chatLeft-30,borderTop+39,30,getHeight);
                chatArea.setBounds(getWidth-chatLeft,borderTop+39,chatLeft-borderRight,getHeight);
                chatting = true;
            }
            chatVisible = !chatVisible;
        }
        else if(newGame.contains(pressX,pressY))
        {
            try{
                objectToServer.writeObject(new PVP(local));
                objectToServer.reset();
            } catch(IOException ex)
            {
                System.out.println("Could not send match.");
            }
            localInput = "";
        }
        else chatting = chatArea.contains(pressX, pressY);
        repaint();
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
        if(chatting){
            if(evt.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                if(localInput.length() != 0)
                    localInput = localInput.substring(0,localInput.length()-1);
            }
            else if(evt.getKeyCode() == KeyEvent.VK_ENTER && localInput.length()>0)
            {
                try{
                    objectToServer.writeObject(new Message(localInput,local));
                    objectToServer.reset();
                } catch(IOException e)
                {
                    System.out.println("Could not send Message.");
                }
                localInput = "";
            }
            else if(evt.getKeyCode() == KeyEvent.VK_UP)
            {
                if(messageStartIndex<messageCache.size()-1)
                    messageStartIndex++;
            }
            else if(evt.getKeyCode() == KeyEvent.VK_DOWN)
            {
                if(messageStartIndex>0)
                    messageStartIndex--;
            }
            else{
                if( validChar(evt.getKeyChar()) != -1 )
                    localInput = localInput + evt.getKeyChar();
            }
        }
        repaint();
    }

    private class listenForEvents implements Runnable
    {
        listenForEvents() { }
        public void run()
        {
            while(true)
            {
                try{
                    Object object = objectFromServer.readObject();
                    if(object instanceof ArrayList) {
                        ArrayList curr = (ArrayList)object;
                        if(curr.get(0) instanceof Message)
                            messageCache = (ArrayList<Message>)object;
                        else
                            matchCache = (ArrayList<Match>)object;
                    }
                }catch(Exception e){System.out.println(e); break;}
            }
            System.exit(1);
        }
    }
}