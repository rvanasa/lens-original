package net.rvanasa.lens;

import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.impl.context.Environment;

public interface LensContext
{
	public Environment getEnv();
	
	public LensValue getTargetValue();
	
	public LensContext getParent();
	
	public boolean isValue(String id);
	
	default LensType getValueType(String id)
	{
		return isValue(id) ? get(id).getType() : Lens.UNKNOWN;
	}
	
	public LensValue get(String id);
	
	public void set(String id, LensValue value);
	
	public void add(String id, LensValue value);
	
	public LensFunction getFunction(String id, ParamMatcher args);
	
	public LensOperator getOperator(String symbol, OperatorPosition type, LensType a, LensType b);
	
	default LensType getOperatorReturnType(String symbol, OperatorPosition type, LensType a, LensType b)
	{
		return getOperator(symbol, type, a, b).getReturnType(a, b);
	}
	
	public boolean isType(String name);
	
	public LensType getType(String name);
	
	public boolean isAnonymousPresent();
	
	public LensValue getNextAnonymous();
	
	public LensValue viewNextAnonymous();
	
	public void addAnonymousValue(LensValue value);
	
	public void clearAnonymousValues();
	
	default LensType getAnonymousType()
	{
		return isAnonymousPresent() ? viewNextAnonymous().getType() : Lens.UNKNOWN;
	}
	
	public TypeContext createTypeContext();
	
	default LensValue eval(String data)
	{
		return getEnv().getEvaluator().eval(this, data);
	}
}
