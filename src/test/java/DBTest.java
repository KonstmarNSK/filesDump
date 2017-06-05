import com.kostya.filesDump.configs.MainConfig;
import com.kostya.filesDump.entities.User;
import com.kostya.filesDump.repositories.interfaces.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by Костя on 14.05.2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = MainConfig.class)
@ActiveProfiles("test")
public class DBTest {
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext applicationContext;

    @Autowired
    UserRepository userRepository;

    @Before
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @WithMockUser
    @Test
    public void createUser(){
        User newUser = new User();
        newUser.setPassword("123");
        newUser.setEmail("ccc@bbb.aaa");
        newUser.addAuthority("ROLE_USER");

        userRepository.putUser(newUser);

        User user = userRepository.getUserByEmail("ccc@bbb.aaa");
        org.junit.Assert.assertNotNull(user);
    }

}
