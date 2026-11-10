package fi.haagahelia.artstore.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fi.haagahelia.artstore.domain.AppUser;
import fi.haagahelia.artstore.domain.AppUserRepository;

@Service 
public class UserDetailServiceImpl implements UserDetailsService{

    private final AppUserRepository appUserRepository;

    public UserDetailServiceImpl(AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository; 
	}
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        AppUser curruser = appUserRepository.findByUsername(username);
        if (curruser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                username,
                curruser.getPasswordHash(),
                AuthorityUtils.createAuthorityList(curruser.getRole()));
    }


}
