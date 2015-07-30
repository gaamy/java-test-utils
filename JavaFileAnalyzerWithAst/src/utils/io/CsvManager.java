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
package utils.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CsvManager {

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static List<String> readIntoList(String path, boolean hasTitle) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					path));
			String line;
			try {
				// read title
				if (hasTitle) {
					line = bufferedReader.readLine();
				}
				// read first line of data
				line = bufferedReader.readLine();

				while (line != null) {
					list.add(line);
					line = bufferedReader.readLine();

				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 
	 * @param list
	 * @param title
	 * @param aResultFilePath
	 */
	public static void writeListInCSV(List<String> list, String title,
			String aResultFilePath, boolean hasTitle) {

		PrintStream writer;
		try {
			writer = new PrintStream(new File(aResultFilePath));
			if (hasTitle) {
				writer.println(title);
			}
			for (int i = 0; i < list.size(); i++) {
				writer.println(list.get(i));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param dataPath
	 * @return
	 */
	public static Map<String, String[]> readIntoMap(String dataPath,
			boolean hasTitle) {
		Map<String, String[]> map = new HashMap<String, String[]>();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					dataPath));
			String line;
			try {
				// read title
				if (hasTitle) {
					line = bufferedReader.readLine();
				}
				// read first line of data
				line = bufferedReader.readLine();

				while (line != null) {
					String[] elts = line.split(";");
					String[] tab = new String[elts.length - 1];
					for (int i = 0; i < tab.length; i++) {
						tab[i] = elts[i + 1];
					}
					map.put(elts[0], tab);
					line = bufferedReader.readLine();

				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 
	 * @param dataPath
	 * @return
	 */
	public static Map<String, String> readIntoSimpleMap(String dataPath,
			boolean hasTitle) {
		Map<String, String> map = new LinkedHashMap<String, String>();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					dataPath));
			String line;
			try {
				// read title
				if (hasTitle) {
					line = bufferedReader.readLine();
				}
				// read first line of data
				line = bufferedReader.readLine();

				while (line != null) {
					String[] elts = line.split(";");
					String[] tab = new String[elts.length - 1];

					map.put(elts[0], elts[1]);
					line = bufferedReader.readLine();

				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 
	 * @param map
	 * @param path
	 * @param title
	 * @param hasTitle
	 */
	public static void writeMapInCSV(Map<String, String[]> map, String path,
			String title, boolean hasTitle) {
		PrintStream writer;
		try {
			writer = new PrintStream(new File(path));
			if (hasTitle) {
				writer.println(title);
			}
			Iterator<Entry<String, String[]>> iterator = map.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String[]> entry = (Entry<String, String[]>) iterator
						.next();
				StringBuffer buffer = new StringBuffer();
				buffer.append((String) entry.getKey());
				String tab[] = (String[]) entry.getValue();
				for (int k = 0; k < tab.length; k++) {
					String s = tab[k];
					buffer.append(";");
					buffer.append(s);
				}
				writer.println(buffer.toString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param dataPath
	 * @return
	 */
	public static Map<String, List<String>> readIntoDynamicMap(String dataPath,
			boolean hasTitle) {
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					dataPath));
			String line;
			try {
				// read title
				if (hasTitle) {
					line = bufferedReader.readLine();
				}
				// read first line of data
				line = bufferedReader.readLine();

				while (line != null) {
					String[] elts = line.split(";");
					List<String> listOfElts = new ArrayList<String>();
					for (int i = 1; i < elts.length; i++) {
						listOfElts.add(elts[i]);
					}

					map.put(elts[0], listOfElts);
					line = bufferedReader.readLine();

				}
				bufferedReader.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 
	 * @param map
	 * @param path
	 * @param title
	 * @param hasTitle
	 */
	public static void writeDynamicMapInCSV(Map<String, List<String>> map,
			String path, String title, boolean hasTitle) {
		PrintStream writer;
		try {
			writer = new PrintStream(new File(path));
			if (hasTitle) {
				writer.println(title);
			}
			Iterator<Entry<String, List<String>>> iterator = map.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, List<String>> entry = (Entry<String, List<String>>) iterator
						.next();
				StringBuffer buffer = new StringBuffer();
				buffer.append((String) entry.getKey());
				List<String> listOfElts = entry.getValue();
				for (String elt : listOfElts) {
					buffer.append(";");
					buffer.append(elt);
				}
				writer.println(buffer.toString());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param map
	 * @param path
	 * @param title
	 * @param hasTitle
	 */
	public static void writeSimpleMapInCSV(Map<String, String> map,
			String path, String title, boolean hasTitle) {
		PrintStream writer;
		try {
			writer = new PrintStream(new File(path));
			if (hasTitle) {
				writer.println(title);
			}
			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>) iterator.next();
				StringBuffer buffer = new StringBuffer();
				buffer.append((String) entry.getKey());
				buffer.append(";");
				buffer.append((String) entry.getValue());

				writer.println(buffer.toString());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	
	public static void updateFile(String path, String info) {
		try {

			File file = new File(path);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// true = append file
			FileWriter fileWritter = new FileWriter(path, true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(info);
			bufferWritter.write("\n");
			bufferWritter.close();

			// System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String getFileTitle(String path) {
		String title = "";

		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					path));

			title = bufferedReader.readLine();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return title;
	}

}
