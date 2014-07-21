/*
 * Copyright 2008-2010 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.sun.btrace.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A factory class for shared method ids
 * @author Jaroslav Bachorik
 */
public class MethodID {
    private static final ConcurrentMap<String, Integer> methodIds = new ConcurrentHashMap<String, Integer>();
    static final AtomicInteger lastMehodId = new AtomicInteger(1);

    /**
     * Generates a unique method id based on the provided method tag
     * @param methodTag The tag used to distinguish between methods
     * @return An ID belonging to the provided method tag
     */
    public static int getMethodId(String methodTag) {
        Integer id = null;
        if (methodIds.putIfAbsent(methodTag, -1) == null) {
            id = lastMehodId.getAndIncrement();
            methodIds.put(methodTag, id);
        } else {
            while ((id = methodIds.get(methodTag)) == -1) {
                Thread.yield();
            }
        }
        return id;
    }
}
