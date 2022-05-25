package com.xianghe.ivy.http.response

/**
 * @Author:Ycl
 * @Data:2017/5/4
 * @Desc:
 */
object RespCode {
    const val CODE_0 = 0 //error
    const val CODE_1 = 1 //OK
    const val CODE_2 = 2 //数据为null
//    const val CODE_200 = 200 //OK - [GET]：服务器成功返回用户请求的数据，该操作是幂等的（Idempotent）。
//    const val CODE_201 = 201 //CREATED - [POST/PUT/PATCH]：用户新建或修改数据成功。
//    const val CODE_202 = 202 //Accepted - [*]：表示一个请求已经进入后台排队（异步任务）
//    const val CODE_204 = 204 //NO CONTENT - [DELETE]：用户删除数据成功。
//    const val CODE_400 = 400 //INVALID REQUEST - [POST/PUT/PATCH]：用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的。
//    const val CODE_401 = 401 //Unauthorized - [*]：表示用户没有权限（令牌、用户名、密码错误）。
//    const val CODE_403 = 403 //Forbidden - [*] 表示用户得到授权（与401错误相对），但是访问是被禁止的。
//    const val CODE_404 = 404 //NOT FOUND - [*]：用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的。
//    const val CODE_406 = 406 //Not Acceptable - [GET]：用户请求的格式不可得（比如用户请求JSON格式，但是只有XML格式）。
//    const val CODE_410 = 410 //Gone -[GET]：用户请求的资源被永久删除，且不会再得到的。
//    const val CODE_422 = 422 //Unprocesable entity - [POST/PUT/PATCH] 当创建一个对象时，发生一个验证错误。
//    const val CODE_500 = 500 //INTERNAL SERVER ERROR - [*]：服务器发生错误，用户将无法判断发出的请求是否成功。
//    const val CODE_503 = 503 //账号在其他手机上面登录，需要重新登录

    const val CODE_901 = 901 // 无网络
    //    const val CODE_902 = 902 // 文件不存在
//    const val CODE_903 = 903
    const val CODE_1000 = 1000 // 已经被登录了


    // 针对本地文件上传定义的Code
    const val CODE_601 = 601 // 上传已完成
    const val CODE_602 = 602 // 上传中
    const val CODE_603 = 603 // 上传文件不存在

    const val CODE_604 = 604 // oss init exception endpoint is null
    const val CODE_605 = 605 // oss init 参数为null
    const val CODE_606 = 606 // token获取失败

    const val CODE_607 = 607 // 9.0 系统上传无反应
    const val CODE_608 = 608 // 取消上传

    // 下载
    const val CODE_609 = 609 // 取消下载
    const val CODE_610 = 610 // 网络较差，下载失败


    // 水印添加失败
    const val CODE_611 = 611
    const val CODE_612 = 612 // 取消装换
    const val CODE_613 = 613 // 取消失败
    const val CODE_614 = 614 // 视频0帧


}