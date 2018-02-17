package br.com.lucaslprimo.bakingtime.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import br.com.lucaslprimo.bakingtime.R;
import br.com.lucaslprimo.bakingtime.data.Step;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lucas Primo on 01-Feb-18.
 */

public class StepDetailsFragment extends Fragment implements ExoPlayer.EventListener {

    private static final String INSTANCE_PLAYBACK_POSITION = "playback_position";
    private static final String INSTANCE_CURRENT_WINDOW = "current_window";

    Intent intent;
    Context context;
    @BindView(R.id.my_player) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.step_description) TextView mStepDescription;
    @BindView(R.id.index_steps) TextView mIndexSteps;
    @BindView(R.id.next_step) Button mNextButton;
    @BindView(R.id.previous_step) Button mPreviousButton;
    @BindView(R.id.frame_player) FrameLayout mFramePlayer;
    boolean isTwoPanel = false ;
    boolean isLandscape = false;
    boolean hasVideo = false;

    SimpleExoPlayer mPlayer;
    Step[] mStepsList;
    public int indexStep;
    Step stepSelected;
    String videoUrl;

    View view;

    int currentWindow = 0;
    long playbackPosition = 0;

    public StepDetailsFragment(){}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        this.intent = this.getActivity().getIntent();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.context = activity.getApplicationContext();
        this.intent = activity.getIntent();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState!=null && savedInstanceState.containsKey(INSTANCE_CURRENT_WINDOW))
        {
            currentWindow = savedInstanceState.getInt(INSTANCE_CURRENT_WINDOW);
            playbackPosition = savedInstanceState.getLong(INSTANCE_PLAYBACK_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_step_details,container,false);

        ButterKnife.bind(this,view);

        if(mStepsList==null && intent.getExtras()!=null)
        {
            Parcelable[] parcelableArray=  intent.getExtras().getParcelableArray(MasterStepsListActivity.EXTRA_STEPS);
            if(parcelableArray!=null)
            {
                mStepsList = new Step[parcelableArray.length];
                for(int i = 0;i<parcelableArray.length;i++)
                {
                    mStepsList[i] = (Step) parcelableArray[i];
                }
            }

            indexStep =  intent.getExtras().getInt(MasterStepsListActivity.EXTRA_STEP_INDEX);
        }

        refreshData();

        if(isTwoPanel)
        {
            mNextButton.setVisibility(View.INVISIBLE);
            mPreviousButton.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        mPlayer.setPlayWhenReady(false);
        outState.putInt(INSTANCE_CURRENT_WINDOW,mPlayer.getCurrentWindowIndex());
        outState.putLong(INSTANCE_PLAYBACK_POSITION,mPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    void refreshUi()
    {
        if(isLandscape)
        {
            if(!hasVideo)
            {
                mFramePlayer.setVisibility(View.GONE);
                showInfo();
            }else
            {
                mFramePlayer.setVisibility(View.VISIBLE);
                hideInfo();
            }
        }else
        {
            mFramePlayer.setVisibility(View.VISIBLE);
            showInfo();
        }

    }

    void hideInfo()
    {
        mStepDescription.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);
        mPreviousButton.setVisibility(View.GONE);
        mIndexSteps.setVisibility(View.GONE);
    }

    void showInfo() {

        //Check if index is at the end or at the start to hide previous and next button when necessary
        if(indexStep == 0) {
            mNextButton.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.INVISIBLE);
        }
        else
        if(indexStep >= mStepsList.length-1) {
            mNextButton.setVisibility(View.INVISIBLE);
            mPreviousButton.setVisibility(View.VISIBLE);
        }else
        {
            mNextButton.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.VISIBLE);
        }

        mIndexSteps.setVisibility(View.VISIBLE);
        mStepDescription.setVisibility(View.VISIBLE);
    }

    void refreshData()
    {
        releasePlayer();

        stepSelected = mStepsList[indexStep];

        //Step has Video
        if(stepSelected.getVideoUrl()!=null && !stepSelected.getVideoUrl().isEmpty())
        {
            videoUrl =stepSelected.getVideoUrl();
            initializePlayer();
            mFramePlayer.setVisibility(View.VISIBLE);

            hasVideo =true;
        }else
            //Step has Image
            if(stepSelected.getThumbnailUrl()!=null && !stepSelected.getThumbnailUrl().isEmpty())
            {
                //The API is returning .mp4 on thumbnailUrl
                videoUrl = stepSelected.getThumbnailUrl();
                initializePlayer();
                mFramePlayer.setVisibility(View.VISIBLE);
                hasVideo =true;

            }else //Step has nothing but the description
            {
                mFramePlayer.setVisibility(View.GONE);
                hasVideo = false;
            }

        mStepDescription.setText(stepSelected.getDescription());

        mIndexSteps.setText(String.format(getString(R.string.index_text),indexStep,mStepsList.length-1));

        if(hasVideo && isLandscape && !isTwoPanel)
        {
            enterFullscreen();
            hideInfo();
        }else {
            exitFullscreen();
            showInfo();
        }
    }

    @OnClick(R.id.previous_step)
    void clickPrevious(View v)
    {
        indexStep--;
        refreshData();
    }

    @OnClick(R.id.next_step)
    void clickNext(View v)
    {
        indexStep++;
        refreshData();
    }

    void initializePlayer()
    {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(context),
                new DefaultTrackSelector(), new DefaultLoadControl());

        mPlayerView.setPlayer(mPlayer);

        mPlayer.setPlayWhenReady(true);

        Uri uri = Uri.parse(videoUrl);

        MediaSource mediaSource =new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(getString(R.string.app_name))).createMediaSource(uri);
        mPlayer.prepare(mediaSource, true, false);

        mPlayer.addListener(this);

        mPlayer.seekTo(currentWindow,playbackPosition);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }


    @Override
    public void onPause() {
        super.onPause();
        if(mPlayer !=null)
        {
            mPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        if(!isTwoPanel) {

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                isLandscape = true;

                if (hasVideo)
                    enterFullscreen();

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                isLandscape = false;

                exitFullscreen();
            }

            refreshUi();

        }
    }

    void enterFullscreen()
    {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    void exitFullscreen()
    {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }
}
