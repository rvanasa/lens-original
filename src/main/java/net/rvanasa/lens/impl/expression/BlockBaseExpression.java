package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.exception.LensStacktrace;

public class BlockBaseExpression extends MultiExpression
{
	private final TypeReference type;
	
	public BlockBaseExpression(TypeReference type, LensExpression[] expressions)
	{
		super(expressions);
		
		this.type = type;
	}
	
	@Override
	public TypeReference getExpType()
	{
		return type;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensType type = getExpType().resolve(stack);
		super.eval(stack.createChild());
		
		if(stack.isReturned())
		{
			LensValue value = stack.getReturnValue();
			try
			{
				return type.getTyped(value);
			}
			catch(LensException e)
			{
				throw new LensException("'" + value + "' cannot be returned as " + type, e);
			}
		}
		else if(type.isAssignableFrom(Lens.VOID))
		{
			return Lens.UNDEFINED;
		}
		else
		{
			throw new LensStacktrace(this, new LensException(getExpType() + " was never returned"));
		}
	}
	
	@Override
	public String toString()
	{
		return join("{", super.toString(), "}");
	}
}
