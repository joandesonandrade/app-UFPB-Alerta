package com.joandeson.ufpbalerta.utilMe;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.AnalyticsGoogle;

/**
 * Created by JoHN on 08/02/2019.
 */

public class analytic {

    private Tracker mTracker;
    private String nome;
    private Activity mActivity;

    public analytic(String nome, Activity mActivity) {
        this.nome = nome;
        this.mActivity = mActivity;
    }

    public void registroTela(){

        AnalyticsGoogle application = (AnalyticsGoogle)mActivity.getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.setScreenName(nome);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

}
