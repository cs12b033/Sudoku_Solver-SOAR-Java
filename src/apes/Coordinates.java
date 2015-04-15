package apes;

public class Coordinates {
    public int x = 0;
    public int y = 0;

    public Coordinates() {
    }

    @Override
    public int hashCode() {
        return x + y;
    }

    @Override
    public boolean equals(Object obj) {
        Coordinates p = (Coordinates)obj;
        if(p == null)
            return false;
        return this.x == p.x && this.y == p.y;
    }

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
