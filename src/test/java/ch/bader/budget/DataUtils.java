package ch.bader.budget;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DataUtils {

    public static String getMogoDataSourceString() throws IOException {
        Properties props = new Properties();
        FileInputStream fis;

        fis = new FileInputStream("src/main/resources/application.properties");
        props.load(fis);

        return "mongodb://" +
            props.getProperty("spring.data.mongodb.username") +
            ":" +
            props.getProperty("spring.data.mongodb.password") +
            "@localhost";
    }


}
