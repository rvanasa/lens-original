package net.rvanasa.lens.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class CollectionHelpers
{
	private CollectionHelpers()
	{
	}
	
	public static <T> Stream<T> stream(Iterable<T> input)
	{
		return StreamSupport.stream(input.spliterator(), false);
	}
	
	public static <T, R> R[] map(T[] input, Function<T, R> function, IntFunction<R[]> builder)
	{
		R[] values = builder.apply(input.length);
		for(int i = 0; i < input.length; i++)
		{
			values[i] = function.apply(input[i]);
		}
		return values;
	}
	
	public static <T, R> R[] mapIndexed(T[] input, BiFunction<Integer, T, R> function, IntFunction<R[]> builder)
	{
		R[] values = builder.apply(input.length);
		for(int i = 0; i < input.length; i++)
		{
			values[i] = function.apply(i, input[i]);
		}
		return values;
	}
	
	public static <T, R> R[] mapIndexed(List<T> input, BiFunction<Integer, T, R> function, IntFunction<R[]> builder)
	{
		R[] values = builder.apply(input.size());
		for(int i = 0; i < input.size(); i++)
		{
			values[i] = function.apply(i, input.get(i));
		}
		return values;
	}
	
	public static <T, R> R[] map(Collection<T> input, Function<T, R> function, IntFunction<R[]> builder)
	{
		R[] values = builder.apply(input.size());
		int i = 0;
		for(T item : input)
		{
			values[i++] = function.apply(item);
		}
		return values;
	}
	
	public static <T, R> ArrayList<R> map(Iterable<T> input, Function<T, R> function)
	{
		ArrayList<R> values = new ArrayList<>();
		for(T item : input)
		{
			values.add(function.apply(item));
		}
		return values;
	}
	
	public static <T, R> ArrayList<R> map(List<T> input, Function<T, R> function)
	{
		ArrayList<R> values = new ArrayList<>(input.size());
		for(int i = 0; i < input.size(); i++)
		{
			values.add(i, function.apply(input.get(i)));
		}
		return values;
	}
	
	public static <T, R> ArrayList<R> map(T[] input, Function<T, R> function)
	{
		ArrayList<R> values = new ArrayList<>(input.length);
		for(int i = 0; i < input.length; i++)
		{
			values.add(i, function.apply(input[i]));
		}
		return values;
	}
	
	public static <T, R> ArrayList<R> map(Collection<T> input, Function<T, R> function)
	{
		ArrayList<R> values = new ArrayList<>(input.size());
		int i = 0;
		for(T item : input)
		{
			values.add(i++, function.apply(item));
		}
		return values;
	}
	
	public static <T> ArrayList<T> filter(T[] input, Predicate<T> condition)
	{
		ArrayList<T> values = new ArrayList<>();
		for(int i = 0; i < input.length; i++)
		{
			T item = input[i];
			if(condition.test(item))
			{
				values.add(item);
			}
		}
		return values;
	}
	
	public static <T> ArrayList<T> filter(Iterable<T> input, Predicate<T> condition)
	{
		ArrayList<T> values = new ArrayList<>();
		for(T item : input)
		{
			if(condition.test(item))
			{
				values.add(item);
			}
		}
		return values;
	}
	
	public static <T> ArrayList<T> filter(List<T> input, Predicate<T> condition)
	{
		ArrayList<T> values = new ArrayList<>();
		for(int i = 0; i < input.size(); i++)
		{
			T item = input.get(i);
			if(condition.test(item))
			{
				values.add(item);
			}
		}
		return values;
	}
	
	public static <T> boolean anyMatch(T[] input, Predicate<T> condition)
	{
		for(int i = 0; i < input.length; i++)
		{
			if(condition.test(input[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	public static <T> boolean anyMatch(List<T> input, Predicate<T> condition)
	{
		for(int i = 0; i < input.size(); i++)
		{
			if(condition.test(input.get(i)))
			{
				return true;
			}
		}
		return false;
	}
	
	public static <T> boolean allMatch(T[] input, Predicate<T> condition)
	{
		for(int i = 0; i < input.length; i++)
		{
			if(!condition.test(input[i]))
			{
				return false;
			}
		}
		return true;
	}
	
	public static <T> boolean allMatch(List<T> input, Predicate<T> condition)
	{
		for(int i = 0; i < input.size(); i++)
		{
			if(!condition.test(input.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	public static <A, B> boolean compare(A[] a, B[] b, BiPredicate<A, B> condition)
	{
		if(a.length != b.length)
		{
			return false;
		}
		for(int i = 0; i < a.length; i++)
		{
			if(!condition.test(a[i], b[i]))
			{
				return false;
			}
		}
		return true;
	}
	
	public static <A, B> boolean compare(List<A> a, List<B> b, BiPredicate<A, B> condition)
	{
		if(a.size() != b.size())
		{
			return false;
		}
		for(int i = 0; i < a.size(); i++)
		{
			if(!condition.test(a.get(i), b.get(i)))
			{
				return false;
			}
		}
		return true;
	}
}
