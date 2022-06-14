/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package walkingkooka.j2cl.java.util;

import walkingkooka.NeverError;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.CharSequences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * <a href="https://tools.ietf.org/html/rfc4648"></a>
 */
public final class Base64 implements PublicStaticHelper {

    public static Base64.Encoder getEncoder() {
        return Base64.Encoder.RFC4648;
    }

    public static Base64.Encoder getUrlEncoder() {
        return Base64.Encoder.RFC4648_URLSAFE;
    }

    public static Base64.Encoder getMimeEncoder() {
        return Base64.Encoder.RFC2045;
    }

    public static Base64.Encoder getMimeEncoder(final int lineLength,
                                                final byte[] lineSeparator) {
        Objects.requireNonNull(lineSeparator);

        return lineLength / 4 <= 0 ?
                Encoder.RFC4648 :
                getMimeEncoder0(lineLength, lineSeparator);
    }

    private static Base64.Encoder getMimeEncoder0(final int lineLength,
                                                  final byte[] lineSeparator) {
        int at = 0;
        for (final byte value : lineSeparator) {
            if (-1 != RFC4648_ALPHABET.indexOf(value)) {
                throw new IllegalArgumentException("Illegal base64 line separator at " + at + " character 0x" + Integer.toString(value, 16));
            }
            at++;
        }
        return new Base64.Encoder(RFC4648_ALPHABET_CHARS,
                lineLength / 4 * 4,
                lineSeparator,
                Base64EncoderPadding.WITH);
    }

    public static Decoder getDecoder() {
        return Decoder.RFC4648;
    }

    public static Decoder getUrlDecoder() {
        return Decoder.RFC4648_URLSAFE;
    }

    public static Decoder getMimeDecoder() {
        return Decoder.RFC2045;
    }

    /**
     * <pre>
     *                       Table 1: The Base 64 Alphabet
     *
     *      Value Encoding  Value Encoding  Value Encoding  Value Encoding
     *          0 A            17 R            34 i            51 z
     *          1 B            18 S            35 j            52 0
     *          2 C            19 T            36 k            53 1
     *          3 D            20 U            37 l            54 2
     *          4 E            21 V            38 m            55 3
     *          5 F            22 W            39 n            56 4
     *          6 G            23 X            40 o            57 5
     *          7 H            24 Y            41 p            58 6
     *          8 I            25 Z            42 q            59 7
     *          9 J            26 a            43 r            60 8
     *         10 K            27 b            44 s            61 9
     *         11 L            28 c            45 t            62 +
     *         12 M            29 d            46 u            63 /
     *         13 N            30 e            47 v
     *         14 O            31 f            48 w         (pad) =
     *         15 P            32 g            49 x
     *         16 Q            33 h            50 y
     * </pre>
     */
    // @VisibleForTesting
    final static String RFC4648_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private final static char[] RFC4648_ALPHABET_CHARS = RFC4648_ALPHABET.toCharArray();
    private final static int[] RFC4648_LOOKUP = makeDecoderLookup(RFC4648_ALPHABET_CHARS);

    /**
     * <pre>
     *          Table 2: The "URL and Filename safe" Base 64 Alphabet
     *
     *      Value Encoding  Value Encoding  Value Encoding  Value Encoding
     *          0 A            17 R            34 i            51 z
     *          1 B            18 S            35 j            52 0
     *          2 C            19 T            36 k            53 1
     *          3 D            20 U            37 l            54 2
     *          4 E            21 V            38 m            55 3
     *          5 F            22 W            39 n            56 4
     *          6 G            23 X            40 o            57 5
     *          7 H            24 Y            41 p            58 6
     *          8 I            25 Z            42 q            59 7
     *          9 J            26 a            43 r            60 8
     *         10 K            27 b            44 s            61 9
     *         11 L            28 c            45 t            62 - (minus)
     *         12 M            29 d            46 u            63 _
     *         13 N            30 e            47 v           (underline)
     *         14 O            31 f            48 w
     *         15 P            32 g            49 x
     *         16 Q            33 h            50 y         (pad) =
     * </pre>
     */
    private final static char[] RFC4648_URLSAFE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
    private final static int[] RFC4648_URLSAFE_LOOKUP = makeDecoderLookup(RFC4648_URLSAFE_ALPHABET);

    /**
     * Produces a lookup table using an alphabet character as the index.
     */
    private static int[] makeDecoderLookup(final char[] alphabet) {
        final int[] lookup = new int[256];
        Arrays.fill(lookup, -1);

        int value = 0;

        for (final char c : alphabet) {
            lookup[c] = value;
            value++;
        }

        return lookup;
    }

    private static final int MIMELINEMAX = 76;
    private static final byte[] CRLF = new byte[]{'\r', '\n'};

