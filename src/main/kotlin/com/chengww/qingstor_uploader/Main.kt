package com.chengww.qingstor_uploader

import com.google.gson.Gson
import java.io.File
import java.io.FileWriter

/**
 * Created by chengww on 2021/2/26
 * @author chengww
 */
class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println(Uploader.upload(Uploader.getConfig()))
        }
    }
}


