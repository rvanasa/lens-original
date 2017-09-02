package net.rvanasa.lens.impl.param;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;

public class AnonymousParam implements LensParam
{
	public static final AnonymousParam ANY = new AnonymousParam(Lens.ANY);
	
	private final LensType type;
	
	public AnonymousParam(LensType type)
	{
		this.type = type;
	}
	
	@Override
	public LensType getType()
	{
		return type;
	}
	
	@Override
	public void setupContext(LensValue value, LensContext context)
	{
		context.addAnonymousValue(resolveValue(value));
	}
	
	@Override
	public String toString()
	{
		return "#" + getType();
	}
}
