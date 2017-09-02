package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;

public class EmptyExpression extends AbstractTypedExpression
{
	public static final EmptyExpression INSTANCE = new EmptyExpression();
	
	private EmptyExpression()
	{
		super(Lens.VOID);
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return Lens.UNDEFINED;
	}
	
	@Override
	public String toString()
	{
		return "()";
	}
}
