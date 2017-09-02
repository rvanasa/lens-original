package net.rvanasa.lens.impl.property;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensProperty;

public class ReferenceProperty implements LensProperty
{
	private final Supplier<LensValue> getter;
	private final Consumer<LensValue> setter;
	
	public ReferenceProperty(Supplier<LensValue> getter, Consumer<LensValue> setter)
	{
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public LensValue get()
	{
		return getter.get();
	}
	
	@Override
	public void set(LensValue value)
	{
		setter.accept(value);
	}
}
