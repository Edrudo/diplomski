/**
 *
 */
package gov.nasa.jpf.aprop;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DirectCallStackFrame;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;

import java.util.ArrayList;
import java.util.HashMap;

public class Satisfies extends Contract {

  static HashMap<ClassInfo,ForwardingPredicate> predicates = new HashMap<ClassInfo,ForwardingPredicate>();

  static final String MODEL_PACKAGE = "<model>";
  static final String DEFAULT_PACKAGE = "<default>";

  static String[] predicatePackages;


  public static void init (Config conf) {

    String[] defPredicatePackages = { MODEL_PACKAGE, "gov.nasa.jpf.aprop.predicate", DEFAULT_PACKAGE };
    String[] packages = conf.getStringArray("contract.predicate_packages", defPredicatePackages);

    // internalize for faster comparison
    for (int i=0; i<packages.length; i++) {
      if (packages[i].equals(MODEL_PACKAGE)) {
        packages[i] = MODEL_PACKAGE;
      } else if (packages[i].equals(DEFAULT_PACKAGE)) {
        packages[i] = DEFAULT_PACKAGE;
      }
    }

    predicatePackages = packages;
  }

  class ForwardingPredicate implements Predicate {

    MethodInfo miPred;
    int        predRef; // the predicate object reference

    ForwardingPredicate (ClassInfo ciPred){
      // ciPred is guaranteed to be a Predicate implementor
      MethodInfo  mi = ciPred.getMethod("evaluate(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/String;", true);
      if (mi != null) {
        miPred = mi;
        predRef = newInstance(ciPred);
      }
    }

    void execClinit (ClassInfo ci) {
      if (!ci.isInitialized()) {
        ClassInfo sci = ci.getSuperClass();

        if (sci != null) {
          execClinit(sci);

        } else {
          ThreadInfo ti = ctx.getThreadInfo();
          ci.registerClass(ti);

          MethodInfo clinit = ci.getMethod("<clinit>()V", false);
          if (clinit != null) {
            DirectCallStackFrame frame = clinit.createDirectCallStackFrame(ti, 0);
            ti.executeMethodAtomic(frame);
          } else {
            ci.setInitialized();
          }
        }
      }
    }

    void execCtor (ClassInfo ci, int ref) {
      ClassInfo sci = ci.getSuperClass();

      if (sci != null) {
        execCtor(sci, ref);
      } else {
        MethodInfo miCtor = ci.getMethod("init()V", false);
        if (miCtor != null) {
          // <2do> needs to traverse superclasses
          DirectCallStackFrame frame = miCtor.createDirectCallStackFrame(ThreadInfo.getCurrentThread(), 0);
          frame.pushRef(ref);
          ctx.getThreadInfo().executeMethodAtomic(frame);
        }
      }
    }

    /**
     * beware, this only execs clinit and init synchronously. Any blocking
     * and it will die
     */
    public int newInstance (ClassInfo ci) {
      ThreadInfo ti = ctx.getThreadInfo();
      VM vm = ti.getVM();
      Heap heap = vm.getHeap();

      if (!ci.isInitialized()) {
        execClinit(ci);
      }

      ElementInfo ei = heap.newObject(ci, ti);
      int r = ei.getObjectRef();
      heap.registerPinDown(r); // there are no references in the app

      // notice we do ad hoc init here, which is not allowed to do any blocking
      execCtor(ci,r);

      return r;
    }


    int getRef (ThreadInfo ti, Object o) {
      if (o instanceof ElementInfo) {
        return ((ElementInfo)o).getObjectRef();

      } else {
        // here it gets braindead - we convert JPF into native, to convert back into JPF

        MJIEnv env = ti.getEnv();

        if (o instanceof Integer) {
          return env.newInteger((Integer)o);
        } else if (o instanceof Long) {
          return env.newLong((Long)o);
        } else if (o instanceof Boolean) {
          return env.newBoolean((Boolean)o);
        } else if (o instanceof Double) {
          return env.newDouble((Double)o);
        } else if (o instanceof Float) {
          return env.newFloat((Float)o);

        } else {
          return env.newString(o.toString());
        }
      }
    }

