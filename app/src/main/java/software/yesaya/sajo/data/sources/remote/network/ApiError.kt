package software.yesaya.sajo.data.sources.remote.network

class ApiError {
    var message: String? = null
        internal set

    var errors: Map<String, List<String>>? = null
        internal set
}