package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.value.ContextValue;

public class ContextExpression extends AbstractTypedExpression
{
	public static final ContextExpression INSTANCE = new ContextExpression();
	
	private ContextExpression()
	{
		super(Lens.CONTEXT);
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return new ContextValue(stack.getContext());
	}
	
	@Override
	public String toString()
	{
		return "@";
	}
}
