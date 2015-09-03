import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import tester.*;
import javalib.impworld.*;
import javalib.colors.*;
import javalib.worldimages.*;

class MazeWorld extends World {
    // Defines the x for the maze
    // From (2-100)
    static final int MAZE_X = 10;
    // Defines the y for the maze
    static final int MAZE_Y = 10;

    Random rand = new Random();

    // All the nodes of the maze
    ArrayList<ArrayList<Cell>> mazeGrid;

    // Hashmap to find a cell's representative
    HashMap<Cell, Cell> map;

    // background screen
    WorldImage screen;

    // initial worklist of edges
    ArrayList<Edge> worklist;

    // filtered edges with no duplicated or cycles
    ArrayList<Edge> edges;

    // the player
    Player player = new Player();

    // the type of search that will be executed
    String searchType;

    // the cell that has been taken out of the stack/queue
    Cell removed;

    // the stack for DFS
    Stack<Cell> todo;

    // the queue for BFS
    Queue<Cell> toqueue;

    MazeWorld(String type) {
        this.mazeGrid = new ArrayList<ArrayList<Cell>>();
        this.map = new HashMap<Cell, Cell>();
        this.screen = new FrameImage(new Posn(0, 0), MAZE_X * Cell.CELL_SIZE,
                MAZE_Y * Cell.CELL_SIZE, Color.white);
        this.worklist = new ArrayList<Edge>();
        this.edges = new ArrayList<Edge>();
        this.searchType = type;
        this.todo = new Stack<Cell>();
        this.toqueue = new LinkedList<Cell>();
        init();
        if (type.equals("DFS")) {
            todo.push(mazeGrid.get(0).get(0));
        }

        else {
            toqueue.add(mazeGrid.get(0).get(0));
        }

    }

    public void init() {

        // CREATING GRID OF CELLS
        // CREATING HASHMAP WHERE EACH CELL HAS A REPRESENTATIVE SPANNING TREE
        for (int i = 0; i < MAZE_X; i += 1) {
            ArrayList<Cell> col = new ArrayList<Cell>();
            for (int j = 0; j < MAZE_Y; j += 1) {
                col.add(new Cell(i, j));
                map.put(col.get(j), col.get(j));
            }
            mazeGrid.add(col);
        }

        // CREATING A WORKLIST OF EDGES WITH RANDOM WEIGHTS
        for (int i = 0; i < mazeGrid.size(); i += 1) {
            ArrayList<Cell> temp = mazeGrid.get(i);
            for (int j = 0; j < temp.size(); j += 1) {
                
                if (i == mazeGrid.size() - 1 && j == temp.size() - 1) {
                    // do nothing (bottom right cell)
                }
                // last column of grid
                else if (i == mazeGrid.size() - 1) {
                    worklist.add(new Edge(temp.get(j), temp.get(j + 1), rand
                            .nextInt(MAZE_X)));
                }

                // last row of grid
                else if (j == mazeGrid.size() - 1) {
                    worklist.add(new Edge(temp.get(j), mazeGrid.get(i + 1).get(
                            j), rand.nextInt(MAZE_X)));
                }

                else {
                    worklist.add(new Edge(this.mazeGrid.get(i).get(j),
                            this.mazeGrid.get(i + 1).get(j), rand
                                    .nextInt(MAZE_X)));
                    worklist.add(new Edge(this.mazeGrid.get(i).get(j),
                            this.mazeGrid.get(i).get(j + 1), rand
                                    .nextInt(MAZE_X)));

                }
            }
        }

        // SORTING WORKLIST BY WEIGHT

        Comparator<Edge> comparator = new CompareEdges();

        Collections.sort(worklist, comparator);

        // FILTERING WORKLIST && COMBINING TREES
        for (int i = 0; i < worklist.size(); i += 1) {

            if (!(getRoot(worklist.get(i).first)
                    .equals(getRoot(worklist.get(i).second)))) {

                map.put(getRoot(worklist.get(i).second),
                        getRoot(worklist.get(i).first));

                edges.add(worklist.get(i));
            }

            else {
                // do nothing
            }

        }

        // CONVERTING "EDGES" TO BOOLEANS FOR THE CELLS
        for (int i = 0; i < edges.size(); i += 1) {
            Cell one = edges.get(i).first;
            Cell two = edges.get(i).second;

            if ((two.x == one.x) && (two.y > one.y)) {
                one.south = true;
                two.north = true;

            }

            if ((two.x == one.x) && (two.y < one.y)) {
                one.north = true;
                two.south = true;
            }

            if ((two.y == one.y) && (two.x < one.x)) {
                one.west = true;
                two.east = true;
            }

            if ((two.y == one.y) && (two.x > one.x)) {
                one.east = true;
                two.west = true;
            }

        }

    }

