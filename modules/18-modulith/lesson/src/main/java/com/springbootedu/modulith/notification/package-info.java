/**
 * Lesson 3.3 — the notification module. It may depend on the order module only (checked by verify()).
 */
// tag::allowed-dependencies[]
@ApplicationModule(allowedDependencies = "order")
// end::allowed-dependencies[]
@NullMarked
package com.springbootedu.modulith.notification;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
