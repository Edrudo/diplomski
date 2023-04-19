
import gov.nasa.jpf.annotation.Const;


public class ConstViolation {

  int d;

  @Const
  public void dontDoThis() {
    foo();
  }

  void foo () {
    d = 42;
  }

  public static void main (String[] args){
    ConstViolation t = new ConstViolation();
    t.dontDoThis();
  }
}
