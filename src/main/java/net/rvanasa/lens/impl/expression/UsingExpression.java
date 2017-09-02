package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.context.InvokeContext;
import net.rvanasa.lens.impl.context.UsingContext;

public class UsingExpression extends AbstractExpression
{
	private final LensExpression target;
	
	private final LensExpression exp;
	
	public UsingExpression(LensExpression target, LensExpression statement)
	{
		this.target = target;
		
		this.exp = statement;
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
	public TypeReference getExpType()
	{
		return getExp().getExpType();
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensValue target = getTarget().eval(stack);
		InvokeStack child = /*target instanceof ContextObject ? new InvokeStack(((ContextObject)target).getContext()) : */stack.createContextual(new InvokeContext(new UsingContext(stack.getContext(), target)));
		
		return getExp().eval(child);
	}
	
	@Override
	public String toString()
	{
		return join("using", getTarget(), getExp());
	}
}
