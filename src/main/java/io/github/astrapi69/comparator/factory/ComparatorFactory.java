/**
 * The MIT License
 *
 * Copyright (C) 2022 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.comparator.factory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * A factory for creating custom {@link Comparator} objects
 */
public class ComparatorFactory
{

	private ComparatorFactory()
	{
	}

	/**
	 * Factory method for create a new {@link Comparator} from the given {@link Function} the gets
	 * the bean property. The comparator sorts ascending, in the natural order of the property, and
	 * sorts the elements whose property is null last, as
	 * {@link #newBeanPropertyComparator(Function, boolean, boolean)} with {@code nullsFirst} false
	 * and {@code ascending} true does
	 *
	 * @param <T>
	 *            the generic type of the elements
	 * @param <U>
	 *            the type of the result of the function
	 * @param beanPropertyFunction
	 *            the function used to extract the bean property and create the {@link Comparator}
	 * @return the new {@link Comparator} from the given {@link Function} the gets the bean property
	 */
	public static <T, U extends Comparable<? super U>> Comparator<T> newBeanPropertyComparator(
		Function<? super T, ? extends U> beanPropertyFunction)
	{
		return newBeanPropertyComparator(beanPropertyFunction, false, true);
	}

	/**
	 * Factory method for create a new {@link Comparator} from the given {@link Function} the gets
	 * the bean property
	 *
	 * @param <T>
	 *            the generic type of the elements
	 * @param <U>
	 *            the type of the result of the function
	 * @param beanPropertyFunction
	 *            the function used to extract the bean property and create the {@link Comparator}
	 * @param nullsFirst
	 *            if true the elements whose property is null are sorted first, otherwise last,
	 *            whatever {@code ascending} is
	 * @param ascending
	 *            if true the sort will be ascending, in the natural order of the property,
	 *            otherwise descending, in its reverse order; the elements whose property is null
	 *            are not a part of this order, {@code nullsFirst} places them
	 * @return the new {@link Comparator} from the given {@link Function} the gets the bean property
	 */
	public static <T, U extends Comparable<? super U>> Comparator<T> newBeanPropertyComparator(
		final Function<? super T, ? extends U> beanPropertyFunction, final boolean nullsFirst,
		final boolean ascending)
	{
		Comparator<U> order = ascending ? Comparator.naturalOrder() : Comparator.reverseOrder();
		return Comparator.comparing(beanPropertyFunction,
			nullsFirst ? Comparator.nullsFirst(order) : Comparator.nullsLast(order));
	}

	/**
	 * Factory method for create a new {@link Comparator} from the given {@link List} with the
	 * defined order of the returned {@link Comparator}: an element compares by its position in the
	 * list, by its first one if the list holds it more than once. The comparator takes the order
	 * when it is made, so a later change of the given list does not change it. It finds an element
	 * in the order as a {@link HashMap} finds a key, by {@link Object#hashCode()} and
	 * {@link Object#equals(Object)}, and null as any other element. An element that is not in the
	 * order, null too if the list holds no null, compares as smaller than every element of the
	 * order and as equal to every other such element, so a sorted set or a sorted map made with the
	 * comparator keeps one of them only;
	 * {@link org.apache.commons.collections4.comparators.FixedOrderComparator} refuses such an
	 * element by default
	 *
	 * @param <T>
	 *            the generic type of the elements
	 * @param definedOrder
	 *            the defined order; the method copies it and does not change it
	 * @return the new {@link Comparator} from the given {@link List} with the defined order of the
	 *         returned {@link Comparator}
	 */
	public static <T> Comparator<T> newDefinedOrderComparator(final List<T> definedOrder)
	{
		Objects.requireNonNull(definedOrder);
		final Map<T, Integer> positions = new HashMap<>();
		int position = 0;
		for (final T element : definedOrder)
		{
			positions.putIfAbsent(element, position++);
		}
		return comparingByPosition(positions);
	}

	/**
	 * Factory method for create a new {@link Comparator} from the given {@link List} with a random
	 * order. It shuffles a copy of the given list with a new {@link SecureRandom} and compares by
	 * the index of an element in the copy, as {@link #newRandomComparator(List, SecureRandom)}
	 * does: the given list stays as it is, it may be unmodifiable, and a later change of it does
	 * not change the order of the comparator. An element that is not in the given list compares as
	 * smaller than every element of it and as equal to every other such element, as for
	 * {@link #newDefinedOrderComparator(List)}
	 *
	 * @param <T>
	 *            the generic type of the elements
	 * @param list
	 *            the elements that the comparator orders in a random order; the method copies it
	 *            and does not change it
	 * @return the comparator
	 */
	public static <T> Comparator<T> newRandomComparator(final List<T> list)
	{
		Objects.requireNonNull(list);
		return newRandomComparator(list, new SecureRandom());
	}

