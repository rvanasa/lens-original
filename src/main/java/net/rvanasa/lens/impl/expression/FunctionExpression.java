package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.function.ParamExpFunction;
import net.rvanasa.lens.impl.param.TupleParam;
import net.rvanasa.lens.impl.reference.FunctionTypeReference;
import net.rvanasa.lens.impl.value.FunctionValue;

public class FunctionExpression extends AbstractExpression
{
	private final String name;
	private final LensParam param;
	private final LensExpression body;
	
	private final FunctionTypeReference functionType;
	
	public FunctionExpression(String name, LensParam param, LensExpression body)
	{
		this.name = name;
		this.param = param;
		this.body = body;
		
		this.functionType = new FunctionTypeReference(getParam().getType(), getReturnType());
	}
	
	public String getName()
	{
		return name;
	}
	
	public LensParam getParam()
	{
		return param;
	}
	
	public LensExpression getBody()
	{
		return body;
	}
	
	public TypeReference getReturnType()
	{
		return getBody().getExpType();
	}
	
	@Override
	public FunctionTypeReference getExpType()
	{
		return functionType;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensType returnType = getReturnType().resolve(stack);
		LensFunction function = new ParamExpFunction(stack.getContext(), getParam(), returnType, getBody());
		FunctionValue value = new FunctionValue(function);
		stack.getContext().add(getName(), value);
		
		return value;
	}
	
	@Override
	public int getBlockPrecedence()
	{
		return 5;
	}
	
	@Override
	public String toString()
	{
		String param = getParam().toString();
		return join("def", getName() + (getParam() instanceof TupleParam ? param : "(" + param + ")") + ":", getReturnType(), "=", getBody());
	}
}
