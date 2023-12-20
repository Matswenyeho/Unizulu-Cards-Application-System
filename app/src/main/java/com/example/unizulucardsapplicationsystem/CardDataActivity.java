package com.example.unizulucardsapplicationsystem;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class CardDataActivity extends AppCompatActivity {

    private static final int DISCOVERY_TIME = 120000;
    private static final String TAG = "CardDataActivity";

    private BluetoothAdapter bluetoothAdapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_data);

        context = this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        String userId = getIntent().getStringExtra("userId");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = firestore.collection("Applicant").document(userId);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String surname = documentSnapshot.getString("surname");
                String email = documentSnapshot.getString("email");
                String tittle = documentSnapshot.getString("tittle");
                String identity = documentSnapshot.getString("Identity Number");
                String initials = documentSnapshot.getString("initials");
                String studentno = documentSnapshot.getString("studentno");
                String passport = documentSnapshot.getString("Passport Number");
                String employeeno = documentSnapshot.getString("employeeno");
                String res = documentSnapshot.getString("res");
                String degreetype = documentSnapshot.getString("DegreeType");
                String isStudent = documentSnapshot.getString("isStudent");
                String isEmployee = documentSnapshot.getString("isEmployee");
                String year = documentSnapshot.getString("year");
                String base64Image = documentSnapshot.getString("profilePhoto");

                displayUserData(tittle, surname, email, identity, initials, studentno, passport,
                        employeeno, res, degreetype, isStudent, isEmployee, year, base64Image);
                handlePrintAndEmail(userId, tittle, surname, email, res, identity, employeeno,
                        studentno, initials, passport, degreetype, isStudent, isEmployee, year);
            }
        });
    }

    private void displayUserData(String tittle, String surname, String email, String identity,
                                 String initials, String studentno, String passport, String employeeno,
                                 String res, String degreetype, String isStudent, String isEmployee,
                                 String year, String base64Image) {
        TextView tittleTextView = findViewById(R.id.display_tittle);
        TextView surnameTextView = findViewById(R.id.display_surname);
        TextView identityTextView = findViewById(R.id.display_identity);
        TextView initialsTextView = findViewById(R.id.display_initials);
        TextView studentnoTextView = findViewById(R.id.display_studentno);
        TextView resTextView = findViewById(R.id.display_res);
        TextView degreeTypeTextView = findViewById(R.id.display_degreetype);
        TextView yearTextView = findViewById(R.id.display_year);
        ImageView imageView = findViewById(R.id.virtual_card_image);

        tittleTextView.setText(tittle);
        surnameTextView.setText(surname);

        if (identityTextView != null) {
            if ("Passport Number".equals(identity)) {
                identityTextView.setText("Passport Number: " + passport);
            } else {
                identityTextView.setText("Identity Number: " + identity);
            }
        }

        if (isStudent != null && isStudent.equals("isStudent")) {
            studentnoTextView.setText("Student No: " + studentno);
            yearTextView.setText("Student " + year);

            if ("Post Grad".equals(degreetype)) {
                degreeTypeTextView.setText(degreetype);
            }

            if (!"Off_campus".equals(res)) {
                resTextView.setText(res);
            }
        } else if (isEmployee != null && isEmployee.equals("isEmployee")) {
            studentnoTextView.setText("Employee No: " + employeeno);
            yearTextView.setText("Employee " + year);
        }

        if (base64Image != null) {
            byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void handlePrintAndEmail(String userId, String tittle, String surname, String email,
                                     String res, String identity, String employeeno, String studentno,
                                     String initials, String passport, String degreetype, String isStudent,
                                     String isEmployee, String year) {
        Button printButton = findViewById(R.id.printbtn);

        printButton.setOnClickListener(v -> {
            String cardContent = "Title: " + tittle +
                    "\nSurname: " + surname +
                    "\nEmail: " + email +
                    "\nRes: " + res +
                    "\nDegree Type:" + degreetype +
                    "\nInitials: " + initials +
                    "\nIs Student: " + isStudent +
                    "\nIs Employee: " + isEmployee +
                    "\nPassport Number: " + passport +
                    "\nIdentity Number: " + identity +
                    "\nStudent Number: " + studentno +
                    "\nEmployee Number: " + employeeno +
                    "\nYear: " + year;

            printCard(userId, cardContent);
            sendEmail(email);
        });
    }

    private void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Card Collection");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int workingDaysToAdd = 3;

        while (workingDaysToAdd > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                workingDaysToAdd--;
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(calendar.getTime());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your student card has been printed. Come and collect it " + date);

        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        }
    }

    private void printCard(String userId, String cardContent) {
        BluetoothDevice selectedPrinter = discoverAndSelectPrinter();
        if (isBluetoothPrinterConnected()) {
            byte[] printData = convertCardToPrintData(cardContent);
            sendPrintDataToPrinter(selectedPrinter, printData);
        } else {
            showPrinterNotConnectedMessage();
        }
    }

    private byte[] convertCardToPrintData(String cardContent) {
        return cardContent.getBytes();
    }

    private BluetoothDevice discoverAndSelectPrinter() {
        if (bluetoothAdapter == null) {
            return null;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            context.startActivity(enableBtIntent);
            return null;
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

        try {
            Thread.sleep(DISCOVERY_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Set<BluetoothDevice> discoveredDevices = bluetoothAdapter.getBondedDevices();
        return findPrinterDevice(discoveredDevices);
    }

    private BluetoothDevice findPrinterDevice(Set<BluetoothDevice> discoveredDevices) {
        if (discoveredDevices == null || discoveredDevices.isEmpty()) {
            return null;
        }
        for (BluetoothDevice device : discoveredDevices) {
            if (isDesiredPrinter(device)) {
                return device;
            }
        }

        return null;
    }

    private boolean isDesiredPrinter(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return device.getName() != null && device.getName().contains("PrinterName");
    }

    private void sendPrintDataToPrinter(BluetoothDevice printer, byte[] printData) {
        if (!isBluetoothPrinterConnected()) {
            Log.e(TAG, "Printer is not connected");
            return;
        }

        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            BluetoothSocket bluetoothSocket = printer.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            bluetoothSocket.connect();
            OutputStream outputStream = bluetoothSocket.getOutputStream();
            outputStream.write(printData);
            outputStream.flush();
            outputStream.close();
            bluetoothSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isBluetoothPrinterConnected() {
        return checkBluetoothConnection();
    }

    private boolean checkBluetoothConnection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices == null || pairedDevices.isEmpty()) {
            return false;
        }

        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals("Your Printer Name")) {
                return true;
            }
        }

        return false;
    }

    private void showPrinterNotConnectedMessage() {
        Toast.makeText(context, "Bluetooth printer is not connected. Please connect to a printer.", Toast.LENGTH_SHORT).show();
    }
}
