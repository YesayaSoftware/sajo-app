package software.yesaya.sajo.data.sources.remote.network

class AccessToken {
    lateinit var token_type: String
    var expires_in: Int = 0
    var access_token: String? = null
    var refresh_token: String? = null
}