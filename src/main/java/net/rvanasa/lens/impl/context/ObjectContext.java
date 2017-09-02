package net.rvanasa.lens.impl.context;

import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.TypeContext;

public class ObjectContext extends ChildContext
{
	private final LensValue value;
	
	public ObjectContext(LensValue value, LensContext parent)
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
		return getTargetValue().isValue(id) || super.isValue(id);
	}
	
	@Override
	public LensType getValueType(String id)
	{
		return getTargetValue().getType(id);
	}
	
	@Override
	public LensValue get(String id)
	{
		return getTargetValue().isValue(id) ? getTargetValue().get(id) : super.get(id);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		if(getTargetValue().isAssignable(id))
		{
			getTargetValue().set(id, value);
		}
		else
		{
			super.set(id, value);
		}
	}
	
	@Override
	public void add(String id, LensValue value)
	{
		set(id, value);
	}
	
	@Override
	public LensFunction getFunction(String id, ParamMatcher args)
	{
		return get(id).getFunction();
	}
	
	@Override
	public TypeContext createTypeContext()
	{
		TypeContext context = super.createTypeContext();
		
		// (add object member population)
		// throw new LensException("Object type context population unavailable");
		return context;
	}
}
