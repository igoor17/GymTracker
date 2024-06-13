package es.eduardo.gymtracker.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import es.eduardo.gymtracker.R;

/**
 * Fragment for sending email to support staff for assistance.
 */
public class HelpFragment extends Fragment {

    private EditText issueTitle;
    private EditText issueDescription;
    private Button sendButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        // Initialize UI elements
        issueTitle = view.findViewById(R.id.issue_title);
        issueDescription = view.findViewById(R.id.issue_description);
        sendButton = view.findViewById(R.id.send_button);

        // Set click listener for the send button to initiate sending email
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(issueTitle.getText().toString(), issueDescription.getText().toString());
            }
        });

        return view;
    }

    /**
     * Creates an intent to send an email with issue details to the support staff.
     *
     * @param issueTitle       Title of the issue.
     * @param issueDescription Description of the issue.
     */
    private void sendEmail(String issueTitle, String issueDescription) {
        // Email address of the support staff
        String staffEmail = "gymtracker.staff@gmail.com";

        // Create an email intent with necessary details
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{staffEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, issueTitle);
        emailIntent.putExtra(Intent.EXTRA_TEXT, issueDescription);

        // Start the email intent to open email client
        startActivity(emailIntent);
    }
}
