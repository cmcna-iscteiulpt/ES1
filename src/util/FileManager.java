package util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Grafica.GUI;

public class FileManager {
	public static String rulesDefaultLocation = "filesForTesting/rules.cf",
			hamDefaultLocation = "filesForTesting/ham.log", spamDefaultLocation = "filesForTesting/spam.log";

	private List<String> regras = new ArrayList<String>(), hamMessages = new ArrayList<String>(),
			spamMessages = new ArrayList<String>();

	private GUI GUI;
	private static FileManager instance = null;

	/**
	 * Exists only to defeat instantiation
	 */
	protected FileManager() {
	}

	/**
	 * Returns the instance of the class
	 */
	public static FileManager getInstance() {
		if (instance == null) {
			instance = new FileManager();
		}
		return instance;
	}

	/**
	 * This function initiates the rules data structure, if it has not been yet, and
	 * returns it
	 */
	public List<String> getRegras() {
		if (regras.size() == 0)
			readRulesFile();
		return regras;
	}

	/**
	 * This function initiates the ham data structure, if it has not been yet, and
	 * returns it
	 */
	public List<String> getHamMessages() {
		if (hamMessages.size() == 0)
			readHamFile();
		return hamMessages;
	}

	/**
	 * This function initiates the spam data structure, if it has not been yet, and
	 * returns it
	 */
	public List<String> getSpamMessages() {
		if (spamMessages.size() == 0)
			readSpamFile();
		return spamMessages;
	}

	/**
	 * This function read the anti-spam rules from the file path provided on the
	 * GUI, however it has a default file path from the project folder
	 */
	public void readRulesFile() {
		File file = new File(GUI.getTextPathRules().getText());

		if (file.isFile() && file.getName().endsWith(".cf")) {

			try {
				FileInputStream fstream = new FileInputStream(file);

				try (DataInputStream in = new DataInputStream(fstream)) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					String[] regra = new String[2];
					while ((strLine = br.readLine()) != null) {
						String[] rule = strLine.split("\t");
						regra[0] = rule[0];
						if (rule.length > 1 && rule[1] != null)
							regra[1] = rule[1];
						// Print the content on the console
						this.regras.add(regra[0]);
					}
				}

			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	/**
	 * This function read the ham messages from the file path provided on the GUI,
	 * however it has a default file path from the project folder
	 */

	public void readHamFile() {
		File file = new File(GUI.getTextPathHam().getText());
		if (file.isFile() && file.getName().endsWith(".log")) {
			try {
				FileInputStream fstream = new FileInputStream(file);

				try (DataInputStream in = new DataInputStream(fstream)) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine = br.readLine();
					while (strLine != null) {
						hamMessages.add(strLine);
						strLine = br.readLine();
					}
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	/**
	 * This function read the spam messages from the file path provided on the GUI,
	 * however it has a default file path from the project folder
	 */
	public void readSpamFile() {
		File file = new File(GUI.getTextPathSpam().getText());
		if (file.isFile() && file.getName().endsWith(".log")) {
			try {
				FileInputStream fstream = new FileInputStream(file);

				try (DataInputStream in = new DataInputStream(fstream)) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine = br.readLine();
					while (strLine != null) {
						spamMessages.add(strLine);
						strLine = br.readLine();
					}
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	/**
	 * This function set the GUI, attribute of the class, equal to the received
	 * parameter
	 * 
	 * @param gui
	 */
	public void setGUI(GUI gui) {
		this.GUI = gui;
	}
}
