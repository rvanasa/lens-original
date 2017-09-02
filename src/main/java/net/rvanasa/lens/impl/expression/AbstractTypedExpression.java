package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.TypeReference;

public abstract class AbstractTypedExpression extends AbstractExpression
{
	private final TypeReference type;
	
	public AbstractTypedExpression(TypeReference type)
	{
		this.type = type;
	}
	
	@Override
	public TypeReference getExpType()
	{
		return type;
	}
}
