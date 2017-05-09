//package com.jgasteiz.comics_android.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.TextView;
//import com.jgasteiz.comics_android.activities.SeriesActivity;
//import com.jgasteiz.comics_android.models.Series;
//
//import java.util.ArrayList;
//
//public class SeriesListAdapter extends ArrayAdapter<Series> {
//
//    private SeriesActivity mContext;
//
//    public SeriesListAdapter(Context context, ArrayList<Series> seriesList) {
//        super(context, 0, seriesList);
//        mContext = (SeriesActivity) context;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get the data item for this position
//        final Series series = getItem(position);
//        String postIndex = Integer.toString(position + 1);
//
//        // Inflate the view if necessary.
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.series, parent, false);
//        }
//
//        // Get references to the text views.
//        TextView postIndexView = (TextView) convertView.findViewById(R.id.post_index);
//        TextView postTitleView = (TextView) convertView.findViewById(R.id.post_title);
//        TextView postDomainView = (TextView) convertView.findViewById(R.id.post_domain);
//        TextView postDetailsView = (TextView) convertView.findViewById(R.id.post_details);
//        Button postCommentsButton = (Button) convertView.findViewById(R.id.post_comments_button);
//
//        // Populate the data into the view.
//        if (post != null) {
//            // Set the post index and title
//            postIndexView.setText(postIndex);
//            postTitleView.setText(post.getTitle());
//
//            // Set the post domain, if it has any.
//            if (post.getDomain() != null) {
//                postDomainView.setText(post.getDomain());
//            } else {
//                postDomainView.setVisibility(View.GONE);
//            }
//
//            // Set the post details.
//            String postDetails = "";
//            if (post.getUser() != null) {
//                postDetails = String.format("%s points by %s ", post.getPoints(), post.getUser());
//            }
//            postDetails = String.format("%s%s", postDetails, post.getDisplayTime());
//            if (post.getCommentsCount() > 0) {
//                postDetails = String.format("%s - %s comments", postDetails, post.getCommentsCount());
//            }
//            postDetailsView.setText(postDetails);
//
//            // Finally, hide the comments button if there are no comments or
//            // set the callback when clicked.
//            if (post.getCommentsCount() == 0) {
//                postCommentsButton.setVisibility(View.GONE);
//            } else {
//                // TODO: think about making this nicer?
//                postCommentsButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mContext.navigateToPostComments(post);
//                    }
//                });
//            }
//        }
//
//        return convertView;
//    }
//}
