/*
 * Copyright 2024 Mirco Lindenau | HttpMarco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.httpmarco.polocloud.base.common;

import com.google.gson.*;
import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.api.properties.PropertyPool;

import java.lang.reflect.Type;

public final class PropertiesPoolSerializer implements JsonSerializer<PropertyPool>, JsonDeserializer<PropertyPool> {

    @Override
    public PropertyPool deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var propertiesPool = new PropertyPool();

        jsonElement.getAsJsonObject().asMap()
                .forEach((s, property) -> PropertyPool.PROPERTY_LIST
                        .stream()
                        .filter(it -> it.id().equals(s))
                        .findFirst()
                        .ifPresentOrElse(prop -> propertiesPool.properties().put(prop.id(), jsonDeserializationContext.deserialize(property, Object.class)), () -> {
                            CloudAPI.instance().logger().error("Unknown property found: " + s, null);
                        }));

        return propertiesPool;
    }

    @Override
    public JsonElement serialize(PropertyPool propertyPool, Type type, JsonSerializationContext jsonSerializationContext) {
        var object = new JsonObject();
        propertyPool.properties().forEach((id, o) -> object.add(id, jsonSerializationContext.serialize(o)));
        return object;
    }
}
