package ad1.ss16.pa;

import java.util.*;

@SuppressWarnings( "unchecked" )
public class Network {

    // structure holding the graph
    private LinkedList<Integer>[] graph;

    private int NUMBER_OF_NODES;
    private boolean markedVertexes[];
    private int time = 0;

    private boolean visited[];
    private int disc[];
    private int low[];
    private int parent[];
    private boolean ap[]; // To store articulation points

    /**
     * initializes n empty nodes
     * @param n number of nodes
     */
    public Network(int n) {
        this.NUMBER_OF_NODES = n;
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

    /**
     * adds connection from one vertex from all other vertexes
     * @param v vertext which is getting connected to all others
     */
    public void addAllConnections(int v) {
        for (int i = 0; i < graph.length; i++) {
            addConnection(v, i);
        }
    }

    /**
     * deletes a certain connection in the graph
     * @param v
     * @param w
     */
    public void deleteConnection(int v, int w) {
        // remove graph item in both directions
        graph[v].remove((Integer)w);
        graph[w].remove((Integer)v);

    }

    /**
     * removes all connections from a certain vertex
     * @param v
     */
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
        markedVertexes = new boolean[this.graph.length];
        // set all nodes as not seen so far
        for (int i = 0; i < this.graph.length; i++) {
            markedVertexes[i] = false;
        }

        int componentCount = 0;
        for (int i = 0; i < this.graph.length; i++) {
            if (!markedVertexes[i]) {
                dfs(i); // using dfs algorithm to determine a component
                componentCount += 1;
            }
        }

        return componentCount;
    }

    /**
     * Depth First Search
     * @param vertex the current vertex, from where we start to look around
     */
    private void dfs(int vertex) {
        Stack<Integer> s = new Stack<Integer>();
        s.push(vertex);
        while (!s.isEmpty()) {
            int i = s.pop();
            if (markedVertexes[i] == false) {
                markedVertexes[i] = true;
                for (int j : graph[i])
                    s.push(j);
            }
        }
    }

    /**
     * checks if the given graph has a circle or not
     * @return true if there is a circle, false if not
     */
    public boolean hasCycle() {
        markedVertexes = new boolean[this.graph.length];
        // set all nodes as not seen so far
        for (int i = 0; i < this.graph.length; i++) {
            markedVertexes[i] = false;
        }

        for (int i = 0; i < this.graph.length; i++) {
            if (!markedVertexes[i]) {
                if (hasCycle(i, -1) == true) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasCycle(int currentNode, int parent) {
        // Mark the current node as visited
        markedVertexes[currentNode] = true;

        // Recur for all the vertices adjacent to this vertex
        for (Integer i : graph[currentNode]) {
            // If an adjacent is not visited, then recur for that
            // adjacent
            if (!markedVertexes[i]) {
                if (hasCycle(i, currentNode))
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

    private void APUtil(int u)
    {

        // Count of children in DFS Tree
        int children = 0;

        // Mark the current node as visited
        visited[u] = true;

        // Initialize discovery time and low value
        disc[u] = low[u] = ++time;

        // Go through all vertices adjacent to this
        Iterator<Integer> i = graph[u].iterator();
        while (i.hasNext())
        {
            int v = i.next();  // v is current adjacent of u

            // If v is not visited yet, then make it a child of u
            // in DFS tree and recur for it
            if (!visited[v])
            {
                children++;
                parent[v] = u;
                APUtil(v);

                // Check if the subtree rooted with v has a connection to
                // one of the ancestors of u
                low[u]  = Math.min(low[u], low[v]);

                // u is an articulation point in following cases

                // (1) u is root of DFS tree and has two or more chilren.
                if (parent[u] == -1 && children > 1)
                    ap[u] = true;

                // (2) If u is not root and low value of one of its child
                // is more than discovery value of u.
                if (parent[u] != -1 && low[v] >= disc[u])
                    ap[u] = true;
            }

            // Update low value of u for parent function calls.
            else if (v != parent[u])
                low[u]  = Math.min(low[u], disc[v]);
        }
    }

    /**
     * searches for critical points.
     * @return a list of points, which are critical
     */
    public List<Integer> criticalNodes() {
        List<Integer> critical = new LinkedList<Integer>();
        // Mark all the vertexes as not visited
        visited = new boolean[NUMBER_OF_NODES];
        disc = new int[NUMBER_OF_NODES];
        low = new int[NUMBER_OF_NODES];
        parent = new int[NUMBER_OF_NODES];
        ap = new boolean[NUMBER_OF_NODES]; // To store articulation points

        // Initialize parent and visited, and ap(articulation point)
        for (int i = 0; i < NUMBER_OF_NODES; i++)
        {
            parent[i] = -1;
            visited[i] = false;
            ap[i] = false;
        }

        // Call the recursive helper function to find articulation
        // points in DFS tree rooted with vertex 'i'
        for (int i = 0; i < NUMBER_OF_NODES; i++)
            if (visited[i] == false)
                APUtil(i);

        // Now ap[] contains articulation points, print them
        for (int i = 0; i < NUMBER_OF_NODES; i++)
            if (ap[i] == true)
                critical.add(i);

        return critical;
    }
}



