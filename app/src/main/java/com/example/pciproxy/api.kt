import retrofit2.Call
import WebResponse
import com.google.gson.JsonObject
import retrofit2.http.*


interface api {
    //Get Alias
    @Headers(
        "Accept: application/json",
        "Content-type:application/json",
        "authorization:Basic MTEwMDAzMjU0MTpZVVozWDNQN0NZNnIzOHlW"
    )
    @GET("v1/tokenizations/{tokenizationId}")
    fun getToken(
        @Path("tokenizationId") tokenizationId: String
    ): Call<WebResponse>

    //GetNoShowLink
    @Headers(
        "Accept: application/json",
        "Content-type:application/json",
    )
    @POST("upp/services/v1/noshow/init")
    fun getNoShowLink(
        @Body body: JsonObject
    ): Call<JsonObject>
}