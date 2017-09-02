package net.rvanasa.lens;

import net.rvanasa.lens.exception.LensException;

public interface LensType extends ParamMatcher, TypeReference
{
	public boolean isAssignableFrom(LensType type);
	
	default boolean isEqualComponent(LensType type)
	{
		return isAssignableFrom(type) && type.isAssignableFrom(this);
	}
	
	default boolean isInstance(LensValue value)
	{
		return isAssignableFrom(value.getType());
	}
	
	default LensValue getTyped(LensValue value)
	{
		if(isInstance(value))
		{
			return value;
		}
		throw new LensException(value + " cannot be assigned to " + this);
	}
	
	default LensValue newInstance(LensValue arg)
	{
		throw new LensException(this + " cannot be instantitated");
	}
	
	default LensType getIndexerType()
	{
		return Lens.VAL;
	}
	
	default LensType getInvokeType(ParamMatcher arg)
	{
		return Lens.ANY;
	}
	
	default LensValue getStaticValue()
	{
		return Lens.UNDEFINED;
	}
	
	@Override
	default boolean matchesParam(LensParam param)
	{
		return param.getType().isAssignableFrom(this);
	}
	
	@Override
	default LensType resolve(InvokeStack stack)
	{
		return this;
	}
}
