package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.impl.reference.FunctionTypeReference;
import net.rvanasa.lens.impl.reference.OperatorTypeReference;
import net.rvanasa.lens.impl.value.FunctionValue;

public class OperatorHandleExpression extends AbstractTypedExpression
{
	private final String symbol;
	private final OperatorPosition pos;
	
	private final TypeReference a, b;
	
	public OperatorHandleExpression(String symbol, OperatorPosition pos, TypeReference a, TypeReference b)
	{
		super(new FunctionTypeReference(Lens.ANY, new OperatorTypeReference(symbol, pos, a, b)));
		
		this.symbol = symbol;
		this.pos = pos;
		
		this.a = a;
		this.b = b;
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public OperatorPosition getPosition()
	{
		return pos;
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
	public LensValue eval(InvokeStack stack)
	{
		LensOperator operator = stack.getContext().getOperator(getSymbol(), getPosition(), getA().resolve(stack), getB().resolve(stack));
		return new FunctionValue(operator.asFunction());
	}
	
	@Override
	public String toString()
	{
		return "opr(" + join(getPosition().getStringValue(getSymbol(), getA().toString(), getB().toString())) + ")";
	}
}
