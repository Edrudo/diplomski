import java.util.ArrayList;
import java.util.HashMap;

public class Dpll {
  public static DpllResult solve(Formula formula) {
    System.out.println("CNF formula: " + formula.toStringCnf());
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
      // log(depth,"Only empty clauses! Going up");
      return null;
    }

    ArrayList<String> literals = formula.getAllLiterals();

    // First: Check if UCP is usable
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
      // Only look for PLE if UCP is not usable
      // since UCP has a higher priority than UCP
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

    // Don't do this if UCP result was 2
    // since that would mean we skip the true case and set x := false
    if((!ucpFound && !pleFound) || litValue) {
      // Left part ( x:= true)
      Formula newFormulaLeft = Formula.copy(formula);
      HashMap<String, Boolean> newSettingsLeft = new HashMap<>(variableSetting);

      StringBuilder sb = new StringBuilder();
      sb.append("[L] ").append(lit).append(":= true (");
      for (String key : newSettingsLeft.keySet()) {
        sb.append(" ").append(key).append("=").append(newSettingsLeft.get(key));
      }
      sb.append(" )");
      log(depth, sb.toString());
      newSettingsLeft.put(lit, true);
      newFormulaLeft.set(lit, true);

      DpllResult resultL = solve(newFormulaLeft, newSettingsLeft, depth + 1);

      if (resultL != null) {
        return resultL;
      }
    }

    // Don't do this if UCP result was 1
    // since that would mean we skip the false case and set x := true
    if((!ucpFound && !pleFound) || !litValue) {
      // Right part ( x:= false )
      Formula newFormulaRight = Formula.copy(formula);
      HashMap<String, Boolean> newSettingsR = new HashMap<>(variableSetting);

      StringBuilder sb = new StringBuilder();
      sb.append("[R] ").append(lit).append(":= false (");
      for (String key : newSettingsR.keySet()) {
        sb.append(" ").append(key).append("=").append(newSettingsR.get(key));
      }
      sb.append(" )");
      log(depth, sb.toString());
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
