package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.context.InvokeContext;
import net.rvanasa.lens.impl.reference.CommonTypeReference;

public class IfElseExpression extends AbstractTypedExpression
{
	private final LensExpression condition;
	
	private final LensExpression ifValue;
	private final LensExpression elseValue;
	
	public IfElseExpression(LensExpression condition, LensExpression ifValue, LensExpression elseValue)
	{
		super(new CommonTypeReference(ifValue.getExpType(), elseValue.getExpType()));
		
		this.condition = condition;
		
		this.ifValue = ifValue;
		this.elseValue = elseValue;
	}
	
	public LensExpression getCondition()
	{
		return condition;
	}
	
	public LensExpression getIfValue()
	{
		return ifValue;
	}
	
	public LensExpression getElseValue()
	{
		return elseValue;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		stack = stack.createContextual(new InvokeContext(stack.getContext()));
		return Lens.BOOL.getTyped(getCondition().eval(stack)).handle() ? getIfValue().eval(stack) : getElseValue().eval(stack);
	}
	
	@Override
	public void assign(LensValue value, InvokeStack stack)
	{
		LensExpression exp = Lens.BOOL.getTyped(getCondition().eval(stack)).handle() ? getIfValue() : getElseValue();
		exp.assign(value, stack);
	}
	
	@Override
	public String toString()
	{
		return join("if", getCondition(), getIfValue(), "else", getElseValue());
	}
}
