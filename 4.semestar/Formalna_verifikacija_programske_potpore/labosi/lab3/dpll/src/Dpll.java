import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Dpll {
  public static DpllResult solve(Formula formula) {
    final long timeStart = System.currentTimeMillis();
    HashMap<String, Boolean> settings = new HashMap<>();
    DpllResult result = solve(formula, settings, 0);
    if(result != null) {
      result.setTimeTaken(System.currentTimeMillis()-timeStart);
    }
    return result;
  }

  private static DpllResult solve(Formula formula, HashMap<String, Boolean> literalSettings, int depth) {
    if(formula.getClauses().size() == 0) {
      return new DpllResult(literalSettings);
    }

    if(formula.isOnlyEmptyClauses()) {
      return null;
    }

    ArrayList<String> literals = formula.getAllLiterals();

    String lit = null;
    boolean litValue = false;
    boolean ucpFound = false;
    for(Clause clause : formula.getClauses()) {
      if(clause.getLiterals().size() == 1) {
        ucpFound = true;
        lit = clause.getLiterals().get(0);
        if(lit.startsWith("-")) {
          lit = lit.replaceFirst("-","");
        } else {
          litValue = true;
        }
        break;
      }
    }

    boolean pleFound = false;
    if(!ucpFound) {
      for(Clause c : formula.getClauses()) {
        for(String literal : c.getLiterals()) {
          String oppositeLiteral = literal.startsWith("-") ? literal.replaceFirst("-","") : "-" + literal;
          if(!formula.containsLiteral(oppositeLiteral)) {
            pleFound = true;
            if(literal.startsWith("-")) {
              lit = literal.replaceFirst("-","");
            } else {
              litValue = true;
              lit = literal;
            }
            break;
          }
        }
      }
    }

    if((ucpFound || pleFound) && litValue) {
      Formula newFormulaLeft = Formula.copy(formula);
      HashMap<String, Boolean> newSettingsLeft = new HashMap<>(literalSettings);

      newSettingsLeft.put(lit, true);
      newFormulaLeft.set(lit, true);

      DpllResult resultL = solve(newFormulaLeft, newSettingsLeft, depth + 1);

      if (resultL != null) {
        return resultL;
      }
    }

    if((ucpFound || pleFound) && !litValue) {
      Formula newFormulaRight = Formula.copy(formula);
      HashMap<String, Boolean> newSettingsR = new HashMap<>(literalSettings);

      newSettingsR.put(lit, false);
      newFormulaRight.set(lit, false);

      return solve(newFormulaRight, newSettingsR, depth + 1);
    }

    // VSIDS
    if(!ucpFound && !pleFound) {
      VSIDS topElement  = formula.getLiteralStatistic().poll();
      lit = topElement.literal;
      if(topElement.nonNegativeNum > topElement.negativeNum){
        litValue = true;
      }else{
        litValue = false;
      }

      // first direction
      Formula newFormulaLeft = Formula.copy(formula);
      HashMap<String, Boolean> newSettingsLeft = new HashMap<>(literalSettings);

      newSettingsLeft.put(lit, litValue);
      newFormulaLeft.set(lit, litValue);

      DpllResult resultL = solve(newFormulaLeft, newSettingsLeft, depth + 1);

      if (resultL != null) {
        return resultL;
      }

      // second direction
      litValue = !litValue;
      Formula newFormulaRight = Formula.copy(formula);
      HashMap<String, Boolean> newSettingsR = new HashMap<>(literalSettings);

      newSettingsR.put(lit, litValue);
      newFormulaRight.set(lit, litValue);

      return solve(newFormulaRight, newSettingsR, depth + 1);
    }

    return null;
  }

  private static void log(int tabs, String msg) {
    for(int i = 0; i < tabs; i++) {
      System.out.print("\t");
    }
    System.out.println(msg.replaceAll("-", "¬"));
  }
}
