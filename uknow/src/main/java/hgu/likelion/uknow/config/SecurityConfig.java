package hgu.likelion.uknow.config;


import hgu.likelion.uknow.jwt.JwtAuthenticationFilter;
import hgu.likelion.uknow.jwt.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.antlr.v4.runtime.Token;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    // private final CorsConfig corsConfig;

    @Value("${custom.origin.allowed}")
    private String client;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                // ID, Password 문자열을 Base64로 인코딩하여 전달하는 구조
                .httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer.disable())
                // 쿠키 기반이 아닌 JWT 기반이므로 사용하지 않음
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                // CORS 설정
                // .cors(c -> {
                //             CorsConfigurationSource source = request -> {
                //                 // Cors 허용 패턴
                //                 CorsConfiguration config = new CorsConfiguration();
                //                 config.setAllowedOrigins(
                //                         List.of("*")
                //                 );
                //                 config.setAllowedMethods(
                //                         List.of("*")
                //                 );
                //                 return config;
                //             };
                //             c.configurationSource(source);
                //         }
                // )
                // Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 조건별로 요청 허용/제한 설정
                .authorizeRequests()
                // 회원가입과 로그인은 모두 승인
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                // /admin으로 시작하는 요청은 ADMIN 권한이 있는 유저에게만 허용
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // /user 로 시작하는 요청은 USER 권한이 있는 유저에게만 허용
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().denyAll()
                .and()
                // JWT 인증 필터 적용
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                // 에러 핸들링
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(
                        new AccessDeniedHandler() {
                            @Override
                            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                                // 권한 문제가 발생했을 때 이 부분을 호출한다.
                                response.setStatus(403);
                                response.setCharacterEncoding("utf-8");
                                response.setContentType("text/html; charset=UTF-8");
                                response.getWriter().write("권한이 없는 사용자입니다.");
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(client));
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

//import hgu.likelion.uknow.jwt.JwtAuthenticationFilter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.util.Arrays;
//
//
//@EnableWebSecurity
//@Slf4j
//@RequiredArgsConstructor
//public class SecurityConfig {
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
////        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
//        http
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/auth/**").permitAll()
//                        .anyRequest().authenticated())
//                .cors(customizer -> customizer.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.disable())
//                .httpBasic(httpBasic -> httpBasic.disable())
//                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // session 기반이 아님을 선언
//
//
//        // 필터 체인에 커스텀 필터 추가
////        http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        // 여기서 CORS 설정을 구성하세요. 예:
//        configuration.addAllowedOrigin("*");
//        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
//        configuration.addAllowedHeader("*");
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//
//
//}