    private static final byte MASK = 0x3f;
    static final byte PAD = '=';

    public static class Encoder {

        final static Encoder RFC4648 = new Encoder(RFC4648_ALPHABET_CHARS, -1, new byte[0], Base64EncoderPadding.WITH);
        final static Encoder RFC4648_URLSAFE = new Encoder(RFC4648_URLSAFE_ALPHABET, -1, new byte[0], Base64EncoderPadding.WITH);
        final static Encoder RFC2045 = new Encoder(RFC4648_ALPHABET_CHARS, MIMELINEMAX, CRLF, Base64EncoderPadding.WITH);

        private Encoder(final char[] alphabet,
                        final int maxLineLength,
                        final byte[] separator,
                        final Base64EncoderPadding padding) {
            super();
            this.alphabet = alphabet;
            this.maxLineLength = maxLineLength;
            this.separator = separator;
            this.padding = padding;
        }

        /**
         * <pre>
         * 9.  Illustrations and Examples
         *
         *    To translate between binary and a base encoding, the input is stored
         *    in a structure, and the output is extracted.  The case for base 64 is
         *    displayed in the following figure, borrowed from [5].
         *
         *             +--first octet--+-second octet--+--third octet--+
         *             |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
         *             +-----------+---+-------+-------+---+-----------+
         *             |5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|
         *             +--1.index--+--2.index--+--3.index--+--4.index--+
         * </pre>
         */
        public byte[] encode(final byte[] from) {
            Objects.requireNonNull(from, "from");

            try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {

                final int fromLength = from.length;
                final int maxLineLength = this.maxLineLength;
                final char[] alphabet = this.alphabet;
                final Base64EncoderPadding padding = this.padding;
                final byte[] separator = this.separator;

                int lineWidth = 0;
                int offset = 0;
                int previous = 0;

                for (int i = 0; i < fromLength; i++) {
                    final int value = from[i] & 0xFF;

                    switch (offset) {
                        case 0:
                            if (lineWidth == maxLineLength) {
                                padding.writeSeparator(bytes, separator);
                                lineWidth = 0;
                            }

                            bytes.write(alphabet[value >>> 2]);
                            previous = (value & 0x3) << 4;
                            offset = 1;
                            lineWidth++;
                            break;
                        case 1:
                            bytes.write(alphabet[previous | (value >>> 4)]);
                            previous = (value & 0xf) << 2;
                            offset = 2;
                            lineWidth++;
                            break;
                        case 2:
                            bytes.write(alphabet[previous | (value >>> 6)]);
                            bytes.write(alphabet[value & MASK]);
                            previous = 0;
                            offset = 0;
                            lineWidth++;
                            lineWidth++;
                            break;
                        default:
                            NeverError.unhandledCase(offset, 0, 1, 2);
                            break;
                    }
                }

                switch (offset) {
                    case 0:
                        break;
                    case 1:
                        bytes.write(alphabet[previous]);
                        lineWidth += 1 + padding.write1(bytes);
                        break;
                    case 2:
                        bytes.write(alphabet[previous]);

                        lineWidth += 1 + padding.write2(bytes);
                        break;
                    default:
                        NeverError.unhandledCase(offset, 0, 1, 2);
                        break;
                }

                bytes.flush();
                return bytes.toByteArray();
            } catch (final IOException cause) {
                throw new Error(cause.getMessage(), cause); // shouldnt happen.
            }
        }

        public int encode(final byte[] from,
                          final byte[] to) {
            final byte[] encoded = this.encode(from);
            final int length = encoded.length;

            if (to.length < length) {
                throw new IllegalArgumentException("To " + to.length + " < required " + length);
            }
            System.arraycopy(encoded, 0, to, 0, length);
            return length;
        }

        private final char[] alphabet;
        private final int maxLineLength;
        private final byte[] separator;
        private final Base64EncoderPadding padding;

        @SuppressWarnings("deprecation")
        public String encodeToString(final byte[] src) {
            final byte[] encoded = this.encode(src);
            return new String(encoded);
        }

        public Encoder withoutPadding() {
            return Base64EncoderPadding.WITHOUT == this.padding ?
                    this :
                    new Encoder(this.alphabet, this.maxLineLength, this.separator, Base64EncoderPadding.WITHOUT);
        }

        @Override
        public String toString() {
            final String toString;

            if (RFC4648_ALPHABET_CHARS == this.alphabet) {
                final int max = this.maxLineLength;
                if (-1 == max) {
                    toString = "RFC4648" + this.padding;
                } else {
                    toString = "RFC2045" + this.padding + " lineWidth=" + max;
                }
            } else {
                toString = "RFC4648 URLSAFE" + this.padding;
            }

            return toString;
        }
    }

