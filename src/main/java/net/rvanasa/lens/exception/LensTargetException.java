package net.rvanasa.lens.exception;

import net.rvanasa.lens.LensValue;

public class LensTargetException extends LensException
{
	private final LensValue target;
	
	public LensTargetException(LensValue target)
	{
		super(target + " is not invokable");
		
		this.target = target;
	}
	
	public LensValue getTarget()
	{
		return target;
	}
}
