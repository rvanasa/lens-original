package net.rvanasa.lens.impl.function;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.param.TupleParam;
import net.rvanasa.lens.impl.value.TupleValue;

public class BinaryFunction implements LensFunction
{
	private final LensType a, b;
	private final LensParam param;
	private final LensType returnType;
	private final BinaryFunctionHandle handle;
	
	public BinaryFunction(LensType a, LensType b, LensType returnType, BinaryFunctionHandle handle)
	{
		this.a = a;
		this.b = b;
		
		this.param = TupleParam.get(a, b);
		this.returnType = returnType;
		this.handle = handle;
	}
	
	public LensType getA()
	{
		return a;
	}
	
	public LensType getB()
	{
		return b;
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
	
	public BinaryFunctionHandle getHandle()
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
		
		TupleValue tuple = Lens.cast(getParam().resolveValue(value));
		
		try
		{
			return returnType.getTyped(getHandle().invoke(tuple.getArray()[0], tuple.getArray()[1]));
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
	
	public interface BinaryFunctionHandle
	{
		public LensValue invoke(LensValue a, LensValue b) throws Exception;
	}
}
