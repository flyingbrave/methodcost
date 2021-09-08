package com.yxy.hello.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.yxy.slowmethod.Config
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import static org.objectweb.asm.ClassReader.EXPAND_FRAMES

class MethodCostTransform extends Transform {
    private Project project

    MethodCostTransform(Project project) {
        this.project = project
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        Collection<TransformInput> inputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider

        if (outputProvider != null) {
            outputProvider.deleteAll()
        }
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                traceDirectory(directoryInput, outputProvider)
            }

            input.jarInputs.each { JarInput jarInput ->
                traceJarFiles(jarInput, outputProvider)
            }
        }
    }

    @Override
    String getName() {
        return "classEditorTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }


    static void traceDirectory(DirectoryInput directoryInput, TransformOutputProvider outputProvider) {
        if (directoryInput.file.isDirectory()) {
//            Log.d("TransformInvocation Start!")

            directoryInput.file.eachFileRecurse { File file ->
                def name = file.name
                Config config = new Config()

                if (config.isNeedTraceClass(name)) {
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new MethodCostClassVisitor(Opcodes.ASM6, classWriter)
                    classReader.accept(cv, EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(
                            file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()
                }
            }
        }

        def dest = outputProvider.getContentLocation(directoryInput.name,
                directoryInput.contentTypes, directoryInput.scopes,
                Format.DIRECTORY)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }


    static void traceJarFiles(JarInput jarInput, TransformOutputProvider outputProvider) {

        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())

            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()

            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }

            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                Config config = new Config()

                InputStream inputStream = jarFile.getInputStream(jarEntry)
                if (config.isNeedTraceClass(entryName)) {
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new MethodCostClassVisitor(Opcodes.ASM6, classWriter)
                    classReader.accept(cv, EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }

            jarOutputStream.close()
            jarFile.close()

            def dest = outputProvider.getContentLocation(jarName + md5Name,
                    jarInput.contentTypes, jarInput.scopes, Format.JAR)
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }

    class MethodCostClassVisitor extends ClassVisitor {

        private List<String> notTraceMethods = new ArrayList<>();
        private String className = "";
        private boolean isInConfigPkgList = false;

        MethodCostClassVisitor(int api,ClassVisitor cv) {
            super(api, cv)
            notTraceMethods.add("<init>");
            notTraceMethods.add("<clinit>");
        }

        public void visit(
                final int version,
                final int access,
                final String name,
                final String signature,
                final String superName,
                final String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            this.className = name;
        }

        @Override
        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            boolean isUnImplMethod = access && Opcodes.ACC_ABSTRACT > 0 || access && Opcodes.ACC_INTERFACE > 0;
            if (notTraceMethods.contains(name) || isUnImplMethod || !isInConfigPkgList) {
               return super.visitMethod(access, name, desc, signature, exceptions)
            } else {
                String methodName = className;
                MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions)
                return new MethodCostMethodVisitor(api,
                        mv,
                        access,
                        name,
                        desc,
                        methodName)
            }


        }
    }
    class MethodCostMethodVisitor extends AdviceAdapter{

        private String methodNameParams;

        protected MethodCostMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor,String methodNameParams) {
            super(api, methodVisitor, access, name, descriptor)
            this.methodNameParams=methodNameParams;
        }

        @Override
        protected void onMethodEnter() {
            super.onMethodEnter()
            mv.visitLdcInsn(methodNameParams)
            mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC, MethodTracer.CLASS_PATH,
                    MethodTracer.METHOD_RECORD_METHOD_START,
                    MethodTracer.METHOD_RECORD_METHOD_END_PARAMS,
                    false
            )
        }

        @Override
        protected void onMethodExit(int opcode) {
            super.onMethodExit(opcode)
            mv.visitLdcInsn(methodNameParams)
            mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC, MethodTracer.CLASS_PATH,
                    MethodTracer.METHOD_RECORD_METHOD_END,
                    MethodTracer.METHOD_RECORD_METHOD_END_PARAMS,
                    false
            )
        }
    }


}