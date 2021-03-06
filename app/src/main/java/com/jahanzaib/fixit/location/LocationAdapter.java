package com.jahanzaib.fixit.location;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jahanzaib.fixit.R;

import java.util.ArrayList;

/**
 * Created by Jahanzaib on 1/2/17.
 */

public class LocationAdapter extends ArrayAdapter<LocationDetail> {

    Intent intent;

    SharedPreferences sharedPreferences;

    public LocationAdapter(Context context, ArrayList<LocationDetail> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        final LocationDetail homeDetail = getItem(position);

        View root = convertView;

        if (root == null)
            root = LayoutInflater.from(getContext()).inflate(R.layout.items_wall, parent, false);

        ImageView share = (ImageView) root.findViewById(R.id.share_imageView);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "This is the complain which we need to fixit.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "FIX IT");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        ImageView call = (ImageView) root.findViewById(R.id.call_imageView);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Call", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_DIAL);

                if (homeDetail.getRelatedTo().contains("KMC")) {
                    intent.setData(Uri.parse("tel:" + "+123"));
                }

                if (homeDetail.getRelatedTo().contains("K-Electric")) {
                    intent.setData(Uri.parse("tel:" + "+234"));
                }

                if (homeDetail.getRelatedTo().contains("KDA")) {
                    intent.setData(Uri.parse("tel:" + "+345"));
                }

                if (homeDetail.getRelatedTo().contains("Traffic")) {
                    intent.setData(Uri.parse("tel:" + "+456"));
                }

                if (homeDetail.getRelatedTo().contains("SSGC")) {
                    intent.setData(Uri.parse("tel:" + "+567"));
                }

                if (homeDetail.getRelatedTo().contains("KWSB")) {
                    intent.setData(Uri.parse("tel:" + "+678"));
                }

                try {
                    if (intent.resolveActivity(parent.getContext().getPackageManager()) != null)
                        parent.getContext().startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final String[] To = {""};
        final String[] CC = {""};

        final String allEmail = "customer.care@ke.com.pk \nfor SSGC \nutility.bills@sbp.org.pk \nfor IGP complain \nigpcomplainbranchcpo@gmail.com \nfor FIR karachi \nfir@karachipolice@gov.pk \for Traffic issuse\ntraffic.sind@sindhpolice.gov.pk \n ";

        final ImageView email = (ImageView) root.findViewById(R.id.mail_imageview);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("text/plain");

                if (homeDetail.getRelatedTo().contains("KMC")) {
                    i.setData(Uri.parse("mailto: deputymyor@kmc.gov.pk"));
                }

                if (homeDetail.getRelatedTo().contains("K-Electric")) {
                    i.setData(Uri.parse("mailto: customer.care@ke.com.pk"));

                }

                if (homeDetail.getRelatedTo().contains("KDA")) {
                    i.setData(Uri.parse("mailto: kda@gmail.com"));
                }

                if (homeDetail.getRelatedTo().contains("Traffic")) {
                    i.setData(Uri.parse("mailto: fir@karachipolice.com.pk"));

                }

                if (homeDetail.getRelatedTo().contains("SSGC")) {
                    i.setData(Uri.parse("mailto: info@ssgc.com.pk"));
                }

                if (homeDetail.getRelatedTo().contains("KWSB")) {
                    i.setData(Uri.parse("mailto: e-complainet@kwsb.gos.pk"));

                }

                i.putExtra(Intent.EXTRA_SUBJECT, "From Fixit app");
                i.putExtra(Intent.EXTRA_TEXT, "Please Fix this Issue" + allEmail);

                if (i.resolveActivity(getContext().getPackageManager()) != null) {
                    getContext().startActivity(i);
                } else {
                    Toast.makeText(getContext(), "You don't have any email app login", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView userName = (TextView) root.findViewById(R.id.name_tv);
        TextView relatedTo = (TextView) root.findViewById(R.id.related_To_tv);
        TextView place = (TextView) root.findViewById(R.id.place_tv);
        TextView description = (TextView) root.findViewById(R.id.desc_tv);
        ImageView userImage = (ImageView) root.findViewById(R.id.image_iv);
        TextView solved = (TextView) root.findViewById(R.id.solved);


        userName.setText(homeDetail.getUsername());
        relatedTo.setText(homeDetail.getRelatedTo());
        place.setText(homeDetail.getLocation());
        description.setText(homeDetail.getDes());
        solved.setText("" + homeDetail.getSolved());

        Glide.with(getContext()).load(homeDetail.getImage())
                .thumbnail(0.5f)
                .crossFade()
                .centerCrop()
                .error(android.R.drawable.stat_notify_error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(userImage);

        return root;
    }
}
