package net.rvanasa.lens;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import net.rvanasa.common.Numbers;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.type.AsyncType;
import net.rvanasa.lens.impl.type.BasicLensType;
import net.rvanasa.lens.impl.type.CorrespondingType;
import net.rvanasa.lens.impl.type.ListType;
import net.rvanasa.lens.impl.type.NumberType;
import net.rvanasa.lens.impl.type.OptionalType;
import net.rvanasa.lens.impl.value.BooleanValue;
import net.rvanasa.lens.impl.value.ContextValue;
import net.rvanasa.lens.impl.value.FunctionValue;
import net.rvanasa.lens.impl.value.MapValue;
import net.rvanasa.lens.impl.value.NullValue;
import net.rvanasa.lens.impl.value.NumberValue;
import net.rvanasa.lens.impl.value.StringValue;
import net.rvanasa.lens.impl.value.TypeValue;
import net.rvanasa.lens.impl.value.UndefinedValue;

public final class Lens
{
	private static final List<LensType> TYPES = new ArrayList<>();
	
	private static final <T extends LensType> T register(T type)
	{
		TYPES.add(type);
		return type;
	}
	
	public static List<LensType> getTypes()
	{
		return TYPES;
	}
	
	public static final LensValue TRUE = BooleanValue.TRUE;
	public static final LensValue FALSE = BooleanValue.FALSE;
	public static final LensValue NULL = NullValue.INSTANCE;
	public static final LensValue UNDEFINED = UndefinedValue.INSTANCE;
	
	public static final LensType ANY = register(new BasicLensType("any", type -> true, value -> true));
	public static final LensType UNKNOWN = new BasicLensType("unknown", type -> false, value -> false);
	
	public static final LensType VOID = register(new BasicLensType("void", null, LensValue::isVoid)
	{
		@Override
		public LensValue getTyped(LensValue value)
		{
			if(isInstance(value))
			{
				return Lens.UNDEFINED;
			}
			return super.getTyped(value);
		}
	});
	
	public static final LensType VAL = register(new BasicLensType("val", type -> type != VOID, value -> value != UNDEFINED));
	
	public static final LensType TYPE = register(new CorrespondingType<>("type", TypeValue.class));
	
	public static final LensType MAP = register(new CorrespondingType<MapValue>("map", MapValue.class)
	{
		@Override
		public MapValue getTyped(LensValue value)
		{
			if(value == UNDEFINED)
			{
				return new MapValue();
			}
			return super.getTyped(value);
		}
	});
	
	public static final ListType LIST = new ListType(ANY);
	
	public static final AsyncType ASYNC = new AsyncType(ANY);
	
	public static final CorrespondingType<ContextValue> CONTEXT = register(new CorrespondingType<>("context", ContextValue.class));
	
	public static final CorrespondingType<FunctionValue> DEF = register(new CorrespondingType<>("def", FunctionValue.class));
	
	public static final CorrespondingType<BooleanValue> BOOL = register(new CorrespondingType<>("bool", BooleanValue.class));
	
	public static final CorrespondingType<StringValue> STR = register(new CorrespondingType<StringValue>("str", StringValue.class)
	{
		@Override
		public StringValue getTyped(LensValue value)
		{
			if(value instanceof StringValue)
			{
				return (StringValue)value;
			}
			return new StringValue(value.toString());
		}
	});
	
	public static final NumberType<Number> NUM = register(new NumberType<Number>("num", 0, n -> {
		int i = n.intValue();
		return i == n.doubleValue() ? i : n;
	}, Numbers::getNumber)
	{
		@Override
		public boolean isAssignableFrom(LensType type)
		{
			return type instanceof NumberType;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public NumberValue<Number> getTyped(LensValue value)
		{
			if(value instanceof NumberValue)
			{
				return (NumberValue<Number>)value;
			}
			throw new LensException("Non-numeric value: " + value);
		}
	});
	
	public static final NumberType<Integer> INT = register(new NumberType<>("int", 5, Number::intValue, Integer::decode));
	public static final NumberType<Long> LONG = register(new NumberType<>("long", 6, Number::longValue, Long::decode));
	public static final NumberType<Float> FLOAT = register(new NumberType<>("float", 9, Number::floatValue, Float::parseFloat));
	public static final NumberType<Double> DOUBLE = register(new NumberType<>("double", 10, Number::doubleValue, Double::parseDouble));
	
	public static LensType[] getValueTypes(LensValue... values)
	{
		LensType[] types = new LensType[values.length];
		for(int i = 0; i < values.length; i++)
		{
			types[i] = values[i].getType();
		}
		return types;
	}
	
	public static LensType getCommonType(LensType a, LensType b)
	{
		if(a == ANY || b == ANY)
		{
			return ANY;
		}
		if(a == b)
		{
			return a;
		}
		else if(a.isAssignableFrom(b))
		{
			return a;
		}
		else if(b.isAssignableFrom(a))
		{
			return b;
		}
		else if(a == VOID)
		{
			return new OptionalType(b);
		}
		else if(b == VOID)
		{
			return new OptionalType(a);
		}
		else
		{
			return VAL;
		}
	}
	
	public static LensType getCommonType(LensType... types)
	{
		if(types.length > 0)
		{
			LensType type = types[0];
			for(int i = 1; i < types.length; i++)
			{
				type = getCommonType(type, types[i]);
				if(type == ANY)
				{
					break;
				}
			}
			return type;
		}
		return Lens.ANY;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends LensType> T cast(LensType value)
	{
		try
		{
			return (T)value;
		}
		catch(ClassCastException e)
		{
			throw new LensException("Invalid cast: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends LensValue> T cast(LensValue value)
	{
		try
		{
			return (T)value;
		}
		catch(ClassCastException e)
		{
			throw new LensException("Invalid cast: " + e.getMessage());
		}
	}
	
	@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface LensInject
	{
	}
	
	@Target({ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface LensInvoke
	{
	}
}
