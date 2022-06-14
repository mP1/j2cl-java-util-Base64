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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.j2cl.java.util.Base64.Decoder;
import walkingkooka.j2cl.java.util.Base64.Encoder;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.text.CharSequences;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class Base64Test implements PublicStaticHelperTesting<Base64>, ToStringTesting<Base64> {

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
    public void testGetMimeEncoderInvalidLineSeparatorFails() {
        final int lineWidth = 50;
        final byte[] lineSeparator = new byte[]{'A'};
        assertThrows(IllegalArgumentException.class, () -> java.util.Base64.getMimeEncoder(lineWidth, lineSeparator));
        assertThrows(IllegalArgumentException.class, () -> Base64.getMimeEncoder(lineWidth, lineSeparator));
    }

    @Test
    public void testGetMimeEncoderInvalidLineSeparatorFails2() {
        final int lineWidth = 50;

        for (final char c : Base64.RFC4648_ALPHABET.toCharArray()) {
            final byte[] lineSeparator = new byte[]{(byte) c};
            assertThrows(IllegalArgumentException.class, () -> java.util.Base64.getMimeEncoder(lineWidth, lineSeparator));
            assertThrows(IllegalArgumentException.class, () -> Base64.getMimeEncoder(lineWidth, lineSeparator));
        }
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

        this.checkEquals(
                jdk.encodeToString(values),
                emul.encodeToString(values),
                () -> emul + " encodeToString " + Arrays.toString(values)
        );
    }

    // decode...........................................................................................................

    @Test
    public void testDecodeInvalidFails() {
        final byte[] values = new byte[]{'A', 0};
        assertThrows(IllegalArgumentException.class, () -> java.util.Base64.getDecoder().decode(values));
        assertThrows(IllegalArgumentException.class, () -> Base64.getDecoder().decode(values));
    }

    @Test
    public void testDecodeInvalidSequenceFails() {
        final byte[] values = new byte[]{'A'};
        assertThrows(IllegalArgumentException.class, () -> java.util.Base64.getDecoder().decode(values));
        assertThrows(IllegalArgumentException.class, () -> Base64.getDecoder().decode(values));
    }

    @Test
    public void testDecodeByteArrayByteArrayFails() {
        final byte[] from = new byte[]{'A', 'B'};
        final byte[] to = new byte[0];
        assertThrows(IllegalArgumentException.class, () -> java.util.Base64.getDecoder().decode(from, to));
        assertThrows(IllegalArgumentException.class, () -> Base64.getDecoder().decode(from, to));
    }

    @Test
    public void testDecodeInvalidPadFails() {
        final String raw = "abcd";
        final String encodedWithBadPad = Base64.getEncoder()
                .encodeToString(raw.getBytes())
                + "//";
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Base64.getDecoder().decode(encodedWithBadPad);
                }
        );
        this.checkEquals(
                "Expected pad but got '/' at 8",
                thrown.getMessage(),
                () -> "decode " + CharSequences.quoteIfChars(encodedWithBadPad)
        );
    }

    @Test
    public void testDecodeEmpty() {
        this.decodeAndCheck(0);
    }

    @Test
    public void testDecodeOneByte() {
        this.decodeAndCheck(new byte[]{0});
    }

    @Test
    public void testDecodeOneByte2() {
        this.decodeAndCheck(new byte[]{-1});
    }

    @Test
    public void testDecodeOneByteAll() {
        this.decodeAndCheck(1);
    }

    @Test
    public void testDecodeTwoBytes() {
        this.decodeAndCheck(new byte[]{1, 2});
    }

    @Test
    public void testDecodeTwoBytesAll() {
        this.decodeAndCheck(2);
    }

    @Test
    public void testDecodeThreeBytes() {
        this.decodeAndCheck(3);
    }

    @Test
    public void testDecodeThreeBytesAll() {
        this.decodeAndCheck(3);
    }

    @Test
    public void testDecodeFourBytesAll() {
        this.decodeAndCheck(4);
    }

    @Test
    public void testDecode255BytesAll() {
        this.decodeAndCheck(255);
    }

    private void decodeAndCheck(final byte[] values) {
        this.decodeAndCheck(java.util.Base64.getEncoder(),
                java.util.Base64.getDecoder(),
                Base64.getDecoder(),
                values);
    }

    private void decodeAndCheck(final int length) {
        for (int value = Byte.MIN_VALUE; value <= Byte.MAX_VALUE; value++) {
            final byte[] values = new byte[length];
            Arrays.fill(values, (byte) value);

            this.decodeAndCheck(java.util.Base64.getEncoder(),
                    java.util.Base64.getDecoder(),
                    Base64.getDecoder(),
                    values);

            for (int i = 0; i < length; i++) {
                values[i] = (byte) i;
            }

            this.decodeAndCheck(java.util.Base64.getEncoder(),
                    java.util.Base64.getDecoder(),
                    Base64.getDecoder(),
                    values);
        }
    }

    @Test
    public void testDecodeUrlEmpty() {
        this.decodeUrlAndCheck(0);
    }

    @Test
    public void testDecodeUrlOneByte() {
        this.decodeUrlAndCheck(new byte[]{0});
    }

    @Test
    public void testDecodeUrlOneByte2() {
        this.decodeUrlAndCheck(new byte[]{-1});
    }

    @Test
    public void testDecodeUrlOneByteAll() {
        this.decodeUrlAndCheck(1);
    }

    @Test
    public void testDecodeUrlTwoBytes() {
        this.decodeUrlAndCheck(new byte[]{1, 2});
    }

    @Test
    public void testDecodeUrlTwoBytesAll() {
        this.decodeUrlAndCheck(2);
    }

    @Test
    public void testDecodeUrlThreeBytes() {
        this.decodeUrlAndCheck(3);
    }

    @Test
    public void testDecodeUrlThreeBytesAll() {
        this.decodeUrlAndCheck(3);
    }

    @Test
    public void testDecodeUrlFourBytesAll() {
        this.decodeUrlAndCheck(4);
    }

    @Test
    public void testDecodeUrl255BytesAll() {
        this.decodeUrlAndCheck(255);
    }

    private void decodeUrlAndCheck(final byte[] values) {
        this.decodeAndCheck(java.util.Base64.getUrlEncoder(),
                java.util.Base64.getUrlDecoder(),
                Base64.getUrlDecoder(),
                values);
    }

    private void decodeUrlAndCheck(final int length) {
        for (int value = Byte.MIN_VALUE; value <= Byte.MAX_VALUE; value++) {
            final byte[] values = new byte[length];
            Arrays.fill(values, (byte) value);

            this.decodeUrlAndCheck(values);

            for (int i = 0; i < length; i++) {
                values[i] = (byte) i;
            }

            this.decodeUrlAndCheck(values);
        }
    }

    @Test
    public void testDecodeMimeEmpty() {
        this.decodeMimeAndCheck(0);
    }

    @Test
    public void testDecodeMimeOneByte() {
        this.decodeMimeAndCheck(new byte[]{0});
    }

    @Test
    public void testDecodeMimeOneByte2() {
        this.decodeMimeAndCheck(new byte[]{-1});
    }

    @Test
    public void testDecodeMimeOneByteAll() {
        this.decodeMimeAndCheck(1);
    }

    @Test
    public void testDecodeMimeTwoBytes() {
        this.decodeMimeAndCheck(new byte[]{1, 2});
    }

    @Test
    public void testDecodeMimeTwoBytesAll() {
        this.decodeMimeAndCheck(2);
    }

    @Test
    public void testDecodeMimeThreeBytes() {
        this.decodeMimeAndCheck(3);
    }

    @Test
    public void testDecodeMimeThreeBytesAll() {
        this.decodeMimeAndCheck(3);
    }

    @Test
    public void testDecodeMimeFourBytesAll() {
        this.decodeMimeAndCheck(4);
    }

    @Test
    public void testDecodeMime255BytesAll() {
        this.decodeMimeAndCheck(255);
    }

    private void decodeMimeAndCheck(final int length) {
        for (int value = Byte.MIN_VALUE; value <= Byte.MAX_VALUE; value++) {
            final byte[] values = new byte[length];
            Arrays.fill(values, (byte) value);

            this.decodeMimeAndCheck(values);

            for (int i = 0; i < length; i++) {
                values[i] = (byte) i;
            }

            this.decodeMimeAndCheck(values);
        }
    }

    private void decodeMimeAndCheck(final byte[] values) {
        this.decodeAndCheck(java.util.Base64.getMimeEncoder(),
                java.util.Base64.getMimeDecoder(),
                Base64.getMimeDecoder(),
                values);
    }

    private void decodeAndCheck(final java.util.Base64.Encoder encoder,
                                final java.util.Base64.Decoder jdk,
                                final Base64.Decoder emul,
                                final byte[] values) {
        this.decodeAndCheck(jdk, emul, encoder.encode(values));
        this.decodeAndCheck(jdk, emul, encoder.withoutPadding().encode(values));
    }

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
        assertArrayEquals(jdk.decode(string),
                emul.decode(string),
                () -> emul + " decode(String) " + CharSequences.quoteAndEscape(string));
    }

    @Test
    public void testRfc2045_EncodeAndDecodeRoundtrip() {
        this.encodeAndDecodeRoundtrip(
                Encoder.RFC2045,
                Decoder.RFC2045
        );
    }

    @Test
    public void testRfc4648_EncodeAndDecodeRoundtrip() {
        this.encodeAndDecodeRoundtrip(
                Encoder.RFC4648,
                Decoder.RFC4648
        );
    }

    @Test
    public void testRfc4648UrlSafe_EncodeAndDecodeRoundtrip() {
        this.encodeAndDecodeRoundtrip(
                Encoder.RFC4648_URLSAFE,
                Decoder.RFC4648_URLSAFE
        );
    }

    private void encodeAndDecodeRoundtrip(final Encoder encoder,
                                          final Decoder decoder) {
        final Charset charset = Charset.defaultCharset();

        for (int i = 0; i < 100; i++) {
            final StringBuilder b = new StringBuilder();
            for (int j = 0; j < i; j++) {
                final int c = 'A' + j;
                b.append((char) c);
            }
            final String string = b.toString();
            final String encoded = encoder.encodeToString(string.getBytes(charset));
            this.checkEquals(
                    string,
                    new String(decoder.decode(encoded), charset),
                    () -> "encode and decode roundtrip " + CharSequences.quoteIfChars(string)
            );
        }
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
    public void testEncoderUrlSafeToString() {
        this.toStringAndCheck(Base64.getUrlEncoder(), "RFC4648 URLSAFE WITH PADDING");
    }

    @Test
    public void testEncoderUrlSafeWithoutPaddingToString() {
        this.toStringAndCheck(Base64.getUrlEncoder().withoutPadding(), "RFC4648 URLSAFE");
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

    @Test
    public void testDecoderToString() {
        this.toStringAndCheck(Base64.getDecoder(), "RFC4648");
    }

    @Test
    public void testDecoderUrlToString() {
        this.toStringAndCheck(Base64.getUrlDecoder(), "RFC4648 URLSAFE");
    }

    @Test
    public void testDecoderMimeToString() {
        this.toStringAndCheck(Base64.getMimeDecoder(), "RFC2045");
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
