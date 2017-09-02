package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeContext;

public class TypeDefExpression extends AbstractTypedExpression
{
	private final String name;
	private final LensType type;
	
	public TypeDefExpression(String name, LensType type, TypeContext context)
	{
		super(Lens.VOID);
		
		this.name = name;
		this.type = type;
		
		context.setType(name, type);
	}
	
	public String getName()
	{
		return name;
	}
	
	public LensType getType()
	{
		return type;
	}
	
	@Override
	public int getBlockPrecedence()
	{
		return 100;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return Lens.UNDEFINED;
	}
	
	@Override
	public String toString()
	{
		return join("type", getName(), "=", getType());
	}
}
