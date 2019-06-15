package com.dev.abhishek360.gdump;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.session.MediaSession;
import android.net.Uri;
import android.print.PrinterId;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity
{

    private static final int CHOOSE_IMAGE_CODE = 101 ;
    private String name,email,address,password,confpass,phone_string,pin_string,otp_string;
    private int phone,pincode,otp;
    private FloatingActionButton floatingActionButton;
    private CircleImageView circleImageView;
    private EditText fullName_edittext,email_editText,phone_edittext,address_edittext,pincode_edittext,otp_edittext;
    private EditText password_edittext,conf_pass_edittext;
    private Button register_buttton,sendotp_button;
    private ProgressBar progressBar,imageProgressBar;
    private FirebaseAuth mAuth;
    private Uri uriProfileImage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    //private DatabaseReference databaseReference;
    private String imagePath=null;
    private String  profilePicDownloadUrl="No Profile Pic1";
    private Bitmap bitmap;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences("gdumpPref",MODE_PRIVATE);

        mAuth= FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseDatabase= FirebaseDatabase.getInstance();

        name = getIntent().getExtras().getString("name");
        email = getIntent().getExtras().getString("email");
        String imgUrl = getIntent().getExtras().getString("Url");

        progressBar= (ProgressBar)findViewById(R.id.reg_progressbar);
        fullName_edittext=(EditText)findViewById(R.id.reg_fullName_edittext);
        email_editText=(EditText)findViewById(R.id.reg_email_edittext);
        phone_edittext=(EditText)findViewById(R.id.reg_phone_edittext);
        address_edittext=(EditText)findViewById(R.id.reg_address_edittext);
        pincode_edittext=(EditText)findViewById(R.id.reg_pin_edittext);
        password_edittext=(EditText)findViewById(R.id.reg_password_edittext);
        otp_edittext=(EditText)findViewById(R.id.reg_otp_edittext);
        conf_pass_edittext=(EditText)findViewById(R.id.reg_confpass_edittext);
        register_buttton=(Button) findViewById(R.id.reg_submit_button);
        sendotp_button=(Button) findViewById(R.id.reg_sendotp_button);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.reg_SearchPic_fabbutton);
        circleImageView=(CircleImageView)findViewById(R.id.reg_ProfilePic_cirlceimageview);
        imageProgressBar=(ProgressBar)findViewById(R.id.reg_image_progressbar);



        if(!name.equals("user"))
        {

                fullName_edittext.setText(name);
                email_editText.setText(email);
        }

        sendotp_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateOtp();
            }
        });

        register_buttton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateDetails();
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               showPhotoChooser();
            }
        });

    }

    private void validateOtp()
    {
        phone_string=phone_edittext.getText().toString();
        if(phone_string.isEmpty())
        {
            phone_edittext.setError("Phone No. is Required");
            phone_edittext.requestFocus();
            return;
        }
        else if(!Patterns.PHONE.matcher(phone_string).matches()||phone_string.length()!=10)
        {
            phone_edittext.setError("Please,Enter a vaild  Phone No. without +91");
            phone_edittext.requestFocus();
            return;
        }
        else
        {
            //phone=Integer.parseInt(phone_string);
        }

        myToast("OTP sent to +91 "+phone_string);





    }

    private void validateDetails()
    {
        name=fullName_edittext.getText().toString();
        email=email_editText.getText().toString();
        address=address_edittext.getText().toString();
        pin_string=pincode_edittext.getText().toString();
        phone_string=phone_edittext.getText().toString();
        otp_string=otp_edittext.getText().toString();
        password=password_edittext.getText().toString();
        confpass=conf_pass_edittext.getText().toString();

        if(name.isEmpty())
        {
            fullName_edittext.setError("Please,Enter Full Name!");
            fullName_edittext.requestFocus();
            return;
        }
        else if(email.isEmpty())
        {
            email_editText.setError("Email is Required");
            email_editText.requestFocus();
            return;
        }
        else if(address.isEmpty())
        {
            address_edittext.setError("Address is Required to check G-dump Available Nearby!");
            address_edittext.requestFocus();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            email_editText.setError("Enter a valid Email id!");
            email_editText.requestFocus();
            return;
        }


        if(pin_string.isEmpty())
        {
            pincode_edittext.setError("PIN code is Required");
            pincode_edittext.requestFocus();
            return;
        }
        else
        {
            pincode=Integer.parseInt(pin_string);
        }

        if(phone_string.isEmpty())
        {
            phone_edittext.setError("Phone No. is Required");
            phone_edittext.requestFocus();
            return;
        }
        else if(!Patterns.PHONE.matcher(phone_string).matches()||phone_string.length()!=10)
        {
            phone_edittext.setError("Please,Enter a vaild  Phone No. without +91");
            phone_edittext.requestFocus();
            return;
        }
        else
        {
            //phone=Integer.parseInt(phone_string);
        }

        if(otp_string.isEmpty())
        {
            otp_edittext.setError("OTP is Required");
            otp_edittext.requestFocus();
            return;
        }
        else if(otp_string.length()<6)
        {
            otp_edittext.setError("Min. OTP length is 6");
            otp_edittext.requestFocus();
            return;
        }
        else
        {
            otp=Integer.parseInt(otp_string);

        }

        if(password.isEmpty())
        {
            password_edittext.setError("Password is Required");
            password_edittext.requestFocus();
            return;
        }
        else if(password.length()<6)
        {
            password_edittext.setError("Min. password length is 6");
            password_edittext.requestFocus();
            return;
        }

        if(confpass.isEmpty())
        {
            conf_pass_edittext.setError("Password not matched");
            conf_pass_edittext.requestFocus();
            return;
        }
        else if(!password.equals(confpass))
        {
            conf_pass_edittext.setError("Password not matched");
            conf_pass_edittext.requestFocus();
            return;
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            //imageProgressBar.setVisibility(View.VISIBLE);
            registerUser();
        }



        return;

    }

    private void registerUser()
    {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful())
                {
                    Log.d("Register_page","Data sent to server succesfully");
                    myToast("Registered Successfully");

                    FirebaseUser user= mAuth.getCurrentUser();
                    String token= user.getUid();

                    if(uriProfileImage!=null)
                    {
                        imageProgressBar.setVisibility(View.VISIBLE);
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,30,bytes);
                       // String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,uriProfileImage.getPath(),null);
                        //uriProfileImage=Uri.parse(path);


                        StorageReference profilePicRef = storageReference.child("profilePic/"+token+".jpg");

                        profilePicRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                        {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                imageProgressBar.setVisibility(View.GONE);
                                profilePicDownloadUrl= taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();


                            }
                        }).addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                imageProgressBar.setVisibility(View.GONE);

                                Log.d("Profile Pic:","Profile pic not sent to server!"+e.toString());
                                myToast("Profile pic upload failed!"+e.toString());

                            }
                        });
                    }


                    DatabaseReference childRef= firebaseDatabase.getReference("GDUMP").child("USERS").child(token);


                    childRef.child("EMAIL").setValue(email);
                    childRef.child("ADDRESS").setValue(address);
                    childRef.child("PIN_CODE").setValue(pin_string);
                    childRef.child("PHONE").setValue(phone_string);

                    childRef.child("G_POINTS").setValue(0);


                    if(user!=null)
                    {
                        UserProfileChangeRequest changeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).setPhotoUri(Uri.parse(profilePicDownloadUrl))
                                .build();

                        user.updateProfile(changeRequest).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Log.d("Profile Pic","Url saved to firebase Auth!");

                                }
                                else Log.d("Profile Pic: ERROR :","Url not saved");
                            }
                        });
                    }
                    user=mAuth.getCurrentUser();
                    //name=user.getDisplayName();
                    email=user.getEmail();
                    Uri imgURL=user.getPhotoUrl();


                    spEditor= sharedPreferences.edit();
                    spEditor.putString("FULL_NAME",name);
                    spEditor.putString("EMAIL",email);
                    spEditor.putString("IMG_URL",""+imgURL);
                    spEditor.putString("UID", token);
                    spEditor.putBoolean("IS_USER_LOGGED_IN",true);
                    spEditor.apply();

                    Intent in = new Intent(RegisterActivity.this,HomeActivity.class);
                    startActivity(in);
                    finish();
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                      myToast("Email Id already registered.Please,try Login");
                    }
                    else
                        {

                        Log.w("Register_Page", "Registration Failed");
                        myToast("Registration failed.Please,Try again." + task.getException().getMessage());
                    }
                }
            }




        });


    }

    public void myToast(String msg)
    {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==CHOOSE_IMAGE_CODE&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
        {
            uriProfileImage= data.getData();
            //imagePath=uriProfileImage.getPath();



            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                int wh =(int)(bitmap.getHeight()*(512.0/bitmap.getWidth()));
                Bitmap downScaled = Bitmap.createScaledBitmap(bitmap,512,wh,true);
                circleImageView.setImageBitmap(downScaled);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Log.d("profileImage",""+e.getMessage());
            }

        }


    }

    private void showPhotoChooser()
    {
        Intent in= new Intent();
        in.setType("image/*");
        in.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(in,"Select Profile Image"),CHOOSE_IMAGE_CODE);

    }

}
