package net.rvanasa.lens.impl.resolver;

import net.rvanasa.lens.LensImporter;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeContext;
import net.rvanasa.lens.impl.context.ObjectContext;
import net.rvanasa.lens.impl.value.MapValue;

public class LensEvalImporter implements LensImporter
{
	private final String data;
	
	public LensEvalImporter(String data)
	{
		this.data = data;
	}
	
	public String getData()
	{
		return data;
	}
	
	@Override
	public LensValue handleImport(TypeContext context)
	{
		LensValue value = new MapValue();
		new ObjectContext(value, context.getLensContext()).eval(getData());
		return value;
	}
}
