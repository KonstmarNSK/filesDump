import com.kostya.filesDump.configs.MainConfig;
import com.kostya.filesDump.entities.User;
import com.kostya.filesDump.repositories.interfaces.UserRepository;
import com.kostya.filesDump.utils.FileResolver;
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

import java.net.URLEncoder;

/**
 * Created by Костя on 26.06.2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = MainConfig.class)
@ActiveProfiles("test")
public class tmpTest {
//    %2F
MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileResolver fileResolver;

    @Before
    public void setMockMvc(){
        User newUser = new User();
        newUser.setPassword("kLp*&^:");
        newUser.setEmail("aaa@bbb.ccc");
        newUser.addAuthority("ROLE_USER");
        userRepository.putUser(newUser);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithMockUser
    public void test() throws Exception{
        String encodedPassword = URLEncoder.encode("kLp*&^:", "UTF-8");
        String encodedEmail = URLEncoder.encode("aaa@bbb.ccc", "UTF-8");
        String encodedFilePath = URLEncoder.encode("первая папка", "UTF-8");
               /* +"/"+ URLEncoder.encode("вторая папка", "UTF-8")
                +"/"+  URLEncoder.encode("файл_%.extension", "UTF-8");*/

        String resultingPath = "/rest/userDirectory/"+encodedPassword+"/"+encodedEmail+"/"+encodedFilePath;

        System.out.println("resulting path: "+resultingPath);
        System.out.println();

//        mockMvc.perform(MockMvcRequestBuilders.post(new URI(resultingPath)).servletPath(resultingPath)).andExpect(MockMvcResultMatchers.handler().handlerType(CreateDirectoryController.class));
    }
}
