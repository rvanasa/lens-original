package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeContext;
import net.rvanasa.lens.TypeReference;

public class ImportExpression extends AbstractExpression
{
	private final String location;
	private final String name;
	
	private final LensValue value;
	
	public ImportExpression(String path, String name, TypeContext context)
	{
		this.location = path;
		this.name = name;
		
		this.value = context.handleImport(path);
	}
	
	public String getLocation()
	{
		return location;
	}
	
	public String getName()
	{
		return name;
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
	public int getBlockPrecedence()
	{
		return 100;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		if(getValue() != Lens.UNDEFINED)
		{
			stack.getContext().add(getName(), getValue());
		}
		return getValue();
	}
	
	@Override
	public String toString()
	{
		return join("import", getLocation(), "as", getName());
	}
}
