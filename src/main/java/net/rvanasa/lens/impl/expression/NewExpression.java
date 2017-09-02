package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;

public class NewExpression extends AbstractTypedExpression
{
	private final LensExpression arg;
	
	public NewExpression(TypeReference type, LensExpression arg)
	{
		super(type);
		
		this.arg = arg;
	}
	
	public LensExpression getArg()
	{
		return arg;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return getExpType().resolve(stack).newInstance(getArg().eval(stack));
	}
	
	@Override
	public String toString()
	{
		String argString = getArg().toString();
		if(!(getArg() instanceof BlockExpression))
		{
			argString = "(" + argString + ")";
		}
		return join("new", getExpType() + argString);
	}
}
