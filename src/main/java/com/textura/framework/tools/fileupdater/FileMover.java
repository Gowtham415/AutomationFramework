package com.textura.framework.tools.fileupdater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Moves files from one directory to a specified directory
 *
 */
public class FileMover {
	
	private String targetDirectory;
	
	public FileMover(String targetDirectory){
		this.targetDirectory = targetDirectory;
	}
	
	public void moveFile(String filePath){
		int nameBegin = filePath.lastIndexOf('\\');
		if(nameBegin < 0){
			nameBegin = filePath.lastIndexOf("/");
		}
		String name = filePath.substring(nameBegin);
		File moved = new File(targetDirectory + name);
		File original = new File(filePath);
		try {
			Files.move(original.toPath(), moved.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void moveFiles(String... filePaths){
		for(String filePath : filePaths){
			moveFile(filePath);
		}
	}
}
