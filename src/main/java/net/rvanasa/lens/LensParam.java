package net.rvanasa.lens;

public interface LensParam extends ParamMatcher
{
	public LensType getType();
	
	default boolean isValid(LensValue value)
	{
		return getType().isInstance(value);
	}
	
	default LensValue resolveValue(LensValue value)
	{
		return getType().getTyped(value);
	}
	
	public void setupContext(LensValue value, LensContext context);
	
	@Override
	default boolean matchesParam(LensParam param)
	{
		return param.getType().isAssignableFrom(getType());
	}
}
