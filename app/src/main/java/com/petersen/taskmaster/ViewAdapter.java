package com.petersen.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.amplifyframework.datastore.generated.model.TaskItem;
import java.util.ArrayList;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.AdapterViewHolder> {
    public ArrayList<TaskItem> listOfTasks;
    public OnInteractWithTaskListener listener;

    public ViewAdapter(ArrayList<TaskItem> listOfTasks, OnInteractWithTaskListener listener) {
        this.listOfTasks = listOfTasks;
        this.listener = listener;
    }

    // view holder deals with the passing of data from java to the fragment (list item)
    public static class AdapterViewHolder extends RecyclerView.ViewHolder {
        public TaskItem taskClass;
        public View itemView;


        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }

    @NonNull
    @Override
    // This gets called when a fragment (list item) pops into existence
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //choose which fragment (list item) to build
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);


        final AdapterViewHolder viewHolder = new AdapterViewHolder(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(viewHolder.taskClass);
                listener.taskListener(viewHolder.taskClass);
            }
        });

        return viewHolder;
    }

    public static interface OnInteractWithTaskListener {
        public void taskListener(TaskItem taskClass);
    }


    @Override
    // This gets called when a fragment(list item) has a java class attached to it
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {                  // position is the position in the array
        holder.taskClass = listOfTasks.get(position);

        TextView itemTitle = holder.itemView.findViewById(R.id.task_title);
        TextView itemDescription = holder.itemView.findViewById(R.id.task_description);
        TextView itemState = holder.itemView.findViewById(R.id.task_state);
        itemTitle.setText(holder.taskClass.name);
        itemDescription.setText(holder.taskClass.description);
        itemState.setText(holder.taskClass.state);
    }

    @Override
    // This gets called so it knows how many fragments (list item) to put on the screen at once
    public int getItemCount() {
        return listOfTasks.size();
    }
}

