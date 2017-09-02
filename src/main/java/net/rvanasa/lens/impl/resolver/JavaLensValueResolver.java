package net.rvanasa.lens.impl.resolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.rvanasa.lang.Types;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensImportManager;
import net.rvanasa.lens.LensImporter;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.function.SeqFunction;
import net.rvanasa.lens.impl.param.TupleParam;
import net.rvanasa.lens.impl.type.FunctionType;
import net.rvanasa.lens.impl.type.JavaLensType;
import net.rvanasa.lens.impl.type.ListType;
import net.rvanasa.lens.impl.value.BooleanValue;
import net.rvanasa.lens.impl.value.FunctionValue;
import net.rvanasa.lens.impl.value.JavaLensValue;
import net.rvanasa.lens.impl.value.ListValue;
import net.rvanasa.lens.impl.value.NumberValue;
import net.rvanasa.lens.impl.value.StringValue;
import net.rvanasa.lens.impl.value.TupleValue;
import net.rvanasa.lens.util.CollectionHelpers;

public class JavaLensValueResolver implements LensValueResolver, LensImportManager
{
	private final ClassLoader loader;
	
	private final List<JavaConverterHandle<?>> converters = new ArrayList<>();
	
	public JavaLensValueResolver()
	{
		this(ClassLoader.getSystemClassLoader());
	}
	
	@SuppressWarnings("unchecked")
	public JavaLensValueResolver(ClassLoader loader)
	{
		this.loader = loader;
		
		addConverter(int.class, Lens.INT, obj -> new NumberValue<>(Lens.INT, obj));
		addConverter(long.class, Lens.LONG, obj -> new NumberValue<>(Lens.LONG, obj));
		addConverter(double.class, Lens.DOUBLE, obj -> new NumberValue<>(Lens.DOUBLE, obj));
		addConverter(float.class, Lens.FLOAT, obj -> new NumberValue<>(Lens.FLOAT, obj));
		
		addConverter(String.class, Lens.STR, StringValue::new);
		
		addConverter(List.class, Lens.LIST, list -> {
			ArrayList<LensValue> values = CollectionHelpers.map((List<?>)list, this::getValue);
			return new ListValue(new ListType(Lens.getCommonType(CollectionHelpers.map(values, LensValue::getType, LensType[]::new))), values);
		});
		
		addConverter(Runnable.class, FunctionType.RUNNABLE, obj -> new FunctionValue(new SeqFunction(Lens.VOID, Lens.VOID, args -> {
			obj.run();
			return Lens.UNDEFINED;
		})));
		addConverter(Consumer.class, FunctionType.CONSUMER, obj -> new FunctionValue(new SeqFunction(Lens.VAL, Lens.VOID, args -> {
			obj.accept(TupleValue.get(args).handle(this));
			return Lens.UNDEFINED;
		})));
		addConverter(Supplier.class, FunctionType.SUPPLIER, obj -> new FunctionValue(new SeqFunction(Lens.VOID, Lens.VAL, args -> getValue(obj.get()))));
		addConverter(Predicate.class, FunctionType.PREDICATE, obj -> new FunctionValue(new SeqFunction(Lens.VAL, Lens.BOOL, args -> BooleanValue.get(obj.test(args[0].handle(this))))));
		addConverter(Function.class, new FunctionType(Lens.VAL, Lens.VAL), obj -> new FunctionValue(new SeqFunction(Lens.VAL, Lens.VAL, args -> getValue(obj.apply(args[0].handle(this))))));
		
		addConverter(void.class, Lens.VOID, obj -> Lens.UNDEFINED);
		addConverter(null, Lens.VOID, obj -> Lens.UNDEFINED);
		addConverter(Object.class, Lens.UNKNOWN, obj -> new JavaLensValue<>(obj, this));
	}
	
	public ClassLoader getLoader()
	{
		return loader;
	}
	
	public List<JavaConverterHandle<?>> getConverters()
	{
		return converters;
	}
	
