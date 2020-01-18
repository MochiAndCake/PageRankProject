/*
 * Some basic tests for A04. 
 * Submissions might be tested based on more extensive tests.
 * 
 * 
 * @author Md Atiqur Rahman (mrahm021@uottawa.ca)
 */
 
package csi2510_project;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.io.IOException;


public class TestPageRank{
	
	public static void main(String[] args) throws IOException{
		int topN = 10;				// topN top page-ranked nodes to display
		
		String[] edgeFilenames = {"email-dnc.edges", "small_graph.edges", "small_graph_2.edges"}; // edge file names
		
		PageRank PR = new PageRank();
		
		for(int i = 0; i < edgeFilenames.length; i++){
			Graph graph = CSI2510.readGraph(edgeFilenames[i]);

			Map<Integer, Double> pageRank = PR.computePageRank(graph);
			pageRank = sortPageRank(pageRank);
			
			System.out.println("\n\n\nTesting "+edgeFilenames[i]);
			
			// print topN nodes, PRs, and their adjacency lists in order of decreasing PR values
			int rank = 1;
			System.out.println("\nNumber of nodes in the Graph: " + pageRank.size() + "\n");
			
			for(Integer node : pageRank.keySet()) {
				System.out.println("Rank:" + rank + "\t" + "Node number: " + node + "\t" + "Node PR: " + pageRank.get(node));
				
				System.out.print("\nAdjacent Nodes: ");
				if (graph.getGraphEdges().containsKey(node)) {
					for(Integer edge : graph.getGraphEdges().get(node)) {
						System.out.print(edge + " ");
					}
				}
				System.out.println("\n------------------------------------\n");
				
				if(++rank > topN)
					break;
			}
		}
	
	}

	
	private static Map<Integer, Double> sortPageRank(Map<Integer, Double> pageRank){ 
		// Create a list from entries of HashMap 
		List<Map.Entry<Integer, Double> > list = new ArrayList<Map.Entry<Integer, Double> >(pageRank.entrySet());

		// Sort the list in decreasing order of PRs
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double> >() { 
			public int compare(Map.Entry<Integer, Double> pr1, Map.Entry<Integer, Double> pr2) 
			{ 
				return (pr2.getValue()).compareTo(pr1.getValue()); 
			} 
			}); 
		
		// transfer the entries from sorted list to a LinkedHashMap that preserves the insertion order 
		HashMap<Integer, Double> temp = new LinkedHashMap<Integer, Double>(); 
		for (Map.Entry<Integer, Double> pr : list) { 
			temp.put(pr.getKey(), pr.getValue()); 
		} 
		return temp; 
	} 

	
}
