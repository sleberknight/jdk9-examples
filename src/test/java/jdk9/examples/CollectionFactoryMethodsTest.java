package jdk9.examples;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

/**
 * Exploration of the new JDK9 collection factory methods.
 * <p>
 * See http://openjdk.java.net/jeps/269
 */
public class CollectionFactoryMethodsTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testCreatingLists() {
        List<String> empty = List.of();
        softly.assertThat(empty).isEmpty();
        softly.assertThatThrownBy(() -> empty.add("a string"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        List<String> three = List.of("apple", "pear", "guava");
        softly.assertThat(three).hasSize(3);
        softly.assertThatThrownBy(() -> three.add("kiwi"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        List<Integer> ten = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        softly.assertThat(ten).hasSize(10);
        softly.assertThatThrownBy(() -> ten.add(11))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        List<Integer> spinalTap = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        softly.assertThat(spinalTap).hasSize(11);
        softly.assertThatThrownBy(() -> spinalTap.add(12))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testCreatingSets() {
        Set<String> two = Set.of("oranges", "grapes");
        softly.assertThat(two).hasSize(2);
        softly.assertThatThrownBy(() -> two.add("kiwi"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        Set<String> four = Set.of("oranges", "apples", "bananas", "pears");
        softly.assertThat(four).hasSize(4);
        softly.assertThatThrownBy(() -> four.add("guava"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        Set<String> six = Set.of("oranges", "apples", "bananas", "pears", "kiwi", "papaya");
        softly.assertThat(six).hasSize(6);
        softly.assertThatThrownBy(() -> six.add("guava"))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        Set<Integer> arbitrary = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
        softly.assertThat(arbitrary).hasSize(14);
        softly.assertThatThrownBy(() -> arbitrary.add(15))
                        .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testCreatingMaps() {
        Map<String, Integer> empty = Map.of();
        softly.assertThat(empty).isEmpty();
        softly.assertThatThrownBy(() -> empty.put("apple", 4))
                .isExactlyInstanceOf(UnsupportedOperationException.class);

        Map<String, Integer> fruitCounts = Map.of("apple", 10, "orange", 5, "kiwi", 6);
        softly.assertThat(fruitCounts).hasSize(3);
        softly.assertThat(fruitCounts.get("orange")).isEqualTo(5);
        softly.assertThatThrownBy(() -> empty.put("guava", 7))
                        .isExactlyInstanceOf(UnsupportedOperationException.class);

        Map<String, Integer> randomStuff = Map.ofEntries(
                entry("apple", 2),
                entry("pencil", 6),
                entry("toothbrush", 4),
                entry("almond", 16),
                entry("toothpaste", 1),
                entry("razor", 1),
                entry("shave cream", 1),
                entry("laptop", 1),
                entry("iPhone", 1)
        );
        softly.assertThat(randomStuff).hasSize(9);
        softly.assertThat(randomStuff.get("almond")).isEqualTo(16);
        softly.assertThat(randomStuff.getOrDefault("iPad", 0)).isZero();
        softly.assertThatThrownBy(() -> randomStuff.put("headphones", 1))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }

}
