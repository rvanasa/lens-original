package net.rvanasa.lens.impl.function;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.impl.context.InvokeContext;

public class ParamExpFunction implements LensFunction
{
	private final LensContext context;
	
	private final LensParam param;
	private final LensType returnType;
	private final LensExpression body;
	
	public ParamExpFunction(LensContext context, LensParam param, LensType returnType, LensExpression body)
	{
		this.context = context;
		
		this.param = param;
		this.returnType = returnType;
		this.body = body;
	}
	
	public LensContext getContext()
	{
		return context;
	}
	
	@Override
	public LensParam getParam()
	{
		return param;
	}
	
	@Override
	public LensExpression getBodyExp()
	{
		return body;
	}
	
	public LensType getReturnType()
	{
		return returnType;
	}
	
	@Override
	public LensType getReturnType(LensType param)
	{
		return getReturnType();
	}
	
	@Override
	public boolean isValidArg(ParamMatcher arg)
	{
		return arg.matchesParam(getParam());
	}
	
	@Override
	public LensValue invoke(LensValue value)
	{
		InvokeContext context = new InvokeContext(getContext());
		getParam().setupContext(value, context);
		
		return getBodyExp().eval(new InvokeStack(context));
	}
	
	@Override
	public String toString()
	{
		return getParam() + " => " + getBodyExp();
	}
}
