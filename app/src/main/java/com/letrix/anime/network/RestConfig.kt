package com.letrix.anime.network

class RestConfig {
    companion object {
        const val DEBUG = true

//        const val JKANIME_API = "http://192.168.1.40:4000/api/android/"
        const val JKANIME_API = "https://jkanime-api.vercel.app/api/android/"
        const val JKANIME_HOME = "home"
        const val JKANIME_INFO = "show"
        const val JKANIME_SERVER = "show"
        const val JKANIME_GENRE = "genres"
        const val JKANIME_SEARCH = "search"
        const val FEMBED_API = "${JKANIME_API}stream/fembed"
        const val OKRU_API = "${JKANIME_API}stream/okru"
    }
}