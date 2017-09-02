package net.rvanasa.lens.impl.property;

import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.value.FunctionValue;

public class ReadonlyProperty extends BasicProperty
{
	public ReadonlyProperty(LensValue value)
	{
		super(value);
	}
	
	public ReadonlyProperty(LensFunction function)
	{
		this(new FunctionValue(function));
	}
	
	@Override
	public void set(LensValue value)
	{
		throw new LensException("Cannot replace property of value: " + value);
	}
}
