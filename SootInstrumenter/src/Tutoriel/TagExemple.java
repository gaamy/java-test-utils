package Tutoriel;
import soot.*;
import soot.JastAddJ.ArrayAccess;
import soot.jimple.*;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.AbstractJimpleBinopExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.typing.fast.Integer1Type;
import soot.options.Options;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.*;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;

	import java.io.*;
import java.util.*;

import Tutoriel.LoadAndGenerate;

public class TagExemple {

		protected static List<SootMethod> methodList = new ArrayList<SootMethod>();
		
		public static void main(String args[]){
			//Set classPath
			String exemplePath = "/Users/gamyot/Documents/workspace/Soot_Exemples/src/";
			String objectPath= "/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar";
			
			Scene.v().setSootClassPath(".:"+objectPath+":"+exemplePath);
			Scene.v().loadClassAndSupport("java.lang.Object");
			Scene.v().loadClassAndSupport("java.lang.System");
			
			//Set up the class we’re working with
			SootClass c = Scene.v().loadClassAndSupport("MyExemples.ExempleConstants");
			c.setApplicationClass();
			
			System.out.println("fields : "+c.getFieldCount());
			
			Iterator fieldIt = c.getFields().iterator();
			while(fieldIt.hasNext()){
				SootField f = (SootField)fieldIt.next();
				String fieldName = f.getName();
				Type fieldType = f.getType();
				
				//tag investigation and debug
				List<Tag>  fTag = f.getTags();
				System.out.println("fields : "+f.getType());
				System.out.println("Field name: "+fieldName);
				
				
				System.out.println("SIZA TAGGAAA: " +fTag.size());
				
				for(Tag t : fTag){
					System.out.println("Name: "+t.getName().toString());
					System.out.println("Value: "+t.getValue().toString());
					System.out.println("Class: "+t.getClass().toString());
				}
				if( f.hasTag("ConstantValueTag")){
				   // double val = ((DoubleConstantValueTag)f.getTag("DoubleConstantValueTag")).getDoubleValue();
				    System.out.println("STIRNG TAAAAAAAG______________________ ");
				}
				//
				
			}
		}

	}


		

