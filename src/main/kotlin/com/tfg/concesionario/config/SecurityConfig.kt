package com.tfg.concesionario.config

import com.tfg.concesionario.security.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(private val jwtFilter: JwtFilter) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http
            .csrf { it.disable() }

            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            .authorizeHttpRequests {
                it
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/usuarios/**").hasRole("ADMIN")
                    .requestMatchers("/motos/**").permitAll()
                    .requestMatchers("/motosSegundaMano/**").hasAnyRole("ADMIN","VENDEDOR","CLIENTE")
                    .requestMatchers("/reservas/**").hasAnyRole("ADMIN","CLIENTE","VENDEDOR")
                    .requestMatchers("/ventas/**").hasAnyRole("ADMIN","VENDEDOR")
                    .requestMatchers("/citas/**").hasAnyRole("ADMIN","CLIENTE","MECANICO","VENDEDOR")
                    .requestMatchers("/reparaciones/**").hasAnyRole("ADMIN","MECANICO")
                    .requestMatchers("/imagenes/upload").hasAnyRole("ADMIN","VENDEDOR")
                    .requestMatchers("/imagenes/**").permitAll()
                    .anyRequest().authenticated()
            }

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}