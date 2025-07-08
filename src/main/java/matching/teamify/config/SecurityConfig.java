package matching.teamify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))                  // 1. CORS 설정 활성화 (아래 corsConfigurationSource 빈 사용)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())             // 2. 요청별 인가 규칙 설정 (현재는 모든 요청 허용)
                .formLogin(formLogin -> formLogin.disable())                                        // 3. 기본 폼 로그인 비활성화
                .httpBasic(httpBasic -> httpBasic.disable());                                       // 4. HTTP Basic 인증 비활성화

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://teamify.today", "https://www.teamify.today"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));               // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("*"));                                                          // 모든 요청 헤더 허용
        configuration.setAllowCredentials(true);                                                                    // 쿠키/인증 정보 포함 요청 허용
        configuration.setMaxAge(3600L);                                                                             // pre-flight 요청 캐시 시간 (1시간)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);                                             // 모든 경로 ("/**")에 이 CORS 설정을 적용
        return source;
    }
}
