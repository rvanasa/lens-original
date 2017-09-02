package net.rvanasa.lens;

import java.util.Map;

public interface LensAST
{
	public String getName();
	
	public LensAST getParent();
	
	public Map<String, LensAST> getChildren();
	
	public LensExpression getExpression();
	
	public String toLens();
}
