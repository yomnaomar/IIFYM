package com.karimchehab.IIFYM_Tracker.Activities.Misc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.karimchehab.IIFYM_Tracker.R;

public class activityAbout extends AppCompatActivity implements View.OnClickListener {

    TextView    lblEmailLink, lblPortfolioLink, lblGithubLink, lblPrivacyPolicyLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        lblEmailLink = (TextView) findViewById(R.id.lblEmailLink);
        lblPortfolioLink = (TextView) findViewById(R.id.lblPortfolioLink);
        lblGithubLink = (TextView) findViewById(R.id.lblGithubLink);
        lblPrivacyPolicyLink = (TextView) findViewById(R.id.lblPrivacyPolicyLink);

        lblEmailLink.setOnClickListener(this);
        lblPortfolioLink.setOnClickListener(this);
        lblGithubLink.setOnClickListener(this);
        lblPrivacyPolicyLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lblPortfolioLink:
                Intent portfolioIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://karim-chehab.weebly.com"));
                startActivity(portfolioIntent);
                break;
            case R.id.lblGithubLink:
                Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Karim94/IIFYM"));
                startActivity(githubIntent);
                break;
            case R.id.lblPrivacyPolicyLink:
                Intent privacypolicyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://karim-chehab.weebly.com/iifym-privacy-policy.html"));
                startActivity(privacypolicyIntent);
                break;
        }
    }
}
