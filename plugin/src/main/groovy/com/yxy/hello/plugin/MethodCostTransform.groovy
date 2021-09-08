package com.yxy.hello.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.yxy.slowmethod.Config

import com.yxy.slowmethod.method.GlobalConfig
import com.yxy.slowmethod.method.MethodCostClassVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

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
//        Log.d("ClassEditorTransform Start!")
        boolean isRelease = transformInvocation.getContext().getVariantName().contains("Release");

        def classEditor = GlobalConfig.pluginConfig;

        if (classEditor.enable) {
            Collection<TransformInput> inputs = transformInvocation.inputs
            TransformOutputProvider outputProvider = transformInvocation.outputProvider

            if (outputProvider != null) {
                outputProvider.deleteAll()
            }
//            Log.d("TransformInvocation Start!")

            inputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    traceDirectory(directoryInput, outputProvider)
                }

                input.jarInputs.each { JarInput jarInput ->
                    traceJarFiles(jarInput, outputProvider)
                }
            }
        } else {
//            Log.d("ClassEditorTransform Not Enable. Just Copy files!")

            Collection<TransformInput> inputs = transformInvocation.inputs
            TransformOutputProvider outputProvider = transformInvocation.outputProvider

            inputs.each { TransformInput input ->
                input.directoryInputs.each { DirectoryInput directoryInput ->
                    def dest = outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    FileUtils.copyDirectory(directoryInput.file, dest)
                }

                input.jarInputs.each { JarInput jarInput ->
                    def dest = outputProvider.getContentLocation(jarInput.name,
                            jarInput.contentTypes, jarInput.scopes, Format.JAR)
                    FileUtils.copyFile(jarInput.file, dest)
                }
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
}