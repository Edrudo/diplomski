
import javax.annotation.Nonnull;


public class NonnullViolation {

  @Nonnull String id;

  NonnullViolation (String s){
    id = s.toUpperCase();
  }

  @Nonnull
  Object giveMeSomeObject() {
    return null;
  }

  void doSomethingUnsuspicious(String s) {
    id = s;
  }

  int computeSomething (@Nonnull String s){
    return s.length();
  }

  public static void main (String[] args) {
    NonnullViolation t = new NonnullViolation("a");

    // that would be simple
    //int x = t.giveMeSomeObject().hashCode();

    // that's less simple
    //t.doSomethingUnsuspicious(null);

    int len = t.computeSomething(null);
  }
}
