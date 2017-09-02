package net.rvanasa.lens.impl.type;

import java.util.function.Function;

import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.value.NumberValue;
import net.rvanasa.lens.impl.value.StringValue;

public class NumberType<T extends Number> extends AbstractLensType
{
	private final int precision;
	private final Function<Number, T> function;
	private final Function<String, T> parser;
	
	public NumberType(String name, int precision, Function<Number, T> function, Function<String, T> parser)
	{
		super(name);
		
		this.precision = precision;
		this.function = function;
		this.parser = parser;
	}
	
	public int getPrecision()
	{
		return precision;
	}
	
	public Function<Number, T> getNumberFunction()
	{
		return function;
	}
	
	public Function<String, T> getParserFunction()
	{
		return parser;
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		if(type instanceof NumberType)
		{
			NumberType<?> numType = ((NumberType<?>)type);
			return getPrecision() >= numType.getPrecision();
		}
		return false;
	}
	
	@Override
	public boolean isInstance(LensValue value)
	{
		if(value instanceof NumberValue)
		{
			NumberValue<?> number = (NumberValue<?>)value;
			if(getPrecision() >= number.getType().getPrecision())
			{
				return true;
			}
			else
			{
				Number n = number.handle();
				return getNumberFunction().apply(n).doubleValue() == n.doubleValue();
			}
		}
		return false;
	}
	
	@Override
	public NumberValue<T> getTyped(LensValue value)
	{
		if(value instanceof NumberValue)
		{
			NumberValue<?> number = ((NumberValue<?>)value);
			return getTyped(number.handle());
		}
		else if(value instanceof StringValue)
		{
			return parse(value.getPrintString());
		}
		throw new LensException(value + " is not a valid " + this);
	}
	
	public NumberValue<T> getTyped(Number value)
	{
		return new NumberValue<>(this, getNumberFunction().apply(value));
	}
	
	public NumberValue<T> parse(String data)
	{
		return new NumberValue<>(this, getParserFunction().apply(data));
	}
}
