package net.rvanasa.lens.impl.function;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.param.AnonymousParam;
import net.rvanasa.lens.impl.param.TupleParam;
import net.rvanasa.lens.impl.value.TupleValue;

public class SeqFunction implements LensFunction
{
	private final LensParam param;
	private final LensType returnType;
	private final TransformFunctionHandle handle;
	
	public SeqFunction(LensType[] paramTypes, LensType returnType, TransformFunctionHandle handle)
	{
		this(TupleParam.get(paramTypes), returnType, handle);
	}
	
	public SeqFunction(LensType paramType, LensType returnType, TransformFunctionHandle handle)
	{
		this(new AnonymousParam(paramType), returnType, handle);
	}
	
	public SeqFunction(LensParam param, LensType returnType, TransformFunctionHandle handle)
	{
		this.param = param;
		this.returnType = returnType;
		this.handle = handle;
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
	
	public TransformFunctionHandle getHandle()
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
		
		value = getParam().resolveValue(value);
		
		LensValue[] args;
		if(value instanceof TupleValue)
		{
			args = ((TupleValue)value).getArray();
		}
		else if(value == Lens.UNDEFINED)
		{
			args = new LensValue[0];
		}
		else
		{
			args = new LensValue[] {value};
		}
		
		try
		{
			return returnType.getTyped(getHandle().invoke(args));
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
	
	public interface TransformFunctionHandle
	{
		public LensValue invoke(LensValue[] args) throws Exception;
	}
}
