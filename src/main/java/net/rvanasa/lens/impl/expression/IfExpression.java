package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.context.InvokeContext;
import net.rvanasa.lens.impl.reference.OptionalTypeReference;

public class IfExpression extends AbstractExpression
{
	private final LensExpression condition;
	
	private final LensExpression value;
	private final OptionalTypeReference optionalType;
	
	public IfExpression(LensExpression condition, LensExpression value)
	{
		this.condition = condition;
		
		this.value = value;
		this.optionalType = new OptionalTypeReference(value.getExpType());
	}
	
	public LensExpression getCondition()
	{
		return condition;
	}
	
	public LensExpression getValue()
	{
		return value;
	}
	
	@Override
	public OptionalTypeReference getExpType()
	{
		return optionalType;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		stack = stack.createContextual(new InvokeContext(stack.getContext()));
		LensValue value = Lens.BOOL.getTyped(getCondition().eval(stack)).handle() ? getValue().eval(stack) : Lens.UNDEFINED;
		return getExpType().resolve(stack).getTyped(value);
	}
	
	@Override
	public String toString()
	{
		return join("if", getCondition(), getValue());
	}
}
