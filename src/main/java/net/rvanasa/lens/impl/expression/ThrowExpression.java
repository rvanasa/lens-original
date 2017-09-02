package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.exception.LensThrowException;

public class ThrowExpression extends AbstractTypedExpression
{
	private final LensExpression value;
	
	public ThrowExpression(LensExpression value)
	{
		super(Lens.ANY);
		this.value = value;
	}
	
	public LensExpression getValue()
	{
		return value;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		throw new LensThrowException(getValue().eval(stack));
	}
	
	@Override
	public String toString()
	{
		return join("throw", getValue());
	}
}
