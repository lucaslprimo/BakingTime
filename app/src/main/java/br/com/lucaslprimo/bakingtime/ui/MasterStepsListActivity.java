package br.com.lucaslprimo.bakingtime.ui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.lucaslprimo.bakingtime.MyIdlingResource;
import br.com.lucaslprimo.bakingtime.R;
import br.com.lucaslprimo.bakingtime.data.Recipe;

import static br.com.lucaslprimo.bakingtime.ui.MainActivity.EXTRA_RECIPE;

public class MasterStepsListActivity extends AppCompatActivity implements StepListAdapter.OnClickListener{

    public static final String EXTRA_STEPS = "steps";
    public static final String EXTRA_STEP_INDEX = "step_index";
    public static final String INSTANCE_STEP_INDEX = "instance_step_index";

    int stepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_steps_list);

        if(getIntent().getExtras()!=null) {
            Recipe mRecipe = getIntent().getExtras().getParcelable(EXTRA_RECIPE);
            getSupportActionBar().setTitle(mRecipe.getName());
        }

        if(savedInstanceState !=null && savedInstanceState.containsKey(INSTANCE_STEP_INDEX))
        {
            stepIndex = savedInstanceState.getInt(INSTANCE_STEP_INDEX);
            onItemClick(stepIndex);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putInt(INSTANCE_STEP_INDEX,stepIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(INSTANCE_STEP_INDEX,stepIndex);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onItemClick(int position) {

        stepIndex = position;

        if(getIntent().getExtras()!=null) {
            Recipe mRecipe = getIntent().getExtras().getParcelable(EXTRA_RECIPE);

            if(getResources().getBoolean(R.bool.isTablet))
            {
                StepDetailsFragment detailsFragment = new StepDetailsFragment();
                detailsFragment.isTwoPanel=true;
                detailsFragment.mStepsList = mRecipe.getStepList();
                detailsFragment.indexStep = position;

                if(findViewById(R.id.frame_player)==null) {
                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.frame_details,detailsFragment)
                            .commit();
                }else
                {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_details,detailsFragment)
                            .commit();
                }
            }else
            {
                Intent intent = new Intent(this,StepDetailsActivity.class);

                if(mRecipe!=null) {
                    intent.putExtra(EXTRA_STEPS, mRecipe.getStepList());
                    intent.putExtra(EXTRA_STEP_INDEX, position);
                }

                this.startActivity(intent);
            }
        }
    }
}
