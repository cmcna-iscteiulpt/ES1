package Gráfica;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoSetAndFrontFromDoubleSolutions;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import antiSpamFilter.AntiSpamFilterProblem;
import util.FileManager;

/**
 * 
 * @author Ruben Beirao
 * @author Tiago Santos
 * @author Ben-Hur Fidalgo
 * 
 * @since 17-10-2017
 */

public class GUI {

	private JFrame frame = new JFrame("Projeto_ES1 - Grupo 61");
	private JPanel panelUp = new JPanel();
	private JPanel panelMedium = new JPanel();
	private JPanel panelDown = new JPanel();
	private JTextField textPathRules = new JTextField(FileManager.rulesDefaultLocation);
	private JTextField textPathHam = new JTextField(FileManager.hamDefaultLocation);
	private JTextField textPathSpam = new JTextField(FileManager.spamDefaultLocation);
	FileManager fileManager = FileManager.getInstance();
	String[] colunas = { "Regra", "Peso" };
	String[][] data = {};
	DefaultTableModel modelME = new DefaultTableModel(data, colunas);
	DefaultTableModel modelIN = new DefaultTableModel(data, colunas);

	/**
	 * This method creates the GUI after running all methods
	 */

	public GUI() {
		frame.setLayout(new GridLayout(3, 1));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addFrameContent();
		// frame.setSize(620, 500);
		frame.pack();
		frame.setVisible(true);
		fileManager.setGUI(this);
	}

	public JTextField getTextPathRules() {
		return textPathRules;
	}

	public JTextField getTextPathHam() {
		return textPathHam;
	}

	public JTextField getTextPathSpam() {
		return textPathSpam;
	}

	/**
	 * This method adds all the contents that will appear in the frame
	 */

	private void addFrameContent() {
		// gerar painel superior
		panelUp = painelSuperior();
		// adicionar painel superior
		frame.add(panelUp);
		// gerar painel mediano
		panelMedium = generatePainelMed();
		// adicionar painel mediano
		frame.add(panelMedium);
		// gerar painel inferior
		panelDown = generatePainelInferior();
		// adicionar painel inferior
		frame.add(panelDown);
	}

