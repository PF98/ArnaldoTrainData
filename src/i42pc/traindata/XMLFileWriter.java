package i42pc.traindata;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

/**
 * <h1 style="font-size: 100px;">WIP</h1>
 */
public class XMLFileWriter {	
	private static final String ATT1 = "id";
	private static final String ATT2 = "value";
	private static final String DEFAULT_ROW_TITLE = "row";	
	private static final String NOT_PRINTING_ROW = "#NO#PRINT#";
	
	private FileList fileList;
	private XMLOutputFactory output;
	private XMLStreamWriter writer;

	/**
	 * Constructor for this object
	 * @param fileList The FileList object containing all of the data to be written to file
	 * @param filePath The path of the resulting file
	 */
	public XMLFileWriter(FileList fileList, String filePath) {
		this.fileList = fileList;
		output = XMLOutputFactory.newInstance();
		try {
			writer = output.createXMLStreamWriter(new FileOutputStream(filePath), "utf-8");
			writer.writeStartDocument("utf-8","1.0");
		} catch (Exception e) {
			System.out.println("There's been an error");
		}
	}
	
	/**
	 * This method writes to file all of the data provided
	 * It initializes the file, writes the root tag and starts printing all of the data in the starting file in the FileList
	 * @param rootTagName The name of the root tag in the file
	 * @return True if successful, false otherwise
	 */
	public boolean printAll(String rootTagName) {
		FileData currentFile = fileList.getStartingFile();
		try {
			writer.writeStartElement(rootTagName); // start root tag
			
			if (!addAll(currentFile.getName(), currentFile.getRowTag(), ATT1, ATT2, currentFile.getValidTitles(), ATT1, ATT2)) 
				return false;
			
			writer.writeEndElement();
			writer.writeEndDocument();
			writer.flush();
			writer.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Writes to file all of the data in the given file
	 * @param fileName The name of the file
	 * @param rowTag The tag to be used as a row tag surrounding all of the data for such row
	 * @param rowAttribute The name of the attribute for the row tag (by default, 'id': if the rowTag has name 'service_id', it will be changed to <service id="...">)
	 * @param alternateRowAttribute The alternative name for the attribute of the row tag, if rowTag has no 'id' at the end
	 * @param validTitles The list of valid titles (to be printed) in the file
	 * @param titleAttribute The name of the attribute for every title (by default, 'id': if the rowTag has name 'service_id', it will be changed to <service id="...">)
	 * @param alternateTitleAttribute The alternative name for the attribute of every title, if rowTag has no 'id' at the end
	 * @return True if successfull, false otherwise
	 */
	private boolean addAll(String fileName, String rowTag, String rowAttribute, String alternateRowAttribute, ArrayList<String> validTitles, String titleAttribute, String alternateTitleAttribute) {
		return addAll(fileName, rowTag, rowAttribute, alternateRowAttribute, validTitles, titleAttribute, alternateTitleAttribute, null, null);
	}

	/**
	 * Writes to file all of the data in the given file
	 * This method call can contain a filter for the file
	 * @param fileName The name of the file
	 * @param rowTag The tag to be used as a row tag surrounding all of the data for such row
	 * @param rowAttribute The name of the attribute for the row tag (by default, 'id': if the rowTag has name 'service_id', it will be changed to <service id="...">)
	 * @param alternateRowAttribute The alternative name for the attribute of the row tag, if rowTag has no 'id' at the end
	 * @param validTitles The list of valid titles (to be printed) in the file
	 * @param titleAttribute The name of the attribute for every title (by default, 'id': if the rowTag has name 'service_id', it will be changed to <service id="...">)
	 * @param alternateTitleAttribute The alternative name for the attribute of every title, if rowTag has no 'id' at the end
	 * @param searchValue The only values to accept in the file
	 * @param searchTitle The title for searchValue
	 * @return True if successfull, false otherwise
	 */
	private boolean addAll(String fileName, String rowTag, String rowAttribute, String alternateRowAttribute, ArrayList<String> validTitles, String titleAttribute, String alternateTitleAttribute, String searchValue, String searchTitle) {
		return addAll(fileName, rowTag, rowAttribute, alternateRowAttribute, validTitles, titleAttribute, alternateTitleAttribute, null, null, null);
	}
	/**
	 * Writes to file all of the data in the given file
	 * This method call can contain a filter for the file
	 * This method call can contain a substitutive row name
	 * @param fileName The name of the file
	 * @param rowTag The tag to be used as a row tag surrounding all of the data for such row
	 * @param rowAttribute The name of the attribute for the row tag (by default, 'id': if the rowTag has name 'service_id', it will be changed to <service id="...">)
	 * @param alternateRowAttribute The alternative name for the attribute of the row tag, if rowTag has no 'id' at the end
	 * @param validTitles The list of valid titles (to be printed) in the file
	 * @param titleAttribute The name of the attribute for every title (by default, 'id': if the rowTag has name 'service_id', it will be changed to <service id="...">)
	 * @param alternateTitleAttribute The alternative name for the attribute of every title, if rowTag has no 'id' at the end
	 * @param searchValue The only values to accept in the file
	 * @param searchTitle The title for searchValue
	 * @param substituteRowName The name of the tag to substitute (only for the name, not for the behaviour) the row tag
	 * @return True if successfull, false otherwise
	 */
	private boolean addAll(String fileName, String rowTag, String rowAttribute, String alternateRowAttribute, ArrayList<String> validTitles, String titleAttribute, String alternateTitleAttribute, String searchValue, String searchTitle, String substituteRowName) {
		FileData currentFile;
		// gets the FileData object
		if (searchValue == null) {
			currentFile = fileList.getFile(fileName); // plain file
		}
		else {
			currentFile = fileList.getFile(fileName).cloneWithSomeValues(searchValue, searchTitle); // filtered file
		}
		try {
			ArrayList<String> fileTitles = currentFile.getTitles(); // the titles of the file
			
			// starts the control for search links
			int searchCount = 0;
			String titleOfControl = null;
			FileData searchFile = null;
			int valueToControlIndex = 0;
			currentFile.startLinkOut();
			while (currentFile.nextLink()) {
				if (currentFile.isLinkSearch()) {
					searchCount++;
					if (searchCount > 1) break;
					titleOfControl = currentFile.getLinkDestinationTitle();
					searchFile = fileList.getFile(currentFile.getLinkDestinationFile());
					valueToControlIndex = fileTitles.indexOf(currentFile.getLinkSourceTitle());
				}
			}
			
			if (searchCount > 1)
				return false;
			boolean isLinkSearching = (searchCount == 1);			
			// end of check for search links
			
			// loops through all of the rows of the file
			while (currentFile.hasNextRow()) {
				ArrayList<String> activeRow = currentFile.getNextRow();
				
				// search link check
				boolean rowToPrint = true;
				if (isLinkSearching) {
					String valueToControl = activeRow.get(valueToControlIndex);
					
					FileData elaboratedSearchFile = searchFile.cloneWithSomeValues(valueToControl, titleOfControl);
					
					rowToPrint = elaboratedSearchFile.hasNextRow();
				}
				
				if (rowToPrint) {
					// handles creating a new row
					String rowTitle = null;
					String rowAttributeName = null;
					String attributeVal = null;
					// writes the row element, if required
					if ((rowTag != null || substituteRowName != null) && !NOT_PRINTING_ROW.equals(substituteRowName)) {
						String usedRowTag = (rowTag == null) ? substituteRowName : rowTag;
						ArrayList<String> rowSplit = new ArrayList<String>(Arrays.asList(usedRowTag.split("_")));
						if (rowSplit.get(rowSplit.size() - 1).equals(rowAttribute) && rowSplit.size() > 1) {
							rowTitle = usedRowTag.substring(0, usedRowTag.lastIndexOf('_'));
							rowAttributeName = rowAttribute;
						}
						else {
							rowTitle = usedRowTag;
							rowAttributeName = alternateRowAttribute;
						}
						
						if (substituteRowName != null) {
							rowTitle = substituteRowName;
						}
						writer.writeStartElement(rowTitle); // start row tag
	
						int titleIndex = fileTitles.indexOf(rowTag == null ? usedRowTag : rowTag);
						if (titleIndex != -1) {
							attributeVal = activeRow.get(fileTitles.indexOf(usedRowTag));
							writer.writeAttribute(rowAttributeName, attributeVal);
						}
					}
					else if (substituteRowName == null) {
						rowTitle = DEFAULT_ROW_TITLE;
						writer.writeStartElement(rowTitle); // start row tag
					}
					else if (!NOT_PRINTING_ROW.equals(substituteRowName)) {
						rowTitle = substituteRowName;
					}
					
					// handles writing the content of a row
					int id = 0;
					for (String val : activeRow) {
						String title = fileTitles.get(id);
						// writes the inner data element
						if (!title.equals(rowTag) && validTitles.contains(title)) {
							ArrayList<String> titleSplit = new ArrayList<String>(Arrays.asList(title.split("_")));
							String tagTitle;
							String attribute;
							if (titleSplit.get(titleSplit.size() - 1).equals(titleAttribute) && titleSplit.size() > 1) {
								tagTitle = title.substring(0, title.lastIndexOf('_'));
								attribute = titleAttribute;
							}
							else {
								tagTitle = title;
								attribute = alternateTitleAttribute;
							}
							writer.writeStartElement(tagTitle);
							writer.writeAttribute(attribute, val);
						}					
						// checks if a link exists
						if (currentFile.hasLinkFrom(title) && !currentFile.isLinkSearch()) {
							FileData linkedFile = fileList.getFile(currentFile.getLinkDestinationFile());
							String newRow = currentFile.getLinkRowName();
							String subName = null;
							if (newRow != null) {
								subName = newRow;
							}
							newRow = linkedFile.getRowTag();								
							if (!addAll(linkedFile.getName(), newRow, ATT1, ATT2, linkedFile.getValidTitles(), ATT1, ATT2, val, title, subName))
								return false;
						}
						
						if (!title.equals(rowTag) && validTitles.contains(title)) {
							writer.writeEndElement(); // closes the single element inside a row
						}
						id++;
					}
					if (!NOT_PRINTING_ROW.equals(substituteRowName))
						writer.writeEndElement(); // closes the row
				}
			}			
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
}
