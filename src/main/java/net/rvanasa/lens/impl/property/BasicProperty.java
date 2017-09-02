package net.rvanasa.lens.impl.property;

import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.value.FunctionValue;
import net.rvanasa.lens.LensProperty;

public class BasicProperty implements LensProperty
{
	private LensValue value;
	
	public BasicProperty(LensValue value)
	{
		this.value = value;
	}
	
	@Override
	public LensValue get()
	{
		return value;
	}
	
	@Override
	public void set(LensValue value)
	{
		this.value = value;
	}
	
	@Override
	public String getDescription(String id)
	{
		String desc = LensProperty.super.getDescription(id);
		if(get() instanceof FunctionValue)
		{
			return desc + "(..)";
		}
		return desc;
	}
}
