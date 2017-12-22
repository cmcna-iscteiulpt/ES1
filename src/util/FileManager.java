package util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Gráfica.GUI;


public class FileManager {
	public static String rulesDefaultLocation = "/Users/ben-hurfidalgo/Dropbox/ISCTE/IGE/3º ano/ES/Project/rules.cf",
			hamDefaultLocation = "/Users/ben-hurfidalgo/Dropbox/ISCTE/IGE/3º ano/ES/Project/ham.log",
			spamDefaultLocation = "/Users/ben-hurfidalgo/Dropbox/ISCTE/IGE/3º ano/ES/Project/spam.log";

	List<String> regras = new ArrayList<String>();
	List<String> hamMessages = new ArrayList<String>();
	List<String> spamMessages = new ArrayList<String>();

	private GUI GUI;

	private static FileManager instance = null;

	protected FileManager() {
		// Exists only to defeat instantiation.
	}

	public static FileManager getInstance() {
		if (instance == null) {
			instance = new FileManager();
		}
		return instance;
	}

	public List<String> getRegras() {
		if (regras.size() == 0) 
			readRulesFile();
		return regras;
	}

	public List<String> getHamMessages() {
		if (hamMessages.size() == 0)
			readHamFile();
		return hamMessages;
	}

	public List<String> getSpamMessages() {
		if (spamMessages.size() == 0)
			readSpamFile();
		return spamMessages;
	}

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
	 * 
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

	public void setGUI(GUI gui) {
		this.GUI = gui;		
	}
}
