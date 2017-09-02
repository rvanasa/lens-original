package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.context.InvokeContext;

public class WhileExpression extends AbstractTypedExpression
{
	private final LensExpression condition;
	
	private final LensExpression value;
	
	public WhileExpression(LensExpression condition, LensExpression value)
	{
		super(Lens.VOID);
		
		this.condition = condition;
		
		this.value = value;
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
	public LensValue eval(InvokeStack stack)
	{
		stack = stack.createContextual(new InvokeContext(stack.getContext()));
		while(!Thread.interrupted() && Lens.BOOL.getTyped(getCondition().eval(stack)).handle())
		{
			getValue().eval(stack);
		}
		
		return Lens.UNDEFINED;
	}
	
	@Override
	public String toString()
	{
		return join("while", getCondition(), getValue());
	}
}
