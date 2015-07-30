package instrumenter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;

public class BranchDistanceGraph {

	public static void main(String args[]){
		
		
		//Set classPath
		Scene.v().setSootClassPath(".:/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar:/Users/gamyot/Documents/workspace/Soot_Exemples/src/");
		Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.System");
		//Set up the class we’re working with
		SootClass c = Scene.v().loadClassAndSupport("MyExemples.SampleSoot");
		c.setApplicationClass();
		
		//Get method
		SootMethod m = c.getMethodByName("graphTest");
		Body b = m.retrieveActiveBody();
	
		//
	//	printBodyGraphInfo(b);


	}
	
	public static void printBodyGraphInfo(Body b){
		ExceptionalBlockGraph graph = new ExceptionalBlockGraph(b);
		Iterator<Block> graphIt = graph.iterator();
		
		while(graphIt.hasNext()){
		
			Block currentUnit = graphIt.next();
			System.out.print("Block type: "+ currentUnit.getClass()+" ");
			System.out.print("Block toString: "+ currentUnit.toString()+" ");
			System.out.println("Noeuds predecesseurs:");
			List<Block> predsUnits= graph.getUnexceptionalPredsOf(currentUnit);
			Iterator predsIt = predsUnits.iterator();
			while(predsIt.hasNext())
				System.out.print("Predecesseurs: "+ predsIt.next().toString()+" ");
			
			System.out.println("Noeuds successeurs:");
			List<Block> succsUnits= graph.getUnexceptionalSuccsOf(currentUnit);
			Iterator succsIt = succsUnits.iterator();
			while(succsIt.hasNext())
				System.out.print("Successeur: "+ succsIt.next().toString()+" ");
			
			System.out.println();	
		}
	}


	
}
