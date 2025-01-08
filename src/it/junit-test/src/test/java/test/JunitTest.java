/*
 * Copyright © 2020 Miroslav Pokorny
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
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;
import walkingkooka.text.CharSequences;

import java.nio.charset.Charset;
import java.util.Base64;

@J2clTestInput(JunitTest.class)
public final class JunitTest {

    @Test
    public void testEncodeDecodeRoundtrip() {
        final String data = "abc123def456";
        final Charset charset = Charset.forName("UTF-8");

        final String encoded = Base64.getEncoder().encodeToString(data.getBytes(charset));
        Assert.assertEquals("Started with " + CharSequences.quoteAndEscape(data) + " encoded: " + CharSequences.quoteAndEscape(encoded),
            data,
            new String(Base64.getDecoder().decode(encoded), charset)
        );
    }
}
