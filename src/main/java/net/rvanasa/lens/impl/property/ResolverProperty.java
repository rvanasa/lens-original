package net.rvanasa.lens.impl.property;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensProperty;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensException;

public class ResolverProperty<T> implements LensProperty
{
	private final LensValueResolver resolver;
	
	private final Supplier<? super T> getter;
	private final Consumer<T> setter;
	
	public ResolverProperty(LensValueResolver resolver, Supplier<? super T> getter, Consumer<T> setter)
	{
		this.resolver = resolver;
		this.getter = getter;
		this.setter = setter;
	}
	
	public LensValueResolver getResolver()
	{
		return resolver;
	}
	
	@Override
	public LensValue get()
	{
		return getResolver().getValue(getter.get());
	}
	
	@Override
	public void set(LensValue value)
	{
		try
		{
			@SuppressWarnings("unchecked")
			T handle = (T)value.handle(getResolver());
			setter.accept(handle);
		}
		catch(ClassCastException e)
		{
			throw new LensException(e.getMessage());
		}
	}
}
