package pk.merite.koha.webui.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService, PasswordEncoder {

    private final String name, password, roles;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(@Value("${security.user.name}") String name,
        @Value("${security.user.password}") String password,
        @Value("${security.user.roles}") String roles) {
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (name.equals(username)) {
            return new User(username, password,
                AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
        }
        throw new UsernameNotFoundException("User " + username + " not verified");
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

}
