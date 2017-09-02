package net.rvanasa.lens.impl.param;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;

public class EmptyParam implements LensParam
{
	public static final EmptyParam INSTANCE = new EmptyParam();
	
	private EmptyParam()
	{
	}
	
	@Override
	public LensType getType()
	{
		return Lens.VOID;
	}
	
	@Override
	public void setupContext(LensValue value, LensContext context)
	{
	}
	
	@Override
	public String toString()
	{
		return "()";
	}
}
