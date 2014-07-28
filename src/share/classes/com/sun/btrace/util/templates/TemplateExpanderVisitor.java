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

package com.sun.btrace.util.templates;

import com.sun.btrace.org.objectweb.asm.AnnotationVisitor;
import com.sun.btrace.org.objectweb.asm.Attribute;
import com.sun.btrace.org.objectweb.asm.Handle;
import com.sun.btrace.org.objectweb.asm.Label;
import com.sun.btrace.org.objectweb.asm.MethodVisitor;
import com.sun.btrace.org.objectweb.asm.Opcodes;
import com.sun.btrace.org.objectweb.asm.Type;
import com.sun.btrace.org.objectweb.asm.TypePath;
import com.sun.btrace.util.LocalVariableHelper;
import com.sun.btrace.util.templates.impl.CallTimeStampExpander;
import com.sun.btrace.util.templates.impl.MethodTimeStampExpander;
import java.util.ArrayList;
import java.util.Collection;

/**
 * An ASM visitor providing customized template expansion
 * @author Jaroslav Bachorik
 * @since 1.3
 */
public class TemplateExpanderVisitor extends MethodVisitor implements LocalVariableHelper {
    final private LocalVariableHelper lvs;

    final private Collection<TemplateExpander> expanders = new ArrayList<TemplateExpander>();

    public TemplateExpanderVisitor(LocalVariableHelper lvs,
                             String className, String methodName,
                             String desc) {
        super(Opcodes.ASM4, (MethodVisitor)lvs);
        this.lvs = lvs;

        this.expanders.add(new MethodTimeStampExpander(className, methodName, desc));
        this.expanders.add(new CallTimeStampExpander(className, methodName, desc));
    }

    public int storeNewLocal(Type type) {
        return lvs.storeNewLocal(type);
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        Template t = BTraceTemplates.getTemplate(owner, name, desc);

        if (expandTemplate(t) == TemplateExpander.Result.PASSED) {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isIface) {
        Template t = BTraceTemplates.getTemplate(owner, name, desc);

        if (expandTemplate(t) == TemplateExpander.Result.PASSED) {
            super.visitMethodInsn(opcode, owner, name, desc, isIface);
        }
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitVarInsn(opcode, var);
        }
    }

    @Override
    public void visitMultiANewArrayInsn(String string, int i) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitMultiANewArrayInsn(string, i);
        }
    }

    @Override
    public void visitLookupSwitchInsn(Label label, int[] ints, Label[] labels) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitLookupSwitchInsn(label, ints, labels);
        }
    }

    @Override
    public void visitTableSwitchInsn(int i, int i1, Label label, Label... labels) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitTableSwitchInsn(i, i1, label, labels);
        }
    }

    @Override
    public void visitIincInsn(int i, int i1) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitIincInsn(i, i1);
        }
    }

    @Override
    public void visitLdcInsn(Object o) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitLdcInsn(o);
        }
    }

    @Override
    public void visitJumpInsn(int i, Label label) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitJumpInsn(i, label);
        }
    }

    @Override
    public void visitInvokeDynamicInsn(String string, String string1, Handle handle, Object... os) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitInvokeDynamicInsn(string, string1, handle, os);
        }
    }

    @Override
    public void visitFieldInsn(int i, String string, String string1, String string2) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitFieldInsn(i, string, string1, string2);
        }
    }

    @Override
    public void visitTypeInsn(int i, String string) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitTypeInsn(i, string);
        }
    }

    @Override
    public void visitIntInsn(int i, int i1) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitIntInsn(i, i1);
        }
    }

    @Override
    public void visitInsn(int i) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitInsn(i);
        }
    }

    @Override
    public void visitLineNumber(int i, Label label) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitLineNumber(i, label);
        }
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int i, TypePath tp, Label[] labels, Label[] labels1, int[] ints, String string, boolean bln) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            return super.visitLocalVariableAnnotation(i, tp, labels, labels1, ints, string, bln);
        }
        return null;
    }

    @Override
    public void visitLocalVariable(String string, String string1, String string2, Label label, Label label1, int i) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitLocalVariable(string, string1, string2, label, label1, i);
        }
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int i, TypePath tp, String string, boolean bln) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            return super.visitTryCatchAnnotation(i, tp, string, bln);
        }
        return null;
    }

    @Override
    public void visitTryCatchBlock(Label label, Label label1, Label label2, String string) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitTryCatchBlock(label, label1, label2, string);
        }
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int i, TypePath tp, String string, boolean bln) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            return super.visitInsnAnnotation(i, tp, string, bln);
        }
        return null;
    }

    @Override
    public void visitLabel(Label label) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitLabel(label);
        }
    }

    @Override
    public void visitAttribute(Attribute atrbt) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitAttribute(atrbt);
        }
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int i, String string, boolean bln) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            return super.visitParameterAnnotation(i, string, bln);
        }
        return null;
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int i, TypePath tp, String string, boolean bln) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            return super.visitTypeAnnotation(i, tp, string, bln);
        }
        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String string, boolean bln) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            return super.visitAnnotation(string, bln);
        }
        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            return super.visitAnnotationDefault();
        }
        return null;
    }

    @Override
    public void visitParameter(String string, int i) {
        if (expandTemplate(null) == TemplateExpander.Result.PASSED) {
            super.visitParameter(string, i);
        }
    }

    @Override
    public void visitEnd() {
        expandTemplate(null);
        super.visitEnd();
    }

    private boolean expanding = false;

    private TemplateExpander.Result expandTemplate(Template newTemplate) {
        if (expanding) return TemplateExpander.Result.PASSED;

        for(TemplateExpander exp : expanders) {
            TemplateExpander.Result r = exp.expand(this, newTemplate);
            if (r != TemplateExpander.Result.PASSED) {
                return r;
            }
        }
        return TemplateExpander.Result.PASSED;
    }

    public void expand(
        TemplateExpander.Consumer<TemplateExpanderVisitor> f)  {
        try {
            if (!expanding) {
                expanding = true;
                f.consume(this);
            }
        } finally {
            expanding = false;
        }
    }
}
