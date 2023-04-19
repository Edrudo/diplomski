
import gov.nasa.jpf.annotation.*;

class ContractViolationBase {

  @Ensures("Result > 0")
  public int getLoopCount(int c){
    return c -1;
  }
}


@Invariant({"d within 40 +- 5",
            "a > 0"})
public class ContractViolation extends ContractViolationBase {

  double d = 0;
  int a = 0;

  ContractViolation() {
    a = 42;
    d = 42;
    // we don't check invariants until the object is constructed
  }

  @Requires("c > 0")
  @Ensures("Result >= 0")
  public int getLoopCount(int c){
    return c - 3;
  }

  public void doSomething (int n){
    for (int i=0; i<n; i++){
      d += 1.0;
    }
  }
  

  public static void main (String[] args){
    ContractViolation t = new ContractViolation();

    int n = t.getLoopCount(3);  // would violate strengthening base postcondition
    //int n = t.getLoopCount(8);

    t.doSomething(n);
  }
}
