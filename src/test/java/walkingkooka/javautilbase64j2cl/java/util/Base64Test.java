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

package walkingkooka.javautilbase64j2cl.java.util;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.javautilbase64j2cl.java.util.Base64.Encoder;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.text.CharSequences;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class Base64Test implements PublicStaticHelperTesting<Base64>, ToStringTesting {

    @Test
    public void testWithoutPaddingWithoutPadding() {
        final Encoder encoder = Base64.getEncoder();
        final Encoder without = encoder.withoutPadding();
        assertSame(without, without.withoutPadding());
    }

    @Test
    public void testEncoderEncodeByteFromNullFails() {
        final byte[] from = null;
        assertThrows(NullPointerException.class, () -> java.util.Base64.getEncoder().encode(from));
        assertThrows(NullPointerException.class, () -> Base64.getEncoder().encode(from));
    }

    @Test
    public void testEncoderEncodeBytesBytesFromNullFails() {
        final byte[] from = null;
        final byte[] to = null;
        assertThrows(NullPointerException.class, () -> java.util.Base64.getEncoder().encode(from, to));
        assertThrows(NullPointerException.class, () -> Base64.getEncoder().encode(from, to));
    }

    @Test
    public void testEncoderEncodeBytesBytesToNullFails() {
        final byte[] from = new byte[5];
        final byte[] to = null;
        assertThrows(NullPointerException.class, () -> java.util.Base64.getEncoder().encode(from, to));
        assertThrows(NullPointerException.class, () -> Base64.getEncoder().encode(from, to));
    }

    @Test
    public void testEncoderEncodeBytesBytesToSmallFails() {
        final byte[] from = new byte[5];
        final byte[] to = new byte[6];
        assertThrows(IllegalArgumentException.class, () -> java.util.Base64.getEncoder().encode(from, to));
        assertThrows(IllegalArgumentException.class, () -> Base64.getEncoder().encode(from, to));
    }

    @Test
    public void testEncodeByteEmpty() {
        this.encodeAndCheck(0);
    }

    @Test
    public void testEncodeByteOnce() {
        this.encodeAndCheck(1);
    }

    @Test
    public void testEncodeByteTwice() {
        this.encodeAndCheck(2);
    }

    @Test
    public void testEncodeByteThrice() {
        this.encodeAndCheck(3);
    }

    @Test
    public void testEncodeByteFour() {
        this.encodeAndCheck(4);
    }

    @Test
    public void testEncodeByteFive() {
        this.encodeAndCheck(5);
    }

    @Test
    public void testEncodeByte57MimeEncoder() {
        final java.util.Base64.Encoder jdk = java.util.Base64.getMimeEncoder();
        final Base64.Encoder emul = Base64.getMimeEncoder();

        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
            final byte[] values = new byte[19 * 3];
            final byte value = (byte) i;
            Arrays.fill(values, value);

            this.encodeAndCheck(jdk, emul, values);
        }
    }

    @Test
    public void testEncodeMimeEncoderLineLengthLineSeparator() {
        for (int lineLength = 0; lineLength < 255; lineLength++) {
            final byte[] separator = new byte[]{(byte) '\n', (byte) '\n', (byte) '\n'};

            final java.util.Base64.Encoder jdk = java.util.Base64.getMimeEncoder(lineLength, separator);
            final Base64.Encoder emul = Base64.getMimeEncoder(lineLength, separator);

            for (int i = 0; i < 255; i++) {
                final byte[] values = new byte[i];
                final byte value = (byte) i;
                Arrays.fill(values, value);

                this.encodeAndCheck(jdk, emul, values);
            }
        }
    }

    @Test
    public void testEncodeByteMany() {
        for (int i = 0; i < 255; i++) {
            this.encodeAndCheck(i);
        }
    }

    @Test
    public void testEncode() {
        for (int i = 0; i < 255; i++) {
            final byte[] values = new byte[i];

            for (int j = 0; j < 255; j++) {
                final byte value = (byte) i;
                Arrays.fill(values, value);
            }

            this.encodeAndCheck(values);
        }
    }

    private void encodeAndCheck(final int length) {
        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
            this.encodeAndCheck((byte) i, length);
        }
    }

    private void encodeAndCheck(final byte value,
                                final int length) {
        final byte[] values = new byte[length];
        Arrays.fill(values, value);

        this.encodeAndCheck(values);
    }

    // encode...........................................................................................................

    private void encodeAndCheck(final byte[] values) {
        this.encodeAndCheck(java.util.Base64.getEncoder(),
                Base64.getEncoder(),
                values);

        this.encodeAndCheck(java.util.Base64.getEncoder().withoutPadding(),
                Base64.getEncoder().withoutPadding(),
                values);

        this.encodeAndCheck(java.util.Base64.getUrlEncoder(),
                Base64.getUrlEncoder(),
                values);

        this.encodeAndCheck(java.util.Base64.getUrlEncoder().withoutPadding(),
                Base64.getUrlEncoder().withoutPadding(),
                values);

        this.encodeAndCheck(java.util.Base64.getMimeEncoder(),
                Base64.getMimeEncoder(),
                values);
    }

    private void encodeAndCheck(final java.util.Base64.Encoder jdk,
                                final Base64.Encoder emul,
                                final byte[] values) {
        assertArrayEquals(jdk.encode(values),
                emul.encode(values),
                () -> emul + " encode(byte[]) " + Arrays.toString(values));
        {
            final byte[] encoded = new byte[values.length * 3 + 4];
            final byte[] encoded2 = new byte[values.length * 3 + 4];

            jdk.encode(values, encoded);
            emul.encode(values, encoded2);

            assertArrayEquals(encoded,
                    encoded2,
                    () -> emul + " encode(byte[], byte[]) " + Arrays.toString(values));

            assertArrayEquals(encoded,
                    encoded2,
                    () -> emul + " encode(byte[], byte[]) without padding " + Arrays.toString(values));

        }

        assertEquals(jdk.encodeToString(values),
                emul.encodeToString(values),
                () -> emul + " encodeToString " + Arrays.toString(values));
    }

    // decode...........................................................................................................

    private void decodeAndCheck(final java.util.Base64.Decoder jdk,
                                final Base64.Decoder emul,
                                final byte[] values) {
        assertArrayEquals(jdk.decode(values),
                emul.decode(values),
                () -> "decode(byte[]) " + Arrays.toString(values));

        final byte[] decoded = new byte[values.length];
        final byte[] decoded2 = new byte[values.length];

        jdk.decode(values, decoded);
        emul.decode(values, decoded2);

        assertArrayEquals(decoded,
                decoded2,
                () -> "decode(byte[], byte[]) " + Arrays.toString(values));


        final String string = new String(values);
        assertEquals(jdk.decode(string),
                emul.decode(string),
                () -> "decode " + CharSequences.quoteAndEscape(string));
    }

    // toString.........................................................................................................

    @Override
    public void testCheckToStringOverridden() {
    }

    @Test
    public void testEncoderBasicToString() {
        this.toStringAndCheck(Base64.getEncoder(), "RFC4648 WITH PADDING");
    }

    @Test
    public void testEncoderBasicWithoutPaddingToString() {
        this.toStringAndCheck(Base64.getEncoder().withoutPadding(), "RFC4648");
    }

    @Test
    public void testEncoderMimeToString() {
        this.toStringAndCheck(Base64.getMimeEncoder(), "RFC2045 WITH PADDING lineWidth=76");
    }

    @Test
    public void testEncoderMimeLineWidthZeroToString() {
        this.toStringAndCheck(Base64.getMimeEncoder(0, new byte[]{'\r'}), "RFC4648 WITH PADDING");
    }

    @Test
    public void testEncoderMimeLineWidthNonZeroToString() {
        this.toStringAndCheck(Base64.getMimeEncoder(50, new byte[]{'\r'}), "RFC2045 WITH PADDING lineWidth=48");
    }

    @Test
    public void testEncoderMimeWithoutPaddingToString() {
        this.toStringAndCheck(Base64.getMimeEncoder().withoutPadding(), "RFC2045 lineWidth=76");
    }

    // PublicStaticHelper...............................................................................................

    @Override
    public Class<Base64> type() {
        return Base64.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return true;
    }
}