	/**
	 * Factory method for create a new {@link Comparator} from the given {@link List} with a random
	 * order. It shuffles a copy of the given list with the given {@link SecureRandom} and compares
	 * by the index of an element in the copy: the given list stays as it is, it may be
	 * unmodifiable, and a later change of it does not change the order of the comparator. An
	 * element that is not in the given list compares as smaller than every element of it and as
	 * equal to every other such element, as for {@link #newDefinedOrderComparator(List)}
	 *
	 * @param <T>
	 *            the generic type of the elements
	 * @param list
	 *            the elements that the comparator orders in a random order; the method copies it
	 *            and does not change it
	 * @param secureRandom
	 *            the secure random object for random generation
	 * @return the comparator
	 */
	public static <T> Comparator<T> newRandomComparator(final List<T> list,
		SecureRandom secureRandom)
	{
		Objects.requireNonNull(list);
		final List<T> shuffled = new ArrayList<>(list);
		Collections.shuffle(shuffled, secureRandom);
		return Comparator.comparing(shuffled::indexOf);
	}

	/**
	 * Factory method for create a comparator for random sort of map values. The comparator takes
	 * the values of the map when it is made, so a later change of the map does not change it. It
	 * orders the values that are equal by {@link Comparable#compareTo(Object)} as one value, as a
	 * {@link TreeSet} keeps them, so a value that is equal to another by compareTo but not by
	 * {@link Object#equals(Object)}, as the {@link java.math.BigDecimal} 1.00 is to 1.0, takes the
	 * position of that other value. A value that is not a value of the map compares as smaller than
	 * every value of the map and as equal to every other such value, as for
	 * {@link #newDefinedOrderComparator(List)}
	 *
	 * @param <K>
	 *            the generic type of the key
	 * @param <V>
	 *            the generic type of the value
	 * @param map
	 *            the map
	 * @param secureRandom
	 *            the secure random object for random generation
	 * @return the comparator with random sort of map values
	 */
	public static <K, V> Comparator<V> newRandomMapValuesComparator(Map<K, V> map,
		SecureRandom secureRandom)
	{
		final List<V> order = new ArrayList<>(new TreeSet<>(map.values()));
		Collections.shuffle(order, secureRandom);
		return comparingInTheOrderOfTheirEquals(map.values(), order);
	}

	/**
	 * Factory method for create a comparator for sort with the map values. The comparator takes the
	 * values of the map when it is made, so a later change of the map does not change it. It orders
	 * the values in their natural order, as a {@link TreeSet} does, so a value that is equal to
	 * another by {@link Comparable#compareTo(Object)} but not by {@link Object#equals(Object)}, as
	 * the {@link java.math.BigDecimal} 1.00 is to 1.0, compares as equal to that other value. A
	 * value that is not a value of the map compares as smaller than every value of the map and as
	 * equal to every other such value, as for {@link #newDefinedOrderComparator(List)}
	 *
	 * @param <K>
	 *            the generic type of the key
	 * @param <V>
	 *            the generic type of the value
	 * @param map
	 *            the map
	 * @return the comparator for sort with the map values
	 */
	public static <K, V> Comparator<V> newMapValuesComparator(Map<K, V> map)
	{
		return comparingInTheOrderOfTheirEquals(map.values(),
			new ArrayList<>(new TreeSet<>(map.values())));
	}

	/**
	 * Makes a comparator of the given values in the given order of the values that a
	 * {@link TreeSet} keeps of them: a value takes the position of the value of the order that is
	 * equal to it by {@link Comparable#compareTo(Object)}, which is the value itself or one that
	 * the {@link TreeSet} kept in its place. It finds a value as a {@link HashMap} finds a key, as
	 * {@link #newDefinedOrderComparator(List)} does, so an object that is not one of the given
	 * values takes the position -1 and compares as smaller than every one of them
	 *
	 * @param <V>
	 *            the generic type of the values
	 * @param values
	 *            the values
	 * @param order
	 *            the values that a {@link TreeSet} of the given values keeps, in the order of the
	 *            comparator
	 * @return the comparator
	 */
	private static <V> Comparator<V> comparingInTheOrderOfTheirEquals(final Collection<V> values,
		final List<V> order)
	{
		final Map<V, Integer> positionsInTheOrder = new TreeMap<>();
		for (int position = 0; position < order.size(); position++)
		{
			positionsInTheOrder.put(order.get(position), position);
		}
		final Map<V, Integer> positions = new HashMap<>();
		for (final V value : values)
		{
			positions.putIfAbsent(value, positionsInTheOrder.get(value));
		}
		return comparingByPosition(positions);
	}

	/**
	 * Makes a comparator by the given positions: an object compares by its position, and an object
	 * that has none by the position -1, before every object that has one
	 *
	 * @param <T>
	 *            the generic type of the objects
	 * @param positions
	 *            the position of each object
	 * @return the comparator
	 */
	private static <T> Comparator<T> comparingByPosition(final Map<T, Integer> positions)
	{
		return Comparator.comparingInt(object -> positions.getOrDefault(object, -1));
	}

}
