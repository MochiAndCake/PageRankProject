// Ann Soong
// Date edited: 2019-12-03

/*
 * Computes the PageRank (PR) of each node A in a directed graph using a recursive definition:
 * PR(A) = (1-d) + d (PR(T1)/C(T1) + ... + PR(Tn)/C(Tn))
 * Here d is a damping factor that we will set to 0.85. Nodes T1, T2, ..., Tn are the nodes that
 * connect to A (i.e. having a link going from Ti to A). The term C(Ti) is the number of links outgoing from Ti.
 *
 * @author Md Atiqur Rahman (mrahm021@uottawa.ca)
 */

package csi2510_project;

import java.util.Map;
import java.util.HashMap;

public class PageRank{
	public static final double DAMPING_FACTOR = 0.85;	// damping factor
	private double tolerance;							// tolerance to stop
	private long maxIter;								// max iterations to stop

	PageRank(){
		// default tolerance=0.000001, default maxIter=100
		this(0.000001, 100);
	}

	PageRank(double tolerance, long maxIter){
		this.tolerance = tolerance;
		this.maxIter = maxIter;
	}

	/**
	 * Computes the PageRank (PR) of each node in a graph in an iterative way.
	 * Iteration stops as soon as this.maxIter or this.tolerance whichever is reached first.
	 *
	 * @param graph the Graph to compute PR for
     * @return returns a Map<Integer, Double> mapping each node to its PR
     *
     */

	public Map<Integer, Double> computePageRank(Graph graph){
		// If the graph does not have nodes nor edges, then there is no point in calculating page-rank.
		// So we return null.
		if(graph == null || graph.nodes == null || graph.edges == null){
			return null;
		}

		// A hash map that holds a node with its new corresponding page-rank.
		HashMap<Integer, Double> pagerank = new HashMap<Integer, Double>(graph.nodes.size());

		// A hash map that holds a node with its previous page-rank.
		HashMap<Integer, Double> tmp = new HashMap<Integer, Double>(graph.nodes.size());

		// A hash map that holds a node with its number of edges directed at other nodes.
		HashMap<Integer, Integer> outlinks = numEdges(graph);

		// A hash map that holds a node with an array of nodes that have directed edges at the specified node.
		HashMap<Integer, Integer[] > inlinks = directedNodes(graph);

		// Initializing the page-rank of all nodes as 1 to start off.
		for(int i = 0; i < graph.nodes.size(); i++){
			Integer node = graph.nodes.get(i); // Loop through the nodes and place them into the hash maps with their ranks.
			tmp.put(node, 1d);
			pagerank.put(node, 1d);
		}

		int m = 0; // m is a counter used to keep the iteration under and up to the max iterations.
		double threshold = 1d; // threshold is used to compare with the tolerance to know when to stop the program.

		// node will be the node we are calculating page-rank for.
		// subnode will be a node related to the specified node.
		Integer node, subnode;

		while(threshold > tolerance && m < maxIter){
		// Iterate until the current tolerance (threshold) is below the default tolerance, or up to maxIter times.

			// Iterate through the nodes, calculate page-rank for each node.
			// Equation used: PR(i) = (1-d) + d*PR(T1)/C(T1) + ... + d*PR(Tn)/C(Tn)
			for(int i = 0; i < graph.nodes.size(); i++){
				node = graph.nodes.get(i);

				double sum = 1-DAMPING_FACTOR;

				int j = 0; // j will be a counter used to iterate through the array of inlink nodes.
				// We will iterate through the inlink nodes in order to calculate the summation of the page-rank.
				while(j < (inlinks.get(node)).length && inlinks.get(node)[j] != null){
					subnode = inlinks.get(node)[j];
					if(outlinks.get(subnode) != 0){ // If a subnode has no directed edges outward, we ignore it to avoid infinity.
						sum += DAMPING_FACTOR*(tmp.get(subnode) / outlinks.get(subnode));
					}
					j++;
				}

				pagerank.replace(node, sum); // We update the hash map with the new page-rank.
			}
			// The threshold is calculated using the tmp as the previous page-rank and pagerank as the current one.
			threshold = currentTolerance(graph, tmp, pagerank);

			// We iterate through the nodes once again to update tmp with the new page-ranks.
			for(int i = 0; i < graph.nodes.size(); i++){
				tmp.replace(graph.nodes.get(i), pagerank.get(graph.nodes.get(i)) );
			}
			m++;
		}
		return pagerank;
	}

