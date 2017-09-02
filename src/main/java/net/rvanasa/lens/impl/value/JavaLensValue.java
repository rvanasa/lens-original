package net.rvanasa.lens.impl.value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import net.rvanasa.lang.reflect.Reflector;
import net.rvanasa.lang.reflect.member.IMember;
import net.rvanasa.lang.reflect.search.IMemberQuery;
import net.rvanasa.lens.Lens.LensInvoke;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.exception.LensTargetException;
import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.function.SeqFunction;
import net.rvanasa.lens.impl.resolver.JavaLensValueResolver;
import net.rvanasa.lens.impl.type.JavaLensType;
import net.rvanasa.lens.util.CollectionHelpers;

public class JavaLensValue<T> implements LensValue
{
	private final T handle;
	
	private final JavaLensType<T> type;
	
	private final Reflector reflector;
	
	private final LensFunction function;
	
	@SuppressWarnings("unchecked")
	public JavaLensValue(T handle, JavaLensValueResolver resolver)
	{
		this(handle, (JavaLensType<T>)resolver.getJavaType(handle.getClass()));
	}
	
	public JavaLensValue(T handle, JavaLensType<T> type)
	{
		this.handle = handle;
		this.type = type;
		
		this.reflector = new Reflector(type.getJavaType(), handle);
		
		try
		{
			List<IMember> members = IMemberQuery.methods().annotated(LensInvoke.class).findAll(getReflector());
			if(members.size() == 1)
			{
				IMember member = members.get(0);
				this.function = new SeqFunction(CollectionHelpers.map(member.getParams(), param -> getJavaResolver().getType(param.getType()), LensType[]::new), getJavaResolver().getType(member.getType()), args -> {
					try
					{
						return getJavaResolver().getValue(member.invoke(CollectionHelpers.map(args, value -> value.handle(getJavaResolver()), Object[]::new)));
					}
					catch(Exception e)
					{
						throw new LensException("Failed to invoke " + this, e);
					}
				});
			}
			else if(members.isEmpty())
			{
				this.function = null;
			}
			else
			{
				throw new LensException("Conflicting invocation methods: " + String.join(" | ", CollectionHelpers.map(members, IMember::toString)));
			}
		}
		catch(Exception e)
		{
			throw new LensException("Failed to initialize object invocation", e);
		}
	}
	
	public T handle()
	{
		return handle;
	}
	
	@Override
	public T handle(LensValueResolver resolver)
	{
		return handle();
	}
	
	@Override
	public JavaLensType<T> getType()
	{
		return type;
	}
	
	public Reflector getReflector()
	{
		return reflector;
	}
	
	public Class<T> getJavaType()
	{
		return getType().getJavaType();
	}
	
	public JavaLensValueResolver getJavaResolver()
	{
		return getType().getJavaResolver();
	}
	
	@Override
	public boolean isValue(String id)
	{
		return Stream.concat(Arrays.stream(getJavaType().getFields()), Arrays.stream(getJavaType().getMethods()))
				.anyMatch(member -> member.getName().equals(id));
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return Arrays.stream(getJavaType().getFields())
				.anyMatch(member -> member.getName().equals(id));
	}
	
	@Override
	public LensValue get(String id)
	{
		for(Field field : getJavaType().getFields())
		{
			if(field.getName().equals(id))
			{
				return getJavaResolver().getFieldObject(field, handle());
			}
		}
		
		for(Method method : getJavaType().getMethods())
		{
			if(method.getName().equals(id))
			{
				return getJavaResolver().getFunctionObject(method, handle());
			}
		}
		
		throw new LensException("Could not find member '" + id + "' of " + this);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		try
		{
			getJavaType().getField(id).set(handle(), value.handle(getJavaResolver()));
		}
		catch(Exception e)
		{
			throw new LensException("Could not assign to '" + id + "' of " + this, e);
		}
	}
	
	@Override
	public LensFunction getFunction()
	{
		if(function == null)
		{
			throw new LensTargetException(this);
		}
		return function;
	}
	
	@Override
	public boolean isEqualComponent(LensValue value, Environment env)
	{
		if(value instanceof JavaLensValue)
		{
			JavaLensValue<?> java = (JavaLensValue<?>)value;
			return value == this || (getJavaType().equals(java.getJavaType()) && Objects.equals(handle(), java.handle()));
		}
		return LensValue.super.isEqualComponent(value, env);
	}
	
	@Override
	public String toString()
	{
		Object handle = handle();
		return handle != null ? handle.toString() : "static(" + getJavaType().getName() + ")";
	}
}
