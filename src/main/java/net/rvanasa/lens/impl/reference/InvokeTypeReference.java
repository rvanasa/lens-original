package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeReference;

public class InvokeTypeReference extends AbstractTypeReference
{
	private final TypeReference target;
	private final TypeReference arg;
	
	public InvokeTypeReference(TypeReference target, TypeReference arg)
	{
		this.target = target;
		this.arg = arg;
	}
	
	public TypeReference getTarget()
	{
		return target;
	}
	
	public TypeReference getArg()
	{
		return arg;
	}
	
	@Override
	public LensType resolve(InvokeStack stack)
	{
		return getTarget().resolve(stack).getInvokeType(getArg().resolve(stack));
	}
	
	@Override
	public String toString()
	{
		return "<invoke:" + getTarget() + "(" + getArg() + ")>";
	}
}
