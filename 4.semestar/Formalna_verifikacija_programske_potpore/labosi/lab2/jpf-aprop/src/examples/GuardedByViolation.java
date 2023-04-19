
import javax.annotation.concurrent.GuardedBy;


public class GuardedByViolation {

  @GuardedBy("this")
  int d;

  @GuardedBy("getLock()")
  int myPrecious;

  Object lock = new Object();

  Object getLock() {
    return lock;
  }

  public synchronized void initialize () {
    d = 42;
  }

  public void doSomethingPrecious(Object lock) {
    synchronized (lock) {
      myPrecious = 42; // fooled you! wrong object
    }
  }

  public static void main (String[] args){
    GuardedByViolation t1 = new GuardedByViolation();
    GuardedByViolation t2 = new GuardedByViolation();

    t1.initialize();

    t1.doSomethingPrecious( t2.getLock());
  }
}
