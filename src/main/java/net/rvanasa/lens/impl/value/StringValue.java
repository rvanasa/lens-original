package net.rvanasa.lens.impl.value;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.util.CollectionHelpers;

public class StringValue extends AbstractMemberValue
{
	private final String handle;
	
	public StringValue(String handle)
	{
		if(handle == null)
		{
			throw new LensException("String handle cannot be null");
		}
		this.handle = handle;
	}
	
	public String handle()
	{
		return handle;
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		return handle();
	}
	
	@Override
	public LensType getType()
	{
		return Lens.STR;
	}
	
	@Override
	public int size()
	{
		return handle().length();
	}
	
	@Override
	public Iterable<LensValue> toIterable()
	{
		return CollectionHelpers.map(handle().split(""), StringValue::new);
	}
	
	@Override
	public String getPrintString()
	{
		return handle().toString();
	}
	
	@Override
	public String toString()
	{
		return "\"" + getPrintString().replace("\n", "\\n") + "\"";
	}
	
	@Override
	public boolean isEqualComponent(LensValue value, Environment env)
	{
		if(value instanceof StringValue)
		{
			return handle().equals(((StringValue)value).handle());
		}
		return super.isEqualComponent(value, env);
	}
}