	/**
	 * This method creates the upper section of the GUI
	 * It's divided into three JLabels witch receives the files paths of the rules, ham addresses and the spam addresses
	 * finally creates a button to load the files specified by the user
	 */
	private JPanel painelSuperior() {
		// labels estaticas
		JLabel rules_path = new JLabel("rules.cf path");
		JLabel ham_path = new JLabel("ham.log path");
		JLabel spam_path = new JLabel("spam.log path");
		JButton carregar_ficheiros = new JButton("Carregar ficheiros");

		// mini paineis para cada label + caminho (vazio)
		// linha do rules.cf
		JPanel panelRules = new JPanel(new GridLayout(1, 2));
		panelRules.add(rules_path);
		panelRules.add(textPathRules);

		// linha do ham.log
		JPanel panelHam = new JPanel(new GridLayout(1, 2));
		panelHam.add(ham_path);
		panelHam.add(textPathHam);

		// linha do spam.log
		JPanel panelSpam = new JPanel(new GridLayout(1, 2));
		panelSpam.add(spam_path);
		panelSpam.add(textPathSpam);

		// Botï¿½o para carregar os ficheiros
		carregar_ficheiros.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				readRulesFile();
				fileManager.readHamFile();
				fileManager.readSpamFile();
			}

		});

		// criar painelLocal a ser devolvido com 2 colunas e 3 linhas
		JPanel local = new JPanel();
		local.setLayout(new GridLayout(4, 1));
		local.add(panelRules);
		local.add(panelHam);
		local.add(panelSpam);
		local.add(carregar_ficheiros);

		return local;
	}

	/**
	 * This method creates the middle section of the GUI
	 * It's divided into two panels: the left panel have a table with both the existing rules and their weigh of evaluation
	 * the right panel it's composed of three buttons: the first generates weighs to be given to the each rule; the second evaluate the number of false-positives(FP) and false-negatives(FN); the third saves the actual configuration that is in use.
	 */

	private JPanel generatePainelMed() {

		JPanel local = new JPanel(new GridLayout(1, 2));
		JPanel panelLeft = new JPanel(new BorderLayout());
		JPanel panelRight = new JPanel(new GridLayout(3, 1));
		JPanel panelFalsos = new JPanel();
		JTextField fpositive = new JTextField("FP");
		JTextField fnegative = new JTextField("FN");
		fpositive.setEditable(false);
		fnegative.setEditable(false);

		// pLeft
		JTable jTableManual = genTableManual();
		panelLeft.add(new JScrollPane(jTableManual), BorderLayout.CENTER);
		panelFalsos.add(fpositive);
		panelFalsos.add(fnegative);
		panelLeft.add(panelFalsos, BorderLayout.SOUTH);

		// pRight
		JButton button_auto_config = new JButton("Gerar uma configuracao automatica");
		button_auto_config.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<String> regras = fileManager.getRegras();
				for (int i = 0; i < regras.size(); i++) {
					double p = ThreadLocalRandom.current().nextDouble(-5, 5);
					p = Math.round(p * 10) / 10D;
					modelME.setValueAt(p, i, 1);
				}
			}
		});
		JButton button_aval_calc = new JButton("Avaliar e calcular FP e FN");
		button_aval_calc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fpositive.setText(String.valueOf(calculateFalsePositives(modelME)));
				fnegative.setText(String.valueOf(calculateFalseNegatives(modelME)));
			}
		});
		JButton button_save_config = new JButton("Guardar a configuracao");
		button_save_config.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveConfiguration(data, textPathRules.getText(), modelME);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		});
		panelRight.add(button_auto_config);
		panelRight.add(button_aval_calc);
		panelRight.add(button_save_config);

		// adicionar os paineis interiores ao painel medio
		local.add(panelLeft);
		local.add(panelRight);
		return local;
	}

	/**
	 * This method generates the table design for the manual operation.
	 */

	private JTable genTableManual() {
		JTable local = new JTable(modelME);
		local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return local;
	}

	/**
	 * this method creates the inferior panel witch will be the equals to the  middle panel with the exception of having one less button and the methods in those two buttons being different from the ones in the middle panel.
	 * In the same way, it's divided into two panels: the left panel and the right panel.
	 * the left panel also have a table in the same way as the middle panel.
	 * the right panel only have the random weigh generation but this method instantly calculates the number of FP and FN with the new generated weigh, and the save configuration button.
	 */

	private JPanel generatePainelInferior() {
		JPanel local = new JPanel(new GridLayout(1, 2));
		JPanel panelLeft = new JPanel(new BorderLayout());
		JPanel panelRight = new JPanel(new GridLayout(3, 1));
		JPanel panelFalsos = new JPanel();
		JTextField fpositive = new JTextField("FP: ");
		JTextField fnegative = new JTextField("FN: ");
		fpositive.setEditable(false);
		fnegative.setEditable(false);

		// pLeft
		JTable jTableManual = genTableAuto();
		panelLeft.add(new JScrollPane(jTableManual));
		panelFalsos.add(fpositive);
		panelFalsos.add(fnegative);
		panelLeft.add(panelFalsos, BorderLayout.SOUTH);

		// pRight
		JButton button_auto_config = new JButton("Gerar uma configuracao automatica");

		button_auto_config.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					automaticConfiguration();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				readRulesFileAuto();
				fpositive.setText(String.valueOf(calculateFalsePositives(modelIN)));
				fnegative.setText(String.valueOf(calculateFalseNegatives(modelIN)));
			}
		});

		JButton button_save_config = new JButton("Guardar a configuracao");
		button_save_config.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveConfiguration(data, textPathRules.getText(), modelIN);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
			}
		});
		panelRight.add(button_auto_config);
		panelRight.add(button_save_config);

		// adicionar os paineis interiores ao painel medio
		local.add(panelLeft);
		local.add(panelRight);
		return local;
	}

	/**
	 * this method generates the table used in the automated version
	 */

	private JTable genTableAuto() {
		JTable local = new JTable(modelIN);
		local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return local;
	}

	/**
	 * This method is used to read the file containing all the rules in use
	 * It verifies if the loaded file is a .cf file and, if it is, it 
	 */

	@SuppressWarnings("static-access")
	public void readRulesFile() {
		File file;
		if (textPathRules.getText().length() > 1)
			file = new File(textPathRules.getText());
		else
			file = new File(textPathRules.getText());

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
						modelME.addRow(regra);
						modelIN.addRow(regra);
					}
				}

			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	public void readRulesFileAuto() {
		String pathQuality = "experimentBaseDirectory/RESULTADOS/AntiSpamFilterProblem.rf";
		String pathData = "experimentBaseDirectory/RESULTADOS/AntiSpamFilterProblem.rs";
		File fileQuality = new File(pathQuality);
		int lineLeastFP = findLowestFP(fileQuality);
		File fileData = new File(pathData);
		if (fileData.isFile() && fileData.getName().endsWith(".rs")) {
			try {
				FileInputStream fstream = new FileInputStream(fileData);
				try (DataInputStream in = new DataInputStream(fstream)) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					int k = 0;
					boolean done = false;
					while ((strLine = br.readLine()) != null && !done) {
						if (k == lineLeastFP) {
							String[] weights = strLine.split(" ");
							for (int i = 0; i < weights.length; i++) {
								String info = weights[i];
								double value = Double.parseDouble(info);
								// Print the content on the console
								modelIN.setValueAt(value, i, 1);
							}
							done = true;
						}
						k++;
					}
				}
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	private int findLowestFP(File fileQuality) {
		int lineBest = -1, lineCounter = 0;
		double lowestValue = -1;
		if (fileQuality.getName().endsWith(".rf")) {
			try {
				FileInputStream fstream = new FileInputStream(fileQuality);
				try (DataInputStream in = new DataInputStream(fstream)) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					while ((strLine = br.readLine()) != null) {
						String[] info = strLine.split(" ");
						if (lowestValue == -1) {
							lowestValue = Double.parseDouble(info[0]);
							lineBest = lineCounter;
						} else if (lowestValue > Double.parseDouble(info[0])) {
							lowestValue = Double.parseDouble(info[0]);
							lineBest = lineCounter;
						}
						lineCounter++;
					}
				}
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
		return lineBest;
	}

	/**
	 * 
	 * @param model 
	 * @return
	 */

	public int calculateFalsePositives(DefaultTableModel model) {
		// FALSE POS, hamm into spam
		int falsePositives = 0;
		double valor = 0;
		String[] linha;
		List<String> hamMessages = fileManager.getHamMessages();
		for (int i = 0; i < hamMessages.size(); i++) {
			valor = 0;
			linha = hamMessages.get(i).split("\t");
			// mensagem + REGRAS
			// 0028674f122eeb4cd901867d74f5676c85809 +BAYES_00+ ...
			for (int j = 1; j < linha.length; j++) {
				for (int k = 0; k < model.getRowCount(); k++) {
					if (model.getValueAt(k, 0).equals(linha[j])) {
						double aux;
						if (model.getValueAt(k, 1) instanceof String)
							aux = Double.parseDouble((String) model.getValueAt(k, 1));
						else
							aux = (double) model.getValueAt(k, 1);
						valor += aux;
					}
				}
			}
			if (valor > 5) {
				falsePositives++;
			}
		}
		return falsePositives;

	}

	public int calculateFalseNegatives(DefaultTableModel model) {
		// FALSE POS, hamm into spam
		int falseNegatives = 0;
		double valor = 0;
		String[] linha;
		List<String> spamMessages = fileManager.getSpamMessages();
		for (int i = 0; i < spamMessages.size(); i++) {
			valor = 0;
			linha = spamMessages.get(i).split("\t");
			// mensagem + REGRAS
			// 0028674f122eeb4cd901867d74f5676c85809 +BAYES_00+ ...
			for (int j = 1; j < linha.length; j++) {
				for (int k = 0; k < model.getRowCount(); k++) {
					if (model.getValueAt(k, 0).equals(linha[j])) {
						//
						double aux;
						if (model.getValueAt(k, 1) instanceof String)
							aux = Double.parseDouble((String) model.getValueAt(k, 1));
						else
							aux = (double) model.getValueAt(k, 1);
						//
						valor += aux;
					}
				}
			}
			if (valor < 5) {
				falseNegatives++;
			}
		}
		return falseNegatives;

	}

	public void saveConfiguration(Object[][] data, String path, DefaultTableModel model)
			throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter pw = new PrintWriter(path, "UTF-8");
		for (int i = 0; i < model.getRowCount(); i++) {
			pw.write(model.getValueAt(i, 0) + "\t" + model.getValueAt(i, 1) + "\n");
		}
		pw.close();
	}

	public void writeFalseAuto() {
		// modelIN.setValueAt(p, i, 1);

	}

	private static final int INDEPENDENT_RUNS = 5;

	private void automaticConfiguration() throws IOException {
		String experimentBaseDirectory = "experimentBaseDirectory";

		List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
		problemList.add(new ExperimentProblem<>(new AntiSpamFilterProblem(fileManager.getRegras().size())));

		List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList = configureAlgorithmList(
				problemList);

		Experiment<DoubleSolution, List<DoubleSolution>> experiment = new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>(
				"AntiSpamStudy").setAlgorithmList(algorithmList).setProblemList(problemList)
						.setExperimentBaseDirectory(experimentBaseDirectory).setOutputParetoFrontFileName("FUN")
						.setOutputParetoSetFileName("VAR")
						.setReferenceFrontDirectory(experimentBaseDirectory + "/RESULTADOS")
						.setIndicatorList(Arrays.asList(new PISAHypervolume<DoubleSolution>()))
						.setIndependentRuns(INDEPENDENT_RUNS).setNumberOfCores(8).build();

		new ExecuteAlgorithms<>(experiment).run();
		new GenerateReferenceParetoSetAndFrontFromDoubleSolutions(experiment).run();
		new ComputeQualityIndicators<>(experiment).run();
		new GenerateLatexTablesWithStatistics(experiment).run();
		new GenerateBoxplotsWithR<>(experiment).setRows(1).setColumns(1).run();

	}

	List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
			List<ExperimentProblem<DoubleSolution>> problemList) {
		List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();

		for (int i = 0; i < problemList.size(); i++) {
			Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problemList.get(i).getProblem(),
					new SBXCrossover(1.0, 5),
					new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 10.0))
							.setMaxEvaluations(25000).setPopulationSize(100).build();
			algorithms.add(new ExperimentAlgorithm<>(algorithm, "NSGAII", problemList.get(i).getTag()));
		}

		return algorithms;
	}

	public static void main(String[] args) {
		// main
		new GUI();
		String[] params = new String [2];
	    String[] envp = new String [1];
	    params[0] = "C:\\Program Files\\R\\R-3.4.1\\bin\\x64\\Rscript.exe";
	    params[1] = "C:\\Users\\vbasto\\git\\ES1\\experimentBaseDirectory\\AntiSpamStudy\\R\\HV.Boxplot.R";
	    //Runtime.getRuntime().exec(params);
	    //Process p = Runtime.getRuntime().exec("cmd /C dir");
	      //Process p = Runtime.getRuntime().exec(params);
	    envp[0] = "Path=C:\\Program Files\\R\\R-3.4.1\\bin\\x64";
	    try {
			Process p = Runtime.getRuntime().exec(params, envp, new File("C:\\Users\\vbasto\\git\\ES1\\experimentBaseDirectory\\AntiSpamStudy\\R"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
