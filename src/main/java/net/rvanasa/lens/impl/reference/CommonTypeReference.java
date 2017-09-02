package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeReference;

public class CommonTypeReference extends AbstractTypeReference
{
	public static TypeReference get(TypeReference... types)
	{
		if(types.length == 0)
		{
			return Lens.UNKNOWN;
		}
		TypeReference type = types[0];
		for(int i = 1; i < types.length; i++)
		{
			type = new CommonTypeReference(type, types[i]);
		}
		return type;
	}
	
	private final TypeReference a, b;
	
	public CommonTypeReference(TypeReference a, TypeReference b)
	{
		this.a = a;
		this.b = b;
	}
	
	public TypeReference getA()
	{
		return a;
	}
	
	public TypeReference getB()
	{
		return b;
	}
	
	@Override
	public LensType resolve(InvokeStack stack)
	{
		return Lens.getCommonType(getA().resolve(stack), getB().resolve(stack));
	}
	
	@Override
	public String toString()
	{
		return join(getA(), "|", getB());
	}
}
