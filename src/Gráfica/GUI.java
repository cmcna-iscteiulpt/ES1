package Gráfica;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

public class GUI {

	private JFrame frame;
	private JPanel panel_u;
	private JPanel panel_m;
	private JPanel panel_d;
	private JTextField text_caminho_r = new JTextField();
	private JTextField text_caminho_h = new JTextField();
	private JTextField text_caminho_s = new JTextField();

	public GUI() {
		frame = new JFrame("Projeto_ES1 - Grupo 61");
		frame.setLayout(new GridLayout(3, 1));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addFrameContent();
		// frame.setSize(620, 500);
		frame.pack();
		frame.setVisible(true);
	}

	private void addFrameContent() {
		// gerar painel superior
		panel_u = painelSuperior();
		// adicionar painel superior
		frame.add(panel_u);
		// gerar painel mediano
		panel_m = painelMed();
		// adicionar painel mediano
		frame.add(panel_m);
		// gerar painel inferior
		panel_d = painelInferior();
		// adicionar painel inferior
		frame.add(panel_d);
	}

	private JPanel painelSuperior() {
		// labels estaticas
		JLabel rules = new JLabel("rules.cf");
		JLabel ham = new JLabel("ham.log");
		JLabel spam = new JLabel("spam.log");

		// mini paineis para cada label + caminho (vazio)
		// linha do rules.cf
		JPanel panelR = new JPanel(new GridLayout(1, 2));
		panelR.add(rules);

		panelR.add(text_caminho_r);
		// linha do ham.log
		JPanel panelH = new JPanel(new GridLayout(1, 2));
		panelH.add(ham);
		panelH.add(text_caminho_h);
		// linha do spam.log
		JPanel panelS = new JPanel(new GridLayout(1, 2));
		panelS.add(spam);
		panelS.add(text_caminho_s);

		// criar painelLocal a ser devolvido com 2 colunas e 3 linhas
		JPanel local = new JPanel();
		local.setLayout(new GridLayout(3, 1));
		local.add(panelR);
		local.add(panelH);
		local.add(panelS);

		return local;
	}

	private JPanel painelMed() {

		JPanel local = new JPanel(new GridLayout(1, 2));
		JPanel panelLeft = new JPanel(new BorderLayout());
		JPanel panelRight = new JPanel(new GridLayout(3, 1));
		JPanel panelFalsos = new JPanel();
		JTextField fpositive = new JTextField("FP: ");
		JTextField fnegative = new JTextField("FN: ");

		// pLeft
		JTable jTableManual = genTableManual();
		panelLeft.add(new JScrollPane(jTableManual), BorderLayout.CENTER);
		panelFalsos.add(fpositive);
		panelFalsos.add(fnegative);
		panelLeft.add(panelFalsos, BorderLayout.SOUTH);

		// pRight
		JButton button_auto_config = new JButton("Gerar uma configuração automática");
		JButton button_aval_calc = new JButton("Avaliar e calcular FP e FN");
		JButton button_save_config = new JButton("Guardar a configuração");
		panelRight.add(button_auto_config);
		panelRight.add(button_aval_calc);
		panelRight.add(button_save_config);

		// adicionar os paineis interiores ao painel médio
		local.add(panelLeft);
		local.add(panelRight);
		return local;
	}

	private JTable genTableManual() {
		/*
		 * Then the Table is constructed using these data and columnNames: JTable table
		 * = new JTable(data, columnNames); There are two JTable constructors that
		 * directly accept data (SimpleTableDemo uses the first): JTable(Object[][]
		 * rowData, Object[] columnNames) JTable(Vector rowData, Vector columnNames)
		 */
		String[] nomeColunas = { "Regra", "Peso" };
		Object data[][] = { { "Regra1", "Peso1" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" },
				{ "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" },
				{ "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" },
				{ "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra7", "Peso3" } };
		JTable local = new JTable(data, nomeColunas);
		local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return local;
	}
	
	private JPanel painelInferior() {
		JPanel local = new JPanel(new GridLayout(1, 2));
		JPanel panelLeft = new JPanel(new BorderLayout());
		JPanel panelRight = new JPanel(new GridLayout(3, 1));

		// pLeft
		JTable jTableManual = genTableAuto();
		panelLeft.add(new JScrollPane(jTableManual));

		// pRight
		JButton button_auto_config = new JButton("Gerar uma configuração automática");
		JButton button_save_config = new JButton("Guardar a configuração");
		panelRight.add(button_auto_config);
		panelRight.add(button_save_config);

		// adicionar os paineis interiores ao painel médio
		local.add(panelLeft);
		local.add(panelRight);
		return local;
	}

	private JTable genTableAuto() {
		String[] nomeColunas = { "Regra", "Peso" };
		Object data[][] = { { "Regra1", "Peso1" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" },
				{ "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" },
				{ "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" },
				{ "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra7", "Peso3" } };
		JTable local = new JTable(data, nomeColunas);
		local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return local;
	}

	public static void main(String[] args) {
		new GUI();
	}
}
