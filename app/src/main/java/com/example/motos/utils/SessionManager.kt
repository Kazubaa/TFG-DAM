package com.example.motos.utils


import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("token", token).apply()
    fun getToken(): String? = prefs.getString("token", null)

    fun saveRol(rol: String) = prefs.edit().putString("rol", rol).apply()
    fun getRol(): String? = prefs.getString("rol", null)

    fun saveUsername(username: String) = prefs.edit().putString("username", username).apply()
    fun getUsername(): String? = prefs.getString("username", null)

    fun saveId(id: Long) = prefs.edit().putLong("id", id).apply()
    fun getId(): Long = prefs.getLong("id", -1)

    fun isLoggedIn(): Boolean = getToken() != null

    fun logout() = prefs.edit().clear().apply()

    fun saveClienteId(id: Long) = prefs.edit().putLong("clienteId", id).apply()
    fun getClienteId(): Long = prefs.getLong("clienteId", -1)
}