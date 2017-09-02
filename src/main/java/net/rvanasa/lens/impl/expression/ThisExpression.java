package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.reference.ThisTypeReference;

public class ThisExpression extends AbstractTypedExpression
{
	public static final ThisExpression INSTANCE = new ThisExpression();
	
	private ThisExpression()
	{
		super(new ThisTypeReference());
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return stack.getContext().getTargetValue();
	}
	
	@Override
	public String toString()
	{
		return "this";
	}
}
