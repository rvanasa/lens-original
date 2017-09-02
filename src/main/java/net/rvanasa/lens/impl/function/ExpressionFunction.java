package net.rvanasa.lens.impl.function;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.param.AnonymousParam;

public class ExpressionFunction implements LensFunction
{
	private final LensParam param;
	private final LensType returnType;
	private final ExpressionFunctionHandle handle;
	
	public ExpressionFunction(LensType paramType, LensType returnType, ExpressionFunctionHandle value)
	{
		this(new AnonymousParam(paramType), returnType, value);
	}
	
	public ExpressionFunction(LensParam param, LensType returnType, ExpressionFunctionHandle value)
	{
		this.param = param;
		this.returnType = returnType;
		this.handle = value;
	}
	
	@Override
	public LensParam getParam()
	{
		return param;
	}
	
	public LensType getReturnType()
	{
		return returnType;
	}
	
	@Override
	public LensType getReturnType(LensType type)
	{
		return getReturnType();
	}
	
	public ExpressionFunctionHandle getHandle()
	{
		return handle;
	}
	
	@Override
	public boolean isValidArg(ParamMatcher arg)
	{
		return arg.matchesParam(getParam());
	}
	
	@Override
	public LensValue invoke(LensValue value)
	{
		throw new LensException(this + " must be invoked as an expression");
	}
	
	@Override
	public LensValue invoke(LensExpression args, InvokeStack stack)
	{
		try
		{
			return getHandle().invoke(args, stack);
		}
		catch(LensException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new LensException(e);
		}
	}
	
	@Override
	public String toString()
	{
		return getParam() + " => <" + getReturnType() + ">";
	}
	
	public interface ExpressionFunctionHandle
	{
		public LensValue invoke(LensExpression exp, InvokeStack stack) throws Exception;
	}
}
