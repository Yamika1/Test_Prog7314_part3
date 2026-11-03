
package com.example.studymate

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnRegister: Button
    private lateinit var textviewLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btnRegister = findViewById(R.id.btnRegister)
        textviewLogin = findViewById(R.id.textviewLogin)

        btnRegister.setOnClickListener {

            val firstNameValue = firstName.text.toString().trim()
            val lastNameValue = lastName.text.toString().trim()
            val usernameValue = username.text.toString().trim()
            val emailValue = email.text.toString().trim()
            val passwordValue = password.text.toString()

            if (
                firstNameValue.isEmpty() ||
                lastNameValue.isEmpty() ||
                usernameValue.isEmpty() ||
                emailValue.isEmpty() ||
                passwordValue.isBlank()
            ) {
                Toast.makeText(
                    this,
                    "All fields are required. Please fill in all empty fields",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                btnRegister.isEnabled = false

                register(
                    firstNameValue,
                    lastNameValue,
                    emailValue,
                    usernameValue,
                    passwordValue
                )
            }
        }

        textviewLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    private fun register(
        firstName: String,
        lastName: String,
        email: String,
        username: String,
        password: String
    ) {

        val request = RegisterRequest(
            firstName = firstName,
            lastName = lastName,
            email = email,
            username = username,
            password = password
        )

        RetroFitClient.api.register(request).enqueue(
            object : Callback<RegisterResponse> {

                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {

                    btnRegister.isEnabled = true

                    if (response.isSuccessful) {

                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration Successful!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@RegisterActivity,
                            MainActivity::class.java
                        )

                        startActivity(intent)
                        finish()

                    } else {

                        val errorBody = response.errorBody()?.string()

                        Log.e(
                            "REGISTER_ERROR",
                            "Code: ${response.code()}, Error: $errorBody"
                        )

                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed: $errorBody",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<RegisterResponse>,
                    t: Throwable
                ) {

                    btnRegister.isEnabled = true

                    Log.e(
                        "REGISTER_ERROR",
                        "Registration request failed",
                        t
                    )

                    Toast.makeText(
                        this@RegisterActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }
}