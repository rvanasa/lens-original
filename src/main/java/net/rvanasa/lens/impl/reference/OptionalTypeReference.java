package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.type.OptionalType;

public class OptionalTypeReference extends AbstractTypeReference
{
	private final TypeReference type;
	
	public OptionalTypeReference(TypeReference type)
	{
		this.type = type;
	}
	
	public TypeReference getType()
	{
		return type;
	}
	
	@Override
	public OptionalType resolve(InvokeStack stack)
	{
		return new OptionalType(getType().resolve(stack));
	}
	
	@Override
	public String toString()
	{
		return getType() + "?";
	}
}
