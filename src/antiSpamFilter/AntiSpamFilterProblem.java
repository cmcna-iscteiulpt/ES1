package antiSpamFilter;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import util.FileManager;

@SuppressWarnings("serial")
public class AntiSpamFilterProblem extends AbstractDoubleProblem {

	public AntiSpamFilterProblem() {
		// 10 variables (anti-spam filter rules) by default
		// temos 335 regras no rules.cf **dinamizar
		this(335);
	}

	public AntiSpamFilterProblem(Integer numberOfVariables) {
		setNumberOfVariables(numberOfVariables);
		setNumberOfObjectives(2);
		setName("AntiSpamFilterProblem");

		List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
		List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

		for (int i = 0; i < getNumberOfVariables(); i++) {
			lowerLimit.add(-5.0);
			upperLimit.add(5.0);
		}

		setLowerLimit(lowerLimit);
		setUpperLimit(upperLimit);
	}

	public void evaluate(DoubleSolution solution) {
		// vetor de pesos_solution
		double[] x = new double[getNumberOfVariables()];
		for (int i = 0; i < solution.getNumberOfVariables(); i++) {
			x[i] = solution.getVariableValue(i);
		}

		double FP = calculateFalsePositivesAuto(x);
		// com base nos pesos ir ao spam.log e ham.log calcular o FP e FN
		// fazer um metodo para calcular o numero de FP e FN
		// passar rules.cf para um vetor e depois invovar esse vetor no calculo dos FP e
		// FN
		for (int var = 0; var < solution.getNumberOfVariables() - 1; var++) {
			FP += Math.abs(x[0]); // Example for testing
		}

		double FN = calculateFalseNegativesAuto(x);
		for (int var = 0; var < solution.getNumberOfVariables(); var++) {
			FN += Math.abs(x[1]); // Example for testing
		}

		// quantidade de criterios de avaliacao
		solution.setObjective(0, FP);
		solution.setObjective(1, FN);
	}

	private int getPosicaoRegra(String regra, List<String> regraS) {
		for (int i = 0; i < regraS.size(); i++)
			if (regra.equals(regraS.get(i))) {
				return i;
			}
		return -1;
		// throw new InvalidParameterException();
	}

	public double calculateFalsePositivesAuto(double[] x) {
		// FALSE POS, hamm into spam
		int falsePositives = 0;
		double valor = 0;
		String[] linha;

		List<String> hamS = FileManager.getInstance().getHamMessages();
		List<String> regraS = FileManager.getInstance().getRegras();

		for (int i = 0; i < hamS.size(); i++) {
			valor = 0;
			linha = hamS.get(i).split("\t");
			// percorrer as regras relativas a mensagem do ciclo
			// (e.g.) 0028674f122eeb4cd901867d74f5676c85809 +BAYES_00+ ...
			for (int j = 1; j < linha.length; j++) {
				// encontrar posicao da regra no ficheiro regra
				int posicaoRegra = getPosicaoRegra(linha[j], regraS);
				if (posicaoRegra > -1) {
					// usar valor da posicao para ir buscar o valor da regra na estrutura de dados
					double pesoDaRegra = x[posicaoRegra];
					// mete o valor desta regra no aux
					valor += pesoDaRegra;
				}
			}
			if (valor >= 5) {
				falsePositives++;
			}
		}
		return falsePositives;
	}
	
	public double calculateFalseNegativesAuto(double[] x) {
		// FALSE NEG, Spam into Ham
		int falsePositives = 0;
		double valor = 0;
		String[] linha;

		List<String> spamS = FileManager.getInstance().getSpamMessages();
		List<String> regraS = FileManager.getInstance().getRegras();

		for (int i = 0; i < spamS.size(); i++) {
			valor = 0;
			linha = spamS.get(i).split("\t");
			// percorrer as regras relativas a mensagem do ciclo
			// (e.g.) 0028674f122eeb4cd901867d74f5676c85809 +BAYES_00+ ...
			for (int j = 1; j < linha.length; j++) {
				// encontrar posicao da regra no ficheiro regra
				int posicaoRegra = getPosicaoRegra(linha[j], regraS);
				if (posicaoRegra > -1) {
					// usar valor da posicao para ir buscar o valor da regra na estrutura de dados
					double pesoDaRegra = x[posicaoRegra];
					// mete o valor desta regra no aux
					valor += pesoDaRegra;
				}
			}
			if (valor < 5) {
				falsePositives++;
			}
		}
		return falsePositives;
	}
}
