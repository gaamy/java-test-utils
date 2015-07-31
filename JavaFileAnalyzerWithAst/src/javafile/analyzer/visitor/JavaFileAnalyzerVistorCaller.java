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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

		
		String systemName = "elasticsearch-1.7.1"; 
		 
		String aSourcePath="/Users/gamyot/Google Drive/Été 2015/Stage 2015/resources/system Search/newSystems/"+systemName+"/src";
		//String aSourcePath="/Users/gamyot/Documents/workspace/TestClasses/src";
		//String aSourcePath="/Users/gamyot/Desktop/youssefTest/src";
		 
		
		// using librairies? put the paths here
		// String aClassPath=args[1];
		String aClassPath = "";

		final String[] sourcePathEntries = new String[] { aSourcePath };

		final String[] classpathEntries = new String[] { aClassPath };

		String outputBasePath = "./rsc/comtestStats/";

		try {
			SourceInputsHolder javaProject = new FileSystemJavaProject(
					Arrays.asList(classpathEntries),
					Arrays.asList(sourcePathEntries));

			final JavaParser eclipseSourceCodeParser = new JavaParser(javaProject);

			final JavaFileAnalyzerVisitor visitor = new JavaFileAnalyzerVisitor();
			try{
			eclipseSourceCodeParser.parse(visitor);
			}
			catch(Exception a){
				System.out.println(a.toString());
			}
			visitor.resolveClassDependencies();
			
			/*
			 * collect statistical information about each class on the system
			 */
			// get list of statistical results and print it in csv file
			Map<String,String> resultsMap = visitor.getResultsMap();
			
			String title = "ClassId;Dependencies;tryStatement;ifStatement;forStatement;enhancedForStatement/* foreach */;doStatement;"
					+ "whileStatement;switchCase;switchStatement;catchClause;totalBranches;numOfStatements;"
					+ "FieldDeclaration;synchronizedStatement;methodDeclaration;privateMethod;publicMethod;"
					+ "staticMethod;anonymousClassDeclaration;nbMemberClasses;nbStringParams;nbPrimitiveParams;"
					+ "nbOtherTypesParams;nbOtherTypesParamsLibrary;parameterizedTypeParam;arrayTypeParam;javaImports;trowStatement;"
					+ "NestedBlocks;MaxDepth;importExternal";
			
			CsvManager.writeSimpleMapInCSV(resultsMap,outputBasePath + systemName + "_Stats.csv", title,  true);

			/*
			 * to know the dependencies of every class on the system 
			 */
			// resolveDependencie and print do smth dirty
			Map<String, Set<String>> mapOfFinalDependencies = visitor.resolveClassDependencies();

			Map<String, List<String>> mapToSave = new HashMap<String, List<String>>();
			for (Entry<String, Set<String>> entry : mapOfFinalDependencies.entrySet()) {
				mapToSave.put(entry.getKey(), new ArrayList<String>());
				mapToSave.get(entry.getKey()).add(String.valueOf(entry.getValue().size()));
				mapToSave.get(entry.getKey()).addAll(entry.getValue());

			}
			CsvManager.writeDynamicMapInCSV(mapToSave, outputBasePath
					+ systemName + "_ClassDpdcies.csv", "", false);
			 
			
			/*
			 * to know what imports are used on the project 
			 * */
			//get and print the imports
			title = "Java imports";	
			List<String> listToSave = visitor.getImporList();
			
			CsvManager.writeListInCSV(listToSave, title, outputBasePath + systemName + "_SImports.csv", true);
		
			
			
			
			System.out.println("Done for " + systemName);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}