    // on every tick, goes through the path taken to the end of the maze and
    // animates it
    public void onTick() {

        ArrayList<Cell> neighbors = new ArrayList<Cell>();

        // DFS
        if (this.searchType.equals("DFS")) {
            removed = todo.pop();
        }

        // BFS
        else {
            removed = toqueue.remove();
        }

        if (removed.sameCell(this.mazeGrid.get(MAZE_X - 1).get(MAZE_Y - 1))) {
            // create infinite loop because maze is solved
            removed.visited = true;
            todo = new Stack<Cell>();
            todo.push(removed);
            toqueue = new LinkedList<Cell>();
            toqueue.add(removed);
        }

        else if (removed.visited) {
            // do nothing, already processed
        }

        else {
            removed.visited = true;
            neighbors = removed.getNeighbors(mazeGrid);

            // DFS
            if (this.searchType.equals("DFS")) {
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!neighbors.get(i).visited) {
                        todo.push(neighbors.get(i));
                    }
                }
            }

            // BFS
            else {
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!neighbors.get(i).visited) {
                        toqueue.add(neighbors.get(i));
                    }
                }
            }
        }
    }

    // gets the representative of a cell
    public Cell getRoot(Cell c) {

        while (!map.get(c).equals(c)) {
            c = map.get(c);
        }
        return c;

    }

    // draws the picture
    public WorldImage makeImage() {

        screen = new FrameImage(new Posn(0, 0), MAZE_X * Cell.CELL_SIZE, MAZE_Y
                * Cell.CELL_SIZE, Color.white);

        // overlays the player on top of all cells on top of the background
        for (int i = 0; i < MAZE_X; i += 1) {
            for (int j = 0; j < MAZE_Y; j += 1) {
                screen = new OverlayImages(screen, mazeGrid.get(i).get(j)
                        .draw());
            }
        }
        screen = new OverlayImages(screen, player.draw());

        // if the player reaches the end of the maze
        if (player.x == MAZE_X - 1 && player.y == MAZE_Y - 1) {
            screen = new OverlayImages(screen, new TextImage(new Posn(MAZE_X
                    * Cell.CELL_SIZE / 2, MAZE_Y * Cell.CELL_SIZE / 2),
                    "YOU WIN", Color.black));
        }

        return screen;
    }

    public void onKeyEvent(String ke) {

        // new maze
        if (ke.equals("n")) {
            this.mazeGrid = new ArrayList<ArrayList<Cell>>();
            this.map = new HashMap<Cell, Cell>();
            this.screen = new FrameImage(new Posn(0, 0), MAZE_X
                    * Cell.CELL_SIZE, MAZE_Y * Cell.CELL_SIZE, Color.white);
            this.worklist = new ArrayList<Edge>();
            this.edges = new ArrayList<Edge>();
            this.todo = new Stack<Cell>();
            this.toqueue = new LinkedList<Cell>();
            init();
            if (this.searchType.equals("DFS")) {
                todo.push(mazeGrid.get(0).get(0));
            }

            else {
                toqueue.add(mazeGrid.get(0).get(0));
            }
        }

        // BFS the maze
        else if (ke.equals("b")) {
            this.searchType = "BFS";
            this.toqueue = new LinkedList<Cell>();
            this.todo = new Stack<Cell>();
            for (int i = 0; i < mazeGrid.size(); i += 1) {
                for (int j = 0; j < mazeGrid.get(0).size(); j += 1) {
                    mazeGrid.get(i).get(j).visited = false;
                }
            }
            toqueue.add(mazeGrid.get(0).get(0));
        }

        // DFS the maze
        else if (ke.equals("d")) {
            this.searchType = "DFS";
            this.todo = new Stack<Cell>();
            for (int i = 0; i < mazeGrid.size(); i += 1) {
                for (int j = 0; j < mazeGrid.get(0).size(); j += 1) {
                    mazeGrid.get(i).get(j).visited = false;
                }
            }
            todo.push(mazeGrid.get(0).get(0));
        }

        // move the player
        else {
            player.move(ke, this.mazeGrid);
        }
    }
}

// Comparator for edge Objects
class CompareEdges implements Comparator<Edge> {

    public int compare(Edge object1, Edge object2) {
        if (object1.weight < object2.weight) {
            return -1;
        }

        else if (object1.weight == object2.weight) {
            return 0;
        }

        else {
            return 1;
        }
    }
}
