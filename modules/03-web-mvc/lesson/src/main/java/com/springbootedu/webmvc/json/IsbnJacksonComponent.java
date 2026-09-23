package com.springbootedu.webmvc.json;

import com.springbootedu.webmvc.book.Isbn;
import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

/**
 * Lesson 3.7 — Jackson 3 (package tools.jackson): write the Isbn value object as "9780134685991"
 * instead of {"value": "9780134685991"}. @JacksonComponent registers both classes with Boot's JsonMapper.
 */
// tag::jackson-component[]
@JacksonComponent
public class IsbnJacksonComponent {

    public static class Serializer extends ValueSerializer<Isbn> {

        @Override
        public void serialize(Isbn isbn, JsonGenerator generator, SerializationContext context) {
            generator.writeString(isbn.value());
        }
    }

    public static class Deserializer extends ValueDeserializer<Isbn> {

        @Override
        public Isbn deserialize(JsonParser parser, DeserializationContext context) {
            return new Isbn(parser.getString());   // Jackson 3: getString() replaces getText()
        }
    }
}
// end::jackson-component[]
