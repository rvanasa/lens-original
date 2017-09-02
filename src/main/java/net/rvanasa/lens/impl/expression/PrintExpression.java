package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;

public class PrintExpression extends AbstractExpression
{
	private final LensExpression value;
	
	public PrintExpression(LensExpression value)
	{
		this.value = value;
	}
	
	public LensExpression getValue()
	{
		return value;
	}
	
	@Override
	public TypeReference getExpType()
	{
		return getValue().getExpType();
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensValue value = getValue().eval(stack);
		stack.getEnv().getRuntime().print(value);
		
		return value;
	}
	
	@Override
	public String toString()
	{
		return "print(" + getValue() + ")";
	}
}
