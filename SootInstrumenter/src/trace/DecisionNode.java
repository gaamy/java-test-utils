package trace;

import trace.BranchDistanceOperator;

public class DecisionNode {
	int id;
	Double branchDistance;
	boolean reached;
	

	public DecisionNode(int num){
		id = num;
		reset();
	}
	
	public boolean isReached(){
		return reached;
	}
	
	public Double getBranchDistance(){
		return branchDistance;
	}
	/*
	 * this metohd is called when a decision node is encountered on execution
	 * @param result: the result of the evaluation of the decision node
	 * @param op1: operand 1
	 * @param op2: operand 2
	 * @param operator: from the BranchDistanceOperator enum (EQUAL,NOTEQUAL,GREATHER,GREATHEROREQUAL, LESS, LESSOREQUAL)
	 */
	public void visit(double op1, double op2, BranchDistanceOperator operator){
		this.reached = true;
		branchDistance = operator.evaluate(op1,op2);
	}
	
	public void reset(){
		this.reached = false;
		branchDistance  = Double.MAX_VALUE;
	}

}

