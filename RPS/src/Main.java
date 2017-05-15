/**
 * Created by Kai W. Fleischman on 5/14/2017.
 * This is the Main class of the Realistic Physics Simulator Project
 */

import java.util.*;
import java.io.*;
//import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {

    public static void main(String args[]) throws IOException {

        Container object = new Container();
        object.addWindowListener(new WindowAdapter()
        {public void windowClosing(WindowEvent e)
        {System.exit(0);}});
        object.setSize(1000,650);
        object.setVisible(true);

    }

}

/*
Methods: to check for intersection with other objects, check against borders of map,
draw shapes, move shapes, change speeds
*/


class Container extends Frame implements MouseListener, MouseMotionListener, KeyListener {

    final private int APPX = 1000;
    final private int APPY = 650;
    final private int MENUHEIGHT = 30;
    final private double GRAVITYEARTH = 9.81;
    final private Font BIG = new Font(Font.MONOSPACED, Font.BOLD, 36);
    final private Font SMALL = new Font(Font.MONOSPACED, Font.BOLD, 12);
    final private Color TGRAY = new Color(50,50,50,150);
    final private Color LIGHTGRAY = new Color(217, 217, 217);
    final private Color BLUE = new Color(51, 204, 255);
    final private Color GREEN = new Color(0, 255, 0);
    final private Color ORANGE = new Color(255, 153, 0);
    final private Color PURPLE = new Color(153, 51, 255);
    final private Color RED = new Color(255, 0, 0);
    final private Color YELLOW = new Color(255, 255, 0);
    final private Rectangle APPLET = new Rectangle(0,MENUHEIGHT,APPX,APPY-MENUHEIGHT);

    //Menu Variables
    private double gravity;
    private boolean friction;

    //Objects to be manipulated by the user
    private ArrayList<Entity> entities;
    private Entity grabbed;

    private boolean firstRun;
    private Long startT, elapsedT, dropT;
    private Image backbuffer;
    private Graphics backg;
    private FontMetrics metrics;

    private Entity test;


    public void init() {

        grabbed = null;
        entities = new ArrayList<Entity>();
        test = generateEntity(100,37,150,4,0.5,500,400);
        entities.add(test);
        entities.add(generateEntity(100,4,20,2,0.5,20,400));
        //entities.add(generateEntity(100,3,10,0,1.0,200,20));
        firstRun = true;
        dropT = 1000L;
        startT = elapsedT = 0L;
        gravity = 10;
        friction = false;

        //Listens to events from mouse and keyboard
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

    }

    Container() throws IOException {
        init();
    }

    public void paint(Graphics g) {

        if(firstRun) {
            firstRun = false;
            backbuffer = createImage(getWidth(), getHeight());
            backg = backbuffer.getGraphics();
        }
        update(g);

    }

    public void update(Graphics g) {
        if(startT != 0)
            elapsedT = System.nanoTime() - startT;
        accelerateEntities(750000L);
        startT = System.nanoTime();
        moveEntities(750000L);
        checkBounds();
        backg.setColor(Color.WHITE);
        backg.fillRect(0,0,getWidth(),getHeight());
        drawEntities(backg);
        backg.drawString(""+(grabbed==null),300,300);
        g.drawImage(backbuffer, 0, 0, this);
        repaint();

    }

    private Entity generateEntity(int mass, int sides, int size, int color, double energy, int x, int y) {
        double angleStart = (360.0 / sides) / 2.0;
        double angleChange = 360.0 / sides;
        double currAngle = angleStart;
        int[] xPoints = new int[sides];
        int[] yPoints = new int[sides];
        for(int i = 0; i < sides; i++) {
            currAngle = angleStart + angleChange * i;
            xPoints[i] = (int) (x + size * Math.cos(Math.toRadians(currAngle)));
            yPoints[i] = (int) (y + size * Math.sin(Math.toRadians(currAngle)));
        }
        return new Entity(mass,sides,size,color,energy,xPoints,yPoints,new Coord(x,y));
    }

    private Polygon generatePolygon(int sides, int size, int x, int y) {
        double angleStart = (360.0 / sides) / 2.0;
        double angleChange = 360.0 / sides;
        double currAngle = angleStart;
        int[] xPoints = new int[sides];
        int[] yPoints = new int[sides];
        for(int i = 0; i < sides; i++) {
            currAngle = angleStart + angleChange * i;
            xPoints[i] = (int) (x + size * Math.cos(Math.toRadians(currAngle)));
            yPoints[i] = (int) (y + size * Math.sin(Math.toRadians(currAngle)));
        }
        return new Polygon(xPoints,yPoints,sides);
    }

    private void accelerateEntities(Long time) {
        double seconds = (time) / 1000000000.0;
        for(Entity a : entities) {
            if(grabbed == a)
                continue;
            a.changeSpeed(gravity,seconds);
        }
    }

    public void moveEntities(Long time) {
        for(Entity a : entities) {
            if(grabbed == a)
                continue;
            Coord b = a.getCenter();
            b.setCoord(b.getX(),(int)(b.getY()+a.getVelocity()));
            a.setClickArea(generatePolygon(a.getSides(),a.getSize(),b.getX(),b.getY()));
        }
    }

    public void checkBounds() {
        for(Entity a : entities) {
            int x = a.getCenter().getX();
            Polygon p = a.getClickArea();
            if(p.contains(x,650))
                a.changeSpeed(a.getVelocity()*-1*a.getfER());
        }
    }

    private void drawEntities(Graphics g) {
        for(Entity a : entities) {
            switch(a.getColor()) {
                case 0: g.setColor(BLUE); break;
                case 1: g.setColor(YELLOW); break;
                case 2: g.setColor(RED); break;
                case 3: g.setColor(GREEN); break;
                case 4: g.setColor(ORANGE); break;
                case 5: g.setColor(PURPLE); break;
            }
            g.fillPolygon(a.getClickArea());
            g.setColor(Color.BLACK);
            g.drawPolygon(a.getClickArea());
            g.drawString(""+a.getVelocity(),200,200);
        }
    }


    public void mouseClicked(MouseEvent e)  { }
    public void mouseMoved(MouseEvent e)    { }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseDragged(MouseEvent e)  {
        int pressX = e.getX();
        int pressY = e.getY();
        if(grabbed!=null) {
            Coord b = grabbed.getCenter();
            b.setCoord(pressX,pressY);
            grabbed.setClickArea(generatePolygon(grabbed.getSides(),grabbed.getSize(),b.getX(),b.getY()));
        }
        repaint();
    }
    public void mouseExited(MouseEvent e) 	{ }
    public void mousePressed(MouseEvent e)	{
        int pressX = e.getX();
        int pressY = e.getY();
        for(Entity a : entities) {
            if(a.getClickArea().contains(pressX,pressY)) {
                grabbed = a;
                a.grab();
            }
        }
        repaint();
    }
    public void mouseReleased(MouseEvent e)	{
        if(grabbed!=null) {
            grabbed.release();
            grabbed = null;
        }
        repaint();
    }
    public void keyReleased(KeyEvent evt)   { }
    public void keyTyped(KeyEvent evt)      { }
    public void keyPressed(KeyEvent evt)    { }

}




