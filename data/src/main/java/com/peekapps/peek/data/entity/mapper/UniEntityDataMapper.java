/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.data.entity.mapper;

import com.peekapps.peek.data.entity.UniEntity;
import com.peekapps.peek.domain.University;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Slav on 10/03/2016.
 */
public class UniEntityDataMapper {

    @Inject
    public UniEntityDataMapper() {}

    public University transform(UniEntity uniEntity) {
        University uni = null;
        if (uniEntity != null) {
            uni = new University(uniEntity.getId());
            uni.setName(uniEntity.getName());
            uni.setCity(uniEntity.getCity());
            uni.setLatitude(uniEntity.getLatitude());
            uni.setLongitude(uniEntity.getLongitude());
            uni.setType(uniEntity.getType());
            uni.setAddress(uniEntity.getAddress());
        }
        return uni;
    }

    /**
     * Transform a List of {@link UniEntity} into a Collection of {@link University}.
     *
     * @param uniEntityCollection Object Collection to be transformed.
     * @return {@link University} if valid {@link UniEntity} otherwise null.
     */
    public List<University> transform(Collection<UniEntity> uniEntityCollection) {
        List<University> uniList = new ArrayList<>(uniEntityCollection.size());
        for (UniEntity uniEntity : uniEntityCollection) {
            University uni = transform(uniEntity);
            if (uni != null) {
                uniList.add(uni);
            }
        }
        return uniList;
    }


}
