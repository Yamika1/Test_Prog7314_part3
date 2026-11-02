package com.example.studymate


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.credentials.CredentialManager


class MainActivity : AppCompatActivity() {

    private lateinit var loginPassword : EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSSO : Button
    private lateinit var textviewRegister : TextView
    private lateinit var loginEmail:EditText

    private lateinit var credentialManager: CredentialManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       setContentView(R.layout.activity_main)

        credentialManager = CredentialManager.create(this@MainActivity)

        btnLogin.setOnClickListener {

            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_SHORT).show()
            }
            else{
           login(email, password)
            }
        }

        textviewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnSSO.setOnClickListener {
          googleSSO()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun googleSSO()
    {

    }


    fun login(email: String, password: String){
        val request = LoginRequest(email=email, password = password)
        RetroFitClient.api.login(request).enqueue(object : Callback<LoginResponse?>{

            override fun onResponse(p0: Call<LoginResponse?>, p1: Response<LoginResponse?>) {
                    if (p1.isSuccessful){
                        val user = p1.body()!!.data

                        Toast.makeText(this@MainActivity,"Login Successful!",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)


                    }else{
                          Toast.makeText(this@MainActivity,"Login Failed! ${p1.message()}",Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(p0: Call<LoginResponse?>, t: Throwable) {
                    Toast.makeText(this@MainActivity,"Error: ${t.message}",Toast.LENGTH_SHORT).show()
                }
            })
        }
    }