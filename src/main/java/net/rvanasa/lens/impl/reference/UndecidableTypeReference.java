package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.exception.LensException;

public class UndecidableTypeReference extends AbstractTypeReference
{
	public static final UndecidableTypeReference INSTANCE = new UndecidableTypeReference();
	
	private UndecidableTypeReference()
	{
	}
	
	@Override
	public LensType resolve(InvokeStack stack)
	{
		throw new LensException("Undecidable type reference");
	}
	
	@Override
	public String toString()
	{
		return "<undecidable>";
	}
}
