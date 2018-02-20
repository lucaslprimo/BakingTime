package br.com.lucaslprimo.bakingtime.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.lucaslprimo.bakingtime.R;
import br.com.lucaslprimo.bakingtime.data.Step;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lucas Primo on 01-Feb-18.
 */

public class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.StepViewHolder> {

    private Step[] mStepList;
    private OnClickListener onClickListener;

    public StepListAdapter(OnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }

    interface OnClickListener{
        void onItemClick(int position);
    }

    public void setStepList(Step[] mStepList) {
        this.mStepList = mStepList;
        notifyDataSetChanged();
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater =  LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_step,parent,false);

        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(mStepList==null)
            return 0;
        else return mStepList.length;
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.step_name) TextView txtStep;

        StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position)
        {
            if(position==0) {
                txtStep.setText(mStepList[position].getShortDescription());
            }else
            {
                txtStep.setText(String.format(itemView.getContext().getString(R.string.step_text), position, mStepList[position].getShortDescription()));
            }
        }

        @Override
        public void onClick(View view) {
            onClickListener.onItemClick(getAdapterPosition());
        }
    }
}