	/*
	 * Goes through the graph to build a hash map containing node and its number of "outlinks".
	 * "Outlinks" refer to the number of directed edges towards other nodes the specified node has.
	 *
	 * @param g the graph with a list of nodes and a hash map representing edges.
	 * @return a hash map containing nodes and the number of outlinks.
	 */
	private HashMap<Integer, Integer> numEdges(Graph g){
		HashMap<Integer, Integer> hash = new HashMap<Integer, Integer>(g.nodes.size());

		// Iterate through the list of nodes.
		for(int i = 0; i < g.nodes.size(); i++){
			Integer node = g.nodes.get(i);

			// If the node has no outlinks, set the number to 0.
			if(g.edges.get(node) == null){
				hash.put(node, 0);
			}
			else{ // Otherwise the node is paired with the amount of outlinks it has.
				hash.put(node, g.edges.get(node).size());
			}
		}
		return hash;
	}

	/*
	 * Goes through the graph to build a hash map containing node and an array of its "inlinks".
	 * "Inlinks" refer to the directed edges other nodes have pointing towards the specified node.
	 *
	 * @param g the graph with a list of nodes and a hash map representing edges.
	 * @return a hash map containing nodes and an array holding inlinks.
	 */
	private HashMap<Integer, Integer[]> directedNodes(Graph g){
		HashMap<Integer, Integer[]> hash = new HashMap<Integer, Integer[]>(g.nodes.size());
		Integer node, subnode;
		Integer[] a;
		int k;

		for(int i = 0; i < g.nodes.size(); i++){
			node = g.nodes.get(i);

			// We create an array to hold the nodes.
			a = new Integer[g.nodes.size()/4];
			k = 0; // k is a counter that will keep track of where to place the next inlink.

			// Iterate through the nodes so we can extract inlinks from every other node.
			for(int j = 0; j < g.nodes.size(); j++){
				if(i != j){
					subnode = g.nodes.get(j);
					if( (g.edges.get(subnode)).contains(node) ){
						if(k >= a.length){ // If the array runs out of space, we expand the size.
							a = extendArray(a);
						}
						a[k] = subnode;
						k++;
					}
				}
			}
			//a = reLength(a);
			hash.put(node, a);
		}
		return hash;
	}

	/*
	 * Given an array that is running out of space, this method creates an array of twice the length,
	 * copies the contents of the smaller array into the new one, and then returns the new array.
	 *
	 * @param a the array that is running out of space.
	 * @return an array with double the length of the array given
	 */
	private Integer[] extendArray(Integer[] a){
		Integer[] b = new Integer[a.length*2];
		for(int i = 0; i < a.length; i++){
			b[i] = a[i];
		}
		return b;
	}

	/*
	 * Calculates the current tolerance of the page-rank and returns the value as a double.
	 *
	 * @param g the graph with a list of nodes and a hash map representing edges.
	 * @param previous a hash map containing the previous page-ranks.
	 * @param current a has map containing the current page-ranks.
	 * @return a double representing the current tolerance.
	 */
	private double currentTolerance(Graph g, HashMap<Integer, Double> previous, HashMap<Integer, Double> current){
		double sum = 0;
		int n = g.nodes.size();
		for(int i = 0; i < n; i++){
			sum += Math.abs( previous.get(g.nodes.get(i)) - current.get(g.nodes.get(i)) );
		}
		return sum/n;
	}
}
