package i42pc.traindata;

import java.io.File;
import java.io.IOException;

public class TrainMain {
	public static final String FOLDER_PATH = "./input/";

	public static void main(String[] args) {
		
		
		FileList fl = new FileList(FOLDER_PATH);
		
		fl.readAllFiles();
		
		System.out.println(fl.toString(1000)); // change to toString(1) to print all of the data, toString(n) to print one entry every n in each file
		
	}

}
