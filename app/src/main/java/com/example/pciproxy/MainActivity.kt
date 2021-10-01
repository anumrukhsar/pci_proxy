package com.example.pciproxy

import WebResponse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import api
import ch.datatrans.payment.api.TransactionRegistry
import ch.datatrans.payment.api.tokenization.TokenizationRequest
import ch.datatrans.payment.api.tokenization.TokenizationRequestListener
import ch.datatrans.payment.api.tokenization.TokenizationRequestRegistry
import ch.datatrans.payment.exception.TokenizationRequestException
import ch.datatrans.payment.paymentmethods.PaymentMethodType
import com.example.pciproxy.databinding.ActivityMainBinding
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity(), TokenizationRequestListener {
    val merchantId = "1100032541"
    val salt = "JydSSRjNLAXRhuaOUvK6hHBcgGugPfCWyuEHQwAphKy8uRqeMu"
    val userEmail = "anum.rukhsar@tpsonline.com"
    var paymentMethodList = listOf(PaymentMethodType.VISA, PaymentMethodType.MASTER_CARD)
    lateinit var binding: ActivityMainBinding
    lateinit var tokenId: String
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sandbox.datatrans.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service = retrofit.create(api::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupPciProxyTokenizationHandler()
    }

    private fun setupPciProxyTokenizationHandler() {

        val tokenReq = TokenizationRequest(
            merchantId,
            paymentMethodList
        )
        tokenReq.listener = this
        tokenReq.options.isTesting = true
        setListener(tokenReq)


    }

    override fun onTokenizationRequestError(exception: TokenizationRequestException) {
        Log.e("tokenizationException=", exception.toString())
    }

    override fun onTokenizationSuccess(tokenizationId: String) {
        Log.e("tokenizationSuccess:tokenId=>", tokenizationId)
        tokenId = tokenizationId

    }

    fun setListener(tokenReq: TokenizationRequest) {
        binding.btnStart.setOnClickListener {
            TokenizationRequestRegistry.startTokenizationRequest(this, tokenReq)
        }
        binding.btnGetToken.setOnClickListener {
            getToken()
        }
    }

    private fun getToken() {
        service.getToken(tokenId).enqueue(object : Callback<WebResponse> {
            override fun onResponse(call: Call<WebResponse>, response: Response<WebResponse>) {
                if (response.code() == 200) {
                    Toast.makeText(this@MainActivity, "success", Toast.LENGTH_SHORT).show()
                    binding.tvResponse.text =
                        response.body().alias.toString() + "\n"+ response.body().maskedCard.toString() + "\n" + response.body().aliasCVV.toString() + "\n" + response.body().expiryMonth.toString() + "\n" + response.body().expiryYear.toString() + "\n"
                getNoShowUrl(response.body().alias.toString(),userEmail)
                }
            }

            override fun onFailure(call: Call<WebResponse>?, t: Throwable?) {
            }

        })
    }

    fun getNoShowUrl(aliasCC:String,userEmail:String){
        val shaSign = (salt+merchantId+aliasCC+userEmail).sha256()
        Log.e("shaSign",shaSign)
        Log.e("aliasCC",aliasCC)
        var body = JsonObject()
        body.addProperty("merchantId",merchantId)
        body.addProperty("aliasCC",aliasCC)
        body.addProperty("userEmail",userEmail)
        body.addProperty("SHASign",shaSign)
        body.addProperty("language","en")
        service.getNoShowLink(body).enqueue(object :Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>?, response: Response<JsonObject>?) {
               Log.e("urlRes=>",response?.body().toString())
                Toast.makeText(this@MainActivity,response?.body().toString(),Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<JsonObject>?, t: Throwable?) {
                Log.e("urlResErr=>",t.toString())

            }

        })

    }
    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
    }

}