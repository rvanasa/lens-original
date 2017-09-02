package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensType;

public class AnonymousTypeReference extends AbstractTypeReference
{
	public static final AnonymousTypeReference INSTANCE = new AnonymousTypeReference();
	
	private AnonymousTypeReference()
	{
	}
	
	@Override
	public LensType resolve(InvokeStack stack)
	{
		return stack.getContext().getAnonymousType();
	}
	
	@Override
	public String toString()
	{
		return "typeof(#)";
	}
}
