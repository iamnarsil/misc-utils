package com.narsil.misc;

/**
 * value object 'Tuple', can be used to contain 2 or 3 objects of any type
 * <p>
 * over 3 objects -> suggest creating new dedicated class
 *
 * @author iamnarsil
 * @version 20230705
 * @since 20230328
 */
public class Tuple {

    public static <A, B> Pair<A, B> collect(A a, B b) {
        return new Pair<>(a, b);
    }

    public static <A, B, C> Ternary<A, B, C> collect(A a, B b, C c) {
        return new Ternary<>(a, b, c);
    }

    public static class Pair<A, B> extends Tuple {

        private final A a;
        private final B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }
    }

    public static class Ternary<A, B, C> extends Tuple {

        private final A a;
        private final B b;
        private final C c;

        public Ternary(A a, B b, C c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }

        public C getC() {
            return c;
        }
    }
}
