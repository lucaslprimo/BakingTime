package br.com.lucaslprimo.bakingtime.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.lucaslprimo.bakingtime.R;
import br.com.lucaslprimo.bakingtime.data.Recipe;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lucas Primo on 01-Feb-18.
 */

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder> {

    private Recipe[] mRecipesList;
    private OnClickListener onClickListener;

    public RecipeListAdapter(OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }

    interface OnClickListener{
        void onItemClick(Recipe recipe);
    }

    public void setRecipeList(Recipe[] mRecipesList) {
        this.mRecipesList = mRecipesList;
        notifyDataSetChanged();
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater =  LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_recipe,parent,false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mRecipesList==null)
            return 0;
        else return mRecipesList.length;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_name) TextView txtRecipe;

        RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position)
        {
            txtRecipe.setText(mRecipesList[position].getName());
        }

        @Override
        public void onClick(View view) {
            onClickListener.onItemClick(mRecipesList[getAdapterPosition()]);
        }
    }
}
