package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.type.FunctionType;

public class FunctionTypeReference extends AbstractTypeReference
{
	private final TypeReference param, returnType;
	
	public FunctionTypeReference(TypeReference param, TypeReference returnType)
	{
		this.param = param;
		this.returnType = returnType;
	}
	
	public TypeReference getParam()
	{
		return param;
	}
	
	public TypeReference getReturnType()
	{
		return returnType;
	}
	
	@Override
	public FunctionType resolve(InvokeStack stack)
	{
		return new FunctionType(getParam().resolve(stack), getReturnType().resolve(stack));
	}
	
	@Override
	public String toString()
	{
		return getParam() + " => " + getReturnType();
	}
}
