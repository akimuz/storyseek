package cn.timflux.storyseek;

import com.fasterxml.classmate.Annotations;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class StoryseekApplication {

    public static void main(String[] args){SpringApplication.run(StoryseekApplication.class, args);}
}