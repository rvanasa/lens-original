package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.exception.LensStacktrace;
import net.rvanasa.lens.exception.LensThrowException;
import net.rvanasa.lens.impl.context.InvokeContext;
import net.rvanasa.lens.impl.reference.CommonTypeReference;
import net.rvanasa.lens.impl.value.StringValue;
import net.rvanasa.lens.util.CollectionHelpers;

public class TryCatchExpression extends AbstractTypedExpression
{
	private final LensExpression body;
	
	private final CatchCase[] cases;
	
	public TryCatchExpression(LensExpression body, CatchCase[] cases)
	{
		super(new CommonTypeReference(body.getExpType(), CommonTypeReference.get(CollectionHelpers.map(cases, c -> c.getResult().getExpType(), TypeReference[]::new))));
		
		this.body = body;
		this.cases = cases;
	}
	
	public LensExpression getBody()
	{
		return body;
	}
	
	public CatchCase[] getCases()
	{
		return cases;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		try
		{
			return getBody().eval(stack);
		}
		catch(Throwable e)
		{
			LensValue value;
			while(e instanceof LensStacktrace)
			{
				e = e.getCause();
			}
			if(e instanceof LensThrowException)
			{
				value = ((LensThrowException)e).getValue();
			}
			else
			{
				value = new StringValue(e.toString());
			}
			
			for(CatchCase catchCase : getCases())
			{
				LensValue match = catchCase.matchNullable(value, stack);
				if(match != null)
				{
					return match;
				}
			}
			throw e instanceof LensException ? (LensException)e : new LensException(e);
		}
	}
	
	@Override
	public String toString()
	{
		return join("try", getBody(), join((Object[])getCases()));
	}
	
	public static class CatchCase
	{
		private final LensParam param;
		private final LensExpression result;
		
		public CatchCase(LensParam param, LensExpression result)
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
			return "catch(" + getParam() + ") " + getResult();
		}
	}
}
