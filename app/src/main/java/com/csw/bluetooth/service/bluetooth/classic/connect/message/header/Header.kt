package com.csw.bluetooth.service.bluetooth.classic.connect.message.header

open class Header(val name: String) {

    /**
     * 常见的媒体格式类型如下：
     * text/html ： HTML格式
     * text/plain ：纯文本格式
     * text/xml ： XML格式
     * image/gif ：gif图片格式
     * image/jpeg ：jpg图片格式
     * image/png：png图片格式
     * 以application开头的媒体格式类型：
     *
     * application/xhtml+xml ：XHTML格式
     * application/xml： XML数据格式
     * application/atom+xml ：Atom XML聚合格式
     * application/json： JSON数据格式
     * application/pdf：pdf格式
     * application/msword ： Word文档格式
     * application/octet-stream ： 二进制流数据（如常见的文件下载）
     * application/x-www-form-urlencoded ： <form encType=””>中默认的encType，form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）
     * 另外一种常见的媒体格式是上传文件之时使用的：
     *
     * multipart/form-data ： 需要在表单中进行文件上传时，就需要使用该格式
     */
    object ContentType : Header("content-type") {
        val TYPE_JSON = "application/json"
        val TYPE_IMAGE = "image/png"
    }

    /**
     * body字节长度
     */
    object ContentLength : Header("content-length")

    /**
     * 消息创建时间，毫秒级时间戳
     */
    object MessageCreateTime : Header("message-create-time")


    object HeaderStart : Header("header-start") {
        val DESC = "头开始位置"
    }
}