package ad1.ss16.pa;

import java.util.*;

public class Network {

    // structure holding the graph
    private LinkedList<Integer>[] graph;

    private int[] _componentGraph;
    private ArrayList<Integer> _countComponentGraph;

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
        // devide by 2 because every connection exists 2 times
        return count / 2;
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
        graph[v].remove((Integer)w);
        graph[w].remove((Integer)v);

    }

    public void deleteAllConnections(int v) {
        while(!graph[v].isEmpty()) {
            Integer node = graph[v].remove();
            graph[node].remove((Integer)v);
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

    private int dfs(boolean[] markedVertexes, int vertex, int componentGraph) {
        markedVertexes[vertex] = true;
        _componentGraph[vertex] = componentGraph;
        int count = 1;
        for (Integer node : this.graph[vertex]) {
            if (!markedVertexes[node]) {
                count += dfs(markedVertexes, node, componentGraph);
            }
        }
        return count;
    }

    /**
     * checks if the given graph has a circle or not
     * @return true if there is a circle, false if not
     */
    public boolean hasCycle() {
        boolean marked[] = new boolean[this.graph.length];
        // set all nodes as not seen so far
        for (int i = 0; i < this.graph.length; i++) {
            marked[i] = false;
        }

        for (int i = 0; i < this.graph.length; i++) {
            if (!marked[i]) {
                if (hasCycle(marked, i, -1) == true) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasCycle(boolean[] markedNodes, int currentNode, int parent) {
        // Mark the current node as visited
        markedNodes[currentNode] = true;

        // Recur for all the vertices adjacent to this vertex
        for (Integer i : graph[currentNode]) {
            // If an adjacent is not visited, then recur for that
            // adjacent
            if (!markedNodes[i]) {
                if (hasCycle(markedNodes, i, currentNode))
                    return true;
            }

            // If an adjacent is visited and not parent of current
            // vertex, then there is a cycle.
            else if (i != parent)
                return true;
        }
        return false;
    }

    public int minimalNumberOfConnections(int start, int end) {
        if (start == end) {
            return 0;
        }
        int marked[] = new int[this.graph.length];
        // set all nodes as not seen so far
        for (int i = 0; i < this.graph.length; i++) {
            marked[i] = -1;
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();

        queue.add(start);
        marked[start] = 0;
        int current;
        while (queue.size() > 0) {
            current = queue.removeFirst();
            for (Integer node : graph[current]) {
                if (marked[node] == -1) {
                    marked[node] = marked[current] + 1;
                    if (node == end) {
                        return marked[end];
                    }
                    queue.add(node);
                }
            }

        }

        return marked[end];
    }

    private void cacheInitialGraph() {
        boolean marked[] = new boolean[this.graph.length];
        // set all nodes as not seen so far
        for (int i = 0; i < this.graph.length; i++) {
            marked[i] = false;
        }
        _componentGraph = new int[graph.length];
        _countComponentGraph = new ArrayList<>();
        int componentCount = 0;
        for (int i = 0; i < this.graph.length; i++) {
            if (!marked[i]) {
                _countComponentGraph.add(dfs(marked, i, componentCount));
                componentCount += 1;
            }
        }
    }

    public List<Integer> criticalNodes() {
        List<Integer> critical = new LinkedList<Integer>();
        this.cacheInitialGraph();

        int marked[] = new int[graph.length];
        for (int i = 0; i < graph.length; i++) {
            marked[i] = -1;
        }

        for (int i = 0; i < graph.length; i++) {
            int componentLength = 0;
            if (graph[i].size() > 0) {
                Stack<Integer> s = new Stack<>();
                s.push(graph[i].getFirst());
                while (!s.isEmpty()) {
                    int v = s.pop();
                    if (v != i && marked[v] != i) {
                        marked[v] = i;
                        componentLength += 1;
                        for (Integer j : graph[v]) {
                            if (j != i) {
                                s.push(j);
                            }
                        }
                    }
                }
                if ( _countComponentGraph.get(_componentGraph[i]) - 1 > componentLength ) {
                    critical.add(i);
                }
            }
        }

        return critical;
    }
}



