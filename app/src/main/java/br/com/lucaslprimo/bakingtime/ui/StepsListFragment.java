package br.com.lucaslprimo.bakingtime.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.lucaslprimo.bakingtime.R;
import br.com.lucaslprimo.bakingtime.data.Ingredient;
import br.com.lucaslprimo.bakingtime.data.Recipe;
import br.com.lucaslprimo.bakingtime.data.Step;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.lucaslprimo.bakingtime.ui.MainActivity.EXTRA_RECIPE;

/**
 * Created by Lucas Primo on 01-Feb-18.
 */

public class StepsListFragment extends Fragment implements StepListAdapter.OnClickListener{

    Intent intent;
    Context context;
    Step[] mStepsList;
    Recipe mRecipe;
    StepListAdapter mStepAdapter;
    @BindView(R.id.recycler_view_steps) RecyclerView mRecyclerViewSteps;
    @BindView(R.id.txt_ingredients) TextView txtIngredients;
    @BindView(R.id.scroll_view) NestedScrollView mScrollView;

    private static final String INSTANCE_STEPS = "instance_steps";
    public static final String EXTRA_STEPS = "steps";
    public static final String EXTRA_STEP_INDEX = "step_index";

    public StepsListFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        this.intent = this.getActivity().getIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_steps_list,container,false);

        ButterKnife.bind(this,view);

        mStepAdapter = new StepListAdapter(this);
        mRecyclerViewSteps.setAdapter(mStepAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerViewSteps.setLayoutManager(layoutManager);
        mRecyclerViewSteps.setHasFixedSize(true);
        mRecyclerViewSteps.setNestedScrollingEnabled(false);


        if(intent.getExtras()!=null) {
            mRecipe = intent.getExtras().getParcelable(EXTRA_RECIPE);
            if(mRecipe!=null) {

                mStepsList = mRecipe.getStepList();
                mStepAdapter.setStepList(mStepsList);

                txtIngredients.setText("");
                int index =1;
                for (Ingredient ingredient:mRecipe.getIngredientList()) {
                    txtIngredients.append(
                            String.format(
                                    context.getString(R.string.ingredient_text),
                                    index,
                                    ingredient.getQuantity(),
                                    ingredient.getMeasure(),
                                    ingredient.getIngredient()));
                    index++;
                }
            }
        }

        return view;
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(getActivity(),StepDetailsActivity.class);

        intent.putExtra(EXTRA_STEPS,mStepsList);
        intent.putExtra(EXTRA_STEP_INDEX,position);

        getActivity().startActivity(intent);
    }
}
