package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.value.AsyncValue;

public class AsyncExpression extends AbstractExpression
{
	private final LensExpression value;
	
	public AsyncExpression(LensExpression value)
	{
		this.value = value;
	}
	
	public LensExpression getValue()
	{
		return value;
	}
	
	@Override
	public TypeReference getExpType()
	{
		return getValue().getExpType();
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		AsyncValue async = new AsyncValue(getExpType().resolve(stack));
		stack.getEnv().getRuntime().async(() -> async.update(getValue().eval(stack)));
		return async;
	}
	
	@Override
	public String toString()
	{
		return join("async", getValue());
	}
}
