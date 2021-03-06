/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.btrace.util.templates.impl;

import com.sun.btrace.util.templates.BTraceTemplates;
import com.sun.btrace.util.templates.Template;

/**
 * This template expander takes care of timestamps and duration calculation
 * for Kind.CALL handlers
 * @author Jaroslav Bachorik
 */
public class CallTimeStampExpander extends TimeStampExpander {
    public static final Template START_TIME = new Template("callStartTime", "()J");
    public static final Template END_TIME = new Template("callEndTime", "()J");
    public static final Template DURATION = new Template("callDuration", "()J");

    public static final String CALLID = "callid";

    static {
        BTraceTemplates.registerTemplates(START_TIME, END_TIME, DURATION);
    }

    public CallTimeStampExpander(String className, String methodName, String desc) {
        super(className, methodName, desc,
              START_TIME, END_TIME, DURATION);
    }

    @Override
    protected String getMethodIdString(Template t) {
        String mid = super.getMethodIdString(t);
        if (isAccepted(t)) {
            for(String tag : t.getTags()) {
                if (tag.startsWith(CALLID + "=")) {
                    return mid + "@" + tag.substring(CALLID.length() + 1);
                }
            }
        }
        return mid;
    }

    private boolean isAccepted(Template t) {
        return START_TIME.equals(t) || END_TIME.equals(t) || DURATION.equals(t);
    }
}
