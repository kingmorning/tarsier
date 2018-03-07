package com.tarsier.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorUtils {
    public static final Logger LOG = LoggerFactory.getLogger(MonitorUtils.class);

    public static <T> List<Class<T>> loadClass(String names) {
        List<Class<T>> cs = new ArrayList<>();
        String[] ns = names.split(Constant.COMMA);
        for (String n : ns) {
            if (n != null && n.trim().length() > 0) {
                try {
                    cs.add((Class<T>) Class.forName(n.trim()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return cs;
    }

    public static <T> List<T> getInstances(String names) {
        List<Class<T>> loadClass = loadClass(names);
        List<T> rs = new ArrayList<>();
        for (Class<T> t : loadClass) {
            try {
                rs.add(t.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return rs;
    }

    public static String utf8filte(byte[] bytes) throws UnsupportedEncodingException {
        int length = bytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(length);
        int i = 0;
        // skip BOM
        if (length >= 3 && bytes[0] == -17 && bytes[1] == -69 && bytes[2] == -65) {
            i = 3;
        }
        while (i < length) {
            short b = bytes[i];
            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            } else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            } else if ((b ^ 0xF0) >> 4 == 0) {
//                LOG.debug("ignore 4 bytes:{}", bytes[i]);
                i += 4;
            } else {
//                LOG.debug("ignore byte:{}", bytes[i]);
                i++;
            }
        }
        buffer.flip();
        if (buffer.limit() == bytes.length) {
            return new String(bytes, "utf-8");
        } else {
            byte[] nbs = new byte[buffer.limit()];
            buffer.get(nbs);
            return new String(nbs, "utf-8");
        }
    }
    
    
}
