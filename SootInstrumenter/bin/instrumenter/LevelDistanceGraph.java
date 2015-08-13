package instrumenter;

import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.internal.JIfStmt;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class LevelDistanceGraph {

	public static void main(String args[]){

		//Set classPath
		Scene.v().setSootClassPath(".:/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar:/Users/gamyot/Documents/workspace/SootGraph/src/");
				
		Scene.v().loadClassAndSupport("java.lang.Object");
		Scene.v().loadClassAndSupport("java.lang.System");
		//Set up the class we’re working with
		SootClass c = Scene.v().loadClassAndSupport("MyExemples.SampleSoot");
		c.setApplicationClass();
		
		//Get method
		SootMethod m = c.getMethodByName("test");
		Body b = m.retrieveActiveBody();
	
		printBodyGraphBlocks(b);
		printBodyGraphUnits(b);
		
	}
	//créé un graphe de units de la méhtode et imprime chaque unit suivi de ses predecesseurs et successeurs 
	public static void printBodyGraphUnits(Body b){
		ExceptionalUnitGraph graph = new ExceptionalUnitGraph(b);
		Iterator<Unit> graphIt = graph.iterator();
		System.out.println("______________Unit representation________________");
		while(graphIt.hasNext()){
		
			Unit currentUnit = graphIt.next();
			System.out.println("______________-________________");
			System.out.println("________________________________");
			System.out.println("Block : "+ currentUnit.getClass());
			System.out.println("Block tstring: "+ currentUnit.toString());
			System.out.println("Predecesseurs:___________________________");
			List<Unit> predsUnits= graph.getUnexceptionalPredsOf(currentUnit);
			Iterator predsIt = predsUnits.iterator();
			while(predsIt.hasNext())
				System.out.println("--- "+ predsIt.next().toString()+"; ");
			System.out.println();
			
			System.out.println("Successeurs:___________________________");
			List<Unit> succsUnits= graph.getUnexceptionalSuccsOf(currentUnit);
			Iterator succsIt = succsUnits.iterator();
			while(succsIt.hasNext())
				System.out.println(" "+ succsIt.next().toString()+"; ");
			
			System.out.println();
				
		}
		
	}
	
	//créé un graphe de blocks de la méhtode et imprime chaque bock suivi de ses predecesseurs et successeurs 
	public static void printBodyGraphBlocks(Body b){
		ExceptionalBlockGraph graph = new ExceptionalBlockGraph(b);
		Iterator<Block> graphIt = graph.iterator();
		System.out.println("______________Block representation________________");
		while(graphIt.hasNext()){
		
			Block currentBlock = graphIt.next();
			Unit firstUnit = currentBlock.getHead();
			Unit lastUnit = currentBlock.getTail();
			System.out.println("______________-________________");
			System.out.println("--");


			System.out.println(currentBlock.toString());
			System.out.println("--");
			System.out.println("Premier et dernier units");
			System.out.println("-Head: " +firstUnit.toString()+" ("+ firstUnit.getClass()+")");
			System.out.println("-Tail: " +lastUnit.toString()+" ("+ lastUnit.getClass()+")");
			
			
			System.out.println("Predecesseurs:___________________________");
			List<Block> predsUnits= graph.getUnexceptionalPredsOf(currentBlock);
			Iterator predsIt = predsUnits.iterator();
			while(predsIt.hasNext())
				System.out.println("--- "+ predsIt.next().toString()+"--- ");
			System.out.println();
			
			System.out.println("Successeurs:___________________________");
			List<Block> succsUnits= graph.getUnexceptionalSuccsOf(currentBlock);
			Iterator succsIt = succsUnits.iterator();
			while(succsIt.hasNext())
				System.out.println("--- "+ succsIt.next().toString()+"--- ");
			
			System.out.println();
				
		}
		
	}
	
	/*Evaluates the fitness */
	public float fitness(Body b, Block Target){
		//find taget as a Block
		//evaluates his reachability
			//if reachable
				//fitness=0 : return 0
			//else
				//determine branch distance
				//determine level distance
				//fitness= normalized branch distance + level distance
		
		/*
		 * attention ce n'est pas uen analyse statique
		 * ta besoin d'
		 * */
		
		//TODO: Stub method
		return 0;
	}
	
	public int evaluateLevelDistance(Body b, Unit statement){	
		ExceptionalUnitGraph graph = new ExceptionalUnitGraph(b);
		int count = 0;
		List<Unit> predsUnits= graph.getUnexceptionalPredsOf(statement);
		while(!predsUnits.isEmpty()){
			if(predsUnits.get(0) instanceof JIfStmt)	
				count++;
			predsUnits= graph.getUnexceptionalPredsOf(predsUnits.get(0));
		}	
		return count;
	}
	
	
	
}
