package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;

public class BlockExpression extends MultiExpression
{
	public BlockExpression(LensExpression[] expressions)
	{
		super(expressions);
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return super.eval(stack.createChild());
	}
	
	@Override
	public String toString()
	{
		return join("{", super.toString(), "}");
	}
}
