/*
 * Copyright 2020 Miroslav Pokorny (github.com/mP1)
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

package walkingkooka.j2cl;

import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Test;
import walkingkooka.text.CharSequences;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@J2clTestInput(JavaUtilBase64TestJ2cl.class)
public class JavaUtilBase64TestJ2cl {

    @Test
    public void testEncode() {
        final String raw = "abc123";

        assertEquals("encodeToString " + CharSequences.quoteAndEscape(raw),
                "YWJjMTIz",
                Base64.getEncoder().encodeToString(raw.getBytes()));
    }

    @Test
    public void testDecode() {
        final String text = "YWJjMTIz";
        assertArrayEquals("decode " + CharSequences.quoteAndEscape(text),
                "abc123".getBytes(utf8()),
                Base64.getDecoder().decode(text));
    }

    @Test
    public void testEncodeDecodeRoundtrip() {
        final String raw = "abc123";
        final byte[] rawBytes = raw.getBytes(utf8());

        final String encoded = Base64.getEncoder().encodeToString(rawBytes);
        assertNotEquals(raw, encoded);

        assertArrayEquals(rawBytes, Base64.getDecoder().decode(encoded));
    }

    private static Charset utf8() {
        return StandardCharsets.UTF_8;
    }
}
