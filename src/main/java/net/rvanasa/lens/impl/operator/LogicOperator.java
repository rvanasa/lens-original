package net.rvanasa.lens.impl.operator;

import java.util.function.BinaryOperator;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.value.BooleanValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;

public class LogicOperator implements LensOperator
{
	private final String symbol;
	private final LogicOperatorType type;
	
	public LogicOperator(String symbol, LogicOperatorType type)
	{
		this.symbol = symbol;
		this.type = type;
	}
	
	@Override
	public String getSymbol()
	{
		return symbol;
	}
	
	public LogicOperatorType getLogicType()
	{
		return type;
	}
	
	@Override
	public OperatorPosition getPosition()
	{
		return OperatorPosition.MIDFIX;
	}
	
	@Override
	public LensType getReturnType(LensType a, LensType b)
	{
		return Lens.BOOL;
	}
	
	@Override
	public boolean isValidParams(LensType a, LensType b)
	{
		return Lens.BOOL.isAssignableFrom(a) && Lens.BOOL.isAssignableFrom(b);
	}
	
	@Override
	public BooleanValue operate(LensExpression a, LensExpression b, InvokeStack stack)
	{
		BooleanValue first = Lens.BOOL.getTyped(a.eval(stack));
		boolean handle = first.handle();
		if(getLogicType().getLazyState().isLazyReturn(handle))
		{
			return first;
		}
		else
		{
			return BooleanValue.get(getLogicType().getResult(handle, Lens.BOOL.getTyped(b.eval(stack)).handle()));
		}
	}
	
	@Override
	public LensValue operate(LensValue a, LensValue b)
	{
		boolean result = getLogicType().getResult(Lens.BOOL.getTyped(a).handle(), Lens.BOOL.getTyped(b).handle());
		return BooleanValue.get(result);
	}
	
	public enum LogicOperatorType
	{
		AND_LAZY(LazyState.FIRST_FALSE, Boolean::logicalAnd),
		OR_LAZY(LazyState.FIRST_TRUE, Boolean::logicalOr),
		AND_EAGER(LazyState.NONE, Boolean::logicalAnd),
		OR_EAGER(LazyState.NONE, Boolean::logicalOr);
		
		private final LazyState lazyState;
		private final BinaryOperator<Boolean> handle;
		
		private LogicOperatorType(LazyState lazyState, BinaryOperator<Boolean> handle)
		{
			this.lazyState = lazyState;
			this.handle = handle;
		}
		
		public LazyState getLazyState()
		{
			return lazyState;
		}
		
		public boolean getResult(boolean a, boolean b)
		{
			return handle.apply(a, b);
		}
	}
	
	public enum LazyState
	{
		FIRST_TRUE
		{
			@Override
			public boolean isLazyReturn(boolean value)
			{
				return value;
			}
		},
		FIRST_FALSE
		{
			@Override
			public boolean isLazyReturn(boolean value)
			{
				return !value;
			}
		},
		NONE
		{
			@Override
			public boolean isLazyReturn(boolean value)
			{
				return false;
			}
		};
		
		public abstract boolean isLazyReturn(boolean value);
	}
}
