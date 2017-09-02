package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;

public class CastExpression extends AbstractTypedExpression
{
	private final LensExpression value;
	
	public CastExpression(TypeReference type, LensExpression value)
	{
		super(type);
		
		this.value = value;
	}
	
	public LensExpression getValue()
	{
		return value;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return getValue().eval(stack).cast(getExpType().resolve(stack));
	}
	
	@Override
	public String toString()
	{
		return "(" + join("(" + getValue().toString() + ")", "as", getExpType()) + ")";
	}
}