	public <T> void addConverter(Class<T> clazz, LensType type, Function<T, LensValue> handle)
	{
		getConverters().add(new JavaConverterHandle<>(clazz, type, handle));
	}
	
	public JavaLensType<?> getJavaType(String id)
	{
		try
		{
			return getJavaType(getLoader().loadClass(id));
		}
		catch(ClassNotFoundException e)
		{
			throw new LensException("Java class not found: " + id);
		}
	}
	
	public <T> JavaLensType<T> getJavaType(Class<T> clazz)
	{
		return new JavaLensType<>(clazz, this);
	}
	
	@Override
	public LensType getTypeUncertain(String id)
	{
		try
		{
			return getJavaType(id);
		}
		catch(LensException e)
		{
			return Lens.UNKNOWN;
		}
	}
	
	public LensType getTypeFromObject(Object obj)
	{
		return getType(obj.getClass());
	}
	
	public LensType getType(Class<?> clazz)
	{
		if(LensValue.class.isAssignableFrom(clazz))
		{
			return Lens.VAL;
		}
		
		for(JavaConverterHandle<?> converter : getConverters())
		{
			if(converter.isValidType(clazz))
			{
				return converter.getType();
			}
		}
		
		return getJavaType(clazz);
	}
	
	@Override
	public LensValue getValue(Object obj)
	{
		if(obj instanceof LensValue)
		{
			return (LensValue)obj;
		}
		
		for(JavaConverterHandle<?> converter : getConverters())
		{
			if(converter.isValid(obj))
			{
				return converter.getValue(obj);
			}
		}
		
		throw new LensException("Could not find converter for Java object: " + obj);
	}
	
	public LensValue getFieldObject(Field field, Object target)
	{
		try
		{
			return getValue(field.get(target));
		}
		catch(Exception e)
		{
			throw new LensException(e);
		}
	}
	
	public LensValue getFunctionObject(Method method, Object target)
	{
		return new FunctionValue(getFunction(method, target));
	}
	
	public SeqFunction getFunction(Method method, Object target)
	{
		LensParam param = TupleParam.get(CollectionHelpers.map(method.getParameterTypes(), this::getType, LensType[]::new));
		LensType returnType = getType(method.getReturnType());
		
		if(returnType == Lens.UNKNOWN)
		{
			returnType = Lens.ANY;
		}
		
		return new SeqFunction(param, returnType, args -> {
			try
			{
				return getValue(method.invoke(target, CollectionHelpers.map(args, value -> value.handle(this), Object[]::new)));
			}
			catch(InvocationTargetException e)
			{
				throw new LensException(e.getCause());
			}
			catch(Exception e)
			{
				throw new LensException(e);
			}
		});
	}
	
	@Override
	public LensImporter createImporter(String path)
	{
		return new JavaClassImporter(this, path);
	}
	
	public static class JavaConverterHandle<T>
	{
		private final Class<T> clazz;
		
		private final LensType type;
		private final Function<T, LensValue> handle;
		
		public JavaConverterHandle(Class<T> clazz, LensType type, Function<T, LensValue> handle)
		{
			this.clazz = clazz;
			this.type = type;
			
			this.handle = handle;
		}
		
		public Class<T> getJavaClass()
		{
			return clazz;
		}
		
		public LensType getType()
		{
			return type;
		}
		
		private Function<T, LensValue> getHandle()
		{
			return handle;
		}
		
		public boolean isValid(Object obj)
		{
			if(getJavaClass() == null || obj == null)
			{
				return getJavaClass() == obj;
			}
			
			return obj == null ? getJavaClass() == null : getJavaClass().isAssignableFrom(Types.getPrimitiveFromType(obj.getClass()));
		}
		
		public boolean isValidType(Class<?> clazz)
		{
			return getJavaClass() == null ? clazz == null : getJavaClass().isAssignableFrom(Types.getPrimitiveFromType(clazz));
		}
		
		@SuppressWarnings("unchecked")
		public LensValue getValue(Object obj)
		{
			return getHandle().apply((T)obj);
		}
	}
}
