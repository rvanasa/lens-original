package net.rvanasa.lens.impl.value;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.function.SeqFunction;
import net.rvanasa.lens.impl.type.AsyncType;
import net.rvanasa.lens.impl.type.FunctionType;

public class AsyncValue extends AbstractMemberValue
{
	private final AsyncType type;
	
	private final List<Consumer<LensValue>> callbacks = new ArrayList<>();
	
	private LensValue value = Lens.UNDEFINED;
	private boolean loaded;
	
	public AsyncValue(LensType type)
	{
		super(new MemberLookup());
		
		FunctionType functionType = new FunctionType(type, Lens.ANY);
		getMembers().function(new SeqFunction(functionType, Lens.ANY, args -> {
			AsyncValue async = new AsyncValue(Lens.ANY);
			callback(v -> async.update(functionType.getTyped(args[0]).getFunction().invoke(v)));
			return async;
		}));
		
		this.type = new AsyncType(type);
	}
	
	public AsyncValue(LensType type, LensValue value)
	{
		this(type);
		update(value);
	}
	
	@Override
	public AsyncType getType()
	{
		return type;
	}
	
	private List<Consumer<LensValue>> getCallbacks()
	{
		return callbacks;
	}
	
	public void callback(Consumer<LensValue> callback)
	{
		if(isLoaded())
		{
			callback.accept(getValue());
		}
		else
		{
			getCallbacks().add(callback);
		}
	}
	
	public boolean isLoaded()
	{
		return loaded;
	}
	
	public void update(LensValue value)
	{
		boolean init = !isLoaded();
		
		this.value = getType().getValueType().getTyped(value);
		this.loaded = true;
		
		if(init)
		{
			getCallbacks().forEach(callback -> callback.accept(getValue()));
			getCallbacks().clear();
		}
	}
	
	public LensValue getValue()
	{
		return value;
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		return getValue().handle(resolver);
	}
	
	@Override
	public boolean isValue(String id)
	{
		return false;
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return false;
	}
	
	@Override
	public LensValue get(String id)
	{
		AsyncValue async = new AsyncValue(Lens.VAL);
		callback(v -> async.update(v.get(id)));
		return async;
	}
	
	@Override
	public LensValue getIndex(LensValue key)
	{
		AsyncValue async = new AsyncValue(Lens.VAL);
		callback(v -> async.update(v.getIndex(key)));
		return async;
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		callback(v -> v.set(id, value));
	}
	
	@Override
	public void setIndex(LensValue key, LensValue value)
	{
		callback(v -> v.setIndex(key, value));
	}
	
	@Override
	public boolean isEqualComponent(LensValue value, Environment env)
	{
		if(isLoaded())
		{
			return getValue().isEqualComponent(value, env);
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return "async " + (isLoaded() ? getValue() : "?<" + getType() + ">");
	}
}
