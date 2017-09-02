package net.rvanasa.lens.exception;

import net.rvanasa.lens.LensValue;

public class LensPatternException extends RuntimeException
{
	private final LensValue value;
	
	public LensPatternException(LensValue value)
	{
		super("No default case provided for value: " + value);
		this.value = value;
	}
	
	public LensValue getValue()
	{
		return value;
	}
}
