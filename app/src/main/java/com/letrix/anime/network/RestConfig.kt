package com.letrix.anime.network

class RestConfig {
    companion object {
        const val MIRANIME_API = "http://192.168.1.40:4000/api/v1/"
        const val JIKAN_API = "https://api.jikan.moe/"

        const val JKANIME_API = "http://192.168.1.40:4000/api/android/"
//        const val JKANIME_API = "https://jkanime-api.vercel.app/api/android/"
        const val JKANIME_HOME = "home"
        const val JKANIME_INFO = "show"
        const val JKANIME_SERVER = "show"
        const val JKANIME_GENRE = "genres"
        const val JKANIME_SEARCH = "search"

        const val FEMBED_API = "${JKANIME_API}stream/fembed"
        const val STAPE_API = "${JKANIME_API}stream/stape"
        const val YUPLOAD_APU = "${JKANIME_API}stream/yourupload"
        const val MARU_API = "${JKANIME_API}stream/mailru"
        const val OKRU_API = "${JKANIME_API}stream/okru"
    }
}