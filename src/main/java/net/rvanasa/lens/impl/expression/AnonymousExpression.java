package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.reference.AnonymousTypeReference;

public class AnonymousExpression extends AbstractTypedExpression
{
	public static final AnonymousExpression INSTANCE = new AnonymousExpression();
	
	private AnonymousExpression()
	{
		super(AnonymousTypeReference.INSTANCE);
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return stack.getContext().getNextAnonymous();
	}
	
	@Override
	public String toString()
	{
		return "#";
	}
}
