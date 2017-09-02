package net.rvanasa.lens;

import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.context.InvokeContext;

public class InvokeStack
{
	private final InvokeStack parent;
	
	private final LensContext context;
	
	private boolean returned;
	private LensValue returnValue = Lens.UNDEFINED;
	
	public InvokeStack(LensContext context)
	{
		this(null, context);
	}
	
	public InvokeStack(InvokeStack parent)
	{
		this(parent, parent.getContext());
	}
	
	public InvokeStack(InvokeStack parent, LensContext context)
	{
		this.parent = parent;
		this.context = context;
	}
	
	public LensContext getContext()
	{
		return context;
	}
	
	public Environment getEnv()
	{
		return getContext().getEnv();
	}
	
	public boolean hasParent()
	{
		return parent != null;
	}
	
	public InvokeStack getParent()
	{
		return parent;
	}
	
	public InvokeStack getBaseInvocation()
	{
		return getContext() instanceof InvokeContext || !hasParent() ? this : getParent().getBaseInvocation();
	}
	
	public boolean isReturned()
	{
		return returned;
	}
	
	public LensValue getReturnValue()
	{
		return returnValue;
	}
	
	public void setReturnValue(LensValue value)
	{
		this.returned = true;
		this.returnValue = value;
		
		if(hasParent())
		{
			getParent().setReturnValue(value);
		}
	}
	
	public InvokeStack createChild()
	{
		return createContextual(new InvokeContext(getContext()));
	}
	
	public InvokeStack createContextual(LensContext context)
	{
		return new InvokeStack(this, context);
	}
}
