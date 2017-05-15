import java.awt.*;

/**
 * Created by Kai W. Fleischman on 5/14/2017.
 * These are the objects to be interacted with
 */
class Entity {

    private int mass, size, color, sides;
    private Coord center;
    // fER stands for fall energy retainment - energy retained after hitting a wall
    private double fStatic, fKinetic, fER, velocity;
    private Polygon clickArea;

    public Entity(int m, int sd, int sz, int c, double fER, int[] pointsX, int[] pointsY, Coord ce) {
        sides = sd;
        mass = m;
        size = sz;
        color = c;
        this.fER = fER;
        clickArea = new Polygon(pointsX,pointsY,sd);
        velocity = 0;
        fStatic = fKinetic = 0;
        center = ce;
    }

    public Entity(int m, int sd, int sz, int c, double fER, int[] pointsX, int[] pointsY, double fSt, double fK, Coord ce) {
        new Entity(m, sd, sz, c, fER, pointsX, pointsY, ce);
        fStatic = fSt;
        fKinetic = fK;
    }

    public void changeSpeed(double accel, double time) {
        velocity += accel * time;
    }

    public void changeSpeed(double s) {
        velocity = s;
    }

    public int getMass() {
        return mass;
    }

    public int getColor() {
        return color;
    }

    public double getfStatic() {
        return fStatic;
    }

    public double getfKinetic() {
        return fKinetic;
    }

    public double getfER() {
        return fER;
    }

    public Polygon getClickArea() {
        return clickArea;
    }

    public void setClickArea(Polygon a) {
        clickArea = a;
    }

    public int getSize() {
        return size;
    }

    public Coord getCenter() {
        return center;
    }

    public double getVelocity() {
        return velocity;
    }

    public int getSides() {
        return sides;
    }

}

class Coord {
    private int x, y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}