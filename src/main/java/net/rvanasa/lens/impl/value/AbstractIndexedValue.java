package net.rvanasa.lens.impl.value;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.exception.LensException;

public abstract class AbstractIndexedValue extends AbstractMemberValue
{
	public AbstractIndexedValue()
	{
	}
	
	public AbstractIndexedValue(MemberLookup members)
	{
		super(members);
	}
	
	public abstract boolean isValidIndex(int index);
	
	public abstract LensValue get(int index);
	
	public abstract void set(int index, LensValue value);
	
	public int resolveIndex(LensValue key)
	{
		return Lens.INT.getTyped(key).handle();
	}
	
	@Override
	public boolean isIndex(LensValue key)
	{
		return isValidIndex(resolveIndex(key));
	}
	
	@Override
	public boolean isAssignableIndex(LensValue key)
	{
		return isValidIndex(resolveIndex(key));
	}
	
	@Override
	public LensValue getIndex(LensValue key)
	{
		int index = resolveIndex(key);
		if(!isValidIndex(index))
		{
			throw new LensException(this + " does not contain index: [" + index + "]");
		}
		return get(index);
	}
	
	@Override
	public void setIndex(LensValue key, LensValue value)
	{
		int index = resolveIndex(key);
		if(!isValidIndex(index))
		{
			throw new LensException(this + " cannot assign index: [" + index + "]");
		}
		set(index, value);
	}
}
