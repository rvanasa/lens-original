package net.rvanasa.lens.impl.expression;

import java.util.function.BiFunction;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.impl.operator.MidfixOperator;
import net.rvanasa.lens.impl.operator.PostfixOperator;
import net.rvanasa.lens.impl.operator.PrefixOperator;
import net.rvanasa.lens.impl.value.FunctionValue;

public class OperatorDefExpression extends AbstractTypedExpression
{
	private final String symbol;
	private final LensExpression body;
	
	private final OperatorPosition position;
	
	private final TypeReference a, b, returnType;
	
	public OperatorDefExpression(String symbol, OperatorPosition pos, TypeReference a, TypeReference b, TypeReference returnType, LensExpression body)
	{
		super(Lens.VOID);
		
		this.symbol = symbol;
		this.body = body;
		
		this.position = pos;
		
		this.a = a;
		this.b = b;
		this.returnType = returnType;
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public LensExpression getBody()
	{
		return body;
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
	
	public TypeReference getReturnType()
	{
		return returnType;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		FunctionValue handle = Lens.DEF.getTyped(getBody().eval(stack));
		
		BiFunction<String, LensFunction, LensOperator> builder = getPosition() == OperatorPosition.MIDFIX ? MidfixOperator::new
				: (getPosition() == OperatorPosition.PREFIX ? PrefixOperator::new : PostfixOperator::new);
				
		LensOperator operator = builder.apply(getSymbol(), handle.getFunction());
		
		stack.getEnv().addOperator(operator);
		
		return Lens.UNDEFINED;
	}
	
	@Override
	public int getBlockPrecedence()
	{
		return 4;
	}
	
	@Override
	public String toString()
	{
		return join("opr", getSymbol(), "(" + getPosition().getStringValue(getSymbol(), getA().toString(), getB().toString()) + ")" + ":", getReturnType(), "=", getBody());
	}
}
