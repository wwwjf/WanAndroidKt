package com.wwwjf.base.autoservice

import java.util.*

object BaseServiceLoader {

        fun <S> load(service:Class<S>): S? {
            return ServiceLoader.load(service).iterator().next()
    }
}