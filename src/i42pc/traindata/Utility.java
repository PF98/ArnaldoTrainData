package i42pc.traindata;

import java.util.*;

public class Utility {
	private static Scanner reader = new Scanner(System.in).useDelimiter(System.getProperty("line.separator"));
	
	private static final String WRONG_TYPE_ERROR = "Wrong data inserted: expected ";
	private static final String TRY_AGAIN_MESSAGE = " - Insert your data again: ";
	
	private static final String OUTSIDE_BOUND_ERROR = "Error: the value expeced is outside of the requested interval of (%s, %s). Try again: ";
	
	/**
	 * Deals with the reader to get an input of type double from the keyboard in the console.
	 * @param message The message that gets printed on the console before reading the data. Messages ending with a ": " string are suggested for readability of the request.
	 * @return A double value read from the keyboard.
	 * 
	 * @author paolo faustini
	 */
	public static double getDouble(String message) {
		final String EXPECTED_TYPE = "double";
		System.out.print(message);
		double val = 0.0;
		boolean finished = false;
		while (!finished) {
			try {
				val = reader.nextDouble();
				finished = true;
			}
			catch (InputMismatchException e) {
				System.out.print(WRONG_TYPE_ERROR + EXPECTED_TYPE + TRY_AGAIN_MESSAGE);
				reader.next();
			}
		}
		return val;
	}
	
	/**
	 * Deals with the reader to get an input of type double included between two given bounds.
	 * @param message The message that gets printed on the console before reading the data. Messages ending with a ": " string are suggested for readability of the request.
	 * @param lowerLimit The lower bound of the read value.
	 * @param upperLimit The upper bound of the read value.
	 * @return A double value read from the keyboard and included between two given bounds.
	 * 
	 * @author paolo faustini
	 */
	public static double getDouble(String message, double lowerLimit, double upperLimit) {
		double val = getDouble(message);
		while (val < lowerLimit || val > upperLimit) {
			System.out.print(String.format(OUTSIDE_BOUND_ERROR, niceDouble(lowerLimit), niceDouble(upperLimit)));
			val = getDouble("");
		}
		return val;
	}
	
	
	
	/**
	 * Deals with the reader to get an input of type String from the keyboard in the console.
	 * @param message The message that gets printed on the console before reading the data. Messages ending with a ": " string are suggested for readability of the request.
	 * @return A String value read from the keyboard.
	 * 
	 * @author paolo faustini
	 */
	public static String getString(String message) {
		final String EXPECTED_TYPE = "String";
		System.out.print(message);
		String val = null;
		boolean finished = false;
		while (!finished) {
			try {
				val = reader.next();
				finished = true;
			}
			catch (InputMismatchException e) {
				System.out.print(WRONG_TYPE_ERROR + EXPECTED_TYPE + TRY_AGAIN_MESSAGE);
				reader.next();
			}
		}
		return val;
	}
	
	/**
	 * Deals with the reader to get an input of type int from the keyboard in the console.
	 * @param message The message that gets printed on the console before reading the data. Messages ending with a ": " string are suggested for readability of the request.
	 * @return A int value read from the keyboard.
	 * 
	 * @author paolo faustini
	 */
	public static int getInt(String message) {
		final String EXPECTED_TYPE = "int";
		System.out.print(message);
		int val = 0;
		boolean finished = false;
		while (!finished) {
			try {
				val = reader.nextInt();
				finished = true;
			}
			catch (InputMismatchException e) {
				System.out.print(WRONG_TYPE_ERROR + EXPECTED_TYPE + TRY_AGAIN_MESSAGE);
				reader.next();
			}
		}
		return val;
	}
	
	/**
	 * Deals with the reader to get an input of type int included between two given bounds.
	 * @param message The message that gets printed on the console before reading the data. Messages ending with a ": " string are suggested for readability of the request.
	 * @param lowerLimit The lower bound of the read value.
	 * @param upperLimit The upper bound of the read value.
	 * @return A int value read from the keyboard and included between two given bounds.
	 * 
	 * @author paolo faustini
	 */
	public static int getInt(String message, int lowerLimit, int upperLimit) {
		int val = getInt(message);
		while (val < lowerLimit || val > upperLimit) {
			System.out.print(OUTSIDE_BOUND_ERROR + "(" + lowerLimit + "," + upperLimit + ")" + ": try again: ");
			val = getInt("");
		}
		return val;
	}
	
	/**
	 * Converts a double value into String, removing any unnecessary decimal digits (i.e. 4.0 -> 4)
	 * @param number The double number to be converted
	 */
	public static String niceDouble(double number) {
		Double num = number;
		int numInt = num.intValue();
		if ((double) num == (double) numInt) {
			return String.valueOf(numInt);
		}
		return String.valueOf(number);
	}
}
