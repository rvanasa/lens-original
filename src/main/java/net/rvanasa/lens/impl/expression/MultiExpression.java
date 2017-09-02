package net.rvanasa.lens.impl.expression;

import java.util.Arrays;
import java.util.Comparator;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.exception.LensStacktrace;
import net.rvanasa.lens.util.CollectionHelpers;

public class MultiExpression extends AbstractTypedExpression
{
	private final LensExpression[] expressions;
	
	public MultiExpression(LensExpression[] expressions)
	{
		super(Lens.VOID);
		
		this.expressions = expressions;
		
		Arrays.asList(expressions).sort(Comparator.comparingInt(LensExpression::getBlockPrecedence).reversed());
	}
	
	public LensExpression[] getExpressions()
	{
		return expressions;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		int size = getExpressions().length;
		for(int i = 0; i < size; i++)
		{
			LensExpression exp = getExpressions()[i];
			try
			{
				exp.eval(stack);
				if(stack.isReturned())
				{
					break;
				}
			}
			catch(Exception e)
			{
				throw new LensStacktrace(exp, e);
			}
		}
		return Lens.UNDEFINED;
	}
	
	@Override
	public String toString()
	{
		return join(CollectionHelpers.map(getExpressions(), exp -> {
			String s = exp.toString();
			return s.endsWith("}") ? s : s + ";";
		}, Object[]::new));
	}
}
