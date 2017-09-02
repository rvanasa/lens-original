package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;

public class StaticExpression extends AbstractTypedExpression
{
	private final TypeReference type;
	
	public StaticExpression(TypeReference type)
	{
		super(Lens.VAL);
		
		this.type = type;
	}
	
	public TypeReference getType()
	{
		return type;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return getType().resolve(stack).getStaticValue();
	}
	
	@Override
	public String toString()
	{
		return join("static(" + getType() + ")");
	}
}
