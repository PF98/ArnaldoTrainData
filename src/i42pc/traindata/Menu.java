package i42pc.traindata;

import java.util.*;

public class Menu {
	private Scanner scanner = new Scanner(System.in).useDelimiter(System.getProperty("line.separator"));
	private String title;
	private String[] options;
	
	/**
	 * Default constructor (the other parameters must be set with the setters before calling the choose method).
	 */
	public Menu() {
		this.title = null;
		this.options = null;
	}
	
	/**
	 * Other constructor.
	 * @param title A string containing the title of the menu
	 * @param options An array of strings containing the various choices of the menu
	 */
	public Menu(String title, String[] options) {
		this.title = title;
		this.options = options;
	}
	
	/**
	 * Setter for the title attribute.
	 * @param title The new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Setter for the options attribute.
	 * @param options The new options
	 */
	public void setOptions(String[] options) {
		this.options = options;
	}
	
	/**
	 * This method prints the menu and allows the user to express their choice.
	 * It checks automatically for the right data type and value.
	 * @return The integer value of the choice of the user
	 */
	public int choose() {
		System.out.println('\n');
		System.out.println(title);
		int i = 0;
		int out = 0;
		boolean error = true;
		for (String option : options) {
			System.out.println(i++ + ") " + option);
		}
		do {
			error = true;
			try {
				System.out.print("Make your choice > ");
				out = scanner.nextInt();
				error = false;
			}
			catch (InputMismatchException e) {
				System.out.println("Wrong data type: expected int - Try again.");
				scanner.next();
			}
		} while (out < 0 || out >= i || error);
		return out;
	}
}
