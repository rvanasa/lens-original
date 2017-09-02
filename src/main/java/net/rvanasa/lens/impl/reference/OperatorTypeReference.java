package net.rvanasa.lens.impl.reference;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.exception.LensException;

public class OperatorTypeReference extends AbstractTypeReference
{
	private final String symbol;
	private final OperatorPosition position;
	private final TypeReference a, b;
	
	public OperatorTypeReference(String symbol, OperatorPosition pos, TypeReference a, TypeReference b)
	{
		this.symbol = symbol;
		this.position = pos;
		
		this.a = a;
		this.b = b;
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public OperatorPosition getPosition()
	{
		return position;
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
		LensType a = getA().resolve(stack);
		LensType b = getB().resolve(stack);
		
		try
		{
			return stack.getContext().getOperatorReturnType(getSymbol(), getPosition(), a, b);
		}
		catch(LensException e)
		{
			return Lens.UNKNOWN;
		}
	}
	
	@Override
	public String toString()
	{
		return "<" + getPosition().getStringValue(getSymbol(), String.valueOf(getA()), String.valueOf(getB())) + ">";
	}
}
