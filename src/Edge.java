// represents an Edge
class Edge {
    Cell first;
    Cell second;
    int weight;

    Edge(Cell first, Cell second, int weight) {
        this.first = first;
        this.second = second;
        this.weight = weight;
    }
}