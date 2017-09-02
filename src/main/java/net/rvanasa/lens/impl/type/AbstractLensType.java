package net.rvanasa.lens.impl.type;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.ParamMatcher;

public abstract class AbstractLensType implements LensType
{
	private final String name;
	
	public AbstractLensType(String name)
	{
		this.name = name;
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		return type == this;
	}
	
	@Override
	public LensType getIndexerType()
	{
		return Lens.VAL;
	}
	
	@Override
	public LensType getInvokeType(ParamMatcher arg)
	{
		return Lens.ANY;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
