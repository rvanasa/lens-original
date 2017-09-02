package net.rvanasa.lens.impl.param;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;

public class NamedParam implements LensParam
{
	private final String name;
	private final LensType type;
	
	public NamedParam(String name, LensType type)
	{
		this.name = name;
		this.type = type;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public LensType getType()
	{
		return type;
	}
	
	@Override
	public void setupContext(LensValue value, LensContext context)
	{
		context.add(getName(), resolveValue(value));
	}
	
	@Override
	public String toString()
	{
		return getName() + (getType() != Lens.ANY ? ": " + getType() : "");
	}
}
