package i42pc.traindata;

import java.io.File;
import java.util.ArrayList;

public class TrainMain {
	public static final String FOLDER_PATH = "./input/";
	
	public static final String MENU_MAIN_TITLE = "Welcome to the Train Data Manager. What would you like to do?";
	public static final String[] MENU_MAIN_OPTIONS = {"Set the starting file",
									"Set the row tag name for a file",
									"Add a link between two files",
									"Edit the list of valid titles for a file",
									"Print out the file",
									"Help"
									};

	public static void main(String[] args) {
		String printMessage = null;
		String outFileName = null;
		File test;
		String folderPath = null;
		String rootTag = "services";
		do {
			if (folderPath != null) {
				System.out.println("\tPlease insert a valid folder path.");
			}
			folderPath = Utility.getString(String.format("Please insert the path for the input files (use \"no\" for the default setting: %s): ", FOLDER_PATH));
			
			test = new File(folderPath);
		} while (!test.isDirectory() && !folderPath.equals("no")); 
		System.out.println("Reading all of the files... Please wait");
		
		FileList fl = new FileList(folderPath.equals("no") ? FOLDER_PATH : folderPath);
		fl.readAllFiles();
		
		final String[] OPTION_FIRST = {"Use the custom printing mode", "Write to file the first point of the exercise","Write to file the second point of the exercise", "Write to file the third point of the exercise"};
		Menu firstMenu = new Menu("Which mode would you like to use?", OPTION_FIRST);
		boolean looping;
		int option = firstMenu.choose();
		looping = (option == 0);
		
		Menu menu = new Menu(MENU_MAIN_TITLE, MENU_MAIN_OPTIONS);
		while (looping) {
			switch (menu.choose()) {
			case 0: // starting file
				fl.setStartingFile(chooseFileMenu("What file should be the starting one? ", fl));
				break;
			case 1: // row tag name
				String fileChoice = chooseFileMenu("What file would you like to set the row name to? ", fl);
				String rowChoice = chooseFileTitleMenu(String.format("What title should be the row name for %s? ", fileChoice), fileChoice, fl);
				fl.getFile(fileChoice).setRowTag(rowChoice);
				break;
			case 2: // add a link
				String sourceFile = chooseFileMenu("What file should be the starting point of the link? ", fl);
				String sourceTitle = chooseFileTitleMenu(String.format("What title should be the starting point for the link in the file %s? ", sourceFile), sourceFile, fl);
				String destinationFile = null;
				do {
					if (destinationFile != null)
						System.out.println(String.format("You can't have a link starting and ending in %s.",sourceFile));
					destinationFile = chooseFileMenu("What file should be the ending point of the link? ", fl);
				} while (sourceFile.equals(destinationFile));
				String destinationTitle = chooseFileTitleMenu(String.format("What title should be the ending point for the link in the file %s? ", destinationFile), destinationFile, fl);
				String destRowName = Utility.getString("What should be every row of the sublink be called?"+System.lineSeparator()+"Insert one of the titles in the file, a custom value, \"#NO#PRINT#\" if there is only one element per sublink or \"#null#\" to leave the default value."); 
				
				fl.addFileLink(sourceFile, sourceTitle, destinationFile, destinationTitle, (destRowName.equals("#null#") ? null : destRowName));
				break;
			case 3: // edit valid titles
				String fileChoiceTitles = chooseFileMenu("What file would you like to edit the valid title list for?", fl);
				ArrayList<String> validChoices = chooseValidTitles(String.format("Which titles of %s should be considered valid to be printed? ", fileChoiceTitles),
																fl.getFile(fileChoiceTitles).getValidTitles(), fl.getFile(fileChoiceTitles).getTitles(), fl);
				fl.getFile(fileChoiceTitles).setValidTitles(validChoices);
				break;
			case 4: // print the file
				looping = false;
				rootTag = Utility.getString("Insert the name for the root tag");
				do {
					if (outFileName != null)
						System.out.println("The inserted name is not valid.");
					outFileName = Utility.getString("Insert the file name for the output xml file (.xml is not needed): ");
					printMessage = String.format("Printing the custom made file to %s.\nPlease wait (this could take some time)", outFileName);
				} while (outFileName.indexOf('.') != -1);
				break;
			case 5: // help
				break;
			}
		}
		ArrayList<String> validTitles = new ArrayList<String>();
		
		switch (option) {
		case 1:
			outFileName = "one";
			fl.setStartingFile("trips.txt");
			fl.getStartingFile().setRowTag("service_id");
			validTitles.add("service_id");
			validTitles.add("trip_id");
			validTitles.add("route_id");
			fl.getStartingFile().setValidTitles(validTitles);

			fl.addFileLink(fl.getStartingFileName(), "trip_id", "stop_times.txt", "trip_id", null);
			fl.addFileLink(fl.getStartingFileName(), "route_id", "routes.txt", "route_id", "#NO#PRINT#");
			
			validTitles.clear();
			validTitles.add("arrival_time");
			validTitles.add("departure_time");
			fl.getFile("stop_times.txt").setValidTitles(validTitles);
			fl.getFile("stop_times.txt").setRowTag("stop_id");
			break;
		case 2:
			outFileName = "two";
			fl.setStartingFile("trips.txt");
			fl.getStartingFile().setRowTag("service_id");
			validTitles.add("service_id");
			validTitles.add("trip_id");
			validTitles.add("route_id");
			fl.getStartingFile().setValidTitles(validTitles);
			fl.addSearchLink(fl.getStartingFileName(), "service_id", "calendar_dates.txt", "service_id");
			fl.addFileLink(fl.getStartingFileName(), "trip_id", "stop_times.txt", "trip_id", null);
			fl.addFileLink(fl.getStartingFileName(), "route_id", "routes.txt", "route_id", "#NO#PRINT#");
			
			validTitles.clear();
			validTitles.add("arrival_time");
			validTitles.add("departure_time");
			fl.getFile("stop_times.txt").setValidTitles(validTitles);
			fl.getFile("stop_times.txt").setRowTag("stop_id");
			break;
		case 3:
			fl.setStartingFile("trips.txt");
			fl.getStartingFile().setRowTag("service_id");
			fl.addFileLink(fl.getStartingFileName(), "trip_id", "stop_times.txt", "trip_id", null);
			fl.addFileLink(fl.getStartingFileName(), "route_id", "routes.txt", "route_id", "#NO#PRINT#");
			fl.addFileLink("routes.txt", "agency_id", "agency.txt", "agency_id", "#NO#PRINT#");
			fl.addFileLink("stop_times.txt", "stop_id", "stops.txt", "stop_id", "stop_data");
			fl.addFileLink(fl.getStartingFileName(), "service_id", "calendar.txt", "service_id", "calendar");
			fl.addFileLink("calendar.txt", "service_id", "calendar_dates.txt", "service_id", "date");
			
			fl.getFile("stop_times.txt").setRowTag("stop_id");

			fl.getFile("routes.txt").setRowTag("route_id");

			
			validTitles.clear();
			validTitles = fl.getFile("calendar_dates.txt").getTitles();
			validTitles.remove("service_id");
			fl.getFile("calendar_dates.txt").setValidTitles(validTitles);

			fl.getFile("calendar_dates.txt").setRowTag("date");
			
			
			validTitles.clear();
			validTitles = fl.getFile("stops.txt").getTitles();
			validTitles.remove("stop_id");
			fl.getFile("stops.txt").setValidTitles(validTitles);
			
			
			validTitles.clear();
			validTitles = fl.getFile("stop_times.txt").getTitles();
			validTitles.remove("trip_id");
			fl.getFile("stop_times.txt").setValidTitles(validTitles);
			
			outFileName = "three";
			break;
		}
		if (option != 0)
			printMessage = String.format("Printing the point %d of the exercise to %s.xml.\nThis might take up to a couple of minutes", option, outFileName);
		
		
		
		
		
		
		/*
		
		*/
		String outPath = "./output/" + outFileName + ".xml";
		XMLFileWriter fw = new XMLFileWriter(fl, outPath);
		System.out.println(printMessage);
		System.out.println(fw.printAll(rootTag) ? "The file was successfully printed. The file path is ./output/" + outFileName + ".xml" : "There's been an error while printing the file.");
		
	}

