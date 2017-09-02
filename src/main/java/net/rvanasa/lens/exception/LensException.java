package net.rvanasa.lens.exception;

public class LensException extends RuntimeException
{
	public LensException(String message)
	{
		super(message);
	}
	
	public LensException(Throwable e)
	{
		super(e);
	}
	
	public LensException(String message, Throwable e)
	{
		super(message, e);
	}
}
