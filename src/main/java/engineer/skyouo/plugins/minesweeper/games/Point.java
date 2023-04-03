package engineer.skyouo.plugins.minesweeper.games;

public class Point {
    private int x;
    private int y;
    private boolean visable = false;
    private boolean flagged = false;
    private int value = -1;
    private boolean hasLandmine = false;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasLandmine() {
        return hasLandmine;
    }

    public void setLandmine(boolean hasLandmine) {
        this.hasLandmine = hasLandmine;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isVisable() {
        return visable;
    }

    public void setVisable(boolean visable) {
        this.visable = visable;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isFlagged() {
        return flagged;
    }
}
