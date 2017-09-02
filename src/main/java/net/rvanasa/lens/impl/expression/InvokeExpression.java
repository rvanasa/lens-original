package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.exception.LensPointerException;
import net.rvanasa.lens.exception.LensTargetException;
import net.rvanasa.lens.impl.reference.InvokeTypeReference;

public class InvokeExpression extends AbstractTypedExpression
{
	private final LensExpression target;
	private final LensExpression args;
	
	public InvokeExpression(LensExpression target, LensExpression args)
	{
		super(new InvokeTypeReference(target.getExpType(), args.getExpType()));
		
		this.target = target;
		this.args = args;
	}
	
	public LensExpression getTarget()
	{
		return target;
	}
	
	public LensExpression getArgs()
	{
		return args;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensValue target = getTarget().eval(stack);
		LensFunction function;
		try
		{
			function = target.getFunction();
		}
		catch(LensTargetException e)
		{
			throw new LensException("Cannot invoke " + toString().replace('\n', ' '));
		}
		catch(LensPointerException e)
		{
			throw new LensException("Function is undefined: " + toString().replace('\n', ' '));
		}
		return function.invoke(getArgs(), stack.getBaseInvocation());
	}
	
	@Override
	public String toString()
	{
		return joinDelim("", getTarget(), getArgs() instanceof TupleExpression || getArgs() instanceof EmptyExpression ? getArgs() : "(" + getArgs() + ")");
	}
}
