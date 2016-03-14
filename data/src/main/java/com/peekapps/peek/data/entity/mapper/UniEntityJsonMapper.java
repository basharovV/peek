/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.entity.mapper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.peekapps.peek.data.entity.LocationEntity;
import com.peekapps.peek.data.entity.UniEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;


/**
 * Created by Slav on 10/03/2016.
 */
public class UniEntityJsonMapper {

    private final Gson gson;

    @Inject
    public UniEntityJsonMapper() {
        this.gson = new Gson();
    }

    public List<UniEntity> transformUniEntityCollection(String uniListJsonResponse)
            throws JsonSyntaxException {

        List<UniEntity> uniEntityCollection = new ArrayList<>();
        try {
            JsonParser parser = new JsonParser();
            Type uniEntityType = new TypeToken<UniEntity>() {
            }.getType();
            JsonObject universities = parser.parse(uniListJsonResponse).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry: universities.entrySet()) {
                UniEntity university = gson.fromJson(entry.getValue(), uniEntityType);
                university.setId(entry.getKey());
                uniEntityCollection.add(university);
            }

            return uniEntityCollection;
        } catch (JsonSyntaxException jsonException) {
            throw jsonException;
        }
    }
}
