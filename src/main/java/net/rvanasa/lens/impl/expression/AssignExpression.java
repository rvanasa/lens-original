package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;

public class AssignExpression extends AbstractExpression
{
	private final LensExpression target;
	private final LensExpression value;
	
	public AssignExpression(LensExpression target, LensExpression value)
	{
		this.target = target;
		this.value = value;
	}
	
	@Override
	public TypeReference getExpType()
	{
		return getValue().getExpType();
	}
	
	public LensExpression getTarget()
	{
		return target;
	}
	
	public LensExpression getValue()
	{
		return value;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensValue value = getValue().eval(stack);
		getTarget().assign(value, stack);
		
		return value;
	}
	
	@Override
	public String toString()
	{
		return join(getTarget(), "=", getValue());
	}
}
