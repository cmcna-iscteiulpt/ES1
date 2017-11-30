package antiSpamFilter;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

public class AntiSpamFilterProblem extends AbstractDoubleProblem {

	public AntiSpamFilterProblem() {
	    // 10 variables (anti-spam filter rules) by default 
		  //temos 335 regras no rules.cf **dinamizar
	    this(335);
	  }

	  public AntiSpamFilterProblem(Integer numberOfVariables) {
	    setNumberOfVariables(numberOfVariables);
	    setNumberOfObjectives(2);
	    setName("AntiSpamFilterProblem");

	    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
	    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

	    for (int i = 0; i < getNumberOfVariables(); i++) {
	      lowerLimit.add(-5.0);
	      upperLimit.add(5.0);
	    }

	    setLowerLimit(lowerLimit);
	    setUpperLimit(upperLimit);
	  }

	  public void evaluate(DoubleSolution solution){
		  // vetor de pesos_solution
	    double[] x = new double[getNumberOfVariables()];
	    for (int i = 0; i < solution.getNumberOfVariables(); i++) { 
	      x[i] = solution.getVariableValue(i) ;
	    }

	    double FP = 0.0;
	    //com base nos pesos ir ao spam.log e ham.log calcular o FP e FN
	    //fazer um metodo para calcular o numero de FP e FN
	    //passar rules.cf para um vetor e depois invovar esse vetor no calculo dos FP e FN
	    for (int var = 0; var < solution.getNumberOfVariables() - 1; var++) {
		  FP += Math.abs(x[0]); // Example for testing
	    }
	    
	    double FN = 0.0;
	    for (int var = 0; var < solution.getNumberOfVariables(); var++) {
	    	FN += Math.abs(x[1]); // Example for testing
	    }

	    //quantidade de criterios de avaliacao
	    solution.setObjective(0, FP);
	    solution.setObjective(1, FN);
	  }
	}
