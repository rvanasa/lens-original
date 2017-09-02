package net.rvanasa.lens.impl.type;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.exception.LensException;

public class CorrespondingType<T extends LensValue> extends AbstractLensType
{
	private final Class<T> type;
	
	public CorrespondingType(String name, Class<T> type)
	{
		super(name);
		
		this.type = type;
	}
	
	public Class<T> getObjectClass()
	{
		return type;
	}
	
	@Override
	public boolean isInstance(LensValue value)
	{
		return getObjectClass().isInstance(value);
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		return type == this;
	}
	
	@Override
	public T getTyped(LensValue value)
	{
		if(isInstance(value))
		{
			return Lens.cast(value);
		}
		else
		{
			throw new LensException("Invalid " + this + " value: " + value);
		}
	}
}
