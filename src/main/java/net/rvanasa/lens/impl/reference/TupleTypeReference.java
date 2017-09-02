package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.type.TupleType;
import net.rvanasa.lens.util.CollectionHelpers;

public class TupleTypeReference extends AbstractTypeReference
{
	private final TypeReference[] types;
	
	public TupleTypeReference(TypeReference[] types)
	{
		this.types = types;
	}
	
	public TypeReference[] getTypes()
	{
		return types;
	}
	
	@Override
	public TupleType resolve(InvokeStack stack)
	{
		return new TupleType(CollectionHelpers.map(getTypes(), t -> t.resolve(stack), LensType[]::new));
	}
	
	@Override
	public String toString()
	{
		return "(" + joinDelim(", ", (Object[])getTypes()) + ")";
	}
}