    public static class Decoder {

        final static Decoder RFC4648 = new Decoder(RFC4648_LOOKUP, "RFC4648");
        final static Decoder RFC4648_URLSAFE = new Decoder(RFC4648_URLSAFE_LOOKUP, "RFC4648 URLSAFE");
        final static Decoder RFC2045 = new Decoder(RFC4648_LOOKUP, "RFC2045");

        private Decoder(final int[] lookup,
                        final String toString) {
            super();
            this.lookup = lookup;
            this.toString = toString;
        }

        /**
         * <pre>
         * 9.  Illustrations and Examples
         *
         *    To translate between binary and a base encoding, the input is stored
         *    in a structure, and the output is extracted.  The case for base 64 is
         *    displayed in the following figure, borrowed from [5].
         *
         *             +--first octet--+-second octet--+--third octet--+
         *             |7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|7 6 5 4 3 2 1 0|
         *             +-----------+---+-------+-------+---+-----------+
         *             |5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|5 4 3 2 1 0|
         *             +--1.index--+--2.index--+--3.index--+--4.index--+
         * </pre>
         */
        public byte[] decode(final byte[] from) {
            Objects.requireNonNull(from, "from");

            try (final ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {

                final int fromLength = from.length;
                final int[] lookup = this.lookup;
                final boolean mime = this.isMime();

                int mode = MODE_OCTET_0;
                int previous = 0;

                for (int i = 0; i < fromLength; i++) {
                    final byte c = from[i];
                    if (PAD == c) {
                        mode = MODE_PAD;
                        continue;
                    }


                    final int value = lookup[c];
                    if (-1 == value) {
                        if (mime) {
                            continue;
                        }
                        throw new IllegalArgumentException("Invalid encoding got 0x" + Integer.toHexString(c) + " at " + i);
                    }

                    // read 4 bytes encoded gives 3 decoded
                    switch (mode) {
                        case MODE_OCTET_0:
                            previous = value << 2;
                            mode = MODE_OCTET_1;
                            break;
                        case MODE_OCTET_1:
                            bytes.write(previous | value >> 4);
                            previous = (value & 0xf) << 4;
                            mode = MODE_OCTET_2;
                            break;
                        case MODE_OCTET_2:
                            bytes.write(previous | value >> 2);
                            previous = (value & 0x3) << 6;
                            mode = MODE_OCTET_3;
                            break;
                        case MODE_OCTET_3:
                            bytes.write(previous | value);
                            previous = 0;
                            mode = MODE_OCTET_0;
                            break;
                        case MODE_PAD:
                            throw new IllegalArgumentException("Expected pad but got " + CharSequences.quoteIfChars((char) c) + " at " + i);
                        default:
                            NeverError.unhandledCase(mode, MODE_OCTET_0, MODE_OCTET_1, MODE_OCTET_2, MODE_OCTET_3, MODE_PAD);
                            break;
                    }
                }

                switch (mode) {
                    case MODE_OCTET_0:
                    case MODE_OCTET_2:
                    case MODE_OCTET_3:
                    case MODE_PAD:
                        break;
                    case MODE_OCTET_1:
                        throw new IllegalArgumentException("Invalid encoding " + mode);
                    default:
                        NeverError.unhandledCase(mode, MODE_OCTET_0, MODE_OCTET_1, MODE_OCTET_2, MODE_OCTET_3, MODE_PAD);
                        break;
                }

                if (MODE_OCTET_1 == mode) {

                }

                bytes.flush();
                return bytes.toByteArray();
            } catch (final IOException cause) {
                throw new Error(cause.getMessage(), cause); // shouldnt happen.
            }
        }

        private final static int MODE_OCTET_0 = 0;
        private final static int MODE_OCTET_1 = MODE_OCTET_0 + 1;
        private final static int MODE_OCTET_2 = MODE_OCTET_1 + 1;
        private final static int MODE_OCTET_3 = MODE_OCTET_2 + 1;
        private final static int MODE_PAD = MODE_OCTET_3 + 1;

        private boolean isMime() {
            return this == RFC2045;
        }

        public byte[] decode(final String encoded) {
            return decode(encoded.getBytes(StandardCharsets.ISO_8859_1));
        }

        public int decode(final byte[] from,
                          final byte[] to) {
            final byte[] decoded = this.decode(from);
            final int length = decoded.length;

            if (to.length < length) {
                throw new IllegalArgumentException("To " + to.length + " < required " + length);
            }
            System.arraycopy(decoded, 0, to, 0, length);
            return length;
        }

        private final int[] lookup;

        @Override
        public String toString() {
            return this.toString;
        }

        private final String toString;
    }

    /**
     * Stop creation
     */
    private Base64() {
        throw new UnsupportedOperationException();
    }
}
