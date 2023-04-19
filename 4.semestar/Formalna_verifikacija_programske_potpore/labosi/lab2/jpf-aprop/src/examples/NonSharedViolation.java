
import gov.nasa.jpf.annotation.NonShared;


public class NonSharedViolation {

  @NonShared
  static class SomeObject implements Runnable {
    int d;
    public void run() {
      d = 42;
    }
    public void doNothing() {
      // nothing, really, so it wouldn't be a detectable race
    }
  }

  public static void main (String[] args){
    SomeObject o = new SomeObject();


    Thread t1 = new Thread(o);
    t1.start();
    try {
      t1.join();
      o.doNothing();   // that's actually fine, it's a clean handover
      } catch (InterruptedException ix) {
      throw new RuntimeException("unexpected interrupt");
    }


    Thread t2 = new Thread(o);
    t2.start();
    o.doNothing(); // t2 might be still running, so that's bad
  }

}
