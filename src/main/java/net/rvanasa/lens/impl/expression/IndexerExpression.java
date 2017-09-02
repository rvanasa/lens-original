package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.reference.IndexerTypeReference;

public class IndexerExpression extends AbstractTypedExpression
{
	private final LensExpression target;
	private final LensExpression indexExp;
	
	public IndexerExpression(LensExpression target, LensExpression indexExp)
	{
		super(new IndexerTypeReference(target.getExpType()));
		
		this.target = target;
		this.indexExp = indexExp;
	}
	
	public LensExpression getTarget()
	{
		return target;
	}
	
	public LensExpression getIndexExp()
	{
		return indexExp;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return getTarget().eval(stack).getIndex(getKey(stack));
	}
	
	@Override
	public void assign(LensValue value, InvokeStack stack)
	{
		getTarget().eval(stack).setIndex(getKey(stack), value);
	}
	
	private LensValue getKey(InvokeStack stack)
	{
		return getIndexExp().eval(stack);
	}
	
	@Override
	public String toString()
	{
		return getTarget() + "[" + getIndexExp() + "]";
	}
}
