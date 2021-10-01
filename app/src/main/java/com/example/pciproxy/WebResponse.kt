import com.google.gson.annotations.SerializedName

class WebResponse{
    @SerializedName("alias")
    var alias: String? = null
    @SerializedName("maskedCard")
    var maskedCard: String? = null
    @SerializedName("aliasCVV")
    var aliasCVV: String? = null
    @SerializedName("expiryYear")
    var expiryYear: String? = null
    @SerializedName("expiryMonth")
    var expiryMonth: String? = null
}