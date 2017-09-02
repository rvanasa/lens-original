package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.type.ListType;

public class ListTypeReference extends AbstractTypeReference
{
	private final TypeReference type;
	
	public ListTypeReference(TypeReference type)
	{
		this.type = type;
	}
	
	public TypeReference getType()
	{
		return type;
	}
	
	@Override
	public ListType resolve(InvokeStack stack)
	{
		return new ListType(getType().resolve(stack));
	}
	
	@Override
	public String toString()
	{
		return getType() + "[]";
	}
}
