import java.awt.Color;
import java.util.ArrayList;

import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;

class Player {
    int x;
    int y;
    int xPos;
    int yPos;

    Player() {
        this.x = 0;
        this.y = 0;
        this.xPos = 0;
        this.yPos = 0;
    }

    // moves the player
    public void move(String ke, ArrayList<ArrayList<Cell>> grid) {
        Cell current = grid.get(this.x).get(this.y);

        if (ke.equals("down") && this.y < MazeWorld.MAZE_Y && current.south) {
            this.y = this.y + 1;
        }

        else if (ke.equals("left") && this.x > 0 && current.west) {
            this.x = this.x - 1;
        }

        else if (ke.equals("right") && this.x < MazeWorld.MAZE_X
                && current.east) {
            this.x = this.x + 1;
        }

        else if (ke.equals("up") && this.y > 0 && current.north) {
            this.y = this.y - 1;
        }

        this.xPos = this.x * Cell.CELL_SIZE;
        this.yPos = this.y * Cell.CELL_SIZE;
    }
    
    // draws the player
    public WorldImage draw() {
        return new RectangleImage(new Posn(this.xPos + (Cell.CELL_SIZE / 2),
                this.yPos + (Cell.CELL_SIZE / 2)), Cell.CELL_SIZE / 2,
                Cell.CELL_SIZE / 2, Color.GREEN);
    }

}