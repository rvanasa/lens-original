package net.rvanasa.lens.impl.param;

import java.util.Arrays;

import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.impl.type.TupleType;
import net.rvanasa.lens.impl.value.TupleValue;

public class TupleParam implements LensParam
{
	public static LensParam get(LensType... types)
	{
		if(types.length == 1)
		{
			return new NamedParam("_", types[0]);
		}
		else if(types.length == 0)
		{
			return EmptyParam.INSTANCE;
		}
		else
		{
			LensParam[] params = new LensParam[types.length];
			for(byte i = 0; i < types.length; i++)
			{
				params[i] = new NamedParam("_" + i, types[i]);
			}
			
			return new TupleParam(params);
		}
	}
	
	public static LensParam get(LensParam... params)
	{
		if(params.length == 0)
		{
			return EmptyParam.INSTANCE;
		}
		else if(params.length == 1)
		{
			return params[0];
		}
		else
		{
			return new TupleParam(params);
		}
	}
	
	private final LensParam[] params;
	private final TupleType type;
	
	public TupleParam(LensParam[] params)
	{
		this.params = params;
		this.type = new TupleType(Arrays.stream(params).map(LensParam::getType).toArray(LensType[]::new));
	}
	
	public LensParam[] getParams()
	{
		return params;
	}
	
	@Override
	public TupleType getType()
	{
		return type;
	}
	
	@Override
	public void setupContext(LensValue value, LensContext context)
	{
		TupleValue tuple = getType().getTyped(value);
		for(int i = 0; i < getParams().length; i++)
		{
			getParams()[i].setupContext(tuple.get(i), context);
		}
	}
	
	@Override
	public String toString()
	{
		return "(" + String.join(", ", Arrays.stream(getParams()).map(LensParam::toString).toArray(String[]::new)) + ")";
	}
}
