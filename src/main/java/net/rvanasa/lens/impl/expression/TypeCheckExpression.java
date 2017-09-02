package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.value.BooleanValue;

public class TypeCheckExpression extends AbstractTypedExpression
{
	private final LensExpression value;
	
	private final TypeReference type;
	
	public TypeCheckExpression(LensExpression value, TypeReference type)
	{
		super(Lens.BOOL);
		
		this.value = value;
		
		this.type = type;
	}
	
	public LensExpression getValue()
	{
		return value;
	}
	
	public TypeReference getCheckType()
	{
		return type;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return BooleanValue.get(getCheckType().resolve(stack).isInstance(getValue().eval(stack)));
	}
	
	@Override
	public String toString()
	{
		return "(" + join("(" + getValue() + ")", "is", getCheckType()) + ")";
	}
}
