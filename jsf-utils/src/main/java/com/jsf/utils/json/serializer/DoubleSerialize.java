package com.jsf.utils.json.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * Description: @JsonSerialize(using = DoubleSerialize.class)
 * User: xujunfei
 * Date: 2018-11-24
 * Time: 21:49
 */
public class DoubleSerialize extends JsonSerializer<Double> {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    static {
        df.setMaximumFractionDigits(2); // 显示小数点几位
        df.setGroupingSize(0);
        df.setRoundingMode(RoundingMode.FLOOR);
    }

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException, JsonProcessingException {
        if (value != null) {
            gen.writeNumber(df.format(value));
        }
    }
}
