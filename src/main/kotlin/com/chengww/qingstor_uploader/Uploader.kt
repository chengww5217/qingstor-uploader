package com.chengww.qingstor_uploader

import com.google.gson.Gson
import com.qingstor.sdk.config.EnvContext
import com.qingstor.sdk.exception.QSException
import com.qingstor.sdk.request.BodyProgressListener
import com.qingstor.sdk.request.RequestHandler
import com.qingstor.sdk.service.Bucket
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.net.URLDecoder

/**
 * Created by chengww on 2021/2/26
 * @author chengww
 */
object Uploader {
    private val progressListener: BodyProgressListener by lazy {
        BodyProgressListener { len, size ->
            val progress = when (size) {
                0L -> 0
                else -> {
                    val progressInt = (len * 100 / size).toInt()
                    if (progressInt < 0) 0 else if (progressInt > 100) 100 else progressInt
                }
            }
            ProgressPrinter.print(progress)
        }
    }

    fun getConfig(): Config {
        val config = File(parentFile(), "/config.json")
        if (config.exists() && config.isFile) {
            FileReader(config).use {
                Gson().fromJson(it.readText(), Config::class.java)
            }?.let {
                return it
            } ?: throw FileNotFoundException("Cannot read config.json file")
        } else {
            throw FileNotFoundException("Cannot found ${config.absolutePath}")
        }
    }

    fun upload(config: Config): String {
        val uploadFile = when {
            config.uploadFile.startsWith("./") -> File(parentFile(), config.uploadFile.substring(2))
            config.uploadFile.startsWith("../") -> File(parentFile().parentFile, config.uploadFile.substring(3))
            else -> File(config.uploadFile)
        }
        val filePath = getFilePath(uploadFile, config.pathPrefix)
        Bucket(
            EnvContext(config.ak, config.sk),
            config.zone, config.bucket
        ).run {
            val requestHandler: RequestHandler = putObjectRequest(
                filePath,
                Bucket.PutObjectInput().also { it.bodyInputFile = uploadFile }
            )
            requestHandler.progressListener = progressListener
            val out = requestHandler.send()
            if (out.statueCode == 201 || out.statueCode == 200) {
                println()
                println("Upload success!!")
                return "https://${config.bucket}.${config.zone}.qingstor.com/$filePath"
            } else {
                throw QSException("Error to upload, code: ${out.code}, msg: ${out.message}")
                    .also { it.errorCode = out.statueCode }
            }
        }
    }

    private fun getFilePath(
        uploadFile: File,
        pathPrefix: String?
    ) = when {
        pathPrefix.isNullOrEmpty() -> uploadFile.name
        pathPrefix.endsWith("/") -> "${pathPrefix}${uploadFile.name}"
        else -> "${pathPrefix}/${uploadFile.name}"
    }

    fun parentFile() =
        File(URLDecoder.decode(Uploader::class.java.protectionDomain.codeSource.location.file, "UTF-8")).parentFile
}
