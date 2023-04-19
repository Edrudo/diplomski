package gov.nasa.jpf.aprop;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.JVMFieldInstruction;
import gov.nasa.jpf.vm.Instruction;

public class ContractViolationFacade {
	
	/**
	 * Report a violation when confined exception was handled in not confined scope. 
	 * 
	 * @param threadInfo - current thread info
	 * @param throwingMethod - method that has thrown the exception
	 * @param ci - class info of thrown exception
	 */
	public static void handleConfinedExceptionViolation(ThreadInfo threadInfo, MethodInfo throwingMethod, ClassInfo ci) {
		StringBuilder message = new StringBuilder();
		message.append("ConfinedViolation: ");
		message.append(ci.getName());
		message.append(" was caught within not confined scope ");
		message.append(throwingMethod.getFullName());
		Instruction nextPc = 
			threadInfo.createAndThrowException("java.lang.AssertionError", message.toString());
	    threadInfo.setNextPC(nextPc);
	}
	
	/**
	 * Report a violation when confined field was accessed in not confined scope. 
	 * 
	 * @param threadInfo - current thread info
	 * @param instruction - field instruction 
	 * @param methodInfo - method conducting illegal access
	 */
	public static void throwConfinedFieldViolation(ThreadInfo threadInfo, JVMFieldInstruction instruction, MethodInfo methodInfo) {
		StringBuilder message = new StringBuilder();
		message.append("ConfinedViolation: ");
		message.append(instruction.getFieldInfo().getFullName());
		message.append(" was accessed within not confined scope ");
		message.append(methodInfo.getFullName());
		Instruction nextPc = 
			threadInfo.createAndThrowException("java.lang.AssertionError", message.toString());
	    threadInfo.setNextPC(nextPc);
	}

	/**
	 * Report a violation when confined method was invoked in not confined scope. 
	 * 
	 * @param threadInfo - current thread info
	 * @param invokedMethodInfo - invoked method info
	 * @param invokingMethodInfo - invoking method info
	 */
	public static void throwConfinedMethodViolation(ThreadInfo threadInfo,
			MethodInfo invokedMethodInfo, MethodInfo invokingMethodInfo) {
		StringBuilder message = new StringBuilder();
		message.append("ConfinedViolation: ");
		message.append(invokedMethodInfo.getFullName());
		message.append(" was accessed within not confined scope ");
		message.append(invokingMethodInfo.getFullName());
		Instruction nextPc = 
			threadInfo.createAndThrowException("java.lang.AssertionError", message.toString());
	    threadInfo.setNextPC(nextPc);
	}

	/**
	 * Report a violation when new array was created in not confined scope. 
	 * 
	 * @param threadInfo - current thread info
	 * @param ci - array type
	 * @param methodInfo - method conducting illegal array creation
	 */
	public static void throwConfinedArrayViolation(ThreadInfo threadInfo, 
			ClassInfo ci, MethodInfo methodInfo) {
		StringBuilder message = new StringBuilder();
		message.append("ConfinedViolation: new array of ");
		message.append(ci.getName());
		message.append(" was accessed within not confined scope ");
		message.append(methodInfo.getFullName());
		Instruction nextPc = 
			threadInfo.createAndThrowException("java.lang.AssertionError", message.toString());
	    threadInfo.setNextPC(nextPc);
	}
}
