package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;

public class LiteralExpression extends AbstractExpression
{
	private final LensValue value;
	
	public LiteralExpression(LensValue value)
	{
		this.value = value;
	}
	
	public LensValue getValue()
	{
		return value;
	}
	
	@Override
	public TypeReference getExpType()
	{
		return getValue().getType();
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return value;
	}
	
	@Override
	public String toString()
	{
//		if(getValue() instanceof StringObject)
//		{
//			return "\"" + getValue() + "\"";
//		}
		return getValue().toString();
	}
}
