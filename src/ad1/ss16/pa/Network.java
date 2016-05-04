package ad1.ss16.pa;

import java.util.*;

public class Network {

    // structure holding the graph
    private LinkedList<Integer>[] graph;

    /**
     * initializes n empty nodes
     * @param n number of nodes
     */
    public Network(int n) {
        this.graph = new LinkedList[n];
        for (int i = 0; i < this.graph.length; i++) {
            // initialize all nodes with null, to show that they are empty
            this.graph[i] = new LinkedList();
        }
    }

    /**
     * @return number of nodes in the graph
     */
    public int numberOfNodes() {
        return this.graph.length;
    }

    /**
     * @return number of edges
     */
    public int numberOfConnections() {
        int count = 0;
        for (int i = 0; i < graph.length; i++) {
            count += graph[i].size();
        }
        return count;
    }

    /**
     * addes a new connection, if it doesnt exists already
     * @param v vertex 1
     * @param w vertex 2
     */
    public void addConnection(int v, int w) {
        // we just want simple graphs
        if (v == w) {
            return;
        }
        // lets check if the current connection already exists, if yes then ignore that call
        if (!graph[v].contains(w)) {
            // add graph connection in both direction, because our graph is not directed
            graph[v].add(w);
            graph[w].add(v);
        }
    }

    public void addAllConnections(int v) {
        for (int i = 0; i < graph.length; i++) {
            addConnection(v, i);
        }
    }

    public void deleteConnection(int v, int w) {
        // remove graph item in both directions
        graph[v].remove(w);
        graph[w].remove(v);
    }

    public void deleteAllConnections(int v) {
        for (Integer node : graph[v]) {
            deleteConnection(v, node);
        }
    }

    /**
     * uses the DFS (Depth First Search) algorithm to find all components in the graph
     * O(vertexes + edges)
     * @return number of components in the graph
     */
    public int numberOfComponents() {
        boolean marked[] = new boolean[this.graph.length];
        // set all nodes as not seen so far
        for (int i = 0; i < this.graph.length; i++) {
            marked[i] = false;
        }

        int componentCount = 0;
        for (int i = 0; i < this.graph.length; i++) {
            if (!marked[i]) {
                dfs(marked, i);
                componentCount += 1;
            }
        }

        return componentCount;
    }

    /**
     * Depth First Search
     * @param markedVertexes help array, which represents the marked vertexes (updated via call by reference)
     * @param vertex the current vertex, from where we start to look around
     */
    private void dfs(boolean[] markedVertexes, int vertex) {
        markedVertexes[vertex] = true;
        for (Integer node : this.graph[vertex]) {
            if (!markedVertexes[node]) {
                dfs(markedVertexes, node);
            }
        }
    }

    public boolean hasCycle() {
        return false;
    }

    public int minimalNumberOfConnections(int start, int end){
        return -1;
    }

    public List<Integer> criticalNodes() {
        List<Integer> critical = new LinkedList<Integer>();
        return critical;
    }
}



