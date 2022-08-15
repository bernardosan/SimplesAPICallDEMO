package com.example.simplesapicalldemo

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    var tvText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvText = findViewById(R.id.tvText)

        CallAPILoginAsyncTask().execute()

    }

    private inner class CallAPILoginAsyncTask(): AsyncTask<Any, Void, String>() {

        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String

            var connection: HttpsURLConnection? = null

            try {
                val url = URL("https://run.mocky.io/v3/24ff9ff6-606c-4f71-a0d6-e3d3ca395d1b")
                connection = url.openConnection() as HttpsURLConnection
                connection.doInput = true
                connection.doOutput = true

                val httpResult : Int = connection.responseCode

                if(httpResult == HttpsURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?

                    try {
                        while(reader.readLine().also{line = it} != null){
                            stringBuilder.append(line + "\n")
                        }
                    } catch(e: IOException){
                        e.printStackTrace()
                    } finally {
                        try{
                            inputStream.close()
                        } catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()


                } else{
                    result = connection.responseMessage
                }
            } catch (e: SocketTimeoutException){
                result = "Connection Timeout"
            } catch (e: Exception){
                result = "Error: " + e.message
            } finally {
                connection?.disconnect()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()

            Log.i("JSON RESPONSE RESULT", result!!)


            tvText = findViewById(R.id.tvText)
            val jsonObject = JSONObject(result)
            val name = jsonObject.optString("name")
            val id = jsonObject.optInt("id")

            tvText?.text = "$name - $id"

            val dataListArray = jsonObject.optJSONArray("datalist")
            Log.i("Data list size: ${dataListArray.length()}", dataListArray.toString())

            val listofArray = arrayListOf<String>()

            for (item in 0 until dataListArray.length()){
                var dataItemObject: JSONObject = dataListArray[item] as JSONObject
                listofArray.add("${dataItemObject.optInt("id")} - ${dataItemObject.optString("name")}")
            }
            Log.i("Output", listofArray.toString())
        }


        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.custom_dialog_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }

    }


}
