package Grafica;

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
	 * This method generates the anti-spam filter GUI for testing purposes
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

	/**
	 * This function returns the swing element JTextField containing the path for
	 * the Rules file. By default it point to a file stored locally on the project
	 * folder
	 * 
	 * @return
	 */
	public JTextField getTextPathRules() {
		return textPathRules;
	}

	/**
	 * This function returns the swing element JTextField containing the path for
	 * the ham messages file. By default it point to a file stored locally on the
	 * project folder
	 * 
	 * @return
	 */
	public JTextField getTextPathHam() {
		return textPathHam;
	}

	/**
	 * This function returns the swing element JTextField containing the path for
	 * the spam messages file. By default it point to a file stored locally on the
	 * project folder
	 * 
	 * @return
	 */
	public JTextField getTextPathSpam() {
		return textPathSpam;
	}

	/**
	 * This method adds calls the content generating functions, and adds the first
	 * to the GUI
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
	 * This function return the top panel of the GUI, containing the a file manager
	 * section for testing purposes
	 * 
	 * @return
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

		// Bot�o para carregar os ficheiros
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
	 * This function return the middle panel of the GUI, which provides an interface
	 * for a manual and random configuration of the anti-spam filter
	 * 
	 * @return
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
					saveConfiguration(textPathRules.getText(), modelME);
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
	 * This function return the actual table where the rules, and respective
	 * weights, will be represented in it's parent panel
	 * 
	 * @return
	 */

	private JTable genTableManual() {
		JTable local = new JTable(modelME);
		local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return local;
	}

	/**
	 * This function return the bottom panel of the GUI, which provides an interface
	 * for the automatic configuration of the anti-spam filter
	 * 
	 * @return
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
					saveConfiguration(textPathRules.getText(), modelIN);
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
	 * This function return the actual table where the rules, and respective
	 * weights, will be represented in it's parent panel
	 * 
	 * @return
	 */

	private JTable genTableAuto() {
		JTable local = new JTable(modelIN);
		local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return local;
	}

	/**
	 * This functions reads the goes over the rules files, getting all the names of
	 * the last in order to populate the populate the naming section, on both the
	 * automatic and manual configuration sections. Moreover this function also gets
	 * the weights associated with each rule if available
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

	/**
	 * This function extrapolates over the automatic configuration generated files,
	 * in order to pick and implement best weights game of the anti-spam filter. The
	 * latest affects exclusively the automatic configuration section of the GUI
	 */
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

	/**
	 * This function returns the line of the best configuration available, generated
	 * by the automatic configuration algorithm.
	 * 
	 * @param fileQuality
	 * @return
	 */
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
	 * By being provided a DefaultTableModel, this function goes over the last and
	 * returns the number of false positives as a consequence of the latest
	 * rules-weight configuration
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

	/**
	 * By being provided a DefaultTableModel, this function goes over the last and
	 * returns the number of false negatives as a consequence of the latest
	 * rules-weight configuration
	 * 
	 * @param model
	 * @return
	 */
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

	/**
	 * This function pulls the data out of a given model and writes it on a given
	 * path, in order to allow future use
	 * 
	 * @param data
	 * @param path
	 * @param model
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void saveConfiguration(String path, DefaultTableModel model)
			throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter pw = new PrintWriter(path, "UTF-8");
		for (int i = 0; i < model.getRowCount(); i++) {
			pw.write(model.getValueAt(i, 0) + "\t" + model.getValueAt(i, 1) + "\n");
		}
		pw.close();
	}

	private static final int INDEPENDENT_RUNS = 5;

	/**
	 * This function runs the automatic configuration algorithm, in order to generate
	 * and pick from the top 3 setups generated by last
	 * 
	 * @throws IOException
	 */
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
/**
 * This function calls the GUI constructor and puts it on the screen
 * @param args
 */
	public static void main(String[] args) {
		// main
		new GUI();
	}
}
