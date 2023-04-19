package gov.nasa.jpf.aprop.predicate;

import java.util.ArrayList;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.vm.VM;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.aprop.ContractException;
import gov.nasa.jpf.aprop.NativePredicate;
import gov.nasa.jpf.util.StateExtensionClient;
import gov.nasa.jpf.util.StateExtensionListener;

/**
 * example of native predicate for contracts
 *
 * this one checks if test object values are monotonic decreasing
 */
public class IsMonotonicDecreasing implements NativePredicate, StateExtensionClient<Number> {

  Number lastNumber = null;

  public IsMonotonicDecreasing() {
    registerListener(VM.getVM().getJPF()); // pretty quirky
  }

  public String evaluate(Object testObj, Object[] args) {
    // we don't have args

    if (! (testObj instanceof Number)) {
      throw new ContractException("IsMonotonicDecreasing test object not a Number: " + testObj);
    }

    Number num = (Number)testObj;

    if (lastNumber != null) {
      if (lastNumber.doubleValue() < num.doubleValue()) {
        return ("IsMonotonicDecreasing failed: " + lastNumber + ", " + num);
      }
    }

    lastNumber = num;

    return null; // satisfied
  }

  //--- StateExtension interface
  public Number getStateExtension() {
    return lastNumber;
  }

  public void restore(Number stateExtension) {
    lastNumber = stateExtension;
  }

  public void registerListener(JPF jpf) {
    StateExtensionListener<Number> sel = new StateExtensionListener(this);
    jpf.addSearchListener(sel);
  }
}
