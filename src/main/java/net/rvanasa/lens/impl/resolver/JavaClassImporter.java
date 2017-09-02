package net.rvanasa.lens.impl.resolver;

import net.rvanasa.lens.LensImporter;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeContext;

public class JavaClassImporter implements LensImporter
{
	private final JavaLensValueResolver resolver;
	private final String path;
	
	public JavaClassImporter(JavaLensValueResolver resolver, String path)
	{
		this.resolver = resolver;
		this.path = path;
	}
	
	public JavaLensValueResolver getResolver()
	{
		return resolver;
	}
	
	public String getPath()
	{
		return path;
	}
	
	@Override
	public LensValue handleImport(TypeContext context)
	{
		String name = getPath().substring(getPath().lastIndexOf('.') + 1);
		LensType type = getResolver().getJavaType(getPath());
		
		context.setType(name, type);
		return type.getStaticValue();
	}
}
