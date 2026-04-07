package com.tfg.concesionario.security

import org.springframework.security.core.GrantedAuthority

class RoleAuthority(private val role: String) : GrantedAuthority {

    override fun getAuthority(): String {
        return "ROLE_$role"
    }
}