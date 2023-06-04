import java.util.*;

public class Formula {

  private ArrayList<Clause> clauses;
  private PriorityQueue<VSIDS> literalStatistic;
  private HashMap<String, Integer> nonnegativeLiteralsNum;
  private HashMap<String, Integer> negativeLiteralsNum;

  private int subsumedClauses = 0;

  public Formula(String[][] formula) {
    this.clauses = new ArrayList<>();
    for (String[] c : formula) {
      if(checkSubsumtion(c)){
        this.clauses.add(new Clause(new ArrayList<>(Arrays.asList(c))));
      }else{
        this.subsumedClauses += 1;
      }
    }
    calculateLiteralStatistic();
    System.out.printf("Subsumed clauses: %d\n", this.subsumedClauses);
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
      removeClausesContainingLiteral(literal);
      removeLiteralFromClauses("-" + literal);
    } else {
      removeClausesContainingLiteral("-" + literal);
      removeLiteralFromClauses(literal);
    }
    removeDuplicateClauses();
    calculateLiteralStatistic();
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

  public PriorityQueue<VSIDS> getLiteralStatistic() {
    return this.literalStatistic;
  }

  public HashMap<String, Integer> getNonNegativeLiteralsNum(){
    return this.nonnegativeLiteralsNum;
  }

  public HashMap<String, Integer> getNegativeLiteralsNum(){
    return this.negativeLiteralsNum;
  }

  private void calculateLiteralStatistic(){
    nonnegativeLiteralsNum = new HashMap<>();
    negativeLiteralsNum = new HashMap<>();

    for(Clause c: this.clauses){
      for(String literal: c.getLiterals()){
        if(literal.startsWith("-")){
          String lit = literal.replaceFirst("-", "");
          if(this.negativeLiteralsNum.containsKey(lit)){
            this.negativeLiteralsNum.put(lit, this.negativeLiteralsNum.get(lit) + 1);
          }else{
            this.negativeLiteralsNum.put(lit, 1);
          }
        }else{
          if(this.nonnegativeLiteralsNum.containsKey(literal)){
            this.nonnegativeLiteralsNum.put(literal, this.nonnegativeLiteralsNum.get(literal) + 1);
          }else{
            this.nonnegativeLiteralsNum.put(literal, 1);
          }
        }
      }
    }


    this.literalStatistic = new PriorityQueue<>();
    for(String literal: nonnegativeLiteralsNum.keySet()){
      if(this.negativeLiteralsNum.containsKey(literal)) {
        this.literalStatistic.add(new VSIDS(literal, nonnegativeLiteralsNum.get(literal), nonnegativeLiteralsNum.get(literal)));
      }else{
        this.literalStatistic.add(new VSIDS(literal, nonnegativeLiteralsNum.get(literal), 0));
      }
    }
    for(String literal: negativeLiteralsNum.keySet()){
      if(!this.nonnegativeLiteralsNum.containsKey(literal)){
        this.literalStatistic.add(new VSIDS(literal, 0, negativeLiteralsNum.get(literal)));
      }
    }
  }

  private boolean checkSubsumtion(String[] newClause){
    ArrayList<Clause> subsumedClauses = new ArrayList<>();
    for(Clause c: this.clauses) {
      if(c.getLiterals().size() > newClause.length){
        if(checkSubset(new ArrayList<>(Arrays.asList(newClause)), c.getLiterals())){
          subsumedClauses.add(c);
        }
      }else{
        if(checkSubset(c.getLiterals(), new ArrayList<>(Arrays.asList(newClause)))){
          return false;
        }
      }
    }

    this.subsumedClauses += subsumedClauses.size();

    for(Clause c: subsumedClauses){
      this.clauses.remove(c);
    }
    return true;
  }

  // checkSubset1 checks id c1 is a subset of c2
  private boolean checkSubset(ArrayList<String> subset, ArrayList<String> set){
    for(String c : subset){
      if(!set.contains(c)){
        return false;
      }
    }

    return true;
  }
}