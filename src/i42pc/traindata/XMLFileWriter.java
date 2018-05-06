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
	private String filePath;
	private XMLOutputFactory output;
	private XMLStreamWriter writer;

	
	public XMLFileWriter(FileList fileList, String filePath) {
		this.fileList = fileList;
		this.filePath = filePath;
		output = XMLOutputFactory.newInstance();
		try {
			writer = output.createXMLStreamWriter(new FileOutputStream(filePath), "utf-8");
			writer.writeStartDocument("utf-8","1.0");
		} catch (Exception e) {
			System.out.println("There's been an error");
		}
	}
	
	public boolean printAll(String rootTagName) {
		if (!init(rootTagName))
			return false;
		return true;
	}
	
	private boolean init(String rootTagName) {
		FileData currentFile = fileList.getFile(fileList.getStartingFile());
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
	
	private boolean addAll(String fileName, String rowTag, String rowAttribute, String alternateRowAttribute, ArrayList<String> validTitles, String titleAttribute, String alternateTitleAttribute) {
		return addAll(fileName, rowTag, rowAttribute, alternateRowAttribute, validTitles, titleAttribute, alternateTitleAttribute, null, null);
	}
	private boolean addAll(String fileName, String rowTag, String rowAttribute, String alternateRowAttribute, ArrayList<String> validTitles, String titleAttribute, String alternateTitleAttribute, String searchValue, String searchTitle) {
		return addAll(fileName, rowTag, rowAttribute, alternateRowAttribute, validTitles, titleAttribute, alternateTitleAttribute, null, null, null);
	}
	private boolean addAll(String fileName, String rowTag, String rowAttribute, String alternateRowAttribute, ArrayList<String> validTitles, String titleAttribute, String alternateTitleAttribute, String searchValue, String searchTitle, String substituteRowName) {
		FileData currentFile;
		if (searchValue == null) {
			currentFile = fileList.getFile(fileName);
		}
		else {
			currentFile = fileList.getFile(fileName).cloneWithSomeValues(searchValue, searchTitle);
		}
		try {
			while (currentFile.hasNextRow()) {
				ArrayList<String> activeRow = currentFile.getNextRow();
				ArrayList<String> fileTitles = currentFile.getTitles();
				
				// handles creating a new row
				String rowTitle = null;
				String rowAttributeName = null;
				String attributeVal = null;
				if (rowTag != null) {
					ArrayList<String> rowSplit = new ArrayList<String>(Arrays.asList(rowTag.split("_")));
					if (rowSplit.get(rowSplit.size() - 1).equals(rowAttribute) && rowSplit.size() > 1) {
						rowTitle = rowTag.substring(0, rowTag.lastIndexOf('_'));
						rowAttributeName = rowAttribute;
					}
					else {
						rowTitle = rowTag;
						rowAttributeName = alternateRowAttribute;
					}
					
					if (substituteRowName != null) {
						rowTitle = substituteRowName;
					}
					attributeVal = activeRow.get(fileTitles.indexOf(rowTag));
					writer.writeStartElement(rowTitle); // start row tag
					writer.writeAttribute(rowAttributeName, attributeVal);
				}
				else if (substituteRowName == null || !substituteRowName.equals(NOT_PRINTING_ROW)) {
					rowTitle = DEFAULT_ROW_TITLE;
					writer.writeStartElement(rowTitle); // start row tag
				}
				
				// handles writing the content of a row
				int id = 0;
				for (String val : activeRow) {
					String title = fileTitles.get(id);
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
						
						if (currentFile.hasLinkFrom(title)) {
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
						
						writer.writeEndElement(); // closes the single element inside a row
					}
					id++;
				}
				if (substituteRowName == null || !substituteRowName.equals(NOT_PRINTING_ROW))
					writer.writeEndElement(); // closes the row
			}			
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
}
