package i42pc.traindata;

import java.io.File;

public class TrainMain {

	public static void main(String[] args) {
		
		FileReader fr = new FileReader();
		
		System.out.println(fr.setFilePath("./input/agency.txt"));
		
		File folder = new File("./input/");
		
		for (String f : folder.list()) {
			System.out.println(f.toString());
		}
		
	}

}
