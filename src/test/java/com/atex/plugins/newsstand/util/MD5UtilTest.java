package com.atex.plugins.newsstand.util;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

/**
 * Unit test for {@link com.atex.plugins.newsstand.util.MD5Util}.
 */
@RunWith(MockitoJUnitRunner.class)
public class MD5UtilTest {

    @Test
    public void testMD5FromRFCSet() throws IOException
    {
        final Map<String, String> hashes = Maps.newHashMap();
        hashes.put("", "d41d8cd98f00b204e9800998ecf8427e");
        hashes.put("a", "0cc175b9c0f1b6a831c399e269772661");
        hashes.put("abc", "900150983cd24fb0d6963f7d28e17f72");
        hashes.put("message digest", "f96b697d7cb7938d525a2f31aaf161d0");
        hashes.put("abcdefghijklmnopqrstuvwxyz", "c3fcd3d76192e4007dfb496cca67e13b");
        hashes.put("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", "d174ab98d277d9f5a5611c2c9f419d9f");
        hashes.put("12345678901234567890123456789012345678901234567890123456789012345678901234567890", "57edf4a22be3c955ac49da2e2107b67a");

        for (final Entry<String, String> entry : hashes.entrySet()) {
            final String value = MD5Util.md5(entry.getKey());
            Assert.assertEquals(entry.getValue(), value);
        }
    }

}