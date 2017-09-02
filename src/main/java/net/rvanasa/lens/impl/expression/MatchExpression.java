package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.exception.LensPatternException;
import net.rvanasa.lens.impl.context.InvokeContext;

public class MatchExpression extends AbstractExpression
{
	private final LensExpression target;
	private final MatchCase[] cases;
	
	public MatchExpression(LensExpression target, MatchCase[] cases)
	{
		this.target = target;
		this.cases = cases;
	}
	
	public LensExpression getTarget()
	{
		return target;
	}
	
	public MatchCase[] getCases()
	{
		return cases;
	}
	
	@Override
	public TypeReference getExpType()
	{
		return getTarget().getExpType();
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensValue value = getTarget().eval(stack);
		
		int size = getCases().length;
		for(int i = 0; i < size; i++)
		{
			MatchCase matchCase = getCases()[i];
			LensValue result = matchCase.matchNullable(value, stack);
			if(result != null)
			{
				return result;
			}
		}
		
		throw new LensPatternException(value);
	}
	
	@Override
	public String toString()
	{
		return join(getTarget(), "match", join((Object[])getCases()));
	}
	
	public static class MatchCase
	{
		private final LensParam param;
		private final LensExpression result;
		
		public MatchCase(LensParam param, LensExpression result)
		{
			this.param = param;
			this.result = result;
		}
		
		public LensParam getParam()
		{
			return param;
		}
		
		public LensExpression getResult()
		{
			return result;
		}
		
		public LensValue matchNullable(LensValue value, InvokeStack stack)
		{
			if(getParam().isValid(value))
			{
				InvokeStack invoke = stack.createContextual(new InvokeContext(stack.getContext()));
				getParam().setupContext(value, invoke.getContext());
				return getResult().eval(invoke);
			}
			return null;
		}

		@Override
		public String toString()
		{
			return "case " + getParam() + " => " + getResult();
		}
	}
}
