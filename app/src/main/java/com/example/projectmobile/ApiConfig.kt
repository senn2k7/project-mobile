package com.example.snapchatsphlasscreen

object ApiConfig {

    // Alamat backend TemanStudi di komputer XAMPP
    const val BASE_URL = "http://192.168.1.107/TemanStudi/"

    // Endpoint autentikasi
    const val LOGIN_URL = "${BASE_URL}auth/login.php"
    const val REGISTER_URL = "${BASE_URL}auth/register.php"
    const val GOOGLE_LOGIN_URL = "${BASE_URL}auth/google_login.php"
    const val GOOGLE_WEB_CLIENT_ID = "610156723225-huh6uc6c1d0d0n4mgkg8e2f02v8lefv8.apps.googleusercontent.com"

    // Endpoint pengujian koneksi
    const val TEST_URL = "${BASE_URL}api/test.php"
}
