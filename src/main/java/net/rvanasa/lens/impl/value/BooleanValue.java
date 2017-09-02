package net.rvanasa.lens.impl.value;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValueResolver;

public class BooleanValue extends AbstractMemberValue
{
	public static final BooleanValue TRUE = new BooleanValue(true);
	public static final BooleanValue FALSE = new BooleanValue(false);
	
	private final boolean handle;
	
	public static BooleanValue get(boolean flag)
	{
		return flag ? TRUE : FALSE;
	}
	
	private BooleanValue(boolean handle)
	{
		this.handle = handle;
	}
	
	public boolean handle()
	{
		return handle;
	}
	
	@Override
	public Boolean handle(LensValueResolver resolver)
	{
		return handle();
	}
	
	public BooleanValue not()
	{
		return get(!handle());
	}
	
	@Override
	public LensType getType()
	{
		return Lens.BOOL;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(handle());
	}
}
