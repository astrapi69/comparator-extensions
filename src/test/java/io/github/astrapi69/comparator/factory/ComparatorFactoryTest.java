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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.meanbean.test.BeanTester;

import io.github.astrapi69.test.object.Employee;

/**
 * The unit test class for the class {@link ComparatorFactory}
 */
public class ComparatorFactoryTest
{

	/**
	 * The numbers that the random comparators order
	 */
	private static final List<Integer> ONE_TO_TEN = IntStream.rangeClosed(1, 10).boxed().toList();

	/**
	 * The numbers that the random comparators of a list order with a random of their own: a shuffle
	 * leaves 50 numbers in their order with a chance of 1 in 50!, so a test can assert that their
	 * order is shuffled without a seed
	 */
	private static final List<Integer> ONE_TO_FIFTY = IntStream.rangeClosed(1, 50).boxed().toList();

	/**
	 * The number 1.0, equal to {@link #ONE_POINT_ZERO_ZERO} by
	 * {@link BigDecimal#compareTo(BigDecimal)}, but not by {@link BigDecimal#equals(Object)}, which
	 * compares the scale too
	 */
	private static final BigDecimal ONE_POINT_ZERO = new BigDecimal("1.0");

	/**
	 * The number 1.00, equal to {@link #ONE_POINT_ZERO} by
	 * {@link BigDecimal#compareTo(BigDecimal)}, but not by {@link BigDecimal#equals(Object)}
	 */
	private static final BigDecimal ONE_POINT_ZERO_ZERO = new BigDecimal("1.00");

	/**
	 * The number 0.5, smaller than {@link #ONE_POINT_ZERO} and {@link #ONE_POINT_ZERO_ZERO}
	 */
	private static final BigDecimal HALF = new BigDecimal("0.5");

	/**
	 * Test for method {@link ComparatorFactory#newDefinedOrderComparator(List)} with a fixed order
	 * that is not the natural one: the comparator sorts the numbers 1 to 5 in that order. A
	 * shuffled order without a seed left the numbers in their natural order once in 120 runs, and
	 * the test then held for the natural order too (#58)
	 */
	@Test
	public void testNewDefinedOrderComparator()
	{
		final List<Integer> definedOrder = List.of(3, 1, 5, 2, 4);
		final Comparator<Integer> comparator = ComparatorFactory
			.newDefinedOrderComparator(definedOrder);

		assertEquals(definedOrder, sortedBy(comparator, List.of(1, 2, 3, 4, 5)));
		assertTrue(comparator.compare(3, 1) < 0, "3 before 1");
		assertTrue(comparator.compare(4, 2) > 0, "4 after 2");
		assertEquals(0, comparator.compare(5, 5), "5 with 5");
	}

	/**
	 * Test for method {@link ComparatorFactory#newDefinedOrderComparator(List)} with a list that
	 * changes after the call: the comparator takes the order when it is made, so the change changes
	 * neither its order nor what a sorted set made with it finds (#37)
	 */
	@Test
	public void testNewDefinedOrderComparatorAfterTheListChanges()
	{
		final List<String> priorities = new ArrayList<>(List.of("high", "medium", "low"));
		final Comparator<String> byPriority = ComparatorFactory
			.newDefinedOrderComparator(priorities);
		final TreeSet<String> tasks = new TreeSet<>(byPriority);
		tasks.addAll(List.of("low", "high", "medium"));

		Collections.reverse(priorities);

		assertEquals(List.of("high", "medium", "low"),
			sortedBy(byPriority, List.of("high", "low", "medium")), "the order given");
		assertTrue(tasks.contains("low"), "the set finds low");
		assertTrue(tasks.contains("high"), "the set finds high");
	}

