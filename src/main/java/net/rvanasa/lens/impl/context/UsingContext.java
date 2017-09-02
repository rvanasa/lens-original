package net.rvanasa.lens.impl.context;

import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensValue;

public class UsingContext extends ChildContext
{
	private final LensValue value;
	
	public UsingContext(LensContext parent, LensValue value)
	{
		super(parent);
		
		this.value = value;
	}
	
	@Override
	public LensValue getTargetValue()
	{
		return value;
	}
	
	@Override
	public boolean isValue(String id)
	{
		return super.isValue(id) || id.equals("this") || getTargetValue().isValue(id);
	}
	
	@Override
	public LensValue get(String id)
	{
		if(id.equals("this"))
		{
			return getTargetValue();
		}
		else if(super.isValue(id))
		{
			return super.get(id);
		}
		else
		{
			return getTargetValue().get(id);
		}
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		if(super.isValue(id))
		{
			super.set(id, value);
		}
		else
		{
			getTargetValue().set(id, value);
		}
	}
}
