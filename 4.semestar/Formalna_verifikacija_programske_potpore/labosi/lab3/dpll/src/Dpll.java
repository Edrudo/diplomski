import java.util.ArrayList;
import java.util.HashMap;

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

  private static DpllResult solve(Formula formula, HashMap<String, Boolean> variableSetting, int depth) {
    if(formula.getClauses().size() == 0) {
      return new DpllResult(variableSetting);
    }

    if(formula.isOnlyEmptyClauses()) {
      //log(depth,"Only empty clauses! Going up");
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
        // log(depth, "UCP: " + lit + " => " + (rule == 1 ? "true" : "false"));
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
            // log(depth, "PLE: " + lit + " => " + litValue);
            break;
          }
        }
      }
    }

    if(lit == null) {
      lit = literals.get(0);
    }

    if((!ucpFound && !pleFound) || litValue) {
      Formula newFormulaLeft = Formula.copy(formula);
      HashMap<String, Boolean> newSettingsLeft = new HashMap<>(variableSetting);

      newSettingsLeft.put(lit, true);
      newFormulaLeft.set(lit, true);

      DpllResult resultL = solve(newFormulaLeft, newSettingsLeft, depth + 1);

      if (resultL != null) {
        return resultL;
      }
    }

    if((!ucpFound && !pleFound) || !litValue) {
      Formula newFormulaRight = Formula.copy(formula);
      HashMap<String, Boolean> newSettingsR = new HashMap<>(variableSetting);

      newSettingsR.put(lit, false);
      newFormulaRight.set(lit, false);

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
