package net.rvanasa.lens;

import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.value.TupleValue;

public interface LensFunction
{
	public LensParam getParam();
	
	public LensType getReturnType(LensType param);
	
	default LensExpression getBodyExp()
	{
		throw new LensException("Native function body cannot be resolved");
	}
	
	public boolean isValidArg(ParamMatcher param);
	
	default LensValue invoke(LensExpression args, InvokeStack stack)
	{
		return invoke(args.eval(stack));
	}
	
	public LensValue invoke(LensValue value);
	
	default LensValue invoke(LensValue[] values)
	{
		return invoke(TupleValue.get(values));
	}
}
