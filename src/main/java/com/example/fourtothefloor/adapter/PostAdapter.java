package com.example.fourtothefloor.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fourtothefloor.R;
import com.example.fourtothefloor.activity.FullPostActivity;
import com.example.fourtothefloor.fragment.bottomsheets.CommentBottomSheet;
import com.example.fourtothefloor.model.PostModel;
import com.example.fourtothefloor.rest.ApiClient;
import com.example.fourtothefloor.rest.services.UserInterface;
import com.example.fourtothefloor.util.AgoDateParse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context context;
    List<PostModel> postModels;

    public PostAdapter(Context context, List<PostModel> postModels) {
        this.context = context;
        this.postModels = postModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PostModel postModel = postModels.get(position);
        if (postModel.getPost() != null && postModel.getPost().length() > 1) {
            holder.post.setText(postModel.getPost());
        } else {
            holder.post.setVisibility(View.GONE);
        }
        // set name of user in post
        holder.peopleName.setText(postModel.getName());
        // set privacy level of post
        if (postModel.getPrivacy().equals("0")) {
            holder.privacyIcon.setImageResource(R.drawable.icon_friends); // get privacy levels from UploadActivity
        } else if (postModel.getPrivacy().equals("1")) {
            holder.privacyIcon.setImageResource(R.drawable.icon_onlyme);
        } else {
            holder.privacyIcon.setImageResource(R.drawable.icon_public);
        }

        // comment count
        if(postModel.getCommentCount().equals("0") || postModel.getCommentCount().equals("1")){
            holder.commentTxt.setText(postModel.getCommentCount()+ "Comment");
        }else{
            holder.commentTxt.setText(postModel.getCommentCount()+ "Comments");
        }

        // postlikes
        if (postModel.isLiked()) {
            holder.likeImg.setImageResource(R.drawable.icon_like_selected);
        } else {
            holder.likeImg.setImageResource(R.drawable.icon_like);
        }

        // like count
        if(postModel.getLikeCount().equals("0") || postModel.getLikeCount().equals("1")){
            holder.likeTxt.setText(postModel.getLikeCount()+" Like");
        }else{
            holder.likeTxt.setText(postModel.getLikeCount()+" Likes");
        }

        holder.likeSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.likeSection.setEnabled(false);
                if (!postModel.isLiked()) {
                    //like operation in here
                    operationLike(holder, postModel);

                    UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
                    Call<Integer> call = userInterface.likeUnlike(new AddLike(FirebaseAuth.getInstance().getCurrentUser().getUid(), postModel.getPostId(), postModel.getPostUserId(), 1));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            holder.likeSection.setEnabled(true);
                            if(response.body().equals("0")){
                                operationUnlike(holder,postModel);
                                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            holder.likeSection.setEnabled(true);
                            operationUnlike(holder,postModel);
                            Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    operationUnlike(holder, postModel);

                    // unlike operation in here

                    UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
                    Call<Integer> call = userInterface.likeUnlike(new AddLike(FirebaseAuth.getInstance().getCurrentUser().getUid(), postModel.getPostId(), postModel.getPostUserId(), 0));
                    call.enqueue(new Callback<Integer>() {
                        @Override
                        public void onResponse(Call<Integer> call, Response<Integer> response) {
                            holder.likeSection.setEnabled(true);
                            if(response.body()==null){
                                operationLike(holder,postModel);
                                Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Integer> call, Throwable t) {
                            holder.likeSection.setEnabled(true);
                            operationLike(holder,postModel);
                            Toast.makeText(context,"Something went wrong",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // post
        if (!postModel.getStatusImage().isEmpty()) {
            Picasso.with(context).load(ApiClient.BASE_URL_1+postModel.getStatusImage()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(holder.statusImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(ApiClient.BASE_URL_1+postModel.getStatusImage()).placeholder(R.drawable.default_image_placeholder).into(holder.statusImage);
                }
            });
        } else {
            holder.statusImage.setImageDrawable(null);
        }
        // date
        try {
            holder.date.setText(AgoDateParse.getTimeAgo(AgoDateParse.getTimeInMillsecond(postModel.getStatusTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // user
        if(!postModel.getProfileUrl().isEmpty()){
            Picasso.with(context).load(postModel.getProfileUrl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image_placeholder).into(holder.peopleImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(postModel.getProfileUrl()).placeholder(R.drawable.default_image_placeholder).into(holder.peopleImage);
                }
            });
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, FullPostActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("postModel", Parcels.wrap(postModel));
            intent.putExtra("postBundle", bundle);
            context.startActivity(intent);
        });

        holder.commentSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new CommentBottomSheet();
                Bundle bundle = new Bundle();
                bundle.putParcelable("postModel", Parcels.wrap(postModel));
                bottomSheetDialogFragment.setArguments(bundle);
                FragmentActivity fragmentActivity = (FragmentActivity) context;
                bottomSheetDialogFragment.show(fragmentActivity.getSupportFragmentManager(), "commentFragment");
            }
        });
    }

    private void operationLike(@NonNull ViewHolder holder, PostModel postModel) {
        holder.likeImg.setImageResource(R.drawable.icon_like_selected);
        int count = Integer.parseInt(postModel.getLikeCount());
        count++;
        if (count == 0 || count == 1) {
            holder.likeTxt.setText(count + " Like");
        } else {
            holder.likeTxt.setText(count + " Likes");
        }

        postModels.get(holder.getAdapterPosition()).setLikeCount(count + "");
        postModels.get(holder.getAdapterPosition()).setLiked(true);
    }

    private void operationUnlike(@NonNull ViewHolder holder, PostModel postModel) {


        holder.likeImg.setImageResource(R.drawable.icon_like);
        int count = Integer.parseInt(postModel.getLikeCount());
        count--;

        if (count == 0 || count == 1) {
            holder.likeTxt.setText(count + " Like");
        } else {
            holder.likeTxt.setText(count + " Likes");
        }
        postModels.get(holder.getAdapterPosition()).setLikeCount(count + "");
        postModels.get(holder.getAdapterPosition()).setLiked(false);


    }

    @Override
    public int getItemCount() {
        return postModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.people_image)
        ImageView peopleImage;
        @BindView(R.id.people_name)
        TextView peopleName;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.privacy_icon)
        ImageView privacyIcon;
        @BindView(R.id.memory_meta_rel)
        RelativeLayout memoryMetaRel;
        @BindView(R.id.post)
        TextView post;
        @BindView(R.id.status_image)
        ImageView statusImage;
        @BindView(R.id.like_img)
        ImageView likeImg;
        @BindView(R.id.like_txt)
        TextView likeTxt;
        @BindView(R.id.likeSection)
        LinearLayout likeSection;
        @BindView(R.id.comment_img)
        ImageView commentImg;
        @BindView(R.id.comment_txt)
        TextView commentTxt;
        @BindView(R.id.commentSection)
        LinearLayout commentSection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class AddLike {
        String userId, postId, contentOwnerId;
        int operationType;

        public AddLike(String userId, String postId, String contentOwnerId, int operationType) {
            this.userId = userId;
            this.postId = postId;
            this.contentOwnerId = contentOwnerId;
            this.operationType = operationType;
        }
    }
}
