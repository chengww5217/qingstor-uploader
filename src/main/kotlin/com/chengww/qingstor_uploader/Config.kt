package com.chengww.qingstor_uploader

/**
 * Created by chengww on 2021/2/26
 * @author chengww
 */
data class Config(
    val ak: String,
    val sk: String,
    val zone: String,
    val bucket: String,
    val uploadFile: String,
    val pathPrefix: String?
)
