package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeReference;

public class IndexerTypeReference extends AbstractTypeReference
{
	private final TypeReference type;
	
	public IndexerTypeReference(TypeReference type)
	{
		this.type = type;
	}
	
	public TypeReference getType()
	{
		return type;
	}
	
	@Override
	public LensType resolve(InvokeStack stack)
	{
		return getType().resolve(stack).getIndexerType();
	}
	
	@Override
	public String toString()
	{
		return "<indexer>";
	}
}
