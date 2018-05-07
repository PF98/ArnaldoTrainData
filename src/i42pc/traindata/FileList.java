package i42pc.traindata;

import java.io.File;
import java.util.ArrayList;

/**
 * <p>This class can aggregates multiple {@code FileData} instances.<br>
 * It uses the {@code XMLFileReader} class to read all of the files in a folder, store their data and combine it.
 * @author paolo
 *
 */
public class FileList {
	private ArrayList<FileData> list;
	private File folder;
	private String startingFile;
	
	/**
	 * <p>Basic constructor to initialize the object with the path of the folder containing the files
	 * @param folderPath The path of the folder containing the files
	 */
	public FileList(String folderPath) {
		list = new ArrayList<FileData>();
		File folder = new File(folderPath);
		if (!folder.isDirectory() || !folder.exists()) {
			this.folder = null;
		}
		else
			this.folder = folder;
	}
	
	/**
	 * <p>This method tries to read every file in the specified folder.<br>
	 * If it finds any invalid file it will store its path for future use (see {@link #getInvalidFilePath()}).
	 * @return True if the whole operation is successful, false otherwise
	 */
	public boolean readAllFiles() {
		for (String fileName : folder.list()) {
			File file = new File(folder.getAbsolutePath() + "\\" + fileName);
			if (!file.isDirectory()) {
				XMLFileReader fr = new XMLFileReader();
				fr.setFile(file);
				if (!fr.readAll()) {
					list.clear();
					return false;
				}
				list.add(fr.returnData());
			}
		}
		return true;
	}
	

	/**
	 * <p>Returns a printable version of the data contained in the object</p>
	 * <p>This method allows to print out all of the data in a human-readable form.</p>
	 * <p>By default, this method doesn't print the whole data, but just the names of the files<br>
	 * If you desire to print out data for every file, use {@link #toString(int)}</p>
	 * @return A printable String containing the data of the instance
	 */
	public String toString() {
		return toString(0);
	}

	/**
	 * <p>Returns a printable version of the data contained in the object</p>
	 * <p>This method allows to print out all of the data in a human-readable form.</p>
	 * <p>It allows to specify the number of desired entries, as specified by {@code extendedFactor}, in order to reduce the clogging of the console<br>
	 * @param extendedFactor Specifies after how many entries in a single file it prints one (it must be positive)
	 * @return A printable String containing the data of the instance
	 */
	public String toString(int extendedFactor) {
		StringBuffer out = new StringBuffer();
		out.append("Printing all the data for this FileList: " + System.lineSeparator() + System.lineSeparator());
		for (FileData fd : list) {
			out.append( extendedFactor != 0 ? fd.toString(extendedFactor).replaceAll("\n", "\n\t") : "Avoiding printing the file " + fd.getName() );
			out.append(System.lineSeparator());
		}
		
		return out.toString();
	}
	
	/**
	 * Returns true if the provided String corresponds to the name of a file in the list
	 * @param fileName The given name of the file
	 * @return True if a file is found, false otherwise
	 */
	private boolean isFile(String fileName) {
		if (getFile(fileName) == null) 
			return false;
		return true;
	}
	
	/**
	 * Returns the file object corresponding to the given file name, if it exists
	 * @param fileName The given file name
	 * @return A FileData object containing all of the data if it exists, null otherwise
	 */
	public FileData getFile(String fileName) {
		for (FileData f : list) {
			if (fileName.equals(f.getName()))
				return f;
				
		}
		return null;
	}
	
	/**
	 * Checks if a given file has a given title 
	 * @param fileName The name of the file
	 * @param title The searched title
	 * @return True if the file has such title, false otherwise or if the file doesn't exist
	 */
	private boolean hasTitle(String fileName, String title) {
		FileData file = getFile(fileName);
		if (file == null)
			return false;
		if (file.getTitles().contains(title))
			return true;
		return false;
	}
	
	/**
	 * Sets the starting file for the sequence to be written to file
	 * This file will be read first once starting to write
	 * @param fileName The name of the starting file
	 * @return False if the file name doesn't correspond to a file, true otherwise
	 */
	public boolean setStartingFile(String fileName) {
		if (isFile(fileName)) {
			this.startingFile = fileName;
			return true;
		}
		this.startingFile = null;
		return true;
	}
	
	/**
	 * Returns the name of the starting file of the sequence
	 * @return The name of the starting file, null if it isn't set
	 */
	public String getStartingFileName() {
		return this.startingFile;
	}
	
	/**
	 * Returns the FileData object for the starting file
	 * @return The FileData object for the starting file
	 */
	public FileData getStartingFile() {
		String file = getStartingFileName();
		if (file != null)
			return getFile(file);
		return null;
	}
	
	/**
	 * Returns a list containing all of the file names saved in this object
	 * @return An ArrayList<String> containing all of the file names
	 */
	public ArrayList<String> getAllFilesNames() {
		ArrayList<String> out = new ArrayList<String>();
		for (FileData f : list) {
			out.add(f.getName());
		}
		return out;
	}
	
	/**
	 * Adds a link between two files, specifying a substitutive row name for the destination file of the link
	 * @param sourceFile The file from which the link starts
	 * @param sourceTitle The starting point (a title in sourceFile) for the link
	 * @param destinationFile The file in which the link ends
	 * @param destinationTitle The ending point (a title in destinationFile) for the link
	 * @param destinationRowName The substitutive row name to print the new file
	 * @return True if the parameters are valid and the link is created, false otherwise
	 */
	public boolean addFileLink(String sourceFile, String sourceTitle, String destinationFile, String destinationTitle, String destinationRowName) {
		if (!isFile(sourceFile) || !isFile(destinationFile))
			return false;
		
		if (!hasTitle(sourceFile, sourceTitle) || !hasTitle(destinationFile, destinationTitle))
			return false;
		
		getFile(sourceFile).addLink(sourceTitle, destinationFile, destinationTitle, destinationRowName, false);
				
		return true;
	}
	
	/**
	 * Adds a link between two files
	 * @param sourceFile The file from which the link starts
	 * @param sourceTitle The starting point (a title in sourceFile) for the link
	 * @param destinationFile The file in which the link ends
	 * @param destinationTitle The ending point (a title in destinationFile) for the link
	 * @return True if the parameters are valid and the link is created, false otherwise
	 */
	public boolean addFileLink(String sourceFile, String sourceTitle, String destinationFile, String destinationTitle) {
		return addFileLink(sourceFile, sourceTitle, destinationFile, destinationTitle, null);
	}
	
	/**
	 * Adds a special kind of link: the search one.
	 * This link allows to filter the data of a file through a second file:
	 * The printer will thus print a row of the sourceFile only if the value in the field sourceTitle
	 * exists in destinationFile, in the field destinationTitle
	 * @param sourceFile The file to be filtered
	 * @param sourceTitle The title of the values in sourceFile to be filtered
	 * @param destinationFile The filtering file
	 * @param destinationTitle The title of the values in destinationFile to be checked against
	 * @return
	 */
	public boolean addSearchLink(String sourceFile, String sourceTitle, String destinationFile, String destinationTitle) {
		if (!isFile(sourceFile) || !isFile(destinationFile))
			return false;
		
		if (!hasTitle(sourceFile, sourceTitle) || !hasTitle(destinationFile, destinationTitle))
			return false;
		
		getFile(sourceFile).addLink(sourceTitle, destinationFile, destinationTitle, null, true);
				
		return true;
	}
}
