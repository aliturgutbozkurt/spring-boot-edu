package com.springbootedu.corecontainer.registrar;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.core.env.Environment;

/**
 * Lesson 3.6 — {@link BeanRegistrar} (Spring Framework 7): register beans with plain Java code,
 * e.g. in a loop driven by configuration. It is also AOT/native-image friendly.
 */
// tag::bean-registrar[]
public class NotificationChannelsRegistrar implements BeanRegistrar {

    private static final Map<String, Supplier<NotificationChannel>> KNOWN_CHANNELS = Map.of(
            "email", EmailChannel::new,
            "sms", SmsChannel::new,
            "push", PushChannel::new);

    @Override
    public void register(BeanRegistry registry, Environment env) {
        String[] configured = env.getProperty("bookstore.notifications.channels", String[].class, new String[] {"email"});
        List<String> names = Arrays.stream(configured).map(String::trim).filter(KNOWN_CHANNELS::containsKey).toList();

        for (String name : names) {
            Supplier<NotificationChannel> factory = KNOWN_CHANNELS.get(name);
            registry.registerBean(name + "Channel", NotificationChannel.class,
                    spec -> spec.supplier(context -> factory.get()));
        }
    }
}
// end::bean-registrar[]
