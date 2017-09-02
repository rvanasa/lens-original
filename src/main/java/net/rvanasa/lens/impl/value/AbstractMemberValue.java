package net.rvanasa.lens.impl.value;

import java.util.HashMap;
import java.util.Map;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.exception.LensTargetException;

public abstract class AbstractMemberValue implements LensValue
{
	protected static final MemberLookup EMPTY = new MemberLookup()
	{
		@Override
		public MemberLookup member(String id, LensValue value)
		{
			throw new LensException("Cannot assign to empty member lookup: " + id + " -> " + value);
		}
		
		@Override
		public MemberLookup function(LensFunction function)
		{
			throw new LensException("Cannot assign function to empty member lookup");
		};
	};
	
	private final MemberLookup members;
	
	public AbstractMemberValue()
	{
		this(EMPTY);
	}
	
	public AbstractMemberValue(MemberLookup members)
	{
		this.members = members;
	}
	
	protected MemberLookup getMembers()
	{
		return members;
	}
	
	@Override
	public boolean isValue(String id)
	{
		return getMembers().contains(id);
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return false;
	}
	
	@Override
	public LensValue get(String id)
	{
		if(isValue(id))
		{
			return getMembers().getMap().get(id);
		}
		else
		{
			throw new LensException("'" + id + "' is not a member of " + this + " (" + getType() + ")");
		}
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		if(isAssignable(id))
		{
			getMembers().member(id, value);
		}
		else
		{
			throw new LensException("Cannot set property '" + id + "' of " + getType() + ": " + this);
		}
	}
	
	@Override
	public LensFunction getFunction()
	{
		if(getMembers().getFunction() != null)
		{
			return getMembers().getFunction();
		}
		else
		{
			throw new LensTargetException(this);
		}
	}
	
	@Override
	public abstract String toString();
	
	protected static class MemberLookup
	{
		private final Map<String, LensValue> map = new HashMap<>();
		private LensFunction function;
		
		public Map<String, LensValue> getMap()
		{
			return map;
		}
		
		public LensFunction getFunction()
		{
			return function;
		}
		
		public MemberLookup member(String id, LensValue value)
		{
			getMap().put(id, value);
			return this;
		}
		
		public MemberLookup function(LensFunction function)
		{
			this.function = function;
			return this;
		}
		
		public boolean contains(String id)
		{
			return getMap().containsKey(id);
		}
		
		public LensValue get(String id)
		{
			return getMap().getOrDefault(id, Lens.UNDEFINED);
		}
	}
}
