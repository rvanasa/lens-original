package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.value.TypeValue;

public class TypeExpression extends AbstractTypedExpression
{
	private final TypeReference type;
	
	public TypeExpression(TypeReference type)
	{
		super(Lens.TYPE);
		
		this.type = type;
	}
	
	public TypeReference getType()
	{
		return type;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return new TypeValue(getType().resolve(stack));
	}
	
	@Override
	public String toString()
	{
		return join("type(" + getType() + ")");
	}
}