    int getArrayRef (ThreadInfo ti, Object[] args) {
      if (args != null) {
        MJIEnv env = ti.getEnv();
        int ref = env.newObjectArray("Ljava/lang/Object;", args.length);

        for (int i=0; i<args.length; i++) {
          env.setReferenceArrayElement(ref, i, getRef(ti,args[i]));
        }
        return ref;

      } else {
        return MJIEnv.NULL;
      }
    }

    public String evaluate (Object testObj, Object[] args) {
      if (predRef != -1) {
        ThreadInfo ti = ctx.getThreadInfo();
        MJIEnv env = ti.getEnv();
        DirectCallStackFrame frame = miPred.createDirectCallStackFrame(ti, 0);

        frame.pushRef( predRef);
        frame.pushRef( getRef(ti, testObj));
        frame.pushRef( getArrayRef(ti, args));

        ctx.getThreadInfo().executeMethodAtomic(frame);
        int errRef = frame.pop();

        if (errRef != MJIEnv.NULL) {
          return env.getStringObject(errRef);
        }
      } else {
        // <2do> not sure what's the best default reaction
      }

      return null;
    }

  }

  ContractContext ctx;

  Operand testObj;
  Operand[] args;

  Predicate pred;
  String violation;

  public Satisfies (ContractContext ctx, String id, Operand testObj, ArrayList<Operand> args) {
    this.testObj = testObj;
    this.ctx = ctx;

    if (args != null) {
      int n = args.size();
      if (n > 0) {
        this.args = new Operand[n];
        args.toArray(this.args);
      }
    }

    pred = getPredicate(ctx, id);
  }

  Predicate getPredicate(ContractContext ctx, String id) {
    Predicate p = null;

    if (id.indexOf('.') >= 0){
      p = locatePredicate(id);

    } else {
      String clsName = null;

      String[] pp = predicatePackages;
      for (int i=0; i<pp.length && p == null; i++){
        String pkg = pp[i];

        if (pkg == MODEL_PACKAGE){
          clsName = ctx.getPackage() + '.' + id;
        } else if (pkg == DEFAULT_PACKAGE){
          clsName = id;
        } else {
          clsName = pkg + '.' + id;
        }

        p = locatePredicate(clsName);
      }

    }

    if (p == null){
      log.warning("could not find predicate: " + id);
    }

    return p;
  }

  Predicate locatePredicate (String clsName){
    try {
      Class<?> cls = Class.forName(clsName);
      if (NativePredicate.class.isAssignableFrom(cls)) { // it's native
        return (Predicate) cls.newInstance();
      }
    } catch (IllegalAccessException iax) {
      log.warning("no public constructor: " + clsName);
      return null;
    } catch (InstantiationException iex) {
      log.warning("error intantiating Predicate object: " + clsName);
      return null;
    } catch (ClassNotFoundException cfnx) {
      // can't be native, try model
    }

    // check if we can find a model predicate
    return getModelPredicate(clsName);
  }

  Predicate getModelPredicate (String id) {
    ClassInfo ci = ctx.resolveClassInfo(id);

    if (ci != null) {
      if (!ci.isInstanceOf("gov.nasa.jpf.aprop.Predicate")) {
        log.warning("not a predicate instance: " + id);
        return null;
      }

      ForwardingPredicate p = predicates.get(ci);
      if (p == null) {
        p = new ForwardingPredicate(ci);
        predicates.put(ci,p);
      }
      return p;

    } else {
      return null;
    }
  }

  public boolean holds (VarLookup lookup) {
    if (pred != null) {
      Object testObjVal = testObj.getValue(lookup);
      Object[] argVals = null;

      if (args != null) {
        argVals = new Object[args.length];
        for (int i=0; i<args.length; i++) {
          argVals[i] = args[i].getValue(lookup);
        }
      }

      violation = pred.evaluate(testObjVal, argVals);

    }

    return (violation == null);
  }

  protected void saveOldOperandValues(VarLookup lookup) {
    if (pred != null) {
      testObj.saveOldOperandValue(lookup);

      if (args != null) {
        for (int i=0; i<args.length; i++) {
          args[i].saveOldOperandValue(lookup);
        }
      }
    }
  }

  public String toString() {
    if (violation != null) {
      return violation;
    } else {
      return testObj.toString() + " satisfies " + pred;
    }
  }
}