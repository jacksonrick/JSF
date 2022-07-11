package com.jsf.config.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jsf.model.ResMsg;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-07-06
 * Time: 14:55
 */
@Component
public class OAuthServerExceptionSerializer extends StdSerializer<OAuthServerException> {

    public OAuthServerExceptionSerializer() {
        super(OAuthServerException.class);
    }

    @Override
    public void serialize(OAuthServerException e, JsonGenerator generator, SerializerProvider provider) throws IOException {
        // 自定义返回格式内容
        ResMsg resMsg = ResMsg.unauth(e.getExtendMessage(), e.getMessage());
        generator.writeObject(resMsg);
    }

}
