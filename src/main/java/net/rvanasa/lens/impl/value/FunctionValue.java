package net.rvanasa.lens.impl.value;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.impl.type.FunctionType;

public class FunctionValue extends AbstractMemberValue
{
	private final FunctionType type;
	private final LensFunction function;
	
	public FunctionValue(LensFunction function)
	{
		LensParam param = function.getParam();
		this.type = new FunctionType(param.getType(), function.getReturnType(param.getType()));
		this.function = function;
	}
	
	@Override
	public LensFunction getFunction()
	{
		return function;
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		if(FunctionType.RUNNABLE.isAssignableFrom(getType()))
		{
			return (Runnable)() -> invoke();
		}
		if(FunctionType.SUPPLIER.isAssignableFrom(getType()))
		{
			return (Supplier<?>)() -> invoke().handle(resolver);
		}
		if(FunctionType.CONSUMER.isAssignableFrom(getType()))
		{
			return (Consumer<?>)obj -> invoke(resolver.getValue(obj));
		}
		if(FunctionType.PREDICATE.isAssignableFrom(getType()))
		{
			return (Predicate<?>)obj -> Lens.BOOL.getTyped(invoke(resolver.getValue(obj))).handle();
		}
		if(FunctionType.FUNCTION.isAssignableFrom(getType()))
		{
			return (Function<?, ?>)obj -> invoke(resolver.getValue(obj)).handle(resolver);
		}
		return getFunction();
	}
	
	@Override
	public FunctionType getType()
	{
		return type;
	}
	
	@Override
	public String toString()
	{
		return getFunction().toString();
	}
	
	@Override
	public String getPrintString()
	{
		return "def " + getType().toString();
	}
}
