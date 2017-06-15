import com.kostya.filesDump.configs.MainConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
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
public class FileResolverTest {
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Before
    public void setMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
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
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/userDirectory/123/aaa").servletPath("/rest/userDirectory/123/aaa"))
                .andExpect(MockMvcResultMatchers.content().string("[{\"filename\":\"1.txt\",\"createTime\":1496655576448,\"isDirectory\":false},{\"filename\":\"2.txt\",\"createTime\":1496655576447,\"isDirectory\":false},{\"filename\":\"bbb\",\"createTime\":1495798001550,\"isDirectory\":true}]"));
    }
}