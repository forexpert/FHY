package com.historydatacenter.model;



import com.historydatacenter.model.enumerate.Currency;

import javax.persistence.*;

import static com.historydatacenter.utils.EqualsUtils.areEqual;
import static com.historydatacenter.utils.HashUtils.HASH_UTILS_SEED;
import static com.historydatacenter.utils.HashUtils.hash;
import static com.historydatacenter.utils.ValidationUtils.validateNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: clyde
 * Date: 1/31/12
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
public class Instrument {
    private static final long serialVersionUID = 1933988852136669371L;

        @Enumerated(EnumType.STRING) @Column(nullable = false)
        private Currency currency1;
        @Enumerated(EnumType.STRING) @Column(nullable = false)
        private Currency currency2;

        @Transient
        private String stringRepresentation;
        @SuppressWarnings({"HardcodedFileSeparator"})
        public static final String SEPARATOR = "/";

        /**
         * Default no-arg constructor
         */
        protected Instrument() {}

        /**
         * Useful constructor assigning values to required fields.
         * @param c1 the first currency
         * @param c2 the second currency
         *
         * Note - this constructor allows arbitrary ordering of currencies,
         * which may or may not reflect market accepted precedence of ordering.
         */
        public Instrument(Currency c1, Currency c2)
        {
            validateNotNull(c1,"the first currency cannot be null");
            validateNotNull(c2,"the second currency cannot be null");
            if (c1 == c2) throw new IllegalArgumentException("the two currencies cannot be the same");
            currency1 = c1;currency2 = c2;
            stringRepresentation = c1.toString() + SEPARATOR + c2.toString();
        }

        /**
         * Factory method to construct a currency pairing Instrument, which
         * respects the precedence of currency types.
         *
         * @param c1  A currency which may end up as CCY1 depending on precedence comparison with c2.
         * @param c2  A currency which may end up as CCY2 depending on precednece comparison with c1.
         * @return an Instrument whose currency ordering is based on precedence.
         */
        public static Instrument MakeInstrument(Currency c1, Currency c2)
        {
            validateNotNull(c1,"the first currency cannot be null");
            validateNotNull(c2,"the second currency cannot be null");
            if (c1 == c2) throw new IllegalArgumentException("the two currencies cannot be the same");
            if(c1.precedence() > c2.precedence()) {
                return new Instrument(c1, c2);
            } else {
                return new Instrument(c2, c1);
            }
        }

        /**
         * Convenience method to remake an Instrument respecting the
         * precendence of the currencies involved.
         * @param inst
         * @return a new Instrument object with the currency pairing ordered
         * according to the established precedence.
         */
        public static Instrument MakeInstrument(Instrument inst)
        {
            if(inst.getCurrency1().precedence() > inst.getCurrency2().precedence()) {
                return new Instrument(inst.getCurrency1(), inst.getCurrency2());
            } else {
                return new Instrument(inst.getCurrency2(), inst.getCurrency1());
            }
        }


        /**
         * Construct an Instrument from a String representation.  The string should be of the form "USD/EUR", with
         * two String representations of the Currency enum divided by a single forward slash.  Anything else is
         * illegal and will cause an IllegalArgumentException to be thrown.
         *
         * @param str the currency pair string representation of the Instrument
         */
        @SuppressWarnings({"UnusedCatchParameter", "ThrowInsideCatchBlockWhichIgnoresCaughtException"})
        public Instrument(String str)
        {
            validateNotNull(str, "Instrument.str must be non-null");
            String[] parts = str.split(SEPARATOR);
            if (parts.length != 2)
                throw new IllegalArgumentException("bad string passed to Instrument: " + str);

            try {
                currency1 = Currency.valueOf(parts[0]);
                currency2 = Currency.valueOf(parts[1]);
                stringRepresentation = str;
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException("bad string passed to Instrument: " + str);
            }
        }

        /** @return the first currency (the EUR in EUR/USD) */
        public Currency getCurrency1()
        {
            return currency1;
        }

        /** @return the second currency (the USD in EUR/USD) */
        public Currency getCurrency2()
        {
            return currency2;
        }
        @Override
        public boolean equals(Object another)
        {
            if (!(another instanceof Instrument)) return false;
            if (this == another) return true;
            Instrument that = (Instrument) another;
            return areEqual(currency1,that.currency1) && areEqual(currency2,that.currency2);
        }
        @Override
        public int hashCode()
        {
            return hash(hash(HASH_UTILS_SEED, currency1),currency2);
        }
        @Override
        public String toString()
        {
            if (stringRepresentation == null)
                stringRepresentation = currency1.toString() + SEPARATOR + currency2.toString();
            return stringRepresentation;
        }

        public int compareTo(Instrument other)
        {
            int sense = currency1.compareTo(other.currency1);
            if (sense == 0)
                sense = currency2.compareTo(other.currency2);
            return sense;
        }
}
