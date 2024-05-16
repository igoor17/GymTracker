package es.eduardo.gymtracker.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import es.eduardo.gymtracker.R;

public class HelpFragment extends Fragment {

    private EditText issueTitle;
    private EditText issueDescription;
    private Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        issueTitle = view.findViewById(R.id.issue_title);
        issueDescription = view.findViewById(R.id.issue_description);
        sendButton = view.findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(issueTitle.getText().toString(), issueDescription.getText().toString());
            }
        });

        return view;
    }

    private void sendEmail(String issueTitle, String issueDescription) {

        String staffEmail = "gymtracker.staff@gmail.com";

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, staffEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, issueTitle);
        emailIntent.putExtra(Intent.EXTRA_TEXT, issueDescription);

        startActivity(emailIntent);

    }
}