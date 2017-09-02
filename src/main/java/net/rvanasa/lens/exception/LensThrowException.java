package net.rvanasa.lens.exception;

import net.rvanasa.lens.LensValue;

public class LensThrowException extends LensException
{
	private final LensValue value;
	
	public LensThrowException(LensValue value)
	{
		this(value, null);
	}
	
	public LensThrowException(LensValue value, Exception e)
	{
		super(value.getPrintString(), e);
		this.value = value;
	}
	
	public LensValue getValue()
	{
		return value;
	}
}
