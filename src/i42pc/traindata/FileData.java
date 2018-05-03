package i42pc.traindata;

import java.util.ArrayList;
import java.util.LinkedList;

public class FileData {
	private ArrayList<String> titles;
	private boolean titlesLocked = false;
	private LinkedList<ArrayList<String>> dataSet;
	private int activeRow = 0;
	
	public FileData() {
		titles = new ArrayList<String>();
	}
	
	public boolean setTitles(ArrayList<String> titles) {
		if (titlesLocked)
			return false;
		this.titles = new ArrayList<String>(titles);
		return true;
	}
	public boolean addTitle(String newTitle) {
		if (titlesLocked)
			return false;
		if (titles.contains(newTitle))
			return false;
		this.titles.add(newTitle);
		return true;
	}
	public void lockTitles() {
		this.titlesLocked = true;
	}
	
	public boolean addValue(String title, String value) {
		int index = titles.indexOf(title);
		if (index == -1) 
			return false;
		dataSet.get(activeRow).set(index, value);
		return true;
	}
	
	public boolean addNewLine() {
		if (dataSet.get(activeRow).size() != titles.size()) {
			return false;
		}
		activeRow++;
		return true;
	}
	
	
}
