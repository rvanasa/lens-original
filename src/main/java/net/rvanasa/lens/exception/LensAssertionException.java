package net.rvanasa.lens.exception;

import net.rvanasa.lens.LensExpression;

public class LensAssertionException extends LensException
{
	public LensAssertionException(LensExpression exp)
	{
		this(exp, null);
	}
	
	public LensAssertionException(LensExpression exp, Throwable e)
	{
		super("Assertion failed: `" + exp + '`', e);
	}
}
