
package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.function.ParamExpFunction;
import net.rvanasa.lens.impl.reference.FunctionTypeReference;
import net.rvanasa.lens.impl.value.FunctionValue;

public class LambdaExpression extends AbstractExpression
{
	private final FunctionTypeReference type;
	
	private final LensParam param;
	private final LensExpression body;
	
	public LambdaExpression(LensParam param, LensExpression body)
	{
		this.type = new FunctionTypeReference(param.getType(), body.getExpType());
		
		this.param = param;
		this.body = body;
	}
	
	@Override
	public FunctionTypeReference getExpType()
	{
		return type;
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
		return getExpType().getReturnType();
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensType returnType = getReturnType().resolve(stack.createContextual(stack.getContext()));
		ParamExpFunction function = new ParamExpFunction(stack.getContext(), getParam(), returnType, getBody());
		return new FunctionValue(function);
	}
	
	@Override
	public String toString()
	{
		return join(getParam(), "=>", getBody());
	}
}
