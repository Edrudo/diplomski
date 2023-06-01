import java.util.ArrayList;
import java.util.Arrays;

public class Clause {
  private ArrayList<String> literals;
  public Clause(ArrayList<String> literals) {
    this.literals = literals;
  }
  public Clause(String[] literals) {
    this.literals = new ArrayList<>(Arrays.asList(literals));
  }
  public boolean containsLiterals() {
    return literals.size() != 0;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof Clause)){
      //System.out.println(toString() + " cmp " + obj + "-> false");
      return false;
    }

    Clause other = (Clause) obj;
    if(other.literals.size() != literals.size()) {
      //System.out.println(toString() + " cmp " + obj + "-> false");
      return false;
    }

    if(literals.size() == 0) {
      //System.out.println(toString() + " cmp " + other + "-> true");
      return true;
    }

    for(String lit : literals) {
      if(!other.containsLiteral(lit)) {
        //System.out.println(toString() + " cmp " + other + "-> false");
        return false;
      }
    }
    //System.out.println(toString() + " cmp " + other + "-> true");
    return true;
}

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for(int i = 0; i < this.literals.size() - 1; i++) {
      sb.append(this.literals.get(i)).append(", ");
    }
    sb.append(this.literals.get(this.literals.size() - 1)).append("}");
    return sb.toString();
  }

  public String toStringDisjunction() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for(int i = 0; i < this.literals.size() - 1; i++) {
      sb.append(this.literals.get(i)).append(" ∨ ");
    }
    sb.append(this.literals.get(this.literals.size() - 1)).append(")");
    return sb.toString();
  }

  public boolean containsLiteral(String literal) {
    return literals.contains(literal);
  }

  public void removeLiteral(String literal) {
    literals.remove(literal);
  }

  public ArrayList<String> getLiterals() {
    return literals;
  }

}
