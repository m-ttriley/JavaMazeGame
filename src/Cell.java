import java.awt.Color;
import java.util.ArrayList;

import javalib.worldimages.FrameImage;
import javalib.worldimages.LineImage;
import javalib.worldimages.OverlayImages;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

// represents a Cell
class Cell {
    static final int CELL_SIZE = 20;
    int x;
    int y;
    boolean north = false;
    boolean south = false;
    boolean east = false;
    boolean west = false;
    WorldImage cell;
    boolean visited = false;

    Cell(int x, int y) {
        this.x = x * CELL_SIZE;
        this.y = y * CELL_SIZE;
    }

    // checks if two cells are the same
    public boolean sameCell(Cell that) {
        return (this.x == that.x) && (this.y == that.y);
    }

    // draws the cell
    public WorldImage draw() {
        cell = new FrameImage(new Posn(this.x, this.y), CELL_SIZE
                * MazeWorld.MAZE_X, CELL_SIZE * MazeWorld.MAZE_Y, Color.white);
        if (!this.north) {
            cell = new OverlayImages(cell, new LineImage(new Posn(this.x,
                    this.y), new Posn(this.x + CELL_SIZE, this.y), Color.RED));
        }
        if (!this.south) {
            cell = new OverlayImages(cell, new LineImage(new Posn(this.x + 1,
                    this.y + CELL_SIZE + 1), new Posn(this.x + CELL_SIZE + 1,
                            this.y + CELL_SIZE + 1), Color.GREEN));
        }
        if (!this.east) {
            cell = new OverlayImages(cell, new LineImage(new Posn(this.x
                    + CELL_SIZE + 1, this.y + 1), new Posn(this.x + CELL_SIZE
                            + 1, this.y + CELL_SIZE + 1), Color.BLUE));
        }
        if (!this.west) {
            cell = new OverlayImages(cell,
                    new LineImage(new Posn(this.x, this.y), new Posn(this.x,
                            this.y + CELL_SIZE), Color.YELLOW));
        }

        if (this.visited) {
            cell = new OverlayImages(cell, new RectangleImage(new Posn(this.x
                    + (CELL_SIZE / 2), this.y + (CELL_SIZE / 2)),
                    CELL_SIZE / 2, CELL_SIZE / 2, Color.BLUE));
        }

        return cell;

    }

    // generates a hashcode for the cell
    public int hashCode() {
        return 1000 * this.x + this.y;
    }

    // checks if two cells' hashcodes are equal
    public boolean equals(Object that) {
        if (that instanceof Cell) {
            return this.sameCell((Cell) that);
        }

        else {
            return false;
        }
    }

    // gets a list of neighbors for the cell
    public ArrayList<Cell> getNeighbors(ArrayList<ArrayList<Cell>> grid) {
        int xPos = this.x / CELL_SIZE;
        int yPos = this.y / CELL_SIZE;
        ArrayList<Cell> list = new ArrayList<Cell>();

        if (this.north && yPos >= 0) {
            list.add(grid.get(xPos).get(yPos - 1));
        }

        if (this.south && yPos <= MazeWorld.MAZE_Y - 1) {
            list.add(grid.get(xPos).get(yPos + 1));
        }

        if (this.west && xPos >= 0) {
            list.add(grid.get(xPos - 1).get(yPos));
        }

        if (this.east && xPos <= MazeWorld.MAZE_X - 1) {
            list.add(grid.get(xPos + 1).get(yPos));
        }

        return list;
    }

}
