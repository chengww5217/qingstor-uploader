package com.chengww.qingstor_uploader

import java.lang.StringBuilder

/**
 * Created by chengww on 2021/2/26
 * @author chengww
 */
object ProgressPrinter {
    private const val PREFIX = "Uploading..."

    private var lastPrintProgressTime = 0L

    fun print(progress: Int) {
        if (System.currentTimeMillis() - lastPrintProgressTime > 3000) {
            print("$PREFIX ${progress}%")
            lastPrintProgressTime = System.currentTimeMillis()
        }
    }

}
