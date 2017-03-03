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

//Chess Pictures: 1 = Bishop, 2 = King, 3 = Knight, 4 = Pawn, 5 = Rook, 6 = Queen

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
    private final int SQUARE_SIZE = 85;
    private int orgX, orgY;
    private int hoveringX, hoveringY, hoverOffCenterX, hoverOffCenterY;
    private Image hovering;
    private Image[][] setUp, matchBoard;
    private String[][] mirrorBoard;
    private Rectangle[][] chessSquares;
    private ArrayList<Message> messageCache;
    private Match thisMatch;
    private ArrayList<Rectangle> matches;
    private ArrayList<Match> matchCache;
    private Rectangle chatToggle, chatArea, newGame, tempLeave;
    private Client local;
    private ObjectOutputStream objectToServer;
    private ObjectInputStream objectFromServer;
    private String localInput;
    private FontMetrics metrics;
    private Image backbuffer;
    private Graphics backg;
    private boolean[] menuButtons;
    private Color transparentGray, babyBlue, lightGrey, lightGreyT, babyBlueT, boardBrown, boardTan;
    private int getWidth, getHeight, borderLeft, borderRight, borderTop, borderBottom, chatLeft,
            messageStartIndex, textBoxYValue, charactersPerLine, boxSize, messStart,
            gameButtonHeight, buttonStartHeight;
    private boolean firstGraphicsWindow, chatting, chatVisible, inMatch, firstMatchWindow, isTurn,
                    firstBoard;
    private Boolean isWhite;
    private Image chessBackground;
    private Image[] whitePieces;
    private Image[] blackPieces;
    private Font bigFont, mediumFont, Default;
    private ArrayList<String> Lines;
    private final char[] typableCharacters = {'a','b','c','d','e','f','g','h','i','j','k','l',
            'm','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H',
            'I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3',
            '4','5','6','7','8','9','!','@','#','$','%','^','&','*','(',')','_','-','+','=','{','[',
            '}',']',':',';','"','<',',','>','.','?','/','\\','|','\'',' '};
    private void init()
    {
        isTurn = false;
        isWhite = null;
        hovering = null;
        firstMatchWindow = true;
        whitePieces = new Image[6];
        blackPieces = new Image[6];
        menuButtons = new boolean[3];
        thisMatch = null;
        matches = new ArrayList<>();
        inMatch = false;
        matchCache = new ArrayList<>();
        chatLeft = 345;
        boxSize = 23;
        gameButtonHeight = 100;
        buttonStartHeight = borderTop+39;
        messageCache = new ArrayList<>();
        try {/*cat*///chessBackground = ImageIO.read(new File("C:\\Users\\KaiFl\\IdeaProjects\\High-School-Projects\\Chapp\\src\\cat.jpg"));
            //chessBackground = ImageIO.read(new File("C:\\Users\\s690016\\Pictures\\images.jpg"));
            for(int a = 0; a < 6; a++)
            {
                String name = "C:\\Users\\KaiFl\\Desktop\\Chess Pieces\\W" + (a+1) + ".png";
                whitePieces[a] = ImageIO.read(new File(name));
            }
            for(int a = 0; a < 6; a++)
            {
                String name = "C:\\Users\\KaiFl\\Desktop\\Chess Pieces\\B" + (a+1) + ".png";
                blackPieces[a] = ImageIO.read(new File(name));
            }
        } catch(IOException e) {System.out.println("Could not load images.");}

        chessSquares = new Rectangle[8][8];
        matchBoard = new Image[8][8];

        ////////////////////// Fill String Board /////////////////////
        
        mirrorBoard = new String[8][8];
        for(int i = 0; i < 64; i++)
        {
            mirrorBoard[i/8][i%8] = "  ";
        }
        mirrorBoard[0][0] = "BR"; mirrorBoard[0][1] = "BN";
        mirrorBoard[0][2] = "BB"; mirrorBoard[0][3] = "BQ";
        mirrorBoard[0][4] = "BK"; mirrorBoard[0][5] = "BB";
        mirrorBoard[0][6] = "BN"; mirrorBoard[0][7] = "BR";
        for(int i = 0; i < 8; i++)
            mirrorBoard[1][i] = "BP";
        for(int i = 0; i < 8; i++)
            mirrorBoard[6][i] = "WP";
        mirrorBoard[7][0] = "WR"; mirrorBoard[7][1] = "WN";
        mirrorBoard[7][2] = "WB"; mirrorBoard[7][3] = "WQ";
        mirrorBoard[7][4] = "WK"; mirrorBoard[7][5] = "WB";
        mirrorBoard[7][6] = "WN"; mirrorBoard[7][7] = "WR";

        ////////////////////// Fill Image Board /////////////////////

        setUp = new Image[8][8];
        setUp[0][0] = blackPieces[4]; setUp[0][1] = blackPieces[2];
        setUp[0][2] = blackPieces[0]; setUp[0][3] = blackPieces[5];
        setUp[0][4] = blackPieces[1]; setUp[0][5] = blackPieces[0];
        setUp[0][6] = blackPieces[2]; setUp[0][7] = blackPieces[4];
        for(int i = 0; i < 8; i++)
            setUp[1][i] = blackPieces[3];
        for(int i = 0; i < 8; i++)
            setUp[6][i] = whitePieces[3];
        setUp[7][0] = whitePieces[4]; setUp[7][1] = whitePieces[2];
        setUp[7][2] = whitePieces[0]; setUp[7][3] = whitePieces[5];
        setUp[7][4] = whitePieces[1]; setUp[7][5] = whitePieces[0];
        setUp[7][6] = whitePieces[2]; setUp[7][7] = whitePieces[4];

        /////////////////////////////////////////////////////////////

        matchBoard = setUp;

        chatVisible = true;
        chatting = false;
        bigFont = new Font(Font.MONOSPACED, Font.BOLD, 30);
        mediumFont = new Font(Font.MONOSPACED, Font.BOLD, 20);
        Default = new Font("Monospaced",Font.PLAIN,40);
        localInput = "";
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        transparentGray = new Color(50,50,50,150);
        babyBlue = new Color(0,191,255);
        babyBlueT = new Color(0,191,255,100);
        boardBrown = new Color(90,47,47);
        boardTan = new Color(236,213,158);
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
            tempLeave = new Rectangle(0,0,100,100);
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
        if(!inMatch)
        {
            chatArea(backg);
            homePage(backg);
        }
        else
        {
            matchArea(backg);
            if(hovering!=null)
                hoveringPiece(backg);
        }
        //dichotomyMenu(backg,"Join Match?","Yes","No",1);
        //dichotomyMenu(backg,"Choose Your Side","Black","White",1);
        g.drawImage(backbuffer, 0, 0, this);
        repaint();
    }

    private void hoveringPiece(Graphics g)
    {
        g.drawImage(hovering,hoveringX,hoveringY,SQUARE_SIZE-4,SQUARE_SIZE-4,this);
    }

    private int validChar(char a)
    {
        for( char check : typableCharacters )
            if( a == check )
                return 0;
        return -1;
    }

    private void dichotomyMenu(Graphics g, String message, String optionOne, String optionTwo, int booleanIndex)
    {
        g.setColor(lightGrey);
        g.setFont(bigFont);
        metrics = g.getFontMetrics();
        int length = metrics.stringWidth(message)+12;
        g.fillRoundRect((getWidth-length)/2,(getHeight-150)/2,length,150,20,20);
        g.setColor(Color.BLACK);
        g.drawRoundRect((getWidth-length)/2,(getHeight-150)/2,length,150,20,20);
        g.drawLine((getWidth-length)/2,(getHeight/2)-25,(getWidth+length)/2,(getHeight/2)-25);
        g.drawLine((getWidth-length/2)/2,(getHeight/2)+25,(getWidth+length/2)/2,(getHeight/2)+25);
        g.setColor(Color.WHITE);
        length = metrics.stringWidth(message);
        g.drawString(message,((getWidth-length)/2),(getHeight/2)-40);
        length = metrics.stringWidth(optionOne);
        g.drawString(optionOne,((getWidth-length)/2),(getHeight/2)+10);
        length = metrics.stringWidth(optionTwo);
        g.drawString(optionTwo,((getWidth-length)/2),(getHeight/2)+60);
    }

    private void updateMatchButtons()
    {
        matches.clear();
        buttonStartHeight = borderTop+39;
        int vis = chatVisible ? 1 : 0;
        int nVis = !chatVisible ? 1 : 0;
        for(int a = 0; a < matchCache.size(); a++)
        {
            matches.add(new Rectangle(borderLeft,buttonStartHeight,getWidth-chatLeft*vis-borderRight*nVis-(30+borderLeft),
                    gameButtonHeight));
            buttonStartHeight+=gameButtonHeight;
        }
    }

    private void matchArea(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.drawString(""+isTurn,100,100);
        int spaceAdd = getHeight-SQUARE_SIZE*8;
        spaceAdd/=2;
        spaceAdd = (((spaceAdd-borderTop)+(spaceAdd-borderBottom))/2)-(spaceAdd-borderTop);

        if(firstMatchWindow)
        {
            firstGraphicsWindow = false;
            for(int a = 0; a < 8; a++)
                for (int b = 0; b < 8; b++) {
                    chessSquares[a][b] = new Rectangle(((getWidth / 2) + 4 * SQUARE_SIZE) - (SQUARE_SIZE * (8 - b)),
                            (((getHeight / 2) + 4 * SQUARE_SIZE) - (SQUARE_SIZE * (8 - a))) + spaceAdd,
                            SQUARE_SIZE, SQUARE_SIZE);
                }
        }

        for(int i = 0; i < 8; i++)
            for (int k = 0; k < 8; k++) {
                if (((k * 8 + i + k % 2) % 2 == 0 && isWhite) || ((k * 8 + i + k % 2) % 2 == 1 && !isWhite))
                    g.setColor(boardTan);
                else
                    g.setColor(boardBrown);
                g.fillRect(((getWidth / 2) + 4 * SQUARE_SIZE) - (SQUARE_SIZE * (8 - k)),
                        (((getHeight / 2) + 4 * SQUARE_SIZE) - (SQUARE_SIZE * (8 - i))) + spaceAdd,
                        SQUARE_SIZE, SQUARE_SIZE);
            }

        for(int i = 0; i < 8; i++)
            for (int k = 0; k < 8; k++) {
                if (matchBoard[i][k] != null)
                    g.drawImage(matchBoard[i][k], 2 + (((getWidth / 2) + 4 * SQUARE_SIZE) - (SQUARE_SIZE * (8 - k))),
                            2 + ((((getHeight / 2) + 4 * SQUARE_SIZE) - (SQUARE_SIZE * (8 - i))) + spaceAdd),
                            SQUARE_SIZE - 4, SQUARE_SIZE - 4, this);
            }
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
        int curr;
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

    private String[][] flip(String[][] mat)
    {
        String[][] toGive = new String[8][8];
        for(int a = 0; a <= 7; a++)
        {
            toGive[7-a] = mat[a];
        }
        return toGive;
    }

    private Image[][] flip(Image[][] mat)
    {
        Image[][] toGive = new Image[8][8];
        for(int a = 0; a <= 7; a++)
        {
            toGive[7-a] = mat[a];
        }
        return toGive;
    }

    ////////////////////////////////////////////////
    //         Chess Validation Methods           //
    ////////////////////////////////////////////////

    //Check if Bishop's move is valid
    private boolean validMoveB(int x1, int y1, int x2, int y2, String[][] board) {
        int dX = Math.abs(x2 - x1);
        int dY = Math.abs(y2 - y1);
        return pathIsClear(board, x1, y1, x2, y2) && dX == dY;
    }

    //Check if King's move is valid
    private boolean validMoveK(int x1, int y1, int x2, int y2, String[][] board) {
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);
        return !(dX > 1 || dY > 1);
    }

    //Check if Knight's move is valid
    private boolean validMoveN(int x1, int y1, int x2, int y2) {
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);
        return (dX == 2 && dY == 1) || (dX == 1 && dY == 2);
    }

    //Check if Rook's move is valid
    private boolean validMoveR(int x1, int y1, int x2, int y2, String[][] board) {
        int dX = Math.abs(x2 - x1);
        int dY = Math.abs(y2 - y1);
        return pathIsClear(board, x1, y1, x2, y2) && !(dX != 0 && dY != 0);
    }

    //Check if Pawn's move is valid
    private boolean validMoveP(int x1, int y1, int x2, int y2, String[][] board) {
        if(!isWhite)
        {
            y1 = 7 - y1;
            y2 = 7 - y2;
        }
        System.out.println("Initial position: "+board[y1][x1]);
        System.out.println("Try location: "+board[y2][x2]+"\n");
        boolean moved = true;
        boolean white = board[y1][x1].contains("W");
        if(( board[y1][x1].contains("B") || board[y1][x1].contains("W") ) && y1 == 1)
            moved = false;
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);
        if( !moved && dY == 2 && dX == 0)
            return true;
        if(((white && board[y2][x2].contains("B")) || (!white && board[y2][x2].contains("W")))
            && dX == 1 && y1-y2 > 0)
            return true;
        if(( board[y1][x1].contains("B") || board[y1][x1].contains("W")) && y1-y2 > 0)
            return true;
        return false;
    }

    //Check if Queen's move is valid
    private boolean validMoveQ(int x1, int y1, int x2, int y2, String[][] board) {
        int dX = Math.abs(x2-x1);
        int dY = Math.abs(y2-y1);
        return pathIsClear(board, x1, y1, x2, y2) && ((dX == dY) || (dX > 0 && dY == 0) || (dX == 0 && dY > 0));
    }

    private boolean pathIsClear(String[][] board, int x1, int y1, int x2, int y2) {
        boolean works = true;
        int changeX = x2-x1;
        int changeY = y2-y1;
        int max = Math.max(Math.abs(changeX),Math.abs(changeY));
        if(changeX!=0)
            changeX /= Math.abs(x2-x1);
        if(changeY!=0)
            changeY /= Math.abs(y2-y1);
        int currX = x1+changeX;
        int currY = y1+changeY;
        for(int i = 1; i < max; i++){
            if(!board[currY][currX].equals("  "))
                works = false;
            currX+=changeX;
            currY+=changeY;
        }
        return works;
    }

    ////////////////////////////////////////////////
    //            Java Event Methods              //
    ////////////////////////////////////////////////

    public void mouseClicked(MouseEvent e)
    {
        int pressX = e.getX();
        int pressY = e.getY();
        if(!inMatch)
        {
            if(chatToggle.contains(pressX,pressY))
            {
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
                updateMatchButtons();
            }
            else if(newGame.contains(pressX,pressY))
            {
                firstBoard = true;
                try{
                    isWhite = true;
                    PVP curr = new PVP(local);
                    thisMatch = curr;
                    inMatch = true;
                    isTurn = true;
                    objectToServer.writeObject(curr);
                    objectToServer.reset();
                } catch(IOException ex)
                {
                    inMatch = false;
                    thisMatch = null;
                    System.out.println("Could not send match.");
                }
                localInput = "";
            }
            else chatting = chatArea.contains(pressX, pressY);
            for(int i = 0; i < matches.size(); i++)
            {
                if(matches.get(i).contains(pressX,pressY))
                {
                    firstBoard = true;
                    inMatch = true;
                    thisMatch = matchCache.get(i);
                    try{
                        objectToServer.writeObject(thisMatch);
                        objectToServer.reset();
                    } catch(IOException ex) {System.out.println("Could not send match.");}
                    if(matchCache.get(i).getPlayerTwo()==null)
                    {
                        isWhite = false;
                        mirrorBoard = flip(mirrorBoard);
                        matchBoard = flip(matchBoard);
                    }
                    else
                        isWhite = null;
                    break;
                }
            }
        }
        else
        {
            if(tempLeave.contains(pressX,pressY))
            {
                inMatch = false;
                isWhite = null;
                isTurn = false;
                try{
                    objectToServer.writeObject("leave");
                    objectToServer.reset();
                } catch(IOException ex) {System.out.println("Could not leave match.");}
            }
        }
        repaint();
    }


    public void mouseMoved(MouseEvent e)    { }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseDragged(MouseEvent e)
    {
        int pressX = e.getX();
        int pressY = e.getY();
        if(inMatch)
        {
            if(hovering!=null)
            {
                hoveringX = pressX-hoverOffCenterX;
                hoveringY = pressY-hoverOffCenterY;
            }
        }
        repaint();
    }
    public void mouseExited(MouseEvent e) 	{ }
    public void mousePressed(MouseEvent e)
    {
        int pressX = e.getX();
        int pressY = e.getY();
        if(inMatch)
        {
            for(int a = 0; a < 8; a++)
            {
                for(int b = 0; b < 8; b++)
                {
                    if(chessSquares[a][b].contains(pressX,pressY))
                    {
                        hovering = matchBoard[a][b];
                        matchBoard[a][b] = null;
                        orgY = a;
                        orgX = b;
                        hoverOffCenterX = pressX-((int)chessSquares[a][b].getX())+4;
                        hoverOffCenterY = pressY-((int)chessSquares[a][b].getY())+4;
                        hoveringX = pressX-hoverOffCenterX;
                        hoveringY = pressY-hoverOffCenterY;
                    }
                }
            }
        }
        repaint();
    }
    public void mouseReleased(MouseEvent e)
    {
        int pressX = e.getX();
        int pressY = e.getY();
        if(inMatch)
            if (hovering != null)
            {
                for (int a = 0; a < 8; a++)
                    for (int b = 0; b < 8; b++)
                        if (chessSquares[a][b].contains(pressX, pressY))
                        {
                            boolean validMove = false;
                            if(mirrorBoard[orgY][orgX].length()>1)
                            {
                                String[][] p = flip(mirrorBoard);
                                for(String[] c : p)
                                    System.out.println(Arrays.toString(c));
                                System.out.println();
                                String[][] temp = isWhite ? mirrorBoard : flip(mirrorBoard);
                                switch(mirrorBoard[orgY][orgX].substring(1))
                                {
                                    case "B": validMove = validMoveB(orgX,orgY,b,a,temp); break;
                                    case "K": validMove = validMoveK(orgX,orgY,b,a,temp); break;
                                    case "N": validMove = validMoveN(orgX,orgY,b,a); break;
                                    case "P": validMove = validMoveP(orgX,orgY,b,a,temp); break;
                                    case "R": validMove = validMoveR(orgX,orgY,b,a,temp); break;
                                    case "Q": validMove = validMoveQ(orgX,orgY,b,a,temp); break;
                                    default: validMove = false;
                                }
                            }
                            if ((matchBoard[a][b] == null || ((isWhite && mirrorBoard[a][b].contains("B")) ||
                                    (!isWhite && mirrorBoard[a][b].contains("W")))) && validMove
                                    && isTurn && ((isWhite && mirrorBoard[orgY][orgX].contains("W")) ||
                                    (!isWhite && mirrorBoard[orgY][orgX].contains("B"))) && !(a==orgY && b==orgX)
                                    && (orgY!=b && orgX!=a))
                            {
                                matchBoard[a][b] = hovering;
                                mirrorBoard[a][b] = mirrorBoard[orgY][orgX];
                                mirrorBoard[orgY][orgX] = "  ";
                                try{
                                    String[][] temp;
                                    if(!isWhite)
                                    {
                                        temp = flip(mirrorBoard);
                                    }
                                    else
                                        temp = mirrorBoard;
                                    Board curr = new Board(temp);
                                    objectToServer.writeObject(curr);
                                    objectToServer.reset();
                                } catch(IOException ex) {System.out.println(ex);}
                            }
                            else
                                matchBoard[orgY][orgX] = hovering;
                            hovering = null;
                        }
                if(hovering!=null)
                {
                    matchBoard[orgY][orgX] = hovering;
                    hovering = null;
                }
            }
        repaint();
    }
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
                        if(curr.size()>0)
                        {
                            if(curr.get(0) instanceof Message)
                                messageCache = (ArrayList<Message>)object;
                            else if(curr.get(0) instanceof Match)
                            {
                                matchCache = (ArrayList<Match>)object;
                                updateMatchButtons();
                            }
                        }
                        else
                        {

                            matchCache.clear();
                            updateMatchButtons();
                        }
                    }
                    else if(object instanceof Board)
                    {
                        matchBoard = new Image[8][8];
                        Board board = (Board)object;
                        if(isWhite==false)
                        {
                            mirrorBoard = flip(board.getBoard());
                        }
                        else
                            mirrorBoard = board.getBoard();
                        if(isWhite!=null&&!firstBoard)
                            isTurn = !isTurn;
                        else
                            firstBoard = false;
                        for(int r = 0; r < 8; r++)
                        {
                            for(int c = 0; c < 8; c++)
                            {
                                String curr = mirrorBoard[r][c];
                                switch(curr)
                                {
                                    case "BB": matchBoard[r][c] = blackPieces[0]; break;
                                    case "BK": matchBoard[r][c] = blackPieces[1]; break;
                                    case "BN": matchBoard[r][c] = blackPieces[2]; break;
                                    case "BP": matchBoard[r][c] = blackPieces[3]; break;
                                    case "BR": matchBoard[r][c] = blackPieces[4]; break;
                                    case "BQ": matchBoard[r][c] = blackPieces[5]; break;
                                    case "WB": matchBoard[r][c] = whitePieces[0]; break;
                                    case "WK": matchBoard[r][c] = whitePieces[1]; break;
                                    case "WN": matchBoard[r][c] = whitePieces[2]; break;
                                    case "WP": matchBoard[r][c] = whitePieces[3]; break;
                                    case "WR": matchBoard[r][c] = whitePieces[4]; break;
                                    case "WQ": matchBoard[r][c] = whitePieces[5]; break;
                                    default: matchBoard[r][c] = null;
                                }
                            }
                        }
                    }
                }catch(Exception e){System.out.println(e); break;}
            }
            System.exit(1);
        }
    }
}