package com.rent.scouto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private final static int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount signInAccount;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        AutoCompleteTextView emailid = (AutoCompleteTextView) v.findViewById(R.id.emailidtv);
        AutoCompleteTextView password = (AutoCompleteTextView) v.findViewById(R.id.passwordtv);
        MaterialButton btn = (MaterialButton) v.findViewById(R.id.login_btn);
        ImageView gbtn = (ImageView) v.findViewById(R.id.googlebtnx);

        mAuth = FirebaseAuth.getInstance();

        gbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInWithEmailAndPassword(emailid.getText().toString().trim(), password.getText().toString().trim())
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(requireActivity(), "Log-in successful",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent myIntent = new Intent(requireActivity(), MainActivity.class);
                                    requireActivity().startActivity(myIntent);

                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(requireActivity(), Objects.requireNonNull(task.getException()).getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });


        return v;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    signInAccount = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(requireActivity()));
                                    Intent myIntent = new Intent(requireActivity(), MainActivity.class);
                                    requireActivity().startActivity(myIntent);
                                    // Sign in success, update UI with the signed-in user's information


                                } else {

                                    Toast.makeText(requireActivity(), "Auth failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            } catch (ApiException e) {

                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {

                    case CommonStatusCodes.NETWORK_ERROR:
                        Toast.makeText(requireActivity(), "Check your internet", Toast.LENGTH_SHORT).show();
                        break;

                    case CommonStatusCodes.CANCELED:
                        Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                        break;

                    case CommonStatusCodes.INVALID_ACCOUNT:
                        Toast.makeText(requireActivity(), "Invalid Account", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }
    }


}