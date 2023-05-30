import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

public class Formula {

  private ArrayList<Clause> clauses;

  public Formula(String[][] formula) {
    ArrayList<Clause> clauses = new ArrayList<>();
    for (String[] strings : formula) {
      clauses.add(new Clause(new ArrayList<>(Arrays.asList(strings))));
    }
    this.clauses = clauses;
  }

  public Formula(Formula other) {
    this.clauses = new ArrayList<>();
    this.clauses.addAll(other.getClauses());
  }

  public boolean containsLiteral(String literal) {
    for(Clause clause : clauses) {
      if(clause.containsLiteral(literal)) {
        return true;
      }
    }
    return false;
  }

  public static Formula copy(Formula f) {
    ArrayList<Clause> newClauses = new ArrayList<>();
    for(Clause c : f.getClauses()) {
      ArrayList<String> literals = new ArrayList<>(c.getLiterals());
      Clause cNew = new Clause(literals);
      newClauses.add(cNew);
    }
    return new Formula(newClauses);
  }

  public boolean isOnlyEmptyClauses() {
    for(Clause clause : clauses) {
      if(clause.containsLiterals()) {
        return false;
      }
    }
    return true;
  }

  public void set(String literal, boolean value) {
    if(value) {
      // x := true
      removeClausesContainingLiteral(literal);
      removeLiteralFromClauses("-" + literal);
    } else {
      // x := false
      removeClausesContainingLiteral("-" + literal);
      removeLiteralFromClauses(literal);
    }
    removeDuplicateClauses();
  }

  private void removeDuplicateClauses() {
   ArrayList<Clause> noDupClauses = new ArrayList<>();

   for(Clause c : this.clauses) {
     if(!noDupClauses.contains(c)) {
      noDupClauses.add(c);
     }
   }

   this.clauses = noDupClauses;
  }

  public void removeLiteralFromClauses(String literal) {
    for(Clause c : clauses) {
      if(c.containsLiteral(literal)) {
        c.removeLiteral(literal);
      }
    }
  }

  public void removeClausesContainingLiteral(String literal) {
    ArrayList<Clause> clausesWithoutLiteral = new ArrayList<>();

    for(Clause c : this.clauses) {
      if(!c.containsLiteral(literal)) {
        clausesWithoutLiteral.add(c);
      }
    }

    this.clauses = clausesWithoutLiteral;
  }

  public ArrayList<String> getAllLiterals() {
    LinkedHashSet<String> set = new LinkedHashSet<>();
    for(Clause c : clauses) {
      for(String lit : c.getLiterals()) {
        set.add(lit.startsWith("-") ? lit.replaceFirst("-","") : lit);
      }
    }
    ArrayList<String> literals = new ArrayList<>(set);
    Collections.sort(literals);
    return literals;
  }

  public Formula(ArrayList<Clause> clauses) {
    this.clauses = clauses;
  }

  /**
   * Returns the formula represented by a set of clauses
   * @return
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{ ");
    for(Clause clause : clauses) {
      sb.append(clause.toString());
      if(clauses.indexOf(clause) < clauses.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append(" }");

    return sb.toString();
  }

  public String toStringCnf() {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < this.clauses.size() - 1; i++) {
      sb.append(this.clauses.get(i).toStringDisjunction());
      sb.append(" ∧ ");
    }

    sb.append(this.clauses.get(this.clauses.size() - 1).toStringDisjunction());

    return sb.toString();
  }

  public void removeClause(Clause clause) {
    this.clauses.remove(clause);
  }

  public ArrayList<Clause> getClauses() {
    return this.clauses;
  }

}
