package net.rvanasa.lens.impl.value;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.impl.context.Environment;

public class ContextValue implements LensValue
{
	private final LensContext context;
	
	public ContextValue(LensContext context)
	{
		this.context = context;
	}
	
	public LensContext getContext()
	{
		return context;
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		return getContext();
	}
	
	@Override
	public LensType getType()
	{
		return Lens.CONTEXT;
	}
	
	@Override
	public boolean isValue(String id)
	{
		return getContext().isValue(id);
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return true;
	}
	
	@Override
	public LensValue get(String id)
	{
		return getContext().get(id);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		getContext().set(id, value);
	}
	
	@Override
	public boolean isEqualComponent(LensValue value, Environment env)
	{
		return value instanceof ContextValue && ((ContextValue)value).getContext() == getContext();
	}
	
	@Override
	public String toString()
	{
		return "@" + getContext();
	}
}
