package net.rvanasa.lens.impl.context.type;

import java.util.HashMap;
import java.util.Map;

import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensImporter;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeContext;

public class BaseTypeContext implements TypeContext
{
	private final LensContext context;
	
	private final Map<String, LensType> types = new HashMap<String, LensType>();
	
	private final Map<String, LensValue> imports = new HashMap<>();
	
	public BaseTypeContext(LensContext context)
	{
		this.context = context;
	}
	
	@Override
	public Map<String, LensType> getTypeMap()
	{
		return types;
	}
	
	@Override
	public LensContext getLensContext()
	{
		return context;
	}
	
	@Override
	public TypeContext getParent()
	{
		return this;
	}
	
	public Map<String, LensValue> getImports()
	{
		return imports;
	}
	
	@Override
	public boolean isImported(String path)
	{
		return getImports().containsKey(path);
	}
	
	@Override
	public LensValue handleImport(String path)
	{
		if(!isImported(path))
		{
			LensImporter handler = getEnv().getImportResolver().createImporter(path);
			
			LensValue value = handler.handleImport(this);
			getImports().put(path, value);
			
			return value;
		}
		else
		{
			return getImports().get(path);
		}
	}
}
