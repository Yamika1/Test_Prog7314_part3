package com.example.studymate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginSSOActivity : AppCompatActivity() {
    private lateinit var textViewAccountName : TextView
    private lateinit var textViewAccountEmail : TextView

    private lateinit var textViewAccountMenu : TextView
    private lateinit var textViewPlus: TextView

    private lateinit var layoutExistingAccount: LinearLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_sso_activity)

        textViewAccountName=findViewById<TextView>(R.id.textViewAccountName)
        textViewAccountEmail=findViewById<TextView>(R.id.textViewAccountEmail)

        textViewAccountMenu=findViewById<TextView>(R.id.textViewAccountMenu)
        textViewPlus=findViewById<TextView>(R.id.textViewPlus)
        layoutExistingAccount=findViewById<LinearLayout>(R.id.layoutExistingAccount)




        val sharedPreferences = getSharedPreferences("StudyMatePrefs", MODE_PRIVATE)
        val firstName = sharedPreferences.getString("firstName", "")
        val lastName = sharedPreferences.getString("lastName", "")
        val email = sharedPreferences.getString("email", "")

        textViewAccountName.text = "${firstName} ${lastName}"
        textViewAccountEmail.text = email

        layoutExistingAccount.setOnClickListener {
            loginSSo()

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_sso)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



    }

    fun loginSSo() {
        val sharedPreferences = getSharedPreferences("StudyMatePrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("idToken", null)

        val request = LoginSSO(idToken = token)
            RetroFitClient.api.loginSSO(request).enqueue(object : Callback<LoginResponse?> {
                    override fun onResponse(p0: Call<LoginResponse?>, p1: Response<LoginResponse?>) {
                        if (p1.isSuccessful) {
                            Toast.makeText(this@LoginSSOActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginSSOActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(this@LoginSSOActivity, "Login Failed! ${p1.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(p0: Call<LoginResponse?>, t: Throwable) {
                        Toast.makeText(this@LoginSSOActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

              }

           }

