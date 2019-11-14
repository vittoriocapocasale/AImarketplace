package ai.marketplace.server.common;
import ai.marketplace.server.structures.TimeArea;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;


//Class to convert TimeArea from Json. Time area is passed in the url, so requires manual conversion
@Component
public class TimeAreaConverter implements Converter<String, TimeArea> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public TimeArea convert(String source) {
        try {
            return objectMapper.readValue(source, TimeArea.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}