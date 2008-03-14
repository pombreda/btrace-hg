/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
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

package com.sun.btrace.compiler;

import javax.annotation.processing.Processor;
import com.sun.source.util.JavacTask;
import com.sun.btrace.util.Messages;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Compiler for a BTrace program. Note that a BTrace 
 * program is a Java program that is specially annotated
 * and can *not* use many Java constructs (essentially java--).
 * We use JSR 199 API to compile BTrace program but validate
 * the program (for BTrace safety rules) using JSR 269 and
 * javac's Tree API.
 *
 * @author A. Sundararajan
 */
public class Compiler {
    // JSR 199 compiler
    private JavaCompiler compiler;
    private StandardJavaFileManager stdManager;

    public Compiler() {
        compiler = ToolProvider.getSystemJavaCompiler();
        stdManager = compiler.getStandardFileManager(null, null, null);
    }

    private static void usage(String msg) {
        System.err.println(msg);
        System.exit(1);
    }

    private static void usage() {
        usage(Messages.get("btracec.usage"));
    }

    // simple test main
    public static void main(String[] args) throws Exception {
        Compiler compiler = new Compiler();
        if (args.length == 0) {
            usage();
        }
        
        String classPath = ".";
        String outputDir = ".";
        int count = 0;
        boolean classPathDefined = false;
        boolean outputDirDefined = false;

        for (;;) {
            if (args[count].charAt(0) == '-') {
                if (args.length <= count+1) {
                    usage();
                }              
                if ((args[count].equals("-cp") ||
                    args[count].equals("-classpath"))
                    && !classPathDefined) {
                    classPath = args[++count];
                    classPathDefined = true;
                } else if (args[count].equals("-d") && !outputDirDefined) {
                    outputDir = args[++count];
                    outputDirDefined = true;
                } else {
                    usage();
                }
                count++;
                if (count >= args.length) {
                    break;
                }
            } else {
                break;
            }
        }

        if (args.length <= count) {
            usage();
        }

        File[] files = new File[args.length - count];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(args[i + count]);
            if (! files[i].exists()) {
                usage("File not found: " + files[i]);
            }
        }

        classPath += File.pathSeparator + System.getProperty("java.class.path");
        Map<String, byte[]> classes = compiler.compile(files, 
            new PrintWriter(System.err), ".", classPath);
        if (classes != null) {
            // write .class files.
            for (String c : classes.keySet()) {
                String name = c.replace(".", File.separator);
                int index = name.lastIndexOf(File.separatorChar);
                String dir = outputDir + File.separator;
                if (index != -1) {
                    dir += name.substring(0, index);
                }
                new File(dir).mkdirs();
                String file;
                if (index != -1) {
                    file = name.substring(index+1);
                } else {
                    file = name;
                }
                file += ".class";              
                File out = new File(dir, file);
                FileOutputStream fos = new FileOutputStream(out);
                fos.write(classes.get(c));
                fos.close();
            }
        }
    }

    public Map<String, byte[]> compile(String fileName, String source, 
                     Writer err, String sourcePath, String classPath) {
        // create a new memory JavaFileManager
        MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager);

        // prepare the compilation unit
        List<JavaFileObject> compUnits = new ArrayList<JavaFileObject>(1);
        compUnits.add(manager.makeStringSource(fileName, source));
        return compile(manager, compUnits, err, sourcePath, classPath);
    }

    public Map<String, byte[]> compile(File file, 
                    Writer err, String sourcePath, String classPath) {
        File[] files = new File[1];
        files[0] = file;
        return compile(files, err, sourcePath, classPath);
    }

    public Map<String, byte[]> compile(File[] files,
                    Writer err, String sourcePath, String classPath) {
        Iterable<? extends JavaFileObject> compUnits =
            stdManager.getJavaFileObjects(files);
        return compile(compUnits, err, sourcePath, classPath);
    }

    public Map<String, byte[]> compile(
                    Iterable<? extends JavaFileObject> compUnits, 
                    Writer err, String sourcePath, String classPath) {
        // create a new memory JavaFileManager 
        MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager);  
        return compile(manager, compUnits, err, sourcePath, classPath);
    }

    private Map<String, byte[]> compile(MemoryJavaFileManager manager,
                    Iterable<? extends JavaFileObject> compUnits, 
                    Writer err, String sourcePath, String classPath) {
        // to collect errors, warnings etc.
        DiagnosticCollector<JavaFileObject> diagnostics = 
            new DiagnosticCollector<JavaFileObject>();     

        // javac options
        List<String> options = new ArrayList<String>();
        options.add("-Xlint:all");
        options.add("-g:lines");
        options.add("-deprecation");
        if (sourcePath != null) {
            options.add("-sourcepath");
            options.add(sourcePath);
        }

        if (classPath != null) {
            options.add("-classpath");
            options.add(classPath);
        }
       
        // create a compilation task
        JavacTask task =
            (JavacTask) compiler.getTask(err, manager, diagnostics, 
                         options, null, compUnits);      
        Verifier btraceVerifier = new Verifier(); 
        task.setTaskListener(btraceVerifier);

        // we add BTrace Verifier as a (JSR 269) Processor 
        List<Processor> processors = new ArrayList<Processor>(1);
        processors.add(btraceVerifier);
        task.setProcessors(processors);

        PrintWriter perr;
        if (err instanceof PrintWriter) {
            perr = (PrintWriter)err;
        } else {
            perr = new PrintWriter(err);
        }

        // print dignostics messages in case of failures.
        if (task.call() == false) {
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {                
                perr.println(diagnostic.getMessage(null));
            }
            perr.flush();
            return null;
        }

        // collect .class bytes of all compiled classes
        Map<String, byte[]> classBytes = manager.getClassBytes();
        List<String> classNames = btraceVerifier.getClassNames();
        Map<String, byte[]> result = new HashMap<String, byte[]>();
        for (String name : classNames) {
            if (classBytes.containsKey(name)) {
                result.put(name, classBytes.get(name));
            }
        }
        try {
            manager.close();
        } catch (IOException exp) {
        }
        return result; 
    } 
}
