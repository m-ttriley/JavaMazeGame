import tester.*;
import javalib.impworld.*;
import javalib.colors.*;
import javalib.worldimages.*;

class ExamplesMaze {

    MazeWorld mazeTest;
    Edge e1;
    Edge e2;
    Cell c1;
    Cell c2;
    Player p1;
    Player p2;

    void initWorld() {

        // initializing variables for testing
        this.mazeTest = new MazeWorld("BFS");
        this.e1 = mazeTest.edges.get(0);
        this.e2 = mazeTest.edges.get(1);
        this.c1 = mazeTest.mazeGrid.get(0).get(0);
        this.c2 = mazeTest.mazeGrid.get(0).get(1);
        p1 = new Player();
        p1.move("down", mazeTest.mazeGrid);
    }

    boolean testEdge(Tester t) {
        initWorld();

        // making sure edge list is sorted
        return t.checkExpect(this.e1.weight <= this.e2.weight, true);
    }

    boolean testCell(Tester t) {
        initWorld();

        // making sure cells are positioned properly and the first cell allows
        // for movement
        return t.checkExpect(this.c1.x, this.c2.x)
                && t.checkExpect(this.c1.y, (this.c2.y - Cell.CELL_SIZE))
                && t.checkExpect(this.c1.sameCell(this.c2), false)
                && t.checkExpect(this.c1.south || this.c1.east, true);
    }

    boolean testPlayer(Tester t) {
        initWorld();

        // makes sure that moving the player works correctly
        if (this.c1.south) {
            return t.checkExpect(this.p1.yPos, this.c2.y);
        }

        else {
            return t.checkExpect(this.p1.y, this.c1.y);
        }
    }

    boolean testMaze(Tester t) {
        initWorld();

        // making sure mazeGrid is initialized properly
        // also checking that cyclical edges are in fact removed from worklist
        return t.checkExpect(this.mazeTest.mazeGrid.size(), mazeTest.MAZE_X)
                && t.checkExpect(this.mazeTest.mazeGrid.get(0).size(),
                        mazeTest.MAZE_Y)
                && t.checkExpect(
                        this.mazeTest.worklist.size() > this.mazeTest.edges
                                .size(), true);
    }

    boolean testStack(Tester t) {
        initWorld();

        // checking to make sure the stack or queue is initialized properly
        if (this.mazeTest.searchType.equals("DFS")) {
            return t.checkExpect(this.mazeTest.todo.size(), 1);
        }

        else {
            return t.checkExpect(this.mazeTest.toqueue.size(), 1);
        }
    }

    // checking to make sure that the world performs the correct operations
    // after a tick
    boolean testTick(Tester t) {
        initWorld();

        this.mazeTest.onTick();

        return t.checkExpect(this.mazeTest.todo.size() > 1, true)
                && t.checkExpect(this.mazeTest.removed.visited, true)
                && t.checkExpect(this.mazeTest.removed.x, 0)
                && t.checkExpect(this.mazeTest.removed.y, 0);
    }

    // starting the world
    void testWorld(Tester t) {
        initWorld();
        mazeTest.bigBang(mazeTest.MAZE_X * 20, mazeTest.MAZE_Y * 20, 0.05);
    }

}
