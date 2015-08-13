/* (c) Copyright 2009 and following years, Aminata SABANE,
 * Ecole Polytechnique de Montr̩al.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package javafile.analyzer.visitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jdt.core.dom.ImportDeclaration;

import parser.input.SourceInputsHolder;
import parser.input.impl.FileSystemJavaProject;
import parser.wrapper.JavaParser;
import utils.io.CsvManager;

public class JavaFileAnalyzerVistorCaller {

	
	private List<String> classStats = new ArrayList<String>();


	
	
	public static void main(String args[]) {

		// source of the java files in the well project structured style
		// String aSourcePath = args[0];
		
		//CSV Choices
		final boolean printStatsCsv = true;
		final boolean printDependenciesCsv = true;
		final boolean printImportsCsv = true;
		final boolean printMadumTransformersCsv = true;
		final boolean printMadumInterestingClassesCsv = true;
		
		
		String systemName = "twitter4j-core";
		
		/*
		newSystem
		
		elasticsearch-1.7.1 
		facebook-android-sdk-sdk-version-3.21.1
		fresco-0.6.0
		hibernate-orm-4.2.20 
		jacoco-0.6.4
		jclouds-jclouds-1.9.0
		jmetal4.5
		openmrs-core-1.11.3
		spring-security-4.0.2
		storm-0.9.3
		
		
		old
		
		async-http-client-2.0.0-alpha8
		checkstyle-release5_6
		de.tudarmstadt.ukp.wikipedia.api-0.9.1
		de.tudarmstadt.ukp.wikipedia.revisionmachine-0.8.0
		gdata-client-1.0
		gdata-core-1.0
		guava-18.0
		hibernate-search-4.5.0
		javaml-0.1.7
		jgraph-5.13.0.0
		jgrapht-core
		scribe-java-1.3.5
		twitter4j-core
		*/
		
		String aSourcePath="/Users/gamyot/Google Drive/Été 2015/Stage 2015/resources/system Search/oldSystems/"+systemName+"/src";
		//String aSourcePath="/Users/gamyot/Documents/workspace/TestClasses/src";
		//String aSourcePath="/Users/gamyot/Desktop/youssefTest/src";
		 
		
		// using librairies? put the paths here
		// String aClassPath=args[1];
		String aClassPath = "";

		final String[] sourcePathEntries = new String[] { aSourcePath };

		final String[] classpathEntries = new String[] { aClassPath };

		String outputBasePath = "./rsc/comtestStats/" +"/"+ systemName +"/";
		SourceInputsHolder javaProject=null;
		try {
			 javaProject = new FileSystemJavaProject(
					Arrays.asList(classpathEntries),
					Arrays.asList(sourcePathEntries));
			} catch (Exception e) {
				e.printStackTrace();
			}
			final JavaParser eclipseSourceCodeParser = new JavaParser(javaProject);

			final JavaFileAnalyzerVisitor visitor = new JavaFileAnalyzerVisitor();
			try{
			eclipseSourceCodeParser.parse(visitor);
			}
			catch(Exception a){
				a.printStackTrace();
			}
			visitor.resolveClassDependencies();
			
			//creates the project folder to hold the stats
			createOutputDirectory(outputBasePath);
			
			
			/*
			 * collect statistical information about each class on the system
			 */
			if(printStatsCsv){
				// get list of statistical results and print it in csv file
				Map<String,String> resultsMap = visitor.getResultsMap();
				
				String title = "ClassId;Dependencies;tryStatement;ifStatement;forStatement;enhancedForStatement/* foreach */;doStatement;"
						+ "whileStatement;switchCase;switchStatement;catchClause;totalBranches;numOfStatements;"
						+ "FieldDeclaration;synchronizedStatement;methodDeclaration;privateMethod;publicMethod;"
						+ "staticMethod;anonymousClassDeclaration;nbMemberClasses;nbStringParams;nbPrimitiveParams;"
						+ "nbOtherTypesParams;nbOtherTypesParamsLibrary;parameterizedTypeParam;arrayTypeParam;javaImports;trowStatement;"
						+ "NestedBlocks;MaxDepth;importExternal;madumTransformers";
				
				CsvManager.writeSimpleMapInCSV(resultsMap,outputBasePath + "Stats.csv", title,  true);
			}
			
			/*
			 * to know the dependencies of every class on the system 
			 */
			if(printDependenciesCsv){
				// resolveDependencie and print do smth dirty
				Map<String, Set<String>> mapOfFinalDependencies = visitor.resolveClassDependencies();
	
				Map<String, List<String>> mapToSave = new HashMap<String, List<String>>();
				for (Entry<String, Set<String>> entry : mapOfFinalDependencies.entrySet()) {
					mapToSave.put(entry.getKey(), new ArrayList<String>());
					mapToSave.get(entry.getKey()).add(String.valueOf(entry.getValue().size()));
					mapToSave.get(entry.getKey()).addAll(entry.getValue());
	
				}
				CsvManager.writeDynamicMapInCSV(mapToSave, outputBasePath
						+  "ClassDpdcies.csv", "", false);
				 
			}
			
			/*
			 * to know what imports are used on the project 
			 * */
			if (printImportsCsv){
				//get and print the imports
				String title = "Java imports";	
				List<String> listToSave = visitor.getImporList();
				
				CsvManager.writeListInCSV(listToSave, title, outputBasePath +  "Imports.csv", true);
			}
			
			/*
			 *  to know  the transformers for each field
			 * */
			if (printMadumTransformersCsv){
				//get and print the transformers
				String title = "ClassId;[Fields=respective amount of Transformers]";	
				Map<String,String> transformersMap = visitor.getMadumTransformers();
				//write on csv file
				CsvManager.writeSimpleMapInCSV(transformersMap, outputBasePath
						+ "madumTransformers.csv", title,  true);
			}
			
			/*
			 * madum class search
			 */
			if (printMadumInterestingClassesCsv){
				String title = "ClassId;statements;methods;member_and_anonymous_classes;transformed_atributes;imports";	
				List<String> madumClassesList= visitor.getInterestingMadumClasses();
				CsvManager.writeListInCSV(madumClassesList, title, outputBasePath +"InterestingClasssesForMadum.csv", true);
			}
			
			
			System.out.println("Done for " + systemName);


	}
	
	/**
	 * creates the output directory
	 * @param path
	 */
	private static void createOutputDirectory(String path){
		File f = null;
		try{      
			 // returns pathnames for files and directory
			 f = new File(path);
			 f.mkdir();
		 }catch(Exception e){
			 // if any error occurs
			 e.printStackTrace();
		  }
	}
}
