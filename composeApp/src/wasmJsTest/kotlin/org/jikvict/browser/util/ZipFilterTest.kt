package org.jikvict.browser.util

import js.promise.await
import js.typedarrays.Uint8Array
import js.typedarrays.toUint8Array
import jszip.JSZip
import jszip.load
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import js.core.JsPrimitives.toKotlinByte
import kotlin.js.unsafeCast

class ZipFilterTest {

    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalWasmJsInterop::class)
    @Test
    fun testZipFilterRemovesBuildAndGradle() = runTest {
        val zip = JSZip()
        zip.file("file1.txt", "content1")
        zip.file("build/file2.txt", "content2")
        zip.file(".gradle/file3.txt", "content3")
        zip.file("src/main/kotlin/Main.kt", "content4")

        val options = options()
        val zipDataPromise = zip.generateAsync(options)
        val zipDataJs = zipDataPromise.await()
        val zipDataUint8Array = zipDataJs.unsafeCast<Uint8Array<*>>()
        
        val bytes = ByteArray(zipDataUint8Array.length) { index ->
            zipDataUint8Array[index].toKotlinByte()
        }

        val pickedFile = PickedFile("test.zip", bytes)
        
        val filteredFile = filter(pickedFile)

        val filteredZip = JSZip()
        val loadedFilteredZip = filteredZip.load(filteredFile.bytes.toUByteArray().toUint8Array())

        val files = mutableListOf<String>()
        loadedFilteredZip.forEach { relativePath, _ ->
            files.add(relativePath)
        }

        assertTrue(files.any { it == "file1.txt" }, "Should contain file1.txt")
        assertTrue(files.any { it == "src/main/kotlin/Main.kt" }, "Should contain Main.kt")
        assertFalse(files.any { it.contains("build") }, "Should not contain build files")
        assertFalse(files.any { it.contains(".gradle") }, "Should not contain .gradle files")
    }
}
