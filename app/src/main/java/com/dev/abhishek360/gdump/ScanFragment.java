package com.dev.abhishek360.gdump;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;


public class ScanFragment extends Fragment
{


    private EditText code;
    private Button submitcode;
    private SurfaceView qrScanSurfaceView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private String code_string;
    private String points_string;
    private int points_int = 0;
    private String database_key;

    //private Camera.P params;
    //private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;


    private String[] codeList = {"56", "78", "45", "13", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};

    private OnFragmentInteractionListener mListener;
    final int RequestCameraPermissionID=1001;

    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_scan, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        code = (EditText) v.findViewById(R.id.scan_uniqcode_edittext);
        submitcode = (Button) v.findViewById(R.id.scan_submit_button);
        submitcode.setEnabled(false);
        qrScanSurfaceView = (SurfaceView) v.findViewById(R.id.scan_qr_surfaceview);

        code_string = code.getText().toString();
        sharedPreferences = getActivity().getSharedPreferences("gdumpPref", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String uid = sharedPreferences.getString("UID", "error");
        points_int = sharedPreferences.getInt("POINTS", 0);

        barcodeDetector = new BarcodeDetector.Builder(getActivity())
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector).setAutoFocusEnabled(true)
                .setRequestedPreviewSize(540, 400).build();

        qrScanSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);



                    return;
                }

                try
                {
                    cameraSource.start(qrScanSurfaceView.getHolder());
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
            {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                cameraSource.stop();
            }
        });



        barcodeDetector.setProcessor(new Detector.Processor<Barcode>()
        {
            @Override
            public void release()
            {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
                final SparseArray<Barcode> qrCodes= detections.getDetectedItems();
                if (qrCodes.size()!=0)
                {
                    code.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Vibrator vibrator=(Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                            String cde =qrCodes.valueAt(0).displayValue;
                            validateCode(cde);
                            code.setText(cde);

                        }
                    });
                }
            }
        });

        databaseReference= firebaseDatabase.getReference("GDUMP").child("USERS").child(uid).child("G_POINTS");

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {

                    points_int=dataSnapshot.getValue(Integer.class);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        submitcode.setOnClickListener(new View.OnClickListener()
         {


             @Override

             public void onClick(View v)
             {
                 code_string= code.getText().toString();
                 for(int i=0;i<5;i++)
                 {
                     if(code_string.equals(codeList[i]))
                     {
                         points_int+=5;

                         databaseReference.setValue(points_int);

                         Toast.makeText(getActivity(),"Updated points: "+points_int,Toast.LENGTH_SHORT).show();
                         code.setText("");
                         code.requestFocus();

                         editor.putInt("POINTS", points_int);
                         editor.apply();

                         return;
                     }
                 }

                 Toast.makeText(getActivity(),"Enter Vailid Code!",Toast.LENGTH_SHORT).show();

             }
         });




        return v;
    }

    private void validateCode(String cde)
    {
        if(cde.length()==11)
        {
            if (cde.charAt(3)=='-'&&cde.charAt(7)=='-')
                submitcode.setEnabled(true);

        }
        else
        {
            HomeActivity.tosty("Not a Valid G-dump Code!",getActivity());
            submitcode.setEnabled(false);
        }


    }

    /*private void initCameraFocusListener()
    {
        qrScanSurfaceView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {

                cameraFocus(event, cameraSource, Camera.Parameters.FOCUS_MODE_AUTO);

                return false;
            }
        });

    }*/

   /* private boolean cameraFocus(MotionEvent event, @NonNull CameraSource cameraSource, @NonNull String focusMode)
    {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();


        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

        Rect touchRect = new Rect((int)(x - touchMajor / 2), (int)(y - touchMinor / 2), (int)(x + touchMajor / 2), (int)(y + touchMinor / 2));

        Rect focusArea = new Rect();

        focusArea.set(touchRect.left * 2000 / qrScanSurfaceView.getWidth() - 1000,
                touchRect.top * 2000 / qrScanSurfaceView.getHeight() - 1000,
                touchRect.right * 2000 / qrScanSurfaceView.getWidth() - 1000,
                touchRect.bottom * 2000 / qrScanSurfaceView.getHeight() - 1000);

        // Submit focus area to camera

        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        focusAreas.add(new Camera.Area(focusArea, 1000));

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        params = camera.getParameters();
                        params.setFocusMode(focusMode);
                        params.setFocusAreas(focusAreas);
                        camera.setParameters(params);

                        // Start the autofocus operation

                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean b, Camera camera) {
                                // currently set to auto-focus on single touch
                            }
                        });
                        return true;
                    }

                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }



        return false;
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
            {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case RequestCameraPermissionID:
            {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);

                    return;
                }

                try
                {
                    cameraSource.start(qrScanSurfaceView.getHolder());
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
