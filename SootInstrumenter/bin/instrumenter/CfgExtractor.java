package instrumenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import soot.Body;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.ExceptionalBlockGraph;

/**
 * uses a ExceptionalBlockGraph to get the targets,
 *  decision nodes and the dependencies between
 * @param b
 */
 public class CfgExtractor{
	 //graph 
	ExceptionalBlockGraph graph;
	Iterator<Block> graphIt;
 
	List<Unit> decisionNodeList;
	List<Block> targetList;
	Map<Block,List<Unit>> targetDependenciesMap;
	Stack<Unit> decisioNodeStack;
	
	

	public List<Unit> getDecisionNodeList(){
		return decisionNodeList;
	}

	public List<Block> getTargetList(){
		return targetList;
	}
	
	public Map<Block,List<Unit>> getTargetDependenciesMap(){
		return targetDependenciesMap;
	}
	
	public  CfgExtractor(Body b){
		//initialize the data structures
		graph = new ExceptionalBlockGraph(b);
		graphIt = graph.iterator();
		decisionNodeList = new ArrayList< Unit>();
		targetList = new ArrayList<Block>();
		targetDependenciesMap = new HashMap<Block,List<Unit>>();
		decisioNodeStack = new Stack<Unit>();
		
		//calculate
		evaluateDependencies();
	}
	/**
	 * get the decision nodes and the targets from the block graph
	 * and evaluates the dependencies between
	 */
	private void evaluateDependencies(){	
		//resolveDependencies
		while (graphIt.hasNext()){
			Block currentBlock = graphIt.next();
			//fetching the decision nodes and the targets
			//all blocks are targets, including the decision nodes
			targetList.add(currentBlock);
			//add the stacked decision nodes to the dependencies of the current target
			if (decisioNodeStack.size()>0){
				List<Unit> targetDependenciesList = new ArrayList<Unit>();
				for (Unit dependencie: decisioNodeStack)
					targetDependenciesList.add(dependencie);
				targetDependenciesMap.put(currentBlock,targetDependenciesList);	
			}
			//pop the decision node at end of scope
			if (isEndOfDecisionNodeScope(currentBlock))
				decisioNodeStack.pop();
			//add the decision node to stack
			if (isDecisionNode(currentBlock)){
				decisionNodeList.add(currentBlock.getTail());
				decisioNodeStack.add(currentBlock.getTail());
			}

	}
	//debug
	BranchDistanceDataGet.debugGettDecisionNodesUnits(decisionNodeList,targetList,targetDependenciesMap);

}
	/**
	 * 
	 * @param b
	 * @return
	 */
	public static boolean isDecisionNode(Block b){
		return (b.getSuccs().size() >1);
	}
	/**
	 * 
	 * @param b
	 * @return
	 */
	private static boolean isEndOfDecisionNodeScope(Block b) {
		return (b.getPreds().size() >1);
	}
}