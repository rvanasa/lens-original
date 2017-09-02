package net.rvanasa.lens;

import java.util.Collections;
import java.util.Objects;

import net.rvanasa.lens.exception.LensTargetException;
import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.value.TupleValue;

public interface LensValue extends ParamMatcher
{
	public Object handle(LensValueResolver resolver);
	
	public LensType getType();
	
	public boolean isValue(String id);
	
	default boolean isIndex(LensValue key)
	{
		return isValue(key.getPrintString());
	}
	
	public boolean isAssignable(String id);
	
	default boolean isAssignableIndex(LensValue key)
	{
		return isAssignable(key.getPrintString());
	}
	
	default LensType getType(String id)
	{
		return isValue(id) ? get(id).getType() : Lens.UNKNOWN;
	}
	
	default LensType getTypeIndex(LensValue key)
	{
		return getType(key.getPrintString());
	}
	
	public LensValue get(String id);
	
	default LensValue getIndex(LensValue key)
	{
		return get(key.getPrintString());
	}
	
	public void set(String id, LensValue value);
	
	default void setIndex(LensValue key, LensValue value)
	{
		set(key.getPrintString(), value);
	}
	
	default LensFunction getFunction()
	{
		throw new LensTargetException(this);
	}
	
	default LensValue invoke(LensValue value)
	{
		return getFunction().invoke(value);
	}
	
	default LensValue invoke(LensValue... values)
	{
		return invoke(TupleValue.get(values));
	}
	
	default boolean isEqualComponent(LensValue value, Environment env)
	{
		return this == value || Objects.equals(handle(env.getValueResolver()), value.handle(env.getValueResolver()));
	}
	
	default Iterable<LensValue> toIterable()
	{
		return Collections.singleton(this);
	}
	
	default int size()
	{
		return 1;
	}
	
	default boolean isEmpty()
	{
		return size() == 0;
	}
	
	default boolean isVoid()
	{
		return false;
	}
	
	@Override
	default boolean matchesParam(LensParam param)
	{
		return param.isValid(this);
	}
	
	default LensValue cast(LensType type)
	{
		return type.getTyped(this);
	}
	
	default String getPrintString()
	{
		return toString();
	}
}
