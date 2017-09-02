package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.reference.IdentifierTypeReference;

public class IdentifierExpression extends AbstractTypedExpression
{
	private final String id;
	
	public IdentifierExpression(String id)
	{
		super(new IdentifierTypeReference(id));
		
		this.id = id;
	}
	
	public String getID()
	{
		return id;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensValue value = stack.getContext().get(getID());
		// if(value == Lens.UNDEFINED && !stack.getContext().isValue(getID()))
		// {
		// throw new LensPointerException(getID());
		// }
		return value;
	}
	
	@Override
	public void assign(LensValue value, InvokeStack stack)
	{
		stack.getContext().set(getID(), value);
	}
	
	@Override
	public String toString()
	{
		return getID();
	}
}
