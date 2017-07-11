import com.kostya.filesDump.configs.MainConfig;
import com.kostya.filesDump.entities.User;
import com.kostya.filesDump.repositories.interfaces.UserRepository;
import com.kostya.filesDump.utils.FileResolver;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

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

    @Autowired
    Environment env;

    @Autowired
    FileResolver fileResolver;

    File userDirectory = null;
    String userName = "testUser";
    String userPassword = "123";
    String tmpFileName = "tmpFile";

    @Before
    public void setMockMvc() throws FileNotFoundException{
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        if(userRepository.getUserByEmail(userName) == null) {
            User newUser = new User();
            newUser.setPassword(userPassword);
            newUser.setEmail(userName);
            newUser.addAuthority("ROLE_USER");
            userRepository.putUser(newUser);
        }

        String baseDirectoryPath = env.getProperty("baseDirectoryPath");
        if(null == baseDirectoryPath || baseDirectoryPath.isEmpty()){
            throw new FileNotFoundException("baseDirectoryPath is not set in 'test.properties' file");
        }

        File tmpMultipartDirectory = new File(env.getProperty("tmpStorageForMultipartFiles"));
        if(!tmpMultipartDirectory.exists()){
            if(env.getProperty("createNotExistingPath", Boolean.class)){
                tmpMultipartDirectory.mkdirs();
            }else {
                throw new FileNotFoundException("tmpStorageForMultipartFiles is not set in 'test.properties' file");
            }
        }

        userDirectory = new File(baseDirectoryPath+File.separator+"testUser");
        if(!userDirectory.exists()){
            if(!env.getProperty("createNotExistingPath", Boolean.class)){
                throw new  FileNotFoundException("baseDirectoryPath is not set in 'test.properties' file");
            }
            userDirectory.mkdirs();
        }
        System.out.println("initial value: "+userDirectory);
    }

    @Test
    @WithMockUser
    public void testNotExistingPath() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/userDirectory/"+userPassword+"/"+userName+"/NotExistingDirectory_9097675/someFile")
                .servletPath("/rest/userDirectory/"+userPassword+"/"+userName+"/NotExistingDirectory_9097675/someFile"))
                .andExpect(MockMvcResultMatchers.content().string(""));
    }

    @Test
    @WithMockUser
    public void testExistingFile() throws Exception{
        System.out.println(userDirectory);
        File tmpFile = new File(userDirectory.getAbsolutePath()+File.separator+tmpFileName);
        if(!tmpFile.exists()){
            tmpFile.createNewFile();
        }else {
            System.out.println("deleting existing file");
            Arrays.stream(tmpFile.listFiles()).forEach(File::delete);
            tmpFile.delete();
            System.out.println("Creating "+tmpFile.getAbsolutePath()+": "+tmpFile.createNewFile());
        }

        FileOutputStream outputStream = new FileOutputStream(tmpFile);
        outputStream.write("some content".getBytes("UTF-8"));
        outputStream.close();

        mockMvc.perform(MockMvcRequestBuilders.get("/rest/userFile/"+userPassword+"/"+userName+"/"+tmpFileName)
                .servletPath("/rest/userFile/"+userPassword+"/"+userName+"/"+tmpFileName))
                .andExpect(MockMvcResultMatchers.content().string("some content"));

        tmpFile.delete();
    }

    @Test
    @WithMockUser
    public void testExistingDirectory() throws Exception{
        File tmpFile = new File(userDirectory.getAbsolutePath()+File.separator+tmpFileName);
        tmpFile.mkdir();

        File innerFile = new File(tmpFile.getAbsolutePath()+File.separator+"1.txt");
        File innerDirectory = new File(tmpFile.getAbsolutePath()+File.separator+"subDir");
        innerFile.createNewFile();
        innerDirectory.mkdir();

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/rest/userDirectory/" + userPassword + "/" + userName).servletPath("/rest/userDirectory/" + userPassword + "/" + userName))
                    .andExpect(MockMvcResultMatchers.content().string("[{\"filename\":\"tmpFile\",\"createTime\":"+fileResolver.getFileCreationTime(tmpFile)+",\"lastModified\":"+tmpFile.lastModified()+",\"isDirectory\":true,\"fileSize\":0,\"fileType\":\"unknown\"}]"));
        }finally {
            tmpFile.delete();
        }
    }

    @After
    public void deleteBaseTestDirectory(){
        if(env.getProperty("createNotExistingPath", Boolean.class)) {
            new File(env.getProperty("baseDirectoryPath")).delete();
            new File(env.getProperty("tmpStorageForMultipartFiles")).delete();
        }
    }
}
