package net.rvanasa.lens.impl.type;

import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.value.AsyncValue;
import net.rvanasa.lens.LensType;

public class AsyncType implements LensType
{
	private final LensType type;
	
	public AsyncType(LensType type)
	{
		if(type instanceof AsyncType)
		{
			this.type = ((AsyncType)type).getValueType();
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
		if(type instanceof AsyncType)
		{
			return getValueType().isAssignableFrom(((AsyncType)type).getValueType());
		}
		return getValueType().isAssignableFrom(type);
	}
	
	@Override
	public AsyncValue getTyped(LensValue value)
	{
		if(value instanceof AsyncValue)
		{
			return (AsyncValue)value;
		}
		return new AsyncValue(getValueType(), getValueType().getTyped(value));
	}
	
	@Override
	public String toString()
	{
		return "async " + getValueType();
	}
}
