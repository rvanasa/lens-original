package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.AbstractStringFormer;

public abstract class AbstractTypeReference extends AbstractStringFormer implements TypeReference
{
	@Override
	public abstract String toString();
}
