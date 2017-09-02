package net.rvanasa.lens.exception;

import net.rvanasa.lens.LensExpression;

public class LensStacktrace extends LensException
{
	public static String getStackString(LensExpression expression)
	{
		String data = expression.toString();
		if(data.length() > 50)
		{
			data = data.substring(0, 50) + " ...";
		}
		return data;
	}
	
	private final LensExpression expression;
	
	public LensStacktrace(LensExpression expression, Throwable e)
	{
		super(getStackString(expression), e);
		
		this.expression = expression;
	}
	
	public LensExpression getExpression()
	{
		return expression;
	}
	
	@Override
	public synchronized Throwable fillInStackTrace()
	{
		return null;
	}
}
