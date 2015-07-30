package trace;

import java.util.ArrayList;


public class TraceMethod {

	public static ArrayList<Target> targets = new ArrayList<Target>(); 
	public static ArrayList<DecisionNode> decisionNodes = new ArrayList<DecisionNode>();
	public static boolean built = false;
	
	public static boolean newNode(int id){
		Target newTarget = new Target(id);
		return targets.add(newTarget);
	}
	
	public static boolean newDecisionNode(int id){
		DecisionNode newDecisionNode = new DecisionNode(id);
		return decisionNodes.add(newDecisionNode);	
	}
	
	public static void buildMethodGraph(){
		//
	
		built=true;
	}
	
	public static void reset(){
		for(int i=0;i<8;i++)
			targets.get(i).reset();
		
		for(int i=0;i<7;i++)
			decisionNodes.get(i).reset();
	}
	
	/*
	 * we need a eficient  way to initialise the targets and decisionNodes 
	 *	from the instrumented code
	 * 
	 * 
	 */
	
}
