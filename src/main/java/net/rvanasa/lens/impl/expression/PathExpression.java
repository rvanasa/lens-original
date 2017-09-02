package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.context.ObjectContext;

public class PathExpression extends AbstractTypedExpression
{
	private final LensExpression target;
	private final LensExpression exp;
	
	public PathExpression(LensExpression target, LensExpression exp)
	{
		super(exp.getExpType());
		
		this.target = target;
		this.exp = exp;
	}
	
	public LensExpression getTarget()
	{
		return target;
	}
	
	public LensExpression getExp()
	{
		return exp;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensValue target = getTarget().eval(stack);
		if(target == Lens.UNDEFINED)
		{
			throw new LensException("Value is undefined: " + getTarget());
		}
		return getExp().eval(stack.createContextual(new ObjectContext(target, stack.getEnv().getPathContext())));
	}
	
	@Override
	public void assign(LensValue value, InvokeStack stack)
	{
		LensValue target = getTarget().eval(stack);
		getExp().assign(value, stack.createContextual(new ObjectContext(target, stack.getEnv().getPathContext())));
	}
	
	@Override
	public String toString()
	{
		return getTarget() + "." + getExp();
	}
}
