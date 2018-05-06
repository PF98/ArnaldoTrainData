package i42pc.traindata;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * <p>This class implements a reader for xml files, with interactions with the {@link FileData}</p>
 * <p>It can read xml files with the following structure:<br>
 * <ul><li>The whole file is contained inside a root tag named {@link #ROOT_TAG}</li>
 * <li>Every row of the table must be contained in a tag named {@link #ROW_TAG}</li>
 * <li>Every row must contain all the values inside of title tags.</li>
 * <li>The title tags must be all present (implicit tags are not supported)</li>
 * <li>For empty data values, both self-closing tags ({@code <title />}) and opening and closing tags ({@code <title> </title>}) are supported</li></ul>
 * <p>The reading methods were adapted from {@link https://docs.oracle.com/javase/tutorial/jaxp/stax/example.html}
 */
public class XMLFileReader {
	
	private static final String ROOT_TAG = "table";
	private static final String ROW_TAG = "row";
	
	private String filePath = null;
	private XMLInputFactory xmlif;
    private XMLStreamReader xmlr;
    private FileData fileData;
    
    private String tempTitle;
    
    private boolean finished = false;
	private boolean dataTagOpened = false;
	private boolean dataInserted = false;
	
	/**
	 * <p>Basic constructor that just initializes the object</p>
	 */
	public XMLFileReader() {
		
	}
	
	/**
	 * <p>Sets the path of the file to read to the path of the specified file, if it is available.</p>
	 * <p>This method runs the following controls:<br>
	 * <ul><li>Checking if the path is a valid file</li></ul>
	 * @param filePath The specified file path
	 * @return True if the file path is valid, false otherwise
	 */
	public boolean setFilePath(String filePath) {
		File f = new File(filePath);
		return this.setFile(f);
	}
	
	/**
	 * <p>Sets the path of the file to read to the specified one, if a file is available.</p>
	 * <p>This method runs the following controls:<br>
	 * <ul><li>Checking if the specified file is valid</li></ul>
	 * @param file The specified file object
	 * @return True if the file is valid, false otherwise
	 */
	public boolean setFile(File file) {
		if (file.isFile()) {
			this.filePath = file.getPath();
			return true;
		}
		this.filePath = null;
		return false;
	}
	
	/**
	 * <p> Returns a boolean value, based on whether the saved file path is valid or not.
	 * @return True if the file is valid, false otherwise
	 */
	public boolean hasValidFile() {
		return (this.filePath != null);
	}
	
	/**
	 * <p>Initializes the process of reading the files
	 * @return True if the operation is successful, false otherwise
	 */
	private boolean init() {
		try {
			xmlif = XMLInputFactory.newInstance();
	        xmlr = xmlif.createXMLStreamReader(filePath,new FileInputStream(filePath));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * <p>Every call of this method will automatically handle reading the xml file and calling other private methods to save the extracted data.</p>
	 * <p>This method gets called repeatedly until the end of the document
	 * @return True if the reading is successful, false otherwise
	 */
	private boolean next() {
	    try {
             switch(xmlr.getEventType()) {
            case XMLStreamConstants.START_DOCUMENT:
            	initFile();
            	break;
            case XMLStreamConstants.START_ELEMENT:
            	if (!newElement(xmlr.getLocalName())) 
            		return false;
            	break;
            case XMLStreamConstants.CHARACTERS:
            	if(xmlr.getText().trim().length() > 0 || dataTagOpened)
            		if (!insertValue(tempTitle, xmlr.getText().trim()))
            			return false;
            	break;
            case XMLStreamConstants.END_ELEMENT:
            	closedElement(xmlr.getLocalName());
            	break;
            default:
            	break;
            }
            xmlr.next();
            return true;
	    } catch(Exception e){
	    	System.out.println("Error: " + e.getLocalizedMessage());
	    	return false;
	    }
	}
	
	/**
	 * <p>Private method to initialize the FileData object, where all of the data will be stored
	 */
	private void initFile() {
		fileData = new FileData();
		fileData.setName(new File(filePath).getName());
	}
	
	/**
	 * <p>Private method called when the reader finds the start of an element
	 * @param elementName The name of the element found
	 * @return True if all of the operations are successful, false if any problem occurs
	 */
	private boolean newElement(String elementName) {
		if (elementName.equals(ROOT_TAG)) {
			return true;
		}
		if (elementName.equals(ROW_TAG)) {
			return fileData.addNewLine();
		}
		if (!fileData.hasTitlesLocked()) {
			fileData.addTitle(elementName);
		}
		dataTagOpened = true;
		tempTitle = elementName;
		return true;
	}
	
	/**
	 * <p>Private method called when the reader finds values to be stored
	 * @param title The title of the column for the specified value
	 * @param value The value to be added
	 * @return True if all of the operations are successful, false if any problem occurs
	 */
	private boolean insertValue(String title, String value) {
		dataInserted = fileData.addValue(title, value);
		return dataInserted;
	}
	
	/**
	 * <p>Private method called when the reader finds the end of an element
	 * @param closedElementName The neme of the closing element
	 */
	private void closedElement(String closedElementName) {
		if (closedElementName.equals(ROOT_TAG)) {
			fileData.setFinished();
			finished = true;
			return;
		}
		if (closedElementName.equals(ROW_TAG)) {
			if (!fileData.hasTitlesLocked())
				fileData.lockTitles();
			return;
		}
		if (!dataInserted) {
			insertValue(tempTitle, new String());
		}
		dataInserted = false;
		dataTagOpened = false;
	}
	
	/**
	 * <p>Reads all of the data in the xml file and stores it privately.</p>
	 * <p>See {@link #returnData()} to retrieve all of the data in the form of a {@code FileData} object
	 * @return True if the whole operation is successful, false otherwise
	 */
	public boolean readAll() {
		this.init();
		while (this.next() && !finished);
		return finished;
	}
	
	/**
	 * <p>Returns all of the saved data in the form of a {@code FileData} object
	 * @return A {@code FileData} object containing all of the data
	 */
	public FileData returnData() {
		return fileData;
	}
	
}
