package net.rvanasa.lens.impl.type;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;

public class OptionalType implements LensType
{
	private final LensType type;
	
	public OptionalType(LensType type)
	{
		if(type instanceof OptionalType)
		{
			this.type = ((OptionalType)type).getValueType();
		}
		else
		{
			this.type = type;
		}
	}
	
	public LensType getValueType()
	{
		return type;
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		return getValueType().isAssignableFrom(type) || type == Lens.VOID;
	}
	
	@Override
	public LensValue getTyped(LensValue value)
	{
		if(Lens.VOID.isInstance(value))
		{
			return value;
		}
		return getValueType().getTyped(value);
	}
	
	@Override
	public String toString()
	{
		return getValueType() + "?";
	}
}
