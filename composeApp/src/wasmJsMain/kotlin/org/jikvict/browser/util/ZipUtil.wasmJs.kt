package org.jikvict.browser.util

import js.core.JsPrimitives.toKotlinByte
import js.promise.await
import js.typedarrays.Uint8Array
import js.typedarrays.toUint8Array
import jszip.JSZip
import jszip.JSZipGeneratorOptions
import jszip.load
import kotlin.js.unsafeCast

@OptIn(ExperimentalWasmJsInterop::class)
fun options(): JSZipGeneratorOptions<JsAny> = js(
    """
            type: "uint8array",
            compression: "DEFLATE",
            compressionOptions: {
                level: 9
            }
    """.trimIndent()
)

@OptIn(ExperimentalUnsignedTypes::class, ExperimentalWasmJsInterop::class)
actual suspend fun filter(file: PickedFile): PickedFile {
    val zip = JSZip()

    val uint8Array = file.bytes.toUByteArray().toUint8Array()
    val loadedZip = zip.load(uint8Array)

    val filesToRemove = loadedZip.filter { relativePath, file ->

        val isJunk = relativePath.contains("build") || relativePath.contains(".gradle")

        println("Removing $relativePath: $isJunk")
        return@filter isJunk
    }

    filesToRemove.toList().forEach { file ->
        loadedZip.remove(file.name)
    }

    val resultPromise = loadedZip.generateAsync(options())

    val resultJs = resultPromise.await()

    val resultUint8Array = resultJs.unsafeCast<Uint8Array<*>>()

    val newByteArray = ByteArray(resultUint8Array.length) { index ->
        resultUint8Array[index].toKotlinByte()
    }

    return PickedFile(file.name, newByteArray)
}