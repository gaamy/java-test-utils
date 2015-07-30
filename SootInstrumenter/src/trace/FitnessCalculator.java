package trace;

public final class FitnessCalculator {
		
	public static double normalizeBranchDistance(Double branchDistance){
		return branchDistance/(branchDistance+1);
	}
	
	//Update the value of fitness
	public static double getFitness(Target target){
		if (target.isReached()){
			return 0.0;
		}
		else{
			double branchDistance = Double.MAX_VALUE;
			double aproatchLevel = 0;
			boolean blockingNodeFound = false;
			int i= target.getDependencies().size() - 1;
			//iterates from the last to the first target's dependencie searching for a visited node
			while(i>=0 && !blockingNodeFound){
				//if we found a visited node
				if(target.getDependencies().get(i).isReached()){
					//stop iterating
					blockingNodeFound = true;
					//get the node's branch distance
					branchDistance = target.getDependencies().get(i).getBranchDistance();
				}
				else
					//increment the aproach level
					aproatchLevel+=1.0;
				i--;	
			}
			
			return aproatchLevel + normalizeBranchDistance(branchDistance);
			
		}
	}
	
}
