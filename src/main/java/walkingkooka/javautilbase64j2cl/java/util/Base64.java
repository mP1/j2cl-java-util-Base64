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

import walkingkooka.reflect.PublicStaticHelper;

import java.nio.charset.StandardCharsets;

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

    public static Base64.Encoder getMimeEncoder(int lineLength, byte[] lineSeparator) {
        throw new UnsupportedOperationException();
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

    public static class Encoder {

        final static Encoder RFC4648 = new Encoder();
        final static Encoder RFC4648_URLSAFE = new Encoder();
        final static Encoder RFC2045 = new Encoder();

        private Encoder() {
            super();
        }

        public byte[] encode(final byte[] src) {
            throw new UnsupportedOperationException();
        }

        public int encode(final byte[] src,
                          final byte[] dst) {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("deprecation")
        public String encodeToString(final byte[] src) {
            final byte[] encoded = this.encode(src);
            return new String(encoded, 0, 0, encoded.length);
        }

        public Encoder withoutPadding() {
            throw new UnsupportedOperationException();
        }
    }

    public static class Decoder {

        final static Decoder RFC4648 = new Decoder();
        final static Decoder RFC4648_URLSAFE = new Decoder();
        final static Decoder RFC2045 = new Decoder();

        private Decoder() {
            super();
        }

        public byte[] decode(final byte[] src) {
            throw new UnsupportedOperationException();
        }

        public byte[] decode(final String encoded) {
            return decode(encoded.getBytes(StandardCharsets.ISO_8859_1));
        }

        public int decode(final byte[] src,
                          final byte[] dst) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Stop creation
     */
    private Base64() {
        throw new UnsupportedOperationException();
    }
}
