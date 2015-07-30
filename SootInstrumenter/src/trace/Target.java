package trace;

import java.util.ArrayList;
import java.util.List;


public class Target {
	int id;
	boolean reached;
	List<DecisionNode> dependencies;
	
	public Target(int num){
		dependencies = new ArrayList<DecisionNode>();
		id = num;
		reached = false;
	}
	
	public List<DecisionNode> getDependencies() {
		return dependencies;
	}
	
	public boolean isReached() {
		return this.reached;
	}
	
	//Turn the target reached	
	public void visit(){
			this.reached = true;
	}
	public void reset(){
		this.reached = false;
	}
	
	public double getFitness(){
		return FitnessCalculator.getFitness(this);
	}
	
}
