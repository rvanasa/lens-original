package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;

public class VariableExpression extends AbstractTypedExpression
{
	private final String id;
	private final LensExpression value;
	
	public VariableExpression(String id, LensExpression value)
	{
		super(Lens.VOID);
		
		this.id = id;
		this.value = value;
	}
	
	public String getID()
	{
		return id;
	}
	
	public LensExpression getValue()
	{
		return value;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		stack.getContext().add(getID(), getValue().eval(stack));
		
		return Lens.UNDEFINED;
	}
	
	@Override
	public String toString()
	{
		return join("var", getID(), "=", getValue());
	}
}