	private static ArrayList<String> chooseValidTitles(String message, ArrayList<String> validTitles, ArrayList<String> titles, FileList fileList) {
		boolean finished = false;
		ArrayList<String> validChoices = new ArrayList<String>(validTitles);
		while (!finished) {
			ArrayList<String> menuOptions = new ArrayList<String>();
			menuOptions.add("Exit");
			for (String t : titles) {
				menuOptions.add((validChoices.contains(t) ? "-Y- " : "-N- ") + t);
			}
			Menu myMenu = new Menu("\n\n\n\n\n\n\n\n\n\n" + message, menuOptions.toArray(new String[0]));
			int choice = myMenu.choose();
			switch (choice) {
			case 0:
				finished = true;
				break;
			default:
				if (!validChoices.remove(validTitles.get(choice - 1))) {
					validChoices.add(validTitles.get(choice - 1));
				}
				break;
			}
		}
		return validChoices;
	}

	private static String chooseFileTitleMenu(String message, String fileName, FileList fileList) {
		ArrayList<String> titlesArray = fileList.getFile(fileName).getTitles();
		Menu titleMenu = new Menu(message, titlesArray.toArray(new String[0]));
		
		return titlesArray.get(titleMenu.choose());
	}

	private static String chooseFileMenu(String message, FileList fileList) {
		ArrayList<String> fileArray = fileList.getAllFilesNames();
		Menu fileMenu = new Menu(message, fileArray.toArray(new String[0]));
		
		return fileArray.get(fileMenu.choose());
		
	}

}
