package net.rvanasa.lens.impl.type;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.resolver.JavaLensValueResolver;
import net.rvanasa.lens.impl.value.JavaLensValue;

public class JavaLensType<T> implements LensType
{
	private final Class<T> javaType;
	
	private final JavaLensValueResolver resolver;
	
	private final JavaLensValue<T> staticValue;
	
	public JavaLensType(Class<T> javaType, JavaLensValueResolver resolver)
	{
		this.javaType = javaType;
		
		this.resolver = resolver;
		
		this.staticValue = new JavaLensValue<T>(null, this);
	}
	
	public Class<T> getJavaType()
	{
		return javaType;
	}
	
	public JavaLensValueResolver getJavaResolver()
	{
		return resolver;
	}
	
	@Override
	public JavaLensValue<T> getStaticValue()
	{
		return staticValue;
	}
	
	@Override
	public boolean isInstance(LensValue value)
	{
		Object handle = value.handle(getJavaResolver());
		return handle != null && getJavaType().isInstance(handle);
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		if(type instanceof JavaLensType)
		{
			JavaLensType<?> tp = (JavaLensType<?>)type;
			return getJavaType().isAssignableFrom(tp.getJavaType());
		}
		return false;
	}
	
	@Override
	public LensValue newInstance(LensValue arg)
	{
		try
		{
			for(Constructor<?> constructor : getJavaType().getConstructors())
			{
				TupleType paramType = new TupleType(Arrays.stream(constructor.getParameterTypes())
						.map(getJavaResolver()::getType)
						.toArray(LensType[]::new));
						
				if(paramType.isInstance(arg))
				{
					Object[] argArray = paramType.getTyped(arg).handle(getJavaResolver());
					return getJavaResolver().getValue(constructor.newInstance(argArray));
				}
			}
			
			throw new LensException("Could not find suitable constructor for args: " + arg);
		}
		catch(Exception e)
		{
			throw new LensException("Failed to instantitate " + this + " with args: " + arg, e);
		}
	}
	
	@Override
	public String toString()
	{
		return getJavaType().getName();
	}
}
