package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensType;

public class ThisTypeReference extends AbstractTypeReference
{
	@Override
	public LensType resolve(InvokeStack stack)
	{
		return stack.getContext().getTargetValue().getType();
	}
	
	@Override
	public String toString()
	{
		return "<this>";
	}
}
