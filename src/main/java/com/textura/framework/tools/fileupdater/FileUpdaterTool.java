package com.textura.framework.tools.fileupdater;

import java.io.File;
import java.util.function.Consumer;
import org.json.JSONArray;
import org.json.JSONObject;
import com.textura.framework.configadapter.ConfigComponents;
import com.textura.framework.environment.Project;
import com.textura.framework.tools.fileupdater.FileConverter.FileType;
import com.textura.framework.utils.JavaHelpers;

public class FileUpdaterTool {
	
	public static void main(String[] args){
//		moveFileExample();
//		convertFileExample();
		updateCsvExample();
	}
	
	
	/**
	 * Creates a test csv file with the following contents
	 * column 1,column 2,column 3,column 4
	 * value1-1,value1-2,value1-3,value1-4
	 * value2-1,value2-2,value2-3,value2-4
	 * 
	 * @return path of file created
	 */
	private static String createTestCsvFile(){
		Project.setProduct(ConfigComponents.FRAMEWORK);
		String path = Project.downloads(System.nanoTime() + ".csv");
		JavaHelpers.writeFile(path, "column 1,column 2,column 3,column 4\r\nvalue1-1,value1-2,value1-3,value1-4\r\nvalue2-1,value2-2,value2-3,value2-4");
		return path;
	}
	
	/**
	 * Moves files from test-downloads directory to screenshots directory
	 */
	@SuppressWarnings("unused")
	private static void moveFileExample(){
		Project.setProduct(ConfigComponents.FRAMEWORK);
		String testFile1Path = createTestCsvFile();
		String testFile2Path = createTestCsvFile();
		String testFile3Path = createTestCsvFile();
		
		String directory = Project.screenshots("");
		FileMover mover = new FileMover(directory);
		mover.moveFile(testFile1Path); //move 1 file to directory
		mover.moveFiles(testFile2Path, testFile3Path); //move multiple files to directory
	}
	
	/**
	 * Converts csv to json
	 */
	@SuppressWarnings("unused")
	private static void convertFileExample(){
		try{
			Project.setProduct(ConfigComponents.FRAMEWORK);
			String filePath = createTestCsvFile();
			FileType targetType = FileType.json; 
			String newFormatContents = FileConverter.convertFileTo(filePath, targetType);
			JavaHelpers.writeFile(filePath.replace(".csv", "." + targetType.extension), newFormatContents);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Recreating the scenario where a report has the following changes:
	 *  adds a column named 'new column' with some value and inserts it between column 2 and 3 in the report
	 * 	removes column 4
	 * 	moves column 1 to the end
	 */
	@SuppressWarnings("unused")
	private static void updateCsvExample(){
		try{
			Project.setProduct(ConfigComponents.FRAMEWORK);
			String filePath = createTestCsvFile();
			
			Consumer<JSONObject> updater = (JSONObject line) -> {
				try{
					//add a column to every line
					String newColumnName = "new column";
					String newColumnValue = getNewColumnValue();
					line.put(newColumnName, newColumnValue);
				}
				catch(Exception e){
					e.printStackTrace();
				}

			};
			
			// all the column names in the updated csv file in the expected order
			String columns[] = {"column 2", "new column", "column 3", "column 1"};
			applyUpdatesToFile(filePath, columns, updater);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * This can theoretically be anything, from a db query to using selenium to get values from the application
	 * @return value to be used by new column
	 */
	private static String getNewColumnValue(){
		return "new value";
	}
	
	/**
	 * Convenience method. temporarily converts file to a JSONArray, and passes in each JSONObject to the updater consumer. Writes changes back to file
	 * @param path to file 
	 * @param columns to be included in report. Columns will show up in the same order as array. Json values not in column will be omitted.
	 * @param updater Function to be applied 
	 */
	public static void applyUpdatesToFile(String filePath, String columns[], Consumer<JSONObject> updater){
		try{
			//convert to more easily workable json format
			FileType targetType = FileType.json;
			String jsonString = FileConverter.convertFileTo(filePath, targetType).trim();
			JSONArray jsonArray = new JSONArray(jsonString);

			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject line = jsonArray.getJSONObject(i);
				updater.accept(line);
			}
			
			String newCsv = FileConverter.convertJsonArrayToCsv(columns, jsonArray);
			
			//replace file contents
			JavaHelpers.writeFile(filePath, newCsv);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Applys updates to all files in directory and saves changes
	 * @param directory
	 * @param columns to be included in report. Columns will show up in the same order as array. Json values not in column will be omitted.
	 * @param updater Function to be applied 
	 */
	public static void applyUpdatesToAllFilesInDirectory(String directoryPath, String columns[], Consumer<JSONObject> updater){
		File directory = new File(directoryPath);
		File[] files = directory.listFiles();

		for(File file: files){
			try{
				applyUpdatesToFile(file.getPath(), columns, updater);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void convertAllXlsToCsvInDirectory(String directoryPath){
		File directory = new File(directoryPath);
		File[] files = directory.listFiles();

		for (File file : files) {
			try {
				String filePath = file.getPath();
				if (filePath.contains(".xls")) {
					System.out.println("converting " + filePath);
					String csv = FileConverter.convertFileTo(filePath, FileType.csv, null);
					JavaHelpers.writeFile(filePath.replaceAll("\\.xls$", ".csv"), csv);
					JavaHelpers.delete(file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
