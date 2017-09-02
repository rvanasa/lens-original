package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensType;

public class IdentifierTypeReference extends AbstractTypeReference
{
	private final String id;
	
	public IdentifierTypeReference(String id)
	{
		this.id = id;
	}
	
	public String getID()
	{
		return id;
	}
	
	@Override
	public LensType resolve(InvokeStack stack)
	{
		return stack.getContext().getValueType(getID());
	}
	
	@Override
	public String toString()
	{
		return "typeof(" + getID() + ")";
	}
}
