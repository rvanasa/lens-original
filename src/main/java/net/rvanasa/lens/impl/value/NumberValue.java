package net.rvanasa.lens.impl.value;

import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.type.NumberType;

public class NumberValue<T extends Number> extends AbstractMemberValue
{
	private final NumberType<T> type;
	private final T handle;
	
	public NumberValue(NumberType<T> type, T handle)
	{
		this.type = type;
		this.handle = handle;
	}
	
	public T handle()
	{
		return handle;
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		return handle();
	}
	
	@Override
	public NumberType<T> getType()
	{
		return type;
	}
	
	@Override
	public boolean isEqualComponent(LensValue value, Environment env)
	{
		if(value instanceof NumberValue)
		{
			return value == this || handle().doubleValue() == ((NumberValue<?>)value).handle().doubleValue();
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return handle().toString();
	}
}
