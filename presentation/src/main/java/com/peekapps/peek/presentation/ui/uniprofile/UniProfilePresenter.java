/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.uniprofile;

import com.peekapps.peek.presentation.Presenter;
import com.peekapps.peek.presentation.model.PhotoModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Slav on 23/05/2016.
 */
public class UniProfilePresenter implements Presenter {

    private UniProfileView uniProfileView;

    @Inject
    public UniProfilePresenter() {

    }

    public void setUniProfileView(UniProfileView uniProfileView) {
        this.uniProfileView = uniProfileView;
    }


    public void initialize() {
        loadThumbnails("stirling");     //example
    }

    // ------ DATA RELATED ------

    public void loadThumbnails(String uniId) {
        String[] tempUrls = new String[20];
        tempUrls[0] = "http://www.studyinscotland.org/media/57545/Stirling%20Campus%20960x500.jpg";
        tempUrls[1] = "http://www.undiscoveredscotland.co.uk/stirling/stirlingcastle/images/am-450.jpg";
        tempUrls[2] = "http://static.laterooms.com/hotelphotos/laterooms/290534/gallery/beech-court-university-of-stirling-stirling_280420150859575784.jpg";
        tempUrls[3] = "http://www.studyinscotland.org/media/20320/University-of-Stirling-HeaderGraphic-4.jpg";
        tempUrls[4] = "http://www.cundall.com/UploadedImages/df184e9a-ab90-4646-aa70-1e6e94bca143___Selected.jpg";
        tempUrls[5] = "http://www.sir-robert-mcalpine.com/files/project/31348/Stirling_107___Main.jpg";
        tempUrls[6] = "http://i.telegraph.co.uk/multimedia/archive/01933/Sir_Alex_Ferguson-_1933774i.jpg";
        tempUrls[7] = "https://www.stir.ac.uk/media/schools/sport/sportcentre/images/University%20Gym21.jpg";
        tempUrls[8] = "http://www.studyinscotland.org/media/20325/University-of-Stirling-HeaderGraphic-5.jpg";
        tempUrls[9] = "http://www.byfarthebestofscotland.com/wp-content/uploads/2015/09/dusk-stir.jpg";
        tempUrls[10] = "http://www.pearltraveldiscounts.co.uk/sitemanager/uploads/1377305488fubar-stirling-742.jpg";
        tempUrls[11] = "http://i.dailymail.co.uk/i/pix/2008/04_04/023stirlingDM_468x326.jpg";
        tempUrls[12] = "http://www.groot-brittannie-liefhebbers.nl/afbeelding-plaatsen/stirling-wallace.jpg";
        tempUrls[13] = "http://images2.communitycollegereview.com/article/462x462/0/297/The-Catch-22-of-Community-College-Graduation-Rates-ObjGgo.jpg";
        tempUrls[14] = "http://www.rvc.ac.uk/Media/Default/RVC%20News/gad1.jpg";
        tempUrls[15] = "http://www.bergen.esn.no/sites/bergen.esn.no/files/events/images/confetti_party-1320.jpg";
        tempUrls[16] = "http://www.budapestbikebreeze.com/budapest-bike/mountain-biking-budapest.jpg";


        List<PhotoModel> thumbs = new ArrayList<>();
        for (int i = 0; i < tempUrls.length; i++) {
            thumbs.add(new PhotoModel(tempUrls[i], "0"));
        }
        uniProfileView.setThumbPhotos(thumbs);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
