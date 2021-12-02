/*
* Copyright (c) 2021 Neoark
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

/*
- @package Share & Scan QR Code
- @author Development Team @Neoark Software Pvt Ltd
- @license http://opensource.org/licenses/MIT
- @questions: http://www.support.gowithexperts.com
- @link http://www.neoarks.com
- @since Version 1.0.0
- @filesource
*/


package neoarks.smpl.code.fragment.v2;	//Package name
//Imported other supporting packages - START
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider; 

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import neoarks.smpl.code.R;
import neoarks.smpl.code.activity.V2.WelcomeActivity;
import neoarks.smpl.code.utill.Preferences;
import neoarks.smpl.code.webservice.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
//Imported other supporting packages - END

/* @class: QRFragment
 * @description: This class is used for Generating, Scaning and Sharing QRcode through fragment
 * @layout: qr_code is an xml layout
 * @author Development Team @Neoark Software Pvt Ltd
 */
public class QRFragment extends Fragment {
    View view;
    private ImageView qrCode;
    private TextView id;
    private ImageView pass;
    private TextView canPay;
    private RelativeLayout main;
    private Button shareQR;
    private RelativeLayout rl_id;
    private Button user_id_show;
    private Bitmap qr_code_bitmap;
    private boolean aBoolean=true;
    private boolean flageye=true;
    private static String name="";
    private static String number="";
    private static String id1="";
    private static final int PERMISSION_REQUEST_CODE = 200;


