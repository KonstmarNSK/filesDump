import com.kostya.filesDump.configs.MainConfig;
import com.kostya.filesDump.controllers.rest.GetDirectoryFilesListController;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by Костя on 01.06.2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = MainConfig.class)
@ActiveProfiles("test")
public class FileResolverTest {
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    UserRepository userRepository;

    @Before
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();

        if(userRepository.getUserByEmail("aaa") == null) {
            User newUser = new User();
            newUser.setPassword("123");
            newUser.setEmail("aaa");
            newUser.addAuthority("ROLE_USER");
            userRepository.putUser(newUser);
        }
    }

    @Test
    @WithMockUser
    public void testNotExistingPath() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/userDirectory/userPassword/someNotExistingDirectoryNamed_oiawuehsbv/userEmail/someDirectory/someFile"))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser
    public void testExistingFile() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/userFile/123/aaa/1.txt").servletPath("/rest/userFile/123/aaa/1.txt"))
                .andExpect(MockMvcResultMatchers.content().string("testText"));
    }

    @Test
    @WithMockUser
    public void testExistingDirectory() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/userDirectory/123/aaa").servletPath("/rest/userDirectory/123/aaa"))//.andExpect(MockMvcResultMatchers.handler().handlerType(GetDirectoryFilesListController.class));
                .andExpect(MockMvcResultMatchers.content().string("[{\"filename\":\"1.txt\",\"createTime\":1496655576448,\"lastModified\":1496656050053,\"isDirectory\":false,\"fileSize\":8,\"fileType\":\"text/plain\"},{\"filename\":\"2.txt\",\"createTime\":1496655576447,\"lastModified\":1496655089219,\"isDirectory\":false,\"fileSize\":0,\"fileType\":\"text/plain\"},{\"filename\":\"bbb\",\"createTime\":1495798001550,\"lastModified\":1497610946810,\"isDirectory\":true,\"fileSize\":15208,\"fileType\":\"unknown\"}]"));
    }
}
