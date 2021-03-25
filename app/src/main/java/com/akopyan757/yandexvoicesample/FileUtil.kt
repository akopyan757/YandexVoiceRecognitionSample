package com.akopyan757.yandexvoicesample

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException

object FileUtil {

    fun getByteArrayFromAudio(filePath: String) = try {
        val fis = FileInputStream(filePath)
        val bos = ByteArrayOutputStream()
        val byteArray = ByteArray(BYTE_ARRAY_SIZE)
        var readNum: Int
        while (fis.read().also { num -> readNum = num } != END_EOF) {
            bos.write(byteArray, BYTE_OFFSET, readNum)
        }
        bos.toByteArray()
    } catch (e: IOException) {
        null
    }

    private const val BYTE_ARRAY_SIZE = 1024
    private const val END_EOF = -1
    private const val BYTE_OFFSET = 0
}