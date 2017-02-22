/**
 * @(#)Box.java
 *
 *
 * @author
 * @version 1.00 2015/2/15
 */

import java.awt.*;

public class Box extends java.applet.Applet {

    private boolean oVisible = false;
    private boolean xVisible = false;
    private final Font MYFONT = new Font(Font.MONOSPACED, Font.BOLD, 250);
    private int xCord = 0;
    private int yCord = 0;
    private String position = "";
    private boolean taken = false;
    private String owner = "";
    private String color = "black";
    Image virtualMem, backbuffer;
    //Graphics gBuffer;
    int appletWidth;
    int appletHeight;
    Graphics backg;

    public void init() {
        appletWidth = getWidth();
        appletHeight = getHeight();
        virtualMem = createImage(appletWidth,appletHeight);
        //gBuffer = virtualMem.getGraphics();
        //	backbuffer = createImage(getSize().width, getSize().height);
        //	backg = backbuffer.getGraphics();
    }

    public void paint(Graphics g) {

    }

    public Box(int x, int y, String pos){
        xCord = x;
        yCord = y;
        position = pos;
        owner = pos;
    }
    public void reset(){
        owner = position;
        color = "black";
        taken = false;
        oVisible = false;
        xVisible = false;
    }
    public void setOwnerColor(String olor){
        if( olor.equals("green") ){
            color = olor;
        }
        else if( olor.equals("black") ){
            color = olor;
        }
    }
    public String getOwner(){
        return owner;
    }
    public void xVis(boolean vis){
        xVisible = vis;
        if( vis == true ){
            taken = true;
            owner = "X";
        }
    }
    public void oVis(boolean vis){
        oVisible = vis;
        if( vis == true ){
            taken = true;
            owner = "O";
        }
    }
    public void status(boolean a){
        taken = a;
    }
    public boolean getTaken(){
        return taken;
    }
    public String getPosition(){
        return position;
    }
    public int getX(){
        return xCord;
    }
    public int getY(){
        return yCord;
    }
    public void paintBox(Graphics backg){
        backg.setFont(MYFONT);
        backg.setColor(Color.white);
        backg.fillRect(xCord,yCord,189,189);
        if( oVisible == true ){
            if( color.equals("green") )
                backg.setColor(Color.green);
            else if( color.equals("black") )
                backg.setColor(Color.black);
            backg.drawString("O",xCord+19,yCord+170);
        }
        else if( xVisible == true ){
            if( color.equals("green") )
                backg.setColor(Color.green);
            else if( color.equals("black") )
                backg.setColor(Color.black);
            backg.drawString("X",xCord+19,yCord+170);
        }
    }/** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */

}