	/**
	 * Test for method {@link ComparatorFactory#newDefinedOrderComparator(List)} with an order that
	 * holds an element twice and holds null: the first occurrence of an element counts, and null
	 * takes its position as any element does
	 */
	@Test
	public void testNewDefinedOrderComparatorWithADuplicateAndNull()
	{
		final Comparator<String> comparator = ComparatorFactory
			.newDefinedOrderComparator(Arrays.asList("b", null, "a", "b"));

		assertEquals(Arrays.asList("b", null, "a"),
			sortedBy(comparator, Arrays.asList("a", null, "b")));
	}

	/**
	 * Test for method {@link ComparatorFactory#newDefinedOrderComparator(List)} with an order made
	 * by {@link List#of(Object[])}, which holds no null: null is an element that is not in the
	 * order, so it compares as smaller than every element of it, where the comparator threw as the
	 * {@link List#indexOf(Object)} of such a list does (#37)
	 */
	@Test
	public void testNewDefinedOrderComparatorWithNullAndAnOrderWithoutNull()
	{
		final Comparator<String> comparator = ComparatorFactory
			.newDefinedOrderComparator(List.of("high", "low"));

		assertTrue(comparator.compare(null, "high") < 0, "null before high");
		assertTrue(comparator.compare("low", null) > 0, "low after null");
	}

	/**
	 * A factory method that compares by the position of a number in an order of the numbers 3, 1
	 * and 2
	 *
	 * @param description
	 *            the factory method
	 * @param comparator
	 *            makes the comparator in the test, not in the method source: PIT counts what the
	 *            method source runs as covered by the first invocation alone, and runs only that
	 *            one against the mutants of a factory method
	 */
	record PositionOrder(String description, Supplier<Comparator<Integer>> comparator) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The factory methods that compare by the position in an order: the order given, the values of
	 * a map in their natural order, and the shuffled order of a list and of the values of a map
	 *
	 * @return the factory methods
	 */
	static Stream<PositionOrder> positionOrders()
	{
		final List<Integer> order = List.of(3, 1, 2);
		final Map<String, Integer> numbersByName = Map.of("three", 3, "one", 1, "two", 2);
		return Stream.of(
			new PositionOrder("newDefinedOrderComparator(List)",
				() -> ComparatorFactory.newDefinedOrderComparator(order)),
			new PositionOrder("newMapValuesComparator(Map)",
				() -> ComparatorFactory.newMapValuesComparator(numbersByName)),
			new PositionOrder("newRandomMapValuesComparator(Map, SecureRandom)",
				() -> ComparatorFactory.newRandomMapValuesComparator(numbersByName,
					new SecureRandom())),
			new PositionOrder("newRandomComparator(List)",
				() -> ComparatorFactory.newRandomComparator(order)),
			new PositionOrder("newRandomComparator(List, SecureRandom)",
				() -> ComparatorFactory.newRandomComparator(order, new SecureRandom())));
	}