    /* @class: onCreateView()
     * @description: Render xml layout
     * @layout: Layout IDs will be setup for calling API/endpoint
     * @author Development Team @Neoark Software Pvt Ltd
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) { view = inflater.inflate(R.layout.qr_code, container, false); }
        initView();
        initData();
        return view;
    }

    /* @class: initData()
     * @description: Auto initiated function
     * @layout: Get integrated API response
     * @author Development Team @Neoark Software Pvt Ltd
     */
    private void initData() {
        ((TextView) getActivity().findViewById(R.id.title)).setText("My QR");
        neoarks.smpl.code.utill.ProjectUtill.splitgnum(Preferences.getInstance(getActivity()).getQr(),id);
        String text = Preferences.getInstance(getActivity()).getQr(); // Whatever you need to encode in the QR code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            if(!text.equals("")) { //Check for blank or null value
                BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 1000, 1000);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                qr_code_bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qrCode.setImageBitmap(qr_code_bitmap);
            }
            else { Toast.makeText(getContext(), "Missing something in API response", Toast.LENGTH_SHORT); }
        } catch (WriterException e) { e.printStackTrace(); }

       /* @class: setOnClickListener()
        * @description: Override setOnClickListener function for sharing QRcode
        * @layout: Used for integrated API response
        * @author Development Team @Neoark Software Pvt Ltd
        */
        shareQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) { requestPermissionAndContinue(); }
                else {
                    if (checkPermission()) { sharebitmapqr(); }
                    else { sharebitmapqr(); }
                }
            }
        });

        /* @class: setOnClickListener()
        * @description: Override setOnClickListener function for transfering funds
        * @layout: Used for integrated API response
        * @author Development Team @Neoark Software Pvt Ltd
        */
        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to transfer the fund.")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                neoarks.smpl.code.projectutill.ProjectUtill.manager.INSTANCE.replaceFrgment((AppCompatActivity)getActivity(),new MoneyTransferFragment());
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { return; }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

       /* @class: onClick()
        * @description: Override onClick function for setting stringBuffer for API call
        * @layout: Used for integrated API response
        * @author Development Team @Neoark Software Pvt Ltd
        */
        user_id_show.setVisibility(View.VISIBLE);
        pass.setImageDrawable(QRFragment.this.getResources().getDrawable(R.drawable.hide_pass_word_temp));
        user_id_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aBoolean){
                    String gwe_num=Preferences.getInstance(getContext()).getQr();
                    StringBuffer stringBuffer =new StringBuffer();
                    for(int i=0;i<gwe_num.length();i++){
                        stringBuffer.append(gwe_num.charAt(i));
                        if(stringBuffer.length()==4){ stringBuffer.append("-"); }
                        if(stringBuffer.length()==9){ stringBuffer.append("-"); }
                        if(stringBuffer.length()==14){ stringBuffer.append("-"); }
                        if(stringBuffer.length()==19){ stringBuffer.append("-"); }
                    }
                    if(stringBuffer.length() > 0 ) { stringBuffer.deleteCharAt(stringBuffer.length() - 1); }
                    user_id_show.setText(stringBuffer);
                    aBoolean=false;
                    pass.setImageDrawable(QRFragment.this.getResources().getDrawable(R.drawable.show_pass_word_one));
                }
                else {
                    user_id_show.setText("SHOW MY GWE ID");
                    pass.setImageDrawable(QRFragment.this.getResources().getDrawable(R.drawable.hide_pass_word_temp));
                    aBoolean = true;
                }
            }
        });
    } //initData - closed

    /* @class: initView()
     * @description: Setting up layout IDs of the rendered layout
     * @params: Values of setup lyout IDs will need to pass with the APIs
     * @author Development Team @Neoark Software Pvt Ltd
     */
    private void initView() {
        qrCode = view.findViewById(R.id.qr_code);
        pass = view.findViewById(R.id.pass);
        id = view. findViewById(R.id.id);
        canPay = view. findViewById(R.id.can_pay);
        main = view. findViewById(R.id.main);
        shareQR = view.findViewById(R.id.share_qr);
        rl_id = view.findViewById(R.id.rl_id);
        user_id_show = view.findViewById(R.id.user_id_show);
    }

    /* @class: sharebitmapqr()
     * @description: Share QRcode for scanning and getting payment
     * @params: 
     * @author Development Team @Neoark Software Pvt Ltd
     */
    private void sharebitmapqr() {
        Bitmap icon = qr_code_bitmap;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File gweQRFile = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.GWE_STORAGE + File.separator + Constants.GWE_QRCODE_FILE);
        File gweStorage = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.GWE_STORAGE);
        try {
            if(gweStorage.exists() && gweStorage.isDirectory()) {
                gweQRFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(gweQRFile);
                fo.write(bytes.toByteArray());        //write QR code pattern on QRcode_file.jpg
            }
            else {
                gweStorage.mkdirs();
                gweQRFile.createNewFile();
                FileOutputStream fo = new FileOutputStream(gweQRFile);
                fo.write(bytes.toByteArray());        //write QR code pattern on QRcode_file.jpg
            }
        }
        catch (IOException e) { e.printStackTrace(); }
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", gweQRFile);
        try {
            share.putExtra(Intent.EXTRA_STREAM, uri.parse(Environment.getExternalStorageDirectory() + File.separator + Constants.GWE_STORAGE + File.separator + Constants.GWE_QRCODE_FILE));
            share.putExtra(Intent.EXTRA_TEXT, "Gowithexpert QR code is helpful for receiving and transferring payments into Gowithexperts Wallets");
            startActivity(Intent.createChooser(share, "Share Image"));
        }
        catch (Exception e) { e.getMessage(); }
    }

    /* @class: checkPermission()
     * @description: Check required permissions
     * @params: 
     * @author Development Team @Neoark Software Pvt Ltd
     */
    private boolean checkPermission() {
        boolean permission =  ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        return permission;
    }

    /* @class: requestPermissionAndContinue()
     * @description: Request for required permission for using QRcode
     * @params: QRcode functionality will not work if permissions are denied by the user
     * @author Development Team @Neoark Software Pvt Ltd
     */
    private void requestPermissionAndContinue() {
        if(ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setCancelable(true);
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(new WelcomeActivity(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            }
            else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                sharebitmapqr();
            }
        }
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            sharebitmapqr();
        }
    }

    /* @class: onRequestPermissionsResult()
     * @description: Action based on required permission for using QRcode
     * @params: QRcode functionality will not work if permissions are denied by the user
     * @author Development Team @Neoark Software Pvt Ltd
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {
                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { flag = false; }
                }
                if (flag) { sharebitmapqr(); }
                else { getActivity().finish(); }
            }
            else { getActivity().finish(); }
        }
        else { super.onRequestPermissionsResult(requestCode, permissions, grantResults); }
    }
} //Class - closed
