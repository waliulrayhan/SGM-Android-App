package com.go.sgm_android.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.go.sgm_android.R;
import com.go.sgm_android.model.Comment;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setComments(List<Comment> comments) {
        this.commentList = comments;
        notifyDataSetChanged();
    }

    // Method to remove a comment from the list
    public void removeItem(int position) {
        commentList.remove(position);
        notifyItemRemoved(position);
    }

    // Method to get the Firebase key of a comment at a specific position
    public String getCommentKey(int position) {
        return commentList.get(position).getKey();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView textComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textComment = itemView.findViewById(R.id.text_comment);
        }

        public void bind(Comment comment) {
            textComment.setText(comment.getComment());
        }
    }
}
