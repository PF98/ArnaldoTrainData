package i42pc.traindata;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class FileReader {
	private String filePath = null;
	private XMLInputFactory xmlif;
    private XMLStreamReader xmlr;
    FileData fileData;

	public FileReader() {
		
	}
	
	public boolean setFilePath(String filePath) {
		File f = new File(filePath);
		if (f.isFile()) {
			this.filePath = filePath;
			return true;
		}
		this.filePath = null;
		return false;
	}
	
	public boolean hasValidFile() {
		return (this.filePath != null);
	}
	
	/**
	 * 
	 * @param filename
	 * Adapted from:
	 * https://docs.oracle.com/javase/tutorial/jaxp/stax/example.html
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
	
	private boolean next() {
	    try {
            switch(xmlr.getEventType()) {
            case XMLStreamConstants.START_DOCUMENT:
            	break;
            case XMLStreamConstants.START_ELEMENT:
            	newElement(xmlr.getLocalName());
            	break;
            case XMLStreamConstants.NOTATION_DECLARATION:
            	System.out.println("Inside "+xmlr.getText());
            	break;	            	
            case XMLStreamConstants.CHARACTERS:
            	if(xmlr.getText().trim().length()>0)
            		System.out.println("-> "+xmlr.getText());
            	break;
            case XMLStreamConstants.END_ELEMENT:
            	break;
            default:
            	break;
            }
            xmlr.next();
            return true;
	    } catch(Exception e){
	    	return false;
	    }
	}
	
	private void initFile() {
		fileData = new FileData();
	}
	
	private void newElement(String elementName) {
//		fileData
	}
	
	private void insertValue(String title, String value) {
		
	}
	
	private void insertNewRow() {
		
	}

	public boolean readAll() {
		fileData = new FileData();
		if (!this.init())
			return false;
		try {
			while (xmlr.hasNext()) {
				if (!this.next()) 
					return false;
				//switch
			}
		} catch (Exception e) {
			return false;
		}
		//fileData.addTitle(newTitle)
		return false;
	}
	
}
