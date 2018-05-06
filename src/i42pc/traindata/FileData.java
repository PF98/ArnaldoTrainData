package i42pc.traindata;

import java.util.ArrayList;

/**
 * <p>This class represents the data contained in a xml file.</p>
 * <p>The strucure of the data inside the class is the following:<br>
 * <ul>
 * <li>The data is put in a table, with every column having a title.</li>
 * <li>Every row contains data for each and every title</li>
 * </ul>
 */
public class FileData {
	private String name;
	private ArrayList<String> titles = new ArrayList<String>();
	private boolean titlesLocked = false;
	private ArrayList<ArrayList<String>> dataSet = new ArrayList<ArrayList<String>>();
	private ArrayList<Link> linkList = new ArrayList<Link>();
	private int activeRow = 0;
	private boolean finished = false;
	private Link currentLink;
	
	private String rowTag;
	private ArrayList<String> validTitles = null;
	
	/**
	 * <p>Basic constructor: initializes the object</p>
	 */
	public FileData() {}
	
	private FileData(FileData other) {
		this.name = other.name;
		this.titles = new ArrayList<String>(other.titles);
		this.titlesLocked = true;
		this.dataSet = new ArrayList<ArrayList<String>>(other.dataSet);
		this.linkList = new ArrayList<Link>(other.linkList);
		this.activeRow = 0;
		this.finished = false;
		if (other.validTitles != null)
			this.validTitles = new ArrayList<String>(other.validTitles);
		else
			this.validTitles = null;
	}
	/**
	 * <p>Returns the name of the file</p>
	 * @return The name of the file
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * <p>Sets the name of the file to a given string</p>
	 * @param name The new name of the file
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<String> getNextRow() {
		return new ArrayList<String>(dataSet.get(activeRow++));
	}
	
	public boolean hasNextRow() {
		if (activeRow <= dataSet.size() - 1)
			return true;
		return false;
	}

	public boolean setValidTitles(ArrayList<String> validTitles) {
		for (String vt : validTitles) {
			if (!titles.contains(vt)) {
				this.validTitles = null;				
				return false;
			}
		}
		this.validTitles = new ArrayList<String>(validTitles);
		return true;
	}
	
	public ArrayList<String> getValidTitles() {
		if (this.validTitles != null)
			return new ArrayList<String>(this.validTitles);
		return new ArrayList<String>(this.titles);
	}
	
	public boolean setRowTag(String rowTag) {
		if (titles.contains(rowTag)) {
			this.rowTag = rowTag;				
			return true;
		}
		this.rowTag = null;
		return false;
	}
	
	public String getRowTag() {
		return this.rowTag;
	}
	
	/**
	 * </p>Returns an {@code ArrayList<String>} containing all of the titles
	 * @return
	 */
	public ArrayList<String> getTitles() {
		return new ArrayList<String>(titles);
	}
	
	/**
	 * <p>Sets the list of titles of the data to a given list</p>
	 * @param titles An ArrayList of strings containing the new titles
	 * @return True if the operation is successful
	 */
	public boolean setTitles(ArrayList<String> titles) {
		if (titlesLocked || finished)
			return false;
		this.titles = new ArrayList<String>(titles);
		return true;
	}
	
	/**
	 * <p>Adds a single new title to be added to the list</p>
	 * @param newTitle The new title
	 * @return True if the operation is successful
	 */
	public boolean addTitle(String newTitle) {
		if (titlesLocked || finished)
			return false;
		if (titles.contains(newTitle))
			return false;
		this.titles.add(newTitle);
		return true;
	}
	
	
	/**
	 * <p>This method locks the titles, meaning that the list of titles isn't editable anymore.</p>
	 * <p>It must be called as soon as possible when all the titles are set, as the class uses
	 * the number of entries in the titles list to determine the number of columns in the table for future checks.</p>
	 */
	public void lockTitles() {
		this.titlesLocked = true;
	}
	
	/**
	 * <p>Returns a true if the titles have been locked (rendered unmodifiable)</p>
	 * @return True if the titles have been locked
	 */
	public boolean hasTitlesLocked() {
		return titlesLocked;
	}
	
