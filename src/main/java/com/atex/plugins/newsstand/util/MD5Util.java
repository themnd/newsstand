package com.atex.plugins.newsstand.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/**
 * Simple md5 checksum.
 */
public abstract class MD5Util {

    private static final Logger LOGGER = Logger.getLogger(MD5Util.class.getName());

    public static String md5(final String content) throws IOException {
        return md5(IOUtils.toInputStream(content));
    }

    public static String md5(final InputStream is) throws IOException {

        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final DigestInputStream dis = new DigestInputStream(is, md);
            readStreamFully(dis);
            final byte[] digest = md.digest();
            final String value = encodeHex(digest);
            LOGGER.fine(value);
            return value;
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static void readStreamFully(final InputStream is) throws IOException {
        try {
            final byte[] buffer = new byte[2048];
            while (true) {
                int len = is.read(buffer, 0, buffer.length);
                if (len < buffer.length) {
                    break;
                }
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static String encodeHex(final byte[] digest) {
        final StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}
