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
	private String invalidPath = null;
	
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
					invalidPath = file.getPath();
					list.clear();
					return false;
				}
				list.add(fr.returnData());
			}
		}
		invalidPath = null;
		return true;
	}
	
	/**
	 * Returns the path of the invalid file, if it exists, {@code null} otherwise
	 * @return The path of the invalid file, if it exists, {@code null} otherwise
	 */
	public String getInvalidFilePath() {
		return invalidPath;
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
}