	public void setFinished() {
		activeRow = 0;
		this.finished = true;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public FileData cloneWithSomeValues(String searchValue, String searchTitle) {
		FileData out = new FileData(this);
		ArrayList<ArrayList<String>> filteredDataSet = new ArrayList<ArrayList<String>>();
		int titleId = this.titles.indexOf(searchTitle);
		if (titleId == -1) return null;
		for (ArrayList<String> entry : this.dataSet) {
			if (entry.get(titleId).equals(searchValue)) {
				filteredDataSet.add(new ArrayList<String>(entry));
			}
		}
		out.dataSet = filteredDataSet;
		out.setFinished();
		return out;
	}
	
	/**
	 * <p>Adds a single value to the data table and runs some controls on the data.</p>
	 * <p>The controls run by this method are:<br>
	 * <ul><li>Checking if the specified title exists</li>
	 * <li>Checking if the specified title coincides with the title of the first empty column</li></ul>
	 * @param title The title of the column of the new value
	 * @param value The new value to be inserted
	 * @return True if the operation is successful and all of the checks don't show errors
	 */
	public boolean addValue(String title, String value) {
		if (finished) return false;
		int index = titles.indexOf(title);
		if (index == -1 || index != dataSet.get(activeRow).size()) 
			return false;
		dataSet.get(activeRow).add(value);
		return true;
	}
	
	/**
	 * <p>Initializes a new line in the data table and runs some controls.</p>
	 * <p>The controls run by this method are:<br>
	 * <ul><li>Checking if every cell in the active row has been filled</li></ul>
	 * @return True if the operation is successful and all of the checks don't show errors
	 */
	public boolean addNewLine() {
		if (finished) return false;
		if (dataSet.size() != 0) {
			if (dataSet.get(activeRow).size() != titles.size()) {
				return false;
			}
			activeRow++;
		}
		dataSet.add(new ArrayList<String>());
		return true;
	}
	
	/**
	 * <p>Returns a printable version of the data contained in the object</p>
	 * <p>This method allows to print out all of the data in a human-readable form.</p>
	 * <P>If the dataset is too big, use {@link #toString(int)} to print less data</p>
	 * @return A printable String containing the data of the instance
	 */
	public String toString() {
		return toString(0);
	}
	
	/**
	 * <p>Returns a printable version of the data contained in the object</p>
	 * <p>This method allows to print out all of the data in a human-readable form.</p>
	 * <p>It allows to print less entries, as specified by {@code extendedFactor}, in order to reduce the clogging of the console.</p>
	 * <p>If you desire to print the whole dataset, consider using {@link #toString()}
	 * @param extendedFactor Specifies after how many entries it prints one (it must be positive)
	 * @return A printable String containing the data of the instance
	 */
	public String toString(int extendedFactor) {
		if (extendedFactor < 0) return toString();
		
		StringBuffer out = new StringBuffer();
		boolean first = true;
		out.append("### " + this.name + " ###" + (extendedFactor != 0 ? " (scaled by " + extendedFactor + ")" : "") + System.lineSeparator());
		for (String t : titles) {
			if (!first) {
				out.append(" - ");
			}
			first = false;
			out.append(t);
		}
		out.append(System.lineSeparator());
		int count = 0;
		for (ArrayList<String> al : dataSet) {
			first = true;
			if (count++ % extendedFactor == 0) {
				for (String data : al) {
					if (!first) {
						out.append(" - ");
					}
					first = false;
					out.append(data);
				}
				out.append(System.lineSeparator());
			}
		}
		return out.toString();
	}
	
	public void addLink(String sourceTitle, String destinationFile, String destinationTitle, String destinationRowName) {
		Link newLink = new Link(sourceTitle, destinationFile, destinationTitle, destinationRowName);
		
		linkList.add(newLink);
	}
	
	public boolean hasLinkFrom(String sourceTitle) {
		for (Link l : linkList) {
			if (l.getSourceTitle().equals(sourceTitle)) {
				currentLink = l;
				return true;
			}
		}
		return false;
	}
	
	public String getLinkDestinationFile() {
		return currentLink.getDestinationFile();
	}
	
	public String getLinkDestinationTitle() {
		return currentLink.getDestinationTitle();
	}
	
	public String getLinkRowName() {
		return currentLink.getDestinationRowName();
	}
	
	class Link {
		private String destinationFile;
		private String sourceTitle;
		private String destinationTitle;
		private String destinationRowName;

		public Link(String sourceTitle, String destinationFile, String destinationTitle) {
			this(sourceTitle, destinationFile, destinationTitle, null);
		}
		public Link(String sourceTitle, String destinationFile, String destinationTitle, String destinationRowName) {
			this.destinationFile = destinationFile;
			this.sourceTitle = sourceTitle;
			this.destinationTitle = destinationTitle;
			this.destinationRowName = destinationRowName;
		}

		public String getDestinationFile() {
			return destinationFile;
		}

		public String getSourceTitle() {
			return sourceTitle;
		}

		public String getDestinationTitle() {
			return destinationTitle;
		}
		
		public String getDestinationRowName() {
			return destinationRowName;
		}
	}
}
