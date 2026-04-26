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
    fun webSecurityCustomizer(): org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer {
        return org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(
                "/imagenes/promociones/**",
                "/imagenes/promomecanico/**"
            )
        }
    }

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
                    .requestMatchers("/uploads/**").permitAll()
                    .requestMatchers("/usuarios/**").hasRole("ADMIN")
                    .requestMatchers("/clientes/**").hasAnyRole("ADMIN","CLIENTE","VENDEDOR")
                    .requestMatchers("/motos/**").permitAll()
                    .requestMatchers("/motoCliente/**").hasAnyRole("ADMIN","CLIENTE","MECANICO")
                    .requestMatchers("/imagenes/promociones/**").permitAll()
                    .requestMatchers("/imagenes/promomecanico/**").permitAll()
                    .requestMatchers("/imagenes/motosegundamano/**").permitAll()
                    .requestMatchers("/imagenes/upload").hasAnyRole("ADMIN","VENDEDOR")
                    .requestMatchers("/imagenes/**").permitAll()
                    .requestMatchers("/motosSegundaMano/**").permitAll()
                    .requestMatchers("/mecanicos/**").hasAnyRole("ADMIN","MECANICO")
                    .requestMatchers("/vendedores/**").hasAnyRole("ADMIN","VENDEDOR")
                    .requestMatchers("/reservas/**").hasAnyRole("ADMIN","CLIENTE","VENDEDOR")
                    .requestMatchers("/ventas/**").hasAnyRole("ADMIN","VENDEDOR")
                    .requestMatchers("/citas/**").hasAnyRole("ADMIN","CLIENTE","MECANICO","VENDEDOR")
                    .requestMatchers("/reparaciones/**").hasAnyRole("ADMIN","MECANICO", "CLIENTE")
                    .anyRequest().authenticated()
            }

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}