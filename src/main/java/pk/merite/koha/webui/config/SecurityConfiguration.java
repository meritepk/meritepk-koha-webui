package pk.merite.koha.webui.config;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.View;

import pk.merite.koha.webui.user.UserService;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/webservices/**").hasAnyAuthority("ADMIN")
            .and()
            .exceptionHandling().defaultAuthenticationEntryPointFor(new Http403ForbiddenEntryPoint(),
                new AntPathRequestMatcher("/webservices/**"))
            .and()
            .formLogin()
            .loginPage("/login").permitAll()
            .successHandler(new SimpleUrlAuthenticationSuccessHandler("/webservices/users/me"))
            .failureHandler(new SimpleUrlAuthenticationFailureHandler())
            .and()
            .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .ignoringAntMatchers("/h2-console/**");
    }

    @Bean(name = "error")
    public View defaultErrorView() {
        return new ErrorView();
    }

    public static class ErrorView implements View {
        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        }

        @Override
        public String getContentType() {
            return "text/html";
        }
    }

}
