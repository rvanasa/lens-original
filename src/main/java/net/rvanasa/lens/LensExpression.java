package net.rvanasa.lens;

import net.rvanasa.lens.exception.LensException;

public interface LensExpression
{
	public TypeReference getExpType();
	
	public LensValue eval(InvokeStack stack);
	
	default void assign(LensValue value, InvokeStack stack)
	{
		throw new LensException(this + " cannot be assigned to " + value);
	}
	
	default int getBlockPrecedence()
	{
		return 0;
	}
}