	/**
	 * Test for the factory methods of {@link #positionOrders()} with numbers that are not in the
	 * order: such a number compares as smaller than every number of the order and as equal to every
	 * other such number, so a sorted set made with the comparator keeps one of them only
	 *
	 * @param positionOrder
	 *            the factory method
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("positionOrders")
	public void testAnElementMissingFromTheOrderComesFirst(final PositionOrder positionOrder)
	{
		final Comparator<Integer> comparator = positionOrder.comparator().get();

		for (final Integer number : List.of(1, 2, 3))
		{
			assertTrue(comparator.compare(99, number) < 0, "99 before " + number);
			assertTrue(comparator.compare(number, 99) > 0, number + " after 99");
		}
		assertEquals(0, comparator.compare(99, 98), "two numbers missing from the order");
		final TreeSet<Integer> numbers = new TreeSet<>(comparator);
		numbers.addAll(List.of(99, 98, 1));
		assertEquals(List.of(99, 1), new ArrayList<>(numbers), "the set keeps one of 99 and 98");
	}

	/**
	 * A factory method of a comparator of the values of a map
	 *
	 * @param description
	 *            the factory method
	 * @param newComparator
	 *            the call of the factory method with the given map
	 */
	record MapValuesFactory(String description,
		Function<Map<String, Integer>, Comparator<Integer>> newComparator) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The two factory methods of a comparator of the values of a map
	 *
	 * @return the factory methods
	 */
	static Stream<MapValuesFactory> mapValuesFactories()
	{
		return Stream.of(
			new MapValuesFactory("newMapValuesComparator(Map)",
				ComparatorFactory::newMapValuesComparator),
			new MapValuesFactory("newRandomMapValuesComparator(Map, SecureRandom)",
				map -> ComparatorFactory.newRandomMapValuesComparator(map, new SecureRandom())));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newMapValuesComparator(Map)} and
	 * {@link ComparatorFactory#newRandomMapValuesComparator(Map, SecureRandom)} with a map that
	 * changes after the call: the comparator takes the values when it is made, so a value the map
	 * gets later is still not in its order, and a value the map loses keeps its position
	 *
	 * @param factory
	 *            the factory method
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("mapValuesFactories")
	public void testMapValuesComparatorAfterTheMapChanges(final MapValuesFactory factory)
	{
		final Map<String, Integer> numbersByName = new HashMap<>(
			Map.of("one", 1, "two", 2, "three", 3));
		final Comparator<Integer> comparator = factory.newComparator().apply(numbersByName);

		numbersByName.put("fifty", 50);
		numbersByName.remove("one");

		assertTrue(comparator.compare(50, 3) < 0, "50 is not in the order");
		assertTrue(comparator.compare(1, 99) > 0, "1 is in the order");
	}

	/**
	 * The values of a map, in the order the map iterates them, of which two are equal by
	 * {@link BigDecimal#compareTo(BigDecimal)} but not by {@link BigDecimal#equals(Object)}, and
	 * how the comparator of {@link ComparatorFactory#newMapValuesComparator(Map)} orders them
	 *
	 * @param description
	 *            the name of the case
	 * @param values
	 *            the values of the map, in the order the map iterates them
	 * @param sortedValues
	 *            the values sorted with the comparator
	 * @param signOfTheTwoOnes
	 *            the sign of the comparison of {@link #ONE_POINT_ZERO_ZERO} with
	 *            {@link #ONE_POINT_ZERO}
	 */
	record ValuesEqualByCompareTo(String description, List<BigDecimal> values,
		List<BigDecimal> sortedValues, int signOfTheTwoOnes) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The numbers 1.0, 1.00 and 0.5 as the values of a map, in two iteration orders: the two ones
	 * take one position, the one of the {@link TreeSet} that collapses the values, so the values
	 * come in their natural order, the two ones in the order of the map, as a stable sort keeps
	 * equal elements (#57)
	 *
	 * @return the cases
	 */
	static Stream<ValuesEqualByCompareTo> valuesEqualByCompareTo()
	{
		return Stream.of(
			new ValuesEqualByCompareTo("1.0 first: the natural order, 1.0 before 1.00",
				List.of(ONE_POINT_ZERO, ONE_POINT_ZERO_ZERO, HALF),
				List.of(HALF, ONE_POINT_ZERO, ONE_POINT_ZERO_ZERO), 0),
			new ValuesEqualByCompareTo("1.00 first: the natural order, 1.00 before 1.0",
				List.of(ONE_POINT_ZERO_ZERO, ONE_POINT_ZERO, HALF),
				List.of(HALF, ONE_POINT_ZERO_ZERO, ONE_POINT_ZERO), 0));
	}

	/**
	 * Test for method {@link ComparatorFactory#newMapValuesComparator(Map)} with two values that
	 * are equal by {@link BigDecimal#compareTo(BigDecimal)} but not by
	 * {@link BigDecimal#equals(Object)}
	 *
	 * @param values
	 *            the values of the map and how the comparator orders them
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("valuesEqualByCompareTo")
	public void testNewMapValuesComparatorWithValuesEqualByCompareTo(
		final ValuesEqualByCompareTo values)
	{
		final Comparator<BigDecimal> comparator = ComparatorFactory
			.newMapValuesComparator(mapOf(values.values()));

		assertEquals(values.sortedValues(), sortedBy(comparator, values.values()));
		assertEquals(sortedBy(Comparator.naturalOrder(), values.values()), values.sortedValues(),
			"the natural order");
		assertEquals(values.signOfTheTwoOnes(),
			Integer.signum(comparator.compare(ONE_POINT_ZERO_ZERO, ONE_POINT_ZERO)),
			"1.00 with 1.0");
	}

	/**
	 * Test for method {@link ComparatorFactory#newRandomMapValuesComparator(Map, SecureRandom)}
	 * with the values 1.0, 1.00 and 0.5, which the map iterates in this order: 1.00 takes the
	 * position of 1.0, which the {@link TreeSet} that collapses the values keeps in its place,
	 * whatever the seed (#57)
	 *
	 * @param seed
	 *            the seed of the random
	 * @throws NoSuchAlgorithmException
	 *             if the SUN provider has no SHA1PRNG, which it has in the JDK
	 * @throws NoSuchProviderException
	 *             if the JDK has no SUN provider
	 */
	@ParameterizedTest(name = "seed {0}")
	@ValueSource(longs = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 })
	public void testNewRandomMapValuesComparatorWithValuesEqualByCompareTo(final long seed)
		throws NoSuchAlgorithmException, NoSuchProviderException
	{
		final Comparator<BigDecimal> comparator = ComparatorFactory.newRandomMapValuesComparator(
			mapOf(List.of(ONE_POINT_ZERO, ONE_POINT_ZERO_ZERO, HALF)), seeded(seed));

		assertEquals(0, comparator.compare(ONE_POINT_ZERO_ZERO, ONE_POINT_ZERO), "1.00 as 1.0");
		assertEquals(Integer.signum(comparator.compare(ONE_POINT_ZERO, HALF)),
			Integer.signum(comparator.compare(ONE_POINT_ZERO_ZERO, HALF)), "1.00 with 0.5 as 1.0");
	}

	/**
	 * Makes a map whose values are the given values, in their order
	 *
	 * @param values
	 *            the values
	 * @return the map, which iterates the values in the order given
	 */
	private static Map<String, BigDecimal> mapOf(final List<BigDecimal> values)
	{
		final Map<String, BigDecimal> map = new LinkedHashMap<>();
		for (final BigDecimal value : values)
		{
			map.put("value " + map.size(), value);
		}
		return map;
	}

	/**
	 * A map that the comparators of its values cannot order, and the exception they throw for it
	 * when they are made
	 *
	 * @param description
	 *            the name of the case
	 * @param map
	 *            makes the map
	 * @param exception
	 *            the class of the exception
	 */
	record UnorderableValues(String description, Supplier<Map<String, ?>> map,
		Class<? extends RuntimeException> exception) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The maps whose values {@link java.util.TreeSet#TreeSet(java.util.Collection)} refuses: values
	 * that are not {@link Comparable}, that are not mutually comparable, a null value, and a null
	 * map
	 *
	 * @return the cases
	 */
	static Stream<UnorderableValues> unorderableValues()
	{
		return Stream.of(
			new UnorderableValues("a value that is not Comparable: ClassCastException",
				() -> Map.of("plain", new Object()), ClassCastException.class),
			new UnorderableValues("an Integer and a String: ClassCastException",
				() -> Map.of("number", 1, "word", "one"), ClassCastException.class),
			new UnorderableValues("a null value: NullPointerException",
				() -> Collections.singletonMap("nothing", null), NullPointerException.class),
			new UnorderableValues("a null map: NullPointerException", () -> null,
				NullPointerException.class));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newMapValuesComparator(Map)} and
	 * {@link ComparatorFactory#newRandomMapValuesComparator(Map, SecureRandom)} with a map whose
	 * values they cannot order: both throw when they are made
	 *
	 * @param values
	 *            the map and the exception
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("unorderableValues")
	public void testMapValuesComparatorThrowsForUnorderableValues(final UnorderableValues values)
	{
		assertThrows(values.exception(),
			() -> ComparatorFactory.newMapValuesComparator(values.map().get()),
			"newMapValuesComparator");
		assertThrows(
			values.exception(), () -> ComparatorFactory
				.newRandomMapValuesComparator(values.map().get(), new SecureRandom()),
			"newRandomMapValuesComparator");
	}

	/**
	 * A call of a random comparator factory with a null {@link SecureRandom}
	 *
	 * @param description
	 *            the name of the case
	 * @param newComparator
	 *            the call
	 */
	record NullRandom(String description, Supplier<Comparator<Integer>> newComparator) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The calls with a null random: with no element, one, a map whose two values are equal, and two
	 * elements to order
	 *
	 * @return the cases
	 */
	static Stream<NullRandom> nullRandoms()
	{
		return Stream.of(
			new NullRandom("newRandomComparator, no element",
				() -> ComparatorFactory.newRandomComparator(List.of(), null)),
			new NullRandom("newRandomComparator, one element",
				() -> ComparatorFactory.newRandomComparator(List.of(1), null)),
			new NullRandom("newRandomMapValuesComparator, no value",
				() -> ComparatorFactory.newRandomMapValuesComparator(Map.of(), null)),
			new NullRandom("newRandomMapValuesComparator, two equal values",
				() -> ComparatorFactory.newRandomMapValuesComparator(Map.of("a", 1, "b", 1), null)),
			new NullRandom("newRandomComparator, two elements",
				() -> ComparatorFactory.newRandomComparator(List.of(1, 2), null)),
			new NullRandom("newRandomMapValuesComparator, two values", () -> ComparatorFactory
				.newRandomMapValuesComparator(Map.of("a", 1, "b", 2), null)));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newRandomComparator(List, SecureRandom)} and
	 * {@link ComparatorFactory#newRandomMapValuesComparator(Map, SecureRandom)} with a null random:
	 * they throw a {@link NullPointerException} whatever the number of elements, where they took
	 * the null random for fewer than two, as {@link Collections#shuffle(List, java.util.Random)}
	 * calls the random only when there are two elements to swap (#56)
	 *
	 * @param nullRandom
	 *            the call
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("nullRandoms")
	public void testANullRandomThrows(final NullRandom nullRandom)
	{
		assertThrows(NullPointerException.class, () -> nullRandom.newComparator().get());
	}

	/**
	 * A comparator of {@link ComparatorFactory#newBeanPropertyComparator(Function)} or
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)} by the words
	 * themselves, and the order it sorts the words {@code b, null, c, a} in
	 *
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            the comparator
	 * @param sortedWords
	 *            the words sorted with the comparator
	 */
	record BeanPropertyOrder(String description, Comparator<String> comparator,
		List<String> sortedWords) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The overload with the function alone and the four combinations of the flags: ascending true
	 * sorts ascending and ascending false sorts descending, where they sorted the other way round
	 * before (#22); the overload with the function alone sorts ascending with the null last, as it
	 * did before; nullsFirst places the null whatever ascending is
	 *
	 * @return the comparators and the orders they sort in
	 */
	static Stream<BeanPropertyOrder> beanPropertyOrders()
	{
		final List<String> ascendingNullLast = Arrays.asList("a", "b", "c", null);
		final List<String> descendingNullLast = Arrays.asList("c", "b", "a", null);
		final List<String> ascendingNullFirst = Arrays.asList(null, "a", "b", "c");
		final List<String> descendingNullFirst = Arrays.asList(null, "c", "b", "a");
		return Stream.of(new BeanPropertyOrder("function alone: ascending, the null last",
			ComparatorFactory.newBeanPropertyComparator(Function.identity()), ascendingNullLast),
			new BeanPropertyOrder("nulls last, ascending true: ascending",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), false, true),
				ascendingNullLast),
			new BeanPropertyOrder("nulls last, ascending false: descending",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), false, false),
				descendingNullLast),
			new BeanPropertyOrder("nulls first, ascending true: ascending",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), true, true),
				ascendingNullFirst),
			new BeanPropertyOrder("nulls first, ascending false: descending",
				ComparatorFactory.newBeanPropertyComparator(Function.identity(), true, false),
				descendingNullFirst));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newBeanPropertyComparator(Function)} and
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)}: the order
	 * the comparator sorts the words {@code b, null, c, a} in
	 *
	 * @param order
	 *            the comparator and the order it sorts in
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("beanPropertyOrders")
	public void testNewBeanPropertyComparatorOrder(final BeanPropertyOrder order)
	{
		final List<String> words = Arrays.asList("b", null, "c", "a");

		words.sort(order.comparator());

		assertEquals(order.sortedWords(), words);
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newBeanPropertyComparator(Function)} and
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)} with the
	 * comparators of {@link #beanPropertyOrders()}: two elements with equal properties compare as
	 * equal, whatever the flags, two equal words as well as two nulls
	 *
	 * @param order
	 *            the comparator
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("beanPropertyOrders")
	public void testNewBeanPropertyComparatorEqualProperties(final BeanPropertyOrder order)
	{
		// another instance, equal to b, so that the comparison does not end at the same instance
		assertEquals(0, order.comparator().compare("b", new String("b")), "two equal words");
		assertEquals(0, order.comparator().compare(null, null), "two nulls");
	}

	/**
	 * A combination of the flags of
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)}, and the
	 * index that an employee without id takes among three employees sorted with it
	 *
	 * @param description
	 *            the name of the case
	 * @param nullsFirst
	 *            the flag nullsFirst
	 * @param ascending
	 *            the flag ascending
	 * @param indexOfTheEmployeeWithoutId
	 *            the index of the employee without id in the sorted list
	 */
	record NullPlacement(String description, boolean nullsFirst, boolean ascending,
		int indexOfTheEmployeeWithoutId) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The four combinations of the flags: where the employee without id goes depends on nullsFirst
	 * alone. How ascending orders the employees with an id is not a part of these cases, it is the
	 * one of {@link #beanPropertyOrders()} (#22)
	 *
	 * @return the combinations
	 */
	static Stream<NullPlacement> nullPlacements()
	{
		return Stream.of(new NullPlacement("nulls first, ascending true", true, true, 0),
			new NullPlacement("nulls first, ascending false", true, false, 0),
			new NullPlacement("nulls last, ascending true", false, true, 2),
			new NullPlacement("nulls last, ascending false", false, false, 2));
	}

	/**
	 * Test for method
	 * {@link ComparatorFactory#newBeanPropertyComparator(Function, boolean, boolean)} with a
	 * property that is null: nullsFirst sorts the employee without id before the employees with
	 * one, otherwise after them
	 *
	 * @param placement
	 *            the flags and the index of the employee without id
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("nullPlacements")
	public void testNewBeanPropertyComparatorWithNullProperty(final NullPlacement placement)
	{
		final Employee withoutId = Employee.builder().build();
		final List<Employee> employees = new ArrayList<>(List.of(Employee.builder().id("2").build(),
			withoutId, Employee.builder().id("1").build()));

		employees.sort(ComparatorFactory.newBeanPropertyComparator(Employee::getId,
			placement.nullsFirst(), placement.ascending()));

		assertEquals(placement.indexOfTheEmployeeWithoutId(), employees.indexOf(withoutId));
	}

	/**
	 * Test for method {@link ComparatorFactory#newMapValuesComparator(Map)} with a map whose values
	 * are neither sorted nor free of repeats: the comparator sorts them in their natural order. The
	 * test calls the comparator itself too, as {@link List#sort(Comparator)} takes a null
	 * comparator as the natural order. It asserted only that a comparator came back, on a map whose
	 * values were all 0, and passed for any comparator (#58)
	 */
	@Test
	public void testNewMapValuesComparator()
	{
		final Map<String, Integer> numbersByName = new LinkedHashMap<>();
		numbersByName.put("three", 3);
		numbersByName.put("one", 1);
		numbersByName.put("two", 2);
		numbersByName.put("three again", 3);
		final Comparator<Integer> comparator = ComparatorFactory
			.newMapValuesComparator(numbersByName);

		assertEquals(List.of(1, 2, 3, 3),
			sortedBy(comparator, new ArrayList<>(numbersByName.values())));
		assertTrue(comparator.compare(1, 2) < 0, "1 before 2");
		assertTrue(comparator.compare(3, 2) > 0, "3 after 2");
		assertEquals(0, comparator.compare(3, 3), "3 with 3");
	}

	/**
	 * A factory method of a random comparator that takes a list
	 *
	 * @param description
	 *            the factory method
	 * @param newComparator
	 *            the call of the factory method with the given list
	 */
	record RandomComparatorOfAList(String description,
		Function<List<Integer>, Comparator<Integer>> newComparator) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The two factory methods that take a list: {@link ComparatorFactory#newRandomComparator(List)}
	 * with a random of its own, {@link ComparatorFactory#newRandomComparator(List, SecureRandom)}
	 * with a new {@link SecureRandom}
	 *
	 * @return the factory methods
	 */
	static Stream<RandomComparatorOfAList> randomComparatorsOfAList()
	{
		return Stream.of(
			new RandomComparatorOfAList("newRandomComparator(List)",
				ComparatorFactory::newRandomComparator),
			new RandomComparatorOfAList("newRandomComparator(List, SecureRandom)",
				list -> ComparatorFactory.newRandomComparator(list, new SecureRandom())));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newRandomComparator(List)} and
	 * {@link ComparatorFactory#newRandomComparator(List, SecureRandom)} with a list of the numbers
	 * {@link #ONE_TO_FIFTY} in their order: they shuffle a copy (#24), so the given list stays as
	 * it was and a later change of it does not change the order of the comparator, which is a
	 * shuffled order, not the order given
	 *
	 * @param factory
	 *            the factory method
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("randomComparatorsOfAList")
	public void testNewRandomComparatorLeavesTheGivenList(final RandomComparatorOfAList factory)
	{
		final List<Integer> givenList = new ArrayList<>(ONE_TO_FIFTY);

		final Comparator<Integer> comparator = factory.newComparator().apply(givenList);

		assertEquals(ONE_TO_FIFTY, givenList, "the given list stays as it was");
		final List<Integer> order = sortedBy(comparator, ONE_TO_FIFTY);
		assertNotEquals(ONE_TO_FIFTY, order, "a shuffled order, not the order given");
		Collections.reverse(givenList);
		assertEquals(order, sortedBy(comparator, ONE_TO_FIFTY),
			"a later change of the given list does not change the order");
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newRandomComparator(List)} and
	 * {@link ComparatorFactory#newRandomComparator(List, SecureRandom)} with an unmodifiable list
	 * of the numbers {@link #ONE_TO_FIFTY} in their order: they take it, as they shuffle a copy
	 * (#24), and the comparator orders the numbers in a shuffled order, not the order given
	 *
	 * @param factory
	 *            the factory method
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("randomComparatorsOfAList")
	public void testNewRandomComparatorTakesAnUnmodifiableList(
		final RandomComparatorOfAList factory)
	{
		final Comparator<Integer> comparator = factory.newComparator().apply(ONE_TO_FIFTY);

		assertNotEquals(ONE_TO_FIFTY, sortedBy(comparator, ONE_TO_FIFTY),
			"a shuffled order, not the order given");
	}

	/**
	 * A factory method of a random comparator for the numbers {@link #ONE_TO_TEN}
	 *
	 * @param description
	 *            the factory method
	 * @param newComparator
	 *            the call of the factory method with the given random
	 */
	record RandomOrder(String description,
		Function<SecureRandom, Comparator<Integer>> newComparator) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The two factory methods that take a {@link SecureRandom}: one with a list of the numbers 1 to
	 * 10 in their order, one with a map whose values are the numbers 1 to 10
	 *
	 * @return the factory methods
	 */
	static Stream<RandomOrder> randomOrders()
	{
		final Map<String, Integer> numbersByName = ONE_TO_TEN.stream()
			.collect(Collectors.toMap(number -> "number " + number, Function.identity()));
		return Stream.of(
			new RandomOrder("newRandomComparator(List, SecureRandom)",
				random -> ComparatorFactory.newRandomComparator(new ArrayList<>(ONE_TO_TEN),
					random)),
			new RandomOrder("newRandomMapValuesComparator(Map, SecureRandom)",
				random -> ComparatorFactory.newRandomMapValuesComparator(numbersByName, random)));
	}

	/**
	 * Test for the methods {@link ComparatorFactory#newRandomComparator(List, SecureRandom)} and
	 * {@link ComparatorFactory#newRandomMapValuesComparator(Map, SecureRandom)}: the comparator
	 * orders the numbers in a shuffled order that the given random decides, the same for the same
	 * seed and another for another seed, and not in the order of the numbers given
	 *
	 * @param randomOrder
	 *            the factory method
	 * @throws NoSuchAlgorithmException
	 *             if the SUN provider has no SHA1PRNG, which it has in the JDK
	 * @throws NoSuchProviderException
	 *             if the JDK has no SUN provider
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("randomOrders")
	public void testRandomComparatorOrdersByTheGivenRandom(final RandomOrder randomOrder)
		throws NoSuchAlgorithmException, NoSuchProviderException
	{
		final List<Integer> order = sortedBy(randomOrder.newComparator().apply(seeded(1L)));

		assertEquals(order, sortedBy(randomOrder.newComparator().apply(seeded(1L))),
			"the same seed, the same order");
		assertNotEquals(order, sortedBy(randomOrder.newComparator().apply(seeded(2L))),
			"another seed, another order");
		assertNotEquals(ONE_TO_TEN, order, "a shuffled order, not the order given");
	}

	/**
	 * Sorts the numbers {@link #ONE_TO_TEN} with the given comparator
	 *
	 * @param comparator
	 *            the comparator
	 * @return the sorted numbers
	 */
	private static List<Integer> sortedBy(final Comparator<Integer> comparator)
	{
		return sortedBy(comparator, ONE_TO_TEN);
	}

	/**
	 * Sorts a copy of the given elements with the given comparator
	 *
	 * @param <T>
	 *            the generic type of the elements
	 * @param comparator
	 *            the comparator
	 * @param elements
	 *            the elements
	 * @return the sorted copy
	 */
	private static <T> List<T> sortedBy(final Comparator<T> comparator, final List<T> elements)
	{
		final List<T> sorted = new ArrayList<>(elements);
		sorted.sort(comparator);
		return sorted;
	}

	/**
	 * A random that gives the same numbers on every run: SHA1PRNG, which the method asks the SUN
	 * provider for, and not the first provider that has it, takes a seed that is set before its
	 * first use as its only seed. The seed must not be 0: {@link SecureRandom#setSeed(long)}
	 * ignores 0, and the random then seeds itself at its first use, with another seed on every run
	 *
	 * @param seed
	 *            the seed, not 0
	 * @return the random
	 * @throws NoSuchAlgorithmException
	 *             if the SUN provider has no SHA1PRNG
	 * @throws NoSuchProviderException
	 *             if the JDK has no SUN provider
	 */
	private static SecureRandom seeded(final long seed)
		throws NoSuchAlgorithmException, NoSuchProviderException
	{
		final SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		random.setSeed(seed);
		return random;
	}

	/**
	 * Test method for {@link ComparatorFactory} with {@link BeanTester}
	 */
	@Test
	public void testWithBeanTester()
	{
		final BeanTester beanTester = new BeanTester();
		beanTester.testBean(ComparatorFactory.class);
	}
}
