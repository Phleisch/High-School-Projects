/**
 * Kai Fleischman
 * @version 1.00 2015/2/13
 */


//Current Problems: Robot doesn't block in hard mode
//Stuff to add: Hard mode, Medium mode, Easy mode, Beginner mode, 2x2 mode

//
//Now, need to work on adding score count, adding 2-Player, 1-Player, and 0-Player check Boxes, and
//maybe adding sound?
//

import java.util.*;
import java.io.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TicTacToe extends Applet implements MouseListener, KeyListener{

    /** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */

    //Formula for placing X, or O: Corner of Box where x += 19 && y += 170;
    //
    char key;
    Image virtualMem, backbuffer;
    Graphics gBuffer, backg;
    int appletWidth;
    int appletHeight;
    int xScore, oScore, firstMove, start1x, start2x, end1x, end2x, start1y, start2y, end1y, end2y, exey, yey;
    private Box[] Boxes = new Box[9];
    Rectangle Box1, Box2, Box3, Box4, Box5, Box6, Box7, Box8, Box9, twoPlay, onePlay, hard, medium, easy, beginner, classic;
    boolean goodMove, xTurn, win, twoPlayer, clicked, onePlayer, hardMode, mediumMode, easyMode, beginnerMode, onGoingGame;
    boolean changeXY, codeSteal, allYEqualX, allXEqualY, classicMode, dragging;
    Font MYFONT;
    String sequence;
    public void init() {
        dragging = false;
        exey = 0;
        yey = 0;
        start1y = 0;
        start2y = 0;
        end1y = 0;
        end2y = 0;
        start1x = 0;
        start2x = 0;
        end1x = 0;
        end2x = 0;
        key = ' ';
        sequence = "";
        classicMode = false;
        codeSteal = false;
        changeXY = false;
        allYEqualX = false;
        allXEqualY = false;
        onGoingGame = true;
        clicked = false;
        twoPlayer = false;
        goodMove = true;
        hardMode = true;
        mediumMode = false;
        easyMode = false;
        beginnerMode = false;
        Boxes[0] = new Box(214,39,"1");
        Boxes[1] = new Box(408,39,"2");
        Boxes[2] = new Box(602,39,"3");
        Boxes[3] = new Box(214,233,"4");
        Boxes[4] = new Box(408,233,"5");
        Boxes[5] = new Box(602,233,"6");
        Boxes[6] = new Box(214,427,"7");
        Boxes[7] = new Box(408,427,"8");
        Boxes[8] = new Box(602,427,"9");
        Box1 = new Rectangle(214,39,189,189);  //Top Left Box
        Box2 = new Rectangle(408,39,189,189);  //Top Center Box
        Box3 = new Rectangle(602,39,189,189);  //Top Right Box
        Box4 = new Rectangle(214,233,189,189); //Middle Left Box
        Box5 = new Rectangle(408,233,189,189); //Middle Center Box
        Box6 = new Rectangle(602,233,189,189); //Middle Right Box
        Box7 = new Rectangle(214,427,189,189); //Bottom Left Box 220-280
        Box8 = new Rectangle(408,427,189,189); //Bottom Center Box
        Box9 = new Rectangle(602,427,189,189); //Bottom Right Box
        twoPlay = new Rectangle(59,75,110,35);
        onePlay = new Rectangle(59,170,110,30);
        hard = new Rectangle(70,205,30,15);
        medium = new Rectangle(70,225,30,15);
        easy = new Rectangle(70,245,30,15);
        beginner = new Rectangle(70,265,30,15);
        classic = new Rectangle(70,115,30,15);
        xTurn = true;
        xScore = 0;
        oScore = 0;
        boolean playAgain = true;
        String[][] board = {{"1","2","3"},{"4","5","6"},{"7","8","9"}};
        String firstMove = "0";
        boolean first = true;
        addMouseListener(this);
        addKeyListener(this);
        appletWidth = getWidth();
        appletHeight = getHeight();
        virtualMem = createImage(appletWidth,appletHeight);
        gBuffer = virtualMem.getGraphics();
        win = false;
        backbuffer = createImage(getSize().width, getSize().height);
        backg = backbuffer.getGraphics();
    }

    public void paint(Graphics g) {
        if( !dragging )
            update(g);
        else
            drawXOrY(backg);
    }
    public void update(Graphics g){
        paintBoard(g);
        g.drawImage(backbuffer, 0, 0, this);
        checkWin(g);
        if( !twoPlayer && !xTurn && onGoingGame ){
            R0B0T();
            xTurn = true;
            repaint();
        }
        g.drawImage(backbuffer, 0, 0, this);
    }

    public void paintBoard(Graphics g){
        Font MYFONT = new Font(Font.MONOSPACED, Font.BOLD, 20);
        backg.setFont(MYFONT);
        Color offWhite = new Color(220,220,220);
        backg.setColor(offWhite);
        backg.fillRect(54,75,110,500);
        backg.fillRect(841,75,110,500);
        backg.setColor(Color.black);
        backg.drawString("Options",66,65);
        backg.drawString("Score",865,65);
        backg.fillRect(209,34,587,587);
        backg.drawString("X: "+xScore,853,110);
        backg.drawString("O: "+oScore,853,150);
        if(twoPlayer == true){
            backg.drawString("1-Player",61,200);
            backg.setColor(Color.green);
            backg.drawString("2-Player",61,110);
            Font rYFONT = new Font(Font.MONOSPACED, Font.BOLD, 15);
            backg.setFont(rYFONT);
            if( classicMode == true ){
                backg.drawString("Classic",71,130);
            }
            hardMode = false;
            mediumMode = false;
            easyMode = false;
            beginnerMode = false;
            backg.setColor(Color.black);
            backg.drawString("Hard",71,220);
            backg.drawString("Medium",71,240);
            backg.drawString("Easy",71,260);
            backg.drawString("Beginner",71,280);
        }
        else if(twoPlayer == false){
            backg.drawString("2-Player",61,110);
            backg.setColor(Color.green);
            backg.drawString("1-Player",61,200);
            Font rYFONT = new Font(Font.MONOSPACED, Font.BOLD, 15);
            backg.setFont(rYFONT);
            backg.setColor(Color.black);
            backg.drawString("Classic",71,130);
            backg.setColor(Color.green);
            if( hardMode == true ){
                backg.drawString("Hard",71,220);
                hardMode = true;
                mediumMode = false;
                easyMode = false;
                beginnerMode = false;
                backg.setColor(Color.black);
                backg.drawString("Medium",71,240);
                backg.drawString("Easy",71,260);
                backg.drawString("Beginner",71,280);
            }
            else if( mediumMode == true ){
                backg.drawString("Medium",71,240);
                hardMode = false;
                mediumMode = true;
                easyMode = false;
                beginnerMode = false;
                backg.setColor(Color.black);
                backg.drawString("Hard",71,220);
                backg.drawString("Easy",71,260);
                backg.drawString("Beginner",71,280);
            }
            else if( easyMode == true ){
                backg.drawString("Easy",71,260);
                hardMode = false;
                mediumMode = false;
                easyMode = true;
                beginnerMode = false;
                backg.setColor(Color.black);
                backg.drawString("Hard",71,220);
                backg.drawString("Medium",71,240);
                backg.drawString("Beginner",71,280);
            }
            else if( beginnerMode == true ){
                backg.drawString("Beginner",71,280);
                hardMode = false;
                mediumMode = false;
                easyMode = false;
                beginnerMode = true;
                backg.setColor(Color.black);
                backg.drawString("Hard",71,220);
                backg.drawString("Medium",71,240);
                backg.drawString("Easy",71,260);
            }
        }
        backg.setColor(Color.black);
        if( win == true ){
            firstMove = 0;
            for(int n = 0; n < 9; n++){
                Boxes[n].reset();
            }
            win = false;
        }
        for(int n = 0; n < 9; n++){
            Boxes[n].paintBox(backg);
        }

    }

    //
    //Check for 3 in a row
    //Status: Successful
    //
    public void checkWin(Graphics g){
        int npoints = 4;
        win = false;
        for( int n = 0; n < 3; n++ ){
            if( Boxes[(3*n)].getOwner().equals(Boxes[(3*n)+1].getOwner()) && Boxes[(3*n)+1].getOwner().equals(Boxes[(3*n)+2].getOwner())){
                int xPoints[] = {Boxes[(3*n)].getX()+30,Boxes[(3*n)].getX()+30,Boxes[(3*n)+2].getX()+158,Boxes[(3*n)+2].getX()+158};
                int yPoints[] = {Boxes[(3*n)].getY()+89,Boxes[(3*n)].getY()+99,Boxes[(3*n)+2].getY()+99,Boxes[(3*n)+2].getY()+89};
                backg.setColor(Color.green);
                backg.fillPolygon(xPoints, yPoints, npoints);
                backg.setColor(Color.black);
                backg.drawPolygon(xPoints, yPoints, npoints);
                win = true;
                if( Boxes[(3*n)].getOwner().equals("X") ){
                    xScore++;
                }
                else if( Boxes[(3*n)].getOwner().equals("O") ){
                    oScore++;
                }
                n = 10;
            }
        }
        if( win == false ){
            for( int n = 0; n < 3; n++ ){
                if( Boxes[(0)+n].getOwner().equals(Boxes[(3)+n].getOwner()) && Boxes[(3)+n].getOwner().equals(Boxes[(6)+n].getOwner())){
                    int xPoints[] = {Boxes[(0)+n].getX()+89,Boxes[(0)+n].getX()+99,Boxes[(6)+n].getX()+99,Boxes[(6)+n].getX()+89};
                    int yPoints[] = {Boxes[(0)+n].getY()+30,Boxes[(0)+n].getY()+30,Boxes[(6)+n].getY()+158,Boxes[(6)+n].getY()+158};
                    backg.setColor(Color.green);
                    backg.fillPolygon(xPoints, yPoints, npoints);
                    backg.setColor(Color.black);
                    backg.drawPolygon(xPoints, yPoints, npoints);
                    win = true;
                    if( Boxes[0+n].getOwner().equals("X") ){
                        xScore++;
                    }
                    else if( Boxes[0+n].getOwner().equals("O") ){
                        oScore++;
                    }
                    n = 10;
                }
            }
            if( win == false ){
                if( Boxes[0].getOwner().equals(Boxes[4].getOwner()) && Boxes[4].getOwner().equals(Boxes[8].getOwner())){
                    int xPoints[] = {Boxes[0].getX()+25,Boxes[0].getX()+35,Boxes[8].getX()+164,Boxes[8].getX()+154};
                    int yPoints[] = {Boxes[0].getY()+35,Boxes[0].getY()+25,Boxes[8].getY()+154,Boxes[8].getY()+164};
                    backg.setColor(Color.green);
                    backg.fillPolygon(xPoints, yPoints, npoints);
                    backg.setColor(Color.black);
                    backg.drawPolygon(xPoints, yPoints, npoints);
                    win = true;
                    if( Boxes[0].getOwner().equals("X") ){
                        xScore++;
                    }
                    else if( Boxes[0].getOwner().equals("O") ){
                        oScore++;
                    }
                }
                else if( Boxes[6].getOwner().equals(Boxes[4].getOwner()) && Boxes[4].getOwner().equals(Boxes[2].getOwner())){
                    int xPoints[] = {Boxes[6].getX()+25,Boxes[6].getX()+35,Boxes[2].getX()+164,Boxes[2].getX()+154};
                    int yPoints[] = {Boxes[6].getY()+154,Boxes[6].getY()+164,Boxes[2].getY()+35,Boxes[2].getY()+25};
                    backg.setColor(Color.green);
                    backg.fillPolygon(xPoints, yPoints, npoints);
                    backg.setColor(Color.black);
                    backg.drawPolygon(xPoints, yPoints, npoints);
                    win = true;
                    if( Boxes[6].getOwner().equals("X") ){
                        xScore++;
                    }
                    else if( Boxes[6].getOwner().equals("O") ){
                        oScore++;
                    }
                }
                if( win == false ){
                    boolean occupied = true;
                    for( int p = 0; p < 9; p++ ){
                        if( Boxes[p].getTaken() == true ){

                        }
                        else{
                            occupied = false;
                        }
                    }
                    if( occupied == true ){
                        win = true;
                    }
                }
            }
        }
        if( win == true ){
            onGoingGame = false;
        }
    }

    public void R0B0T(){
        boolean movePicked = false;
        int enemyCount = 0;
        int R0B0TCOUNT = 0;
        int emptySpace = 0;
        int[] dontPick = new int[9];
        int element = 0;
        boolean there = false;
        //
        //
        if( hardMode == true ){
            if( firstMove == 5 ){
                firstMove = 72;
                Boxes[0].oVis(true);
                movePicked = true;
            }
            else{
                if( Boxes[4].getTaken() == false ){
                    Boxes[4].oVis(true);
                    movePicked = true;
                }
                else{
                    for( int r = 0; r < 3; r++ ){
                        for( int k = 0; k < 3; k++ ){
                            if( Boxes[(3*r)+k].getOwner().equals("O") ){
                                R0B0TCOUNT++;
                            }
                            else if( Boxes[(3*r)+k].getOwner().equals("X") ){
                                enemyCount++;
                            }
                            else{
                                emptySpace = (3*r)+k;
                            }
                        }
                        if( R0B0TCOUNT == enemyCount ){
                            there = false;
                            for( int k = 0; k < 9; k++ ){
                                if(dontPick[k] == emptySpace)
                                    there = true;
                            }
                            if( there == false ){
                                dontPick[element] = emptySpace;
                                element++;
                            }

                            R0B0TCOUNT = 0;
                            enemyCount = 0;
                            emptySpace = 0;
                        }
                        else if( R0B0TCOUNT == 2 && Boxes[emptySpace].getTaken() == false ){
                            Boxes[emptySpace].oVis(true);
                            movePicked = true;
                            r = 20;
                            R0B0TCOUNT = 0;
                        }
                        else{
                            R0B0TCOUNT = 0;
                            enemyCount = 0;
                            emptySpace = 0;
                        }
                    }
                    if(movePicked == false){
                        for( int r = 0; r < 3; r++ ){
                            for( int k = 0; k < 3; k++ ){
                                if( Boxes[(3*k)+r].getOwner().equals("O") ){
                                    R0B0TCOUNT++;
                                }
                                else if( Boxes[(3*k)+r].getOwner().equals("X") ){
                                    enemyCount++;
                                }
                                else{
                                    emptySpace = (3*k)+r;
                                }
                            }
                            if( R0B0TCOUNT == enemyCount ){
                                there = false;
                                for( int k = 0; k < 9; k++ ){
                                    if(dontPick[k] == emptySpace)
                                        there = true;
                                }
                                if( there == false ){
                                    dontPick[element] = emptySpace;
                                    element++;
                                }
                                R0B0TCOUNT = 0;
                                enemyCount = 0;
                                emptySpace = 0;
                            }
                            else if( R0B0TCOUNT == 2 && Boxes[emptySpace].getTaken() == false ){
                                Boxes[emptySpace].oVis(true);
                                movePicked = true;
                                r = 20;
                                R0B0TCOUNT = 0;
                            }
                            else{
                                R0B0TCOUNT = 0;
                                enemyCount = 0;
                                emptySpace = 0;
                            }
                        }
                        if(movePicked == false){
                            if( Boxes[0].getOwner().equals("O") && Boxes[4].getOwner().equals("O") && Boxes[8].getTaken() == false ){
                                Boxes[8].oVis(true);
                                movePicked = true;
                            }
                            else if( Boxes[0].getOwner().equals("O") && Boxes[8].getOwner().equals("O") && Boxes[4].getTaken() == false ){
                                Boxes[4].oVis(true);
                                movePicked = true;
                            }
                            else if( Boxes[4].getOwner().equals("O") && Boxes[8].getOwner().equals("O") && Boxes[0].getTaken() == false ){
                                Boxes[0].oVis(true);
                                movePicked = true;
                            }
                            else if( Boxes[6].getOwner().equals("O") && Boxes[4].getOwner().equals("O") && Boxes[2].getTaken() == false ){
                                Boxes[2].oVis(true);
                                movePicked = true;
                            }
                            else if( Boxes[6].getOwner().equals("O") && Boxes[2].getOwner().equals("O") && Boxes[4].getTaken() == false ){
                                Boxes[4].oVis(true);
                                movePicked = true;
                            }
                            else if( Boxes[2].getOwner().equals("O") && Boxes[4].getOwner().equals("O") && Boxes[6].getTaken() == false ){
                                Boxes[6].oVis(true);
                                movePicked = true;
                            }

                            //Block time
                            if(movePicked == false){
                                for( int r = 0; r < 3; r++ ){
                                    for( int k = 0; k < 3; k++ ){
                                        if( Boxes[(3*r)+k].getOwner().equals("X") ){
                                            enemyCount++;
                                        }
                                        else if( Boxes[(3*r)+k].getOwner().equals("O") ){
                                            R0B0TCOUNT++;
                                        }
                                        else{
                                            emptySpace = (3*r)+k;
                                        }
                                    }
                                    if( R0B0TCOUNT == enemyCount ){
                                        there = false;
                                        for( int k = 0; k < 9; k++ ){
                                            if(dontPick[k] == emptySpace)
                                                there = true;
                                        }
                                        if( there == false ){
                                            dontPick[element] = emptySpace;
                                            element++;
                                        }
                                        R0B0TCOUNT = 0;
                                        enemyCount = 0;
                                        emptySpace = 0;
                                    }
                                    if( enemyCount == 2 && Boxes[emptySpace].getTaken() == false ){
                                        Boxes[emptySpace].oVis(true);
                                        movePicked = true;
                                        r = 20;
                                        enemyCount = 0;
                                    }
                                    else{
                                        R0B0TCOUNT = 0;
                                        enemyCount = 0;
                                        emptySpace = 0;
                                    }
                                }
                                if(movePicked == false){
                                    for( int r = 0; r < 3; r++ ){
                                        for( int k = 0; k < 3; k++ ){
                                            if( Boxes[(3*k)+r].getOwner().equals("X") ){
                                                enemyCount++;
                                            }
                                            else if( Boxes[(3*k)+r].getOwner().equals("O") ){
                                                R0B0TCOUNT++;
                                            }
                                            else{
                                                emptySpace = (3*k)+r;
                                            }
                                        }
                                        if( R0B0TCOUNT == enemyCount ){
                                            there = false;
                                            for( int k = 0; k < 9; k++ ){
                                                if(dontPick[k] == emptySpace)
                                                    there = true;
                                            }
                                            if( there == false ){
                                                dontPick[element] = emptySpace;
                                                element++;
                                            }
                                            R0B0TCOUNT = 0;
                                            enemyCount = 0;
                                            emptySpace = 0;
                                        }
                                        else if( enemyCount == 2 && Boxes[emptySpace].getTaken() == false ){
                                            Boxes[emptySpace].oVis(true);
                                            movePicked = true;
                                            r = 20;
                                            enemyCount = 0;
                                        }
                                        else{
                                            R0B0TCOUNT = 0;
                                            enemyCount = 0;
                                            emptySpace = 0;
                                        }
                                    }
                                    if(movePicked == false){
                                        if( Boxes[0].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[8].getTaken() == false ){
                                            Boxes[8].oVis(true);
                                            movePicked = true;
                                        }
                                        else if( Boxes[0].getOwner().equals("X") && Boxes[8].getOwner().equals("X") && Boxes[4].getTaken() == false ){
                                            Boxes[4].oVis(true);
                                            movePicked = true;
                                        }
                                        else if( Boxes[4].getOwner().equals("X") && Boxes[8].getOwner().equals("X") && Boxes[0].getTaken() == false ){
                                            Boxes[0].oVis(true);
                                            movePicked = true;
                                        }
                                        else if( Boxes[6].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[2].getTaken() == false ){
                                            Boxes[2].oVis(true);
                                            movePicked = true;
                                        }
                                        else if( Boxes[6].getOwner().equals("X") && Boxes[2].getOwner().equals("X") && Boxes[4].getTaken() == false ){
                                            Boxes[4].oVis(true);
                                            movePicked = true;
                                        }
                                        else if( Boxes[2].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[6].getTaken() == false ){
                                            Boxes[6].oVis(true);
                                            movePicked = true;
                                        }
                                        if( movePicked == false ){
                                            int ran = (int)((Math.random() * 9));
                                            while( Boxes[ran].getTaken() == true ){
                                                ran = (int)((Math.random() * 9));
                                            }
                                            Boxes[ran].oVis(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //
        //
        else if( mediumMode == true ){
            for( int r = 0; r < 3; r++ ){
                for( int k = 0; k < 3; k++ ){
                    if( Boxes[(3*r)+k].getOwner().equals("O") ){
                        R0B0TCOUNT++;
                    }
                    else if( Boxes[(3*r)+k].getOwner().equals("X") ){
                        enemyCount++;
                    }
                    else{
                        emptySpace = (3*r)+k;
                    }
                }
                if( R0B0TCOUNT == enemyCount ){
                    there = false;
                    for( int k = 0; k < 9; k++ ){
                        if(dontPick[k] == emptySpace)
                            there = true;
                    }
                    if( there == false ){
                        dontPick[element] = emptySpace;
                        element++;
                    }

                    R0B0TCOUNT = 0;
                    enemyCount = 0;
                    emptySpace = 0;
                }
                else if( R0B0TCOUNT == 2 && Boxes[emptySpace].getTaken() == false ){
                    Boxes[emptySpace].oVis(true);
                    movePicked = true;
                    r = 20;
                    R0B0TCOUNT = 0;
                }
                else{
                    R0B0TCOUNT = 0;
                    enemyCount = 0;
                    emptySpace = 0;
                }
            }
            if(movePicked == false){
                for( int r = 0; r < 3; r++ ){
                    for( int k = 0; k < 3; k++ ){
                        if( Boxes[(3*k)+r].getOwner().equals("O") ){
                            R0B0TCOUNT++;
                        }
                        else if( Boxes[(3*k)+r].getOwner().equals("X") ){
                            enemyCount++;
                        }
                        else{
                            emptySpace = (3*k)+r;
                        }
                    }
                    if( R0B0TCOUNT == enemyCount ){
                        there = false;
                        for( int k = 0; k < 9; k++ ){
                            if(dontPick[k] == emptySpace)
                                there = true;
                        }
                        if( there == false ){
                            dontPick[element] = emptySpace;
                            element++;
                        }
                        R0B0TCOUNT = 0;
                        enemyCount = 0;
                        emptySpace = 0;
                    }
                    else if( R0B0TCOUNT == 2 && Boxes[emptySpace].getTaken() == false ){
                        Boxes[emptySpace].oVis(true);
                        movePicked = true;
                        r = 20;
                        R0B0TCOUNT = 0;
                    }
                    else{
                        R0B0TCOUNT = 0;
                        enemyCount = 0;
                        emptySpace = 0;
                    }
                }
                if(movePicked == false){
                    if( Boxes[0].getOwner().equals("O") && Boxes[4].getOwner().equals("O") && Boxes[8].getTaken() == false ){
                        Boxes[8].oVis(true);
                        movePicked = true;
                    }
                    else if( Boxes[0].getOwner().equals("O") && Boxes[8].getOwner().equals("O") && Boxes[4].getTaken() == false ){
                        Boxes[4].oVis(true);
                        movePicked = true;
                    }
                    else if( Boxes[4].getOwner().equals("O") && Boxes[8].getOwner().equals("O") && Boxes[0].getTaken() == false ){
                        Boxes[0].oVis(true);
                        movePicked = true;
                    }
                    else if( Boxes[6].getOwner().equals("O") && Boxes[4].getOwner().equals("O") && Boxes[2].getTaken() == false ){
                        Boxes[2].oVis(true);
                        movePicked = true;
                    }
                    else if( Boxes[6].getOwner().equals("O") && Boxes[2].getOwner().equals("O") && Boxes[4].getTaken() == false ){
                        Boxes[4].oVis(true);
                        movePicked = true;
                    }
                    else if( Boxes[2].getOwner().equals("O") && Boxes[4].getOwner().equals("O") && Boxes[6].getTaken() == false ){
                        Boxes[6].oVis(true);
                        movePicked = true;
                    }

                    //Block time
                    if(movePicked == false){
                        for( int r = 0; r < 3; r++ ){
                            for( int k = 0; k < 3; k++ ){
                                if( Boxes[(3*r)+k].getOwner().equals("X") ){
                                    enemyCount++;
                                }
                                else if( Boxes[(3*r)+k].getOwner().equals("O") ){
                                    R0B0TCOUNT++;
                                }
                                else{
                                    emptySpace = (3*r)+k;
                                }
                            }
                            if( R0B0TCOUNT == enemyCount ){
                                there = false;
                                for( int k = 0; k < 9; k++ ){
                                    if(dontPick[k] == emptySpace)
                                        there = true;
                                }
                                if( there == false ){
                                    dontPick[element] = emptySpace;
                                    element++;
                                }
                                R0B0TCOUNT = 0;
                                enemyCount = 0;
                                emptySpace = 0;
                            }
                            if( enemyCount == 2 && Boxes[emptySpace].getTaken() == false ){
                                Boxes[emptySpace].oVis(true);
                                movePicked = true;
                                r = 20;
                                enemyCount = 0;
                            }
                            else{
                                R0B0TCOUNT = 0;
                                enemyCount = 0;
                                emptySpace = 0;
                            }
                        }
                        if(movePicked == false){
                            for( int r = 0; r < 3; r++ ){
                                for( int k = 0; k < 3; k++ ){
                                    if( Boxes[(3*k)+r].getOwner().equals("X") ){
                                        enemyCount++;
                                    }
                                    else if( Boxes[(3*k)+r].getOwner().equals("O") ){
                                        R0B0TCOUNT++;
                                    }
                                    else{
                                        emptySpace = (3*k)+r;
                                    }
                                }
                                if( R0B0TCOUNT == enemyCount ){
                                    there = false;
                                    for( int k = 0; k < 9; k++ ){
                                        if(dontPick[k] == emptySpace)
                                            there = true;
                                    }
                                    if( there == false ){
                                        dontPick[element] = emptySpace;
                                        element++;
                                    }
                                    R0B0TCOUNT = 0;
                                    enemyCount = 0;
                                    emptySpace = 0;
                                }
                                else if( enemyCount == 2 && Boxes[emptySpace].getTaken() == false ){
                                    Boxes[emptySpace].oVis(true);
                                    movePicked = true;
                                    r = 20;
                                    enemyCount = 0;
                                }
                                else{
                                    R0B0TCOUNT = 0;
                                    enemyCount = 0;
                                    emptySpace = 0;
                                }
                            }
                            if(movePicked == false){
                                if( Boxes[0].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[8].getTaken() == false ){
                                    Boxes[8].oVis(true);
                                    movePicked = true;
                                }
                                else if( Boxes[0].getOwner().equals("X") && Boxes[8].getOwner().equals("X") && Boxes[4].getTaken() == false ){
                                    Boxes[4].oVis(true);
                                    movePicked = true;
                                }
                                else if( Boxes[4].getOwner().equals("X") && Boxes[8].getOwner().equals("X") && Boxes[0].getTaken() == false ){
                                    Boxes[0].oVis(true);
                                    movePicked = true;
                                }
                                else if( Boxes[6].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[2].getTaken() == false ){
                                    Boxes[2].oVis(true);
                                    movePicked = true;
                                }
                                else if( Boxes[6].getOwner().equals("X") && Boxes[2].getOwner().equals("X") && Boxes[4].getTaken() == false ){
                                    Boxes[4].oVis(true);
                                    movePicked = true;
                                }
                                else if( Boxes[2].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[6].getTaken() == false ){
                                    Boxes[6].oVis(true);
                                    movePicked = true;
                                }
                                if( movePicked == false ){
                                    int ran = (int)((Math.random() * 9));
                                    while( Boxes[ran].getTaken() == true ){
                                        ran = (int)((Math.random() * 9));
                                    }
                                    Boxes[ran].oVis(true);
                                }
                            }
                        }
                    }
                }
            }
        }

        //
        //
        else if( easyMode == true ){
            if(movePicked == false){
                for( int r = 0; r < 3; r++ ){
                    for( int k = 0; k < 3; k++ ){
                        if( Boxes[(3*r)+k].getOwner().equals("X") ){
                            enemyCount++;
                        }
                        else if( Boxes[(3*r)+k].getOwner().equals("O") ){
                            R0B0TCOUNT++;
                        }
                        else{
                            emptySpace = (3*r)+k;
                        }
                    }
                    if( R0B0TCOUNT == enemyCount ){
                        there = false;
                        for( int k = 0; k < 9; k++ ){
                            if(dontPick[k] == emptySpace)
                                there = true;
                        }
                        if( there == false ){
                            dontPick[element] = emptySpace;
                            element++;
                        }
                        R0B0TCOUNT = 0;
                        enemyCount = 0;
                        emptySpace = 0;
                    }
                    if( enemyCount == 2 && Boxes[emptySpace].getTaken() == false ){
                        Boxes[emptySpace].oVis(true);
                        movePicked = true;
                        r = 20;
                        enemyCount = 0;
                    }
                    else{
                        R0B0TCOUNT = 0;
                        enemyCount = 0;
                        emptySpace = 0;
                    }
                }
                if(movePicked == false){
                    for( int r = 0; r < 3; r++ ){
                        for( int k = 0; k < 3; k++ ){
                            if( Boxes[(3*k)+r].getOwner().equals("X") ){
                                enemyCount++;
                            }
                            else if( Boxes[(3*k)+r].getOwner().equals("O") ){
                                R0B0TCOUNT++;
                            }
                            else{
                                emptySpace = (3*k)+r;
                            }
                        }
                        if( R0B0TCOUNT == enemyCount ){
                            there = false;
                            for( int k = 0; k < 9; k++ ){
                                if(dontPick[k] == emptySpace)
                                    there = true;
                            }
                            if( there == false ){
                                dontPick[element] = emptySpace;
                                element++;
                            }
                            R0B0TCOUNT = 0;
                            enemyCount = 0;
                            emptySpace = 0;
                        }
                        else if( enemyCount == 2 && Boxes[emptySpace].getTaken() == false ){
                            Boxes[emptySpace].oVis(true);
                            movePicked = true;
                            r = 20;
                            enemyCount = 0;
                        }
                        else{
                            R0B0TCOUNT = 0;
                            enemyCount = 0;
                            emptySpace = 0;
                        }
                    }
                    if(movePicked == false){
                        if( Boxes[0].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[8].getTaken() == false ){
                            Boxes[8].oVis(true);
                            movePicked = true;
                        }
                        else if( Boxes[0].getOwner().equals("X") && Boxes[8].getOwner().equals("X") && Boxes[4].getTaken() == false ){
                            Boxes[4].oVis(true);
                            movePicked = true;
                        }
                        else if( Boxes[4].getOwner().equals("X") && Boxes[8].getOwner().equals("X") && Boxes[0].getTaken() == false ){
                            Boxes[0].oVis(true);
                            movePicked = true;
                        }
                        else if( Boxes[6].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[2].getTaken() == false ){
                            Boxes[2].oVis(true);
                            movePicked = true;
                        }
                        else if( Boxes[6].getOwner().equals("X") && Boxes[2].getOwner().equals("X") && Boxes[4].getTaken() == false ){
                            Boxes[4].oVis(true);
                            movePicked = true;
                        }
                        else if( Boxes[2].getOwner().equals("X") && Boxes[4].getOwner().equals("X") && Boxes[6].getTaken() == false ){
                            Boxes[6].oVis(true);
                            movePicked = true;
                        }
                        if( movePicked == false ){
                            int ran = (int)((Math.random() * 9));
                            while( Boxes[ran].getTaken() == true ){
                                ran = (int)((Math.random() * 9));
                            }
                            Boxes[ran].oVis(true);
                        }
                    }
                }
            }
        }

        //
        //
        else if( beginnerMode == true ){
            int ran = (int)((Math.random() * 9));
            while( Boxes[ran].getTaken() == true ){
                ran = (int)((Math.random() * 9));
            }
            Boxes[ran].oVis(true);
        }
        delay(1500);
    }

    //
    //Place X in open Box if clicked there
    //Status: Successful
    //
    public void mouseClicked(MouseEvent e)  {
        int pressX = e.getX();
        int pressY = e.getY();

        if(( pressX >= Box1.getMinX() && pressX <= Box1.getMaxX() )
                && ( pressY >= Box1.getMinY() && pressY <= Box1.getMaxY())){
            if(firstMove == 0){
                firstMove = 1;
            }
            if( Boxes[0].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[0].xVis(true);
                        Boxes[0].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[0].oVis(true);
                        Boxes[0].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[0].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[0].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[0].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= Box2.getMinX() && pressX <= Box2.getMaxX() )
                && ( pressY >= Box2.getMinY() && pressY <= Box2.getMaxY())){
            if(firstMove == 0){
                firstMove = 2;
            }
            if( Boxes[1].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[1].xVis(true);
                        Boxes[1].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[1].oVis(true);
                        Boxes[1].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[1].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[1].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[1].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= Box3.getMinX() && pressX <= Box3.getMaxX() )
                && ( pressY >= Box3.getMinY() && pressY <= Box3.getMaxY())){
            if(firstMove == 0){
                firstMove = 3;
            }
            if( Boxes[2].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[2].xVis(true);
                        Boxes[2].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[2].oVis(true);
                        Boxes[2].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[2].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[2].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[2].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= Box4.getMinX() && pressX <= Box4.getMaxX() )
                && ( pressY >= Box4.getMinY() && pressY <= Box4.getMaxY())){
            if(firstMove == 0){
                firstMove = 4;
            }
            if( Boxes[3].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[3].xVis(true);
                        Boxes[3].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[3].oVis(true);
                        Boxes[3].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[3].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[3].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[3].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= Box5.getMinX() && pressX <= Box5.getMaxX() )
                && ( pressY >= Box5.getMinY() && pressY <= Box5.getMaxY())){
            if(firstMove == 0){
                firstMove = 5;
            }
            if( Boxes[4].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[4].xVis(true);
                        Boxes[4].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[4].oVis(true);
                        Boxes[4].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[4].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[4].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[4].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= Box6.getMinX() && pressX <= Box6.getMaxX() )
                && ( pressY >= Box6.getMinY() && pressY <= Box6.getMaxY())){
            if(firstMove == 0){
                firstMove = 6;
            }
            if( Boxes[5].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[5].xVis(true);
                        Boxes[5].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[5].oVis(true);
                        Boxes[5].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[5].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[5].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[5].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= Box7.getMinX() && pressX <= Box7.getMaxX() )
                && ( pressY >= Box7.getMinY() && pressY <= Box7.getMaxY())){
            if(firstMove == 0){
                firstMove = 7;
            }
            if( Boxes[6].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[6].xVis(true);
                        Boxes[6].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[6].oVis(true);
                        Boxes[6].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[6].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[6].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[6].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= Box8.getMinX() && pressX <= Box8.getMaxX() )
                && ( pressY >= Box8.getMinY() && pressY <= Box8.getMaxY())){
            if(firstMove == 0){
                firstMove = 8;
            }
            if( Boxes[7].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[7].xVis(true);
                        Boxes[7].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[7].oVis(true);
                        Boxes[7].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[7].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[7].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[7].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= Box9.getMinX() && pressX <= Box9.getMaxX() )
                && ( pressY >= Box9.getMinY() && pressY <= Box9.getMaxY())){
            if(firstMove == 0){
                firstMove = 9;
            }
            if( Boxes[8].getTaken() == true ){
                if( sequence.equalsIgnoreCase("GetRekt") ){
                    if( xTurn == true ){
                        Boxes[8].xVis(true);
                        Boxes[8].oVis(false);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[8].oVis(true);
                        Boxes[8].xVis(false);

                        xTurn = true;
                    }
                }
                else
                    goodMove = false;
            }
            else{
                if( twoPlayer == true ){
                    if( xTurn == true ){
                        Boxes[8].xVis(true);
                        xTurn = false;
                    }
                    else if( xTurn == false ){
                        Boxes[8].oVis(true);
                        xTurn = true;
                    }
                }
                else{
                    Boxes[8].xVis(true);
                    xTurn = false;
                }
            }
        }
        else if(( pressX >= twoPlay.getMinX() && pressX <= twoPlay.getMaxX() )
                && ( pressY >= twoPlay.getMinY() && pressY <= twoPlay.getMaxY())){
            twoPlayer = true;
            hardMode = false;
            mediumMode = false;
            easyMode = false;
            beginnerMode = false;
            classicMode = true;

        }
        else if(( pressX >= onePlay.getMinX() && pressX <= onePlay.getMaxX() )
                && ( pressY >= onePlay.getMinY() && pressY <= onePlay.getMaxY())){
            twoPlayer = false;
            hardMode = true;
            mediumMode = false;
            easyMode = false;
            beginnerMode = false;
            classicMode = false;
        }
        else if(( pressX >= hard.getMinX() && pressX <= hard.getMaxX() )
                && ( pressY >= hard.getMinY() && pressY <= hard.getMaxY())){
            twoPlayer = false;
            hardMode = true;
            mediumMode = false;
            easyMode = false;
            beginnerMode = false;
            classicMode = false;
        }
        else if(( pressX >= medium.getMinX() && pressX <= medium.getMaxX() )
                && ( pressY >= medium.getMinY() && pressY <= medium.getMaxY())){
            twoPlayer = false;
            hardMode = false;
            mediumMode = true;
            easyMode = false;
            beginnerMode = false;
            classicMode = false;
        }
        else if(( pressX >= easy.getMinX() && pressX <= easy.getMaxX() )
                && ( pressY >= easy.getMinY() && pressY <= easy.getMaxY())){
            twoPlayer = false;
            hardMode = false;
            mediumMode = false;
            easyMode = true;
            beginnerMode = false;
            classicMode = false;
        }
        else if(( pressX >= beginner.getMinX() && pressX <= beginner.getMaxX() )
                && ( pressY >= beginner.getMinY() && pressY <= beginner.getMaxY())){
            twoPlayer = false;
            hardMode = false;
            mediumMode = false;
            easyMode = false;
            beginnerMode = true;
            classicMode = false;
        }
        else if(( pressX >= classic.getMinX() && pressX <= classic.getMaxX() )
                && ( pressY >= classic.getMinY() && pressY <= classic.getMaxY())){
            twoPlayer = true;
            classicMode = true;
            hardMode = false;
            mediumMode = false;
            easyMode = false;
            beginnerMode = false;
        }
        //////
        //////
        //////
        ////// VERY IMPORTANT PART OF THIS PROGRAM
        //////
        //////
        //////
        else if( sequence.equalsIgnoreCase("f") && classicMode == true ){
            if( (((start1x > start2x && end1x < end2x) || (start1x < start2x && end1x > end2x))
                    && (start1y <= start2y+15 && start1y >= start2y-15))
                    && ((end1y <= end2y+15 && end1y >= end2y-15)) ){
                if(( start1x >= Box1.getMinX() && start1x <= Box1.getMaxX() )
                        && ( start1y >= Box1.getMinY() && start1y <= Box1.getMaxY())){
                    if( Boxes[0].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[0].xVis(true);
                    }
                }
                else if(( start1x >= Box2.getMinX() && start1x <= Box2.getMaxX() )
                        && ( start1y >= Box2.getMinY() && start1y <= Box2.getMaxY())){
                    if( Boxes[1].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[1].xVis(true);
                    }
                }
                else if(( start1x >= Box3.getMinX() && start1x <= Box3.getMaxX() )
                        && ( start1y >= Box3.getMinY() && start1y <= Box3.getMaxY())){
                    if( Boxes[2].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[2].xVis(true);
                    }
                }
                else if(( start1x >= Box4.getMinX() && start1x <= Box4.getMaxX() )
                        && ( start1y >= Box4.getMinY() && start1y <= Box4.getMaxY())){
                    if( Boxes[3].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[3].xVis(true);
                    }
                }
                else if(( start1x >= Box5.getMinX() && start1x <= Box5.getMaxX() )
                        && ( start1y >= Box5.getMinY() && start1y <= Box5.getMaxY())){
                    if( Boxes[4].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[4].xVis(true);
                    }
                }
                else if(( start1x >= Box6.getMinX() && start1x <= Box6.getMaxX() )
                        && ( start1y >= Box6.getMinY() && start1y <= Box6.getMaxY())){
                    if( Boxes[5].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[5].xVis(true);
                    }
                }
                else if(( start1x >= Box7.getMinX() && start1x <= Box7.getMaxX() )
                        && ( start1y >= Box7.getMinY() && start1y <= Box7.getMaxY())){
                    if( Boxes[6].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[6].xVis(true);
                    }
                }
                else if(( start1x >= Box8.getMinX() && start1x <= Box8.getMaxX() )
                        && ( start1y >= Box8.getMinY() && start1y <= Box8.getMaxY())){
                    if( Boxes[7].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[7].xVis(true);
                    }
                }
                else if(( start1x >= Box9.getMinX() && start1x <= Box9.getMaxX() )
                        && ( start1y >= Box9.getMinY() && start1y <= Box9.getMaxY())){
                    if( Boxes[8].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[8].xVis(true);
                    }
                }

            }
            else if( (((start1y > start2y && end1y < end2y) || (start1y < start2y && end1y > end2y))
                    && (start1x <= start2x+15 && start1x >= start2x-15))
                    && ((end1x <= end2x+15 && end1x >= end2x-15)) ){
                if(( start1x >= Box1.getMinX() && start1x <= Box1.getMaxX() )
                        && ( start1y >= Box1.getMinY() && start1y <= Box1.getMaxY())){
                    if( Boxes[0].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[0].xVis(true);
                    }
                }
                else if(( start1x >= Box2.getMinX() && start1x <= Box2.getMaxX() )
                        && ( start1y >= Box2.getMinY() && start1y <= Box2.getMaxY())){
                    if( Boxes[1].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[1].xVis(true);
                    }
                }
                else if(( start1x >= Box3.getMinX() && start1x <= Box3.getMaxX() )
                        && ( start1y >= Box3.getMinY() && start1y <= Box3.getMaxY())){
                    if( Boxes[2].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[2].xVis(true);
                    }
                }
                else if(( start1x >= Box4.getMinX() && start1x <= Box4.getMaxX() )
                        && ( start1y >= Box4.getMinY() && start1y <= Box4.getMaxY())){
                    if( Boxes[3].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[3].xVis(true);
                    }
                }
                else if(( start1x >= Box5.getMinX() && start1x <= Box5.getMaxX() )
                        && ( start1y >= Box5.getMinY() && start1y <= Box5.getMaxY())){
                    if( Boxes[4].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[4].xVis(true);
                    }
                }
                else if(( start1x >= Box6.getMinX() && start1x <= Box6.getMaxX() )
                        && ( start1y >= Box6.getMinY() && start1y <= Box6.getMaxY())){
                    if( Boxes[5].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[5].xVis(true);
                    }
                }
                else if(( start1x >= Box7.getMinX() && start1x <= Box7.getMaxX() )
                        && ( start1y >= Box7.getMinY() && start1y <= Box7.getMaxY())){
                    if( Boxes[6].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[6].xVis(true);
                    }
                }
                else if(( start1x >= Box8.getMinX() && start1x <= Box8.getMaxX() )
                        && ( start1y >= Box8.getMinY() && start1y <= Box8.getMaxY())){
                    if( Boxes[7].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[7].xVis(true);
                    }
                }
                else if(( start1x >= Box9.getMinX() && start1x <= Box9.getMaxX() )
                        && ( start1y >= Box9.getMinY() && start1y <= Box9.getMaxY())){
                    if( Boxes[8].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[8].xVis(true);
                    }
                }
            }
            else if( ((start1x <= end1x+15)&&(start1x >= end1x-15)) && ((start1y <= end1y+15)&&(start1y >= end1y-15))){
                if(( start1x >= Box1.getMinX() && start1x <= Box1.getMaxX() )
                        && ( start1y >= Box1.getMinY() && start1y <= Box1.getMaxY())){
                    if( Boxes[0].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[0].oVis(true);
                    }
                }
                else if(( start1x >= Box2.getMinX() && start1x <= Box2.getMaxX() )
                        && ( start1y >= Box2.getMinY() && start1y <= Box2.getMaxY())){
                    if( Boxes[1].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[1].oVis(true);
                    }
                }
                else if(( start1x >= Box3.getMinX() && start1x <= Box3.getMaxX() )
                        && ( start1y >= Box3.getMinY() && start1y <= Box3.getMaxY())){
                    if( Boxes[2].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[2].oVis(true);
                    }
                }
                else if(( start1x >= Box4.getMinX() && start1x <= Box4.getMaxX() )
                        && ( start1y >= Box4.getMinY() && start1y <= Box4.getMaxY())){
                    if( Boxes[3].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[3].oVis(true);
                    }
                }
                else if(( start1x >= Box5.getMinX() && start1x <= Box5.getMaxX() )
                        && ( start1y >= Box5.getMinY() && start1y <= Box5.getMaxY())){
                    if( Boxes[4].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[4].oVis(true);
                    }
                }
                else if(( start1x >= Box6.getMinX() && start1x <= Box6.getMaxX() )
                        && ( start1y >= Box6.getMinY() && start1y <= Box6.getMaxY())){
                    if( Boxes[5].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[5].oVis(true);
                    }
                }
                else if(( start1x >= Box7.getMinX() && start1x <= Box7.getMaxX() )
                        && ( start1y >= Box7.getMinY() && start1y <= Box7.getMaxY())){
                    if( Boxes[6].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[6].oVis(true);
                    }
                }
                else if(( start1x >= Box8.getMinX() && start1x <= Box8.getMaxX() )
                        && ( start1y >= Box8.getMinY() && start1y <= Box8.getMaxY())){
                    if( Boxes[7].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[7].oVis(true);
                    }
                }
                else if(( start1x >= Box9.getMinX() && start1x <= Box9.getMaxX() )
                        && ( start1y >= Box9.getMinY() && start1y <= Box9.getMaxY())){
                    if( Boxes[8].getTaken() == true ){
                        goodMove = false;
                    }
                    else{
                        Boxes[8].oVis(true);
                    }
                }


            }
            else{

            }



            start1x = 0;
            start2x = 0;
            end1x = 0;
            end2x = 0;
            start1y = 0;
            start2y = 0;
            end1y = 0;
            end2y = 0;
        }
        else{
        }

        onGoingGame = true;
        sequence = "";
        repaint();
    }
    public void delay(int n)
    {
        long startDelay = System.currentTimeMillis();
        long endDelay = 0;
        while (endDelay - startDelay < n)
            endDelay = System.currentTimeMillis();
    }
    public void mouseEntered(MouseEvent e)  { }
    public void mouseExited(MouseEvent e) 	{ }
    public void mousePressed(MouseEvent e)  {
        int pressX = e.getX();
        int pressY = e.getY();
        if( start1x == 0 ){
            start1x = pressX;
            start1y = pressY;
        }
        else if( start1x != 0 ){
            start2x = pressX;
            start2y = pressY;
        }

    }
    public void mouseReleased(MouseEvent e) {
        int pressX = e.getX();
        int pressY = e.getY();
        if( end1x == 0 ){
            end1x = pressX;
            end1y = pressY;
        }
        else if( end1x != 0 ){
            end2x = pressX;
            end2y = pressY;
        }
    }
    public void mouseDragged(MouseEvent e) {
        int pressX = e.getX();
        int pressY = e.getY();
        exey = pressX;
        yey = pressY;
        dragging = true;
        repaint();
    }
    public void drawXOrY(Graphics g){
        backg.setColor(Color.black);
        backg.fillOval(exey,yey,5,5);
        dragging = false;
    }
    public void keyReleased(KeyEvent evt)   { }
    public void keyTyped(KeyEvent evt)      {
        if( evt.getKeyCode() == KeyEvent.VK_E ){
            win = true;
            repaint();
        }
        key = evt.getKeyChar();
        sequence = sequence + key;
    }
    public void keyPressed(KeyEvent evt)    { }
}