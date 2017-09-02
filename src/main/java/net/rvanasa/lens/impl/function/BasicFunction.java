package net.rvanasa.lens.impl.function;

import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.param.AnonymousParam;

public class BasicFunction implements LensFunction
{
	private final LensParam param;
	private final LensType returnType;
	private final BasicFunctionHandle handle;
	
	public BasicFunction(LensType paramType, LensType returnType, BasicFunctionHandle value)
	{
		this(new AnonymousParam(paramType), returnType, value);
	}
	
	public BasicFunction(LensParam param, LensType returnType, BasicFunctionHandle value)
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
	
	public BasicFunctionHandle getHandle()
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
		LensType returnType = getReturnType(value.getType());
		
		try
		{
			return returnType.getTyped(getHandle().invoke(getParam().resolveValue(value)));
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
	
	public interface BasicFunctionHandle
	{
		public LensValue invoke(LensValue value) throws Exception;
	}
}
