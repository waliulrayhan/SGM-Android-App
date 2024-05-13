package com.go.sgm_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.go.sgm_android.databinding.ActivityAddPowerPlantBinding;
import com.go.sgm_android.model.PowerPlant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddPowerPlantActivity extends AppCompatActivity {

    private ActivityAddPowerPlantBinding binding;
    private DatabaseReference mDatabase;
    private AutoCompleteTextView divisionAutoCompleteTextView, districtAutoCompleteTextView, upazillaAutoCompleteTextView;
    private AutoCompleteTextView autoCompleteTextViewOperator;
    private AutoCompleteTextView autoCompleteTextViewOwnership;
    private AutoCompleteTextView autoCompleteTextViewFuelType;
    private AutoCompleteTextView autoCompleteTextViewMethod;
    private ProgressDialog progressDialog;
    // Define variables to keep track of the previously selected division and district
    private String previousDivision = "";
    private String previousDistrict = "";

    private ArrayAdapter<String> divisionAdapter, districtAdapter, upazillaAdapter;

    // Define your divisions, districts, and upazillas arrays here
    private String[] divisions = {"Barisal", "Chattogram", "Dhaka", "Khulna", "Mymensingh", "Rajshahi", "Rangpur", "Sylhet"};


    private String[][] districts = {
            {"Barisal", "Barguna", "Bhola", "Jhalakathi", "Patuakhali", "Pirojpur"},
            {"Bandarban", "Brahmanbaria", "Chandpur", "Chattogram", "Cox's Bazar", "Cumilla", "Feni", "Khagrachhari", "Lakshmipur", "Noakhali", "Rangamati"},
            {"Dhaka", "Faridpur", "Gazipur", "Gopalganj", "Kishoreganj", "Madaripur", "Manikganj", "Munshiganj", "Narayanganj", "Narsingdi", "Rajbari", "Shariatpur", "Tangail"},
            {"Bagerhat", "Chuadanga", "Jashore", "Jhenaidah", "Khulna", "Kushtia", "Magura", "Meherpur", "Narail", "Satkhira"},
            {"Jamalpur", "Mymensingh", "Netrokona", "Sherpur"},
            {"Bogura", "Chapainawabganj", "Joypurhat", "Naogaon", "Natore", "Pabna", "Rajshahi", "Sirajganj"},
            {"Dinajpur", "Gaibandha", "Kurigram", "Lalmonirhat", "Nilphamari", "Panchagarh", "Rangpur", "Thakurgaon"},
            {"Habiganj", "Moulvibazar", "Sunamganj", "Sylhet"},
    };
    private String[][][] upazillas = {
            {
                    {"Agailjhara", "Babuganj", "Bakerganj", "Barisal Sadar", "Banaripara", "Gournadi", "Hizla", "Mehendiganj", "Muladi", "Wazirpur"},
                    {"Amtali", "Bamna", "Barguna Sadar", "Betagi", "Pathorghata", "Taltali"},
                    {"Bhola Sadar", "Borhan Sddin", "Charfesson", "Doulatkhan", "Lalmohan", "Monpura", "Tazumuddin"},
                    {"Jhalakathi Sadar", "Kathalia", "Nalchity", "Rajapur"},
                    {"Bauphal", "Dashmina", "Dumki", "Galachipa", "Kalapara", "Mirzaganj", "Patuakhali Sadar", "Rangabali"},
                    {"Bhandaria", "Kawkhali", "Mathbaria", "Nesarabad", "Nazirpur", "Pirojpur Sadar", "Zianagar"}
            },
            {
                    {"Alikadam", "Bandarban Sadar", "Lama", "Naikhongchhari", "Rowangchhari", "Ruma", "Thanchi"},
                    {"Akhaura", "Ashuganj", "Bancharampur", "Bijoynagar", "Brahmanbaria Sadar", "Kasba", "Nabinagar", "Nasirnagar", "Sarail"},
                    {"Chandpur Sadar", "Faridgonj", "Hajiganj", "Haimchar", "Kachua", "Matlab North", "Matlab South", "Shahrasti"},
                    {"Anwara", "Banshkhali", "Boalkhali", "Chandanaish", "Fatikchhari", "Hathazari", "Karnafuli", "Lohagara", "Mirsharai", "Patiya", "Rangunia", "Raozan", "Satkania", "Sitakunda", "Sandwip"},
                    {"Chakaria", "Cox's Bazar Sadar", "Eidgaon", "Kutubdia", "Moheshkhali", "Pekua", "Ramu", "Teknaf", "Ukhiya"},
                    {"Barura", "Brahmanpara", "Burichang", "Chandina", "Chauddagram", "Cumilla Sadar", "Daudkandi", "Debidwar", "Homna", "Laksam", "Lalmai", "Meghna", "Monohargonj", "Muradnagar", "Nangalkot", "Sadarsouth", "Titas"},
                    {"Chhagalnaiya", "Daganbhuiyan", "Feni Sadar", "Fulgazi", "Parshuram", "Sonagazi"},
                    {"Dighinala", "Guimara", "Khagrachhari Sadar", "Laxmichhari", "Manikchari", "Matiranga", "Mohalchari", "Panchari", "Ramgarh"},
                    {"Kamalnagar", "Lakshmipur Sadar", "Raipur", "Ramgati", "Ramganj"},
                    {"Begumganj", "Chatkhil", "Companiganj", "Hatia", "Kabirhat", "Noakhali Sadar", "Senbug", "Sonaimori", "Subarnachar"},
                    {"Baghaichari", "Barkal", "Belaichari", "Juraichari", "Kaptai", "Kawkhali", "Langadu", "Naniarchar", "Rajasthali", "Rangamati Sadar"}

            },
            {
                    {"Dhamrai", "Dohar", "Keraniganj", "Nawabganj", "Savar"},
                    {"Alfadanga", "Bhanga", "Boalmari", "Charbhadrasan", "Faridpur Sadar", "Madhukhali", "Nagarkanda", "Sadarpur", "Saltha"},
                    {"Gazipur Sadar", "Kaliganj", "Kaliakair", "Kapasia", "Sreepur"},
                    {"Gopalganj Sadar", "Kashiani", "Kotalipara", "Muksudpur", "Tungipara"},
                    {"Austagram", "Bajitpur", "Bhairab", "Hossainpur", "Itna", "Karimgonj", "Katiadi", "Kishoreganj Sadar", "Kuliarchar", "Mithamoin", "Nikli", "Pakundia", "Tarail"},
                    {"Dasar", "Kalkini", "Madaripur Sadar", "Rajoir", "Shibchar"},
                    {"Gajaria", "Louhajanj", "Munshiganj Sadar", "Sirajdikhan", "Sreenagar", "Tongibari"},
                    {"Doulatpur", "Gior", "Harirampur", "Manikganj Sadar", "Saturia", "Shibaloy", "Singiar"},
                    {"Araihazar", "Bandar", "Narayanganj Sadar", "Rupganj", "Sonargaon"},
                    {"Belabo", "Monohardi", "Narsingdi Sadar", "Palash", "Raipura", "Shibpur"},
                    {"Baliakandi", "Goalanda", "Kalukhali", "Pangsa", "Rajbari Sadar"},
                    {"Bhedarganj", "Damudya", "Gosairhat", "Naria", "Shariatpur Sadar", "Zajira"},
                    {"Basail", "Bhuapur", "Delduar", "Dhanbari", "Ghatail", "Gopalpur", "Kalihati", "Madhupur", "Mirzapur", "Nagarpur", "Sakhipur", "Tangail Sadar"}
            },
            {
                    {"Bagerhat Sadar", "Chitalmari", "Fakirhat", "Kachua", "Mollahat", "Mongla", "Morrelganj", "Rampal", "Sarankhola"},
                    {"Alamdanga", "Chuadanga Sadar", "Damurhuda", "Jibannagar"},
                    {"Abhaynagar", "Bagherpara", "Chougachha", "Jashore Sadar", "Jhikargacha", "Keshabpur", "Manirampur", "Sharsha"},
                    {"Harinakundu", "Jhenaidah Sadar", "Kaliganj", "Kotchandpur", "Moheshpur", "Shailkupa"},
                    {"Botiaghata", "Dakop", "Digholia", "Dumuria", "Fultola", "Koyra", "Paikgasa", "Rupsha", "Terokhada"},
                    {"Bheramara", "Daulatpur", "Khoksa", "Kumarkhali", "Kushtia Sadar", "Mirpur"},
                    {"Magura Sadar", "Mohammadpur", "Shalikha", "Sreepur"},
                    {"Gangni", "Meherpur Sadar", "Mujibnagar"},
                    {"Kalia", "Lohagara", "Narail Sadar"},
                    {"Assasuni", "Debhata", "Kalaroa", "Kaliganj", "Satkhira Sadar", "Shyamnagar", "Tala"}
            },
            {
                    {"Bokshiganj", "Dewangonj", "Islampur", "Jamalpur Sadar", "Madarganj", "Melandah", "Sarishabari"},
                    {"Bhaluka", "Dhobaura", "Fulbaria", "Gafargaon", "Gouripur", "Haluaghat", "Iswarganj", "Muktagacha", "Mymensingh Sadar", "Nandail", "Phulpur", "Tarakanda", "Trishal"},
                    {"Atpara", "Barhatta", "Durgapur", "Kalmakanda", "Kendua", "Khaliajuri", "Madan", "Mohongonj", "Netrokona Sadar", "Purbadhala"},
                    {"Jhenaigati", "Nalitabari", "Nokla", "Sherpur Sadar", "Sreebordi"}
            },
            {
                    {"Adamdighi", "Bogura Sadar", "Dupchanchia", "Gabtali", "Kahaloo", "Nondigram", "Shajahanpur", "Shariakandi", "Sherpur", "Shibganj", "Sonatala"},
                    {"Bholahat", "Chapainawabganj Sadar", "Gomostapur", "Nachol", "Shibganj"},
                    {"Akkelpur", "Joypurhat Sadar", "Kalai", "Khetlal", "Panchbibi"},
                    {"Atrai", "Badalgachi", "Dhamoirhat", "Manda", "Mohadevpur", "Naogaon Sadar", "Niamatpur", "Paikgachha", "Patnitala", "Porsha", "Raninagar", "Sapahar"},
                    {"Bagatipara", "Baraigram", "Gurudaspur", "Lalpur", "Naldanga", "Natore Sadar", "Singra"},
                    {"Atghoria", "Bera", "Bhangura", "Chatmohar", "Faridpur", "Ishurdi", "Pabna Sadar", "Santhia", "Sujanagar"},
                    {"Bagha", "Bagmara", "Charghat", "Durgapur", "Godagari", "Mohonpur", "Paba", "Puthia", "Tanore"},
                    {"Belkuchi", "Chauhali", "Kamarkhand", "Kazipur", "Raigonj", "Shahjadpur", "Sirajganj Sadar", "Tarash", "Ullapara"}
            },
            {
                    {"Birol", "Birampur", "Birganj", "Bochaganj", "Chirirbandar", "Dinajpur Sadar", "Fulbari", "Ghoraghat", "Hakimpur", "Kaharol", "Khansama", "Nawabganj", "Parbatipur"},
                    {"Gaibandha Sadar", "Gobindaganj", "Palashbari", "Phulchari", "Sadullapur", "Saghata", "Sundarganj"},
                    {"Bhurungamari", "Charrajibpur", "Chilmari", "Kurigram Sadar", "Nageshwari", "Phulbari", "Rajarhat", "Rowmari", "Ulipur"},
                    {"Aditmari", "Hatibandha", "Kaliganj", "Lalmonirhat Sadar", "Patgram"},
                    {"Dimla", "Domar", "Jaldhaka", "Kishorganj", "Nilphamari Sadar", "Syedpur"},
                    {"Atwari", "Boda", "Debiganj", "Panchagarh Sadar", "Tetulia"},
                    {"Badargonj", "Gangachara", "Kaunia", "Mithapukur", "Pirgacha", "Pirgonj", "Rangpur Sadar", "Taragonj"},
                    {"Baliadangi", "Haripur", "Pirganj", "Ranisankail", "Thakurgaon Sadar"}
            },
            {
                    {"Ajmiriganj", "Bahubal", "Baniachong", "Chunarughat", "Habiganj Sadar", "Lakhai", "Madhabpur", "Nabiganj"},
                    {"Barlekha", "Juri", "Kamolganj", "Kulaura", "Moulvibazar Sadar", "Rajnagar", "Sreemangal"},
                    {"Bishwambarpur", "Chhatak", "Derai", "Dharmapasha", "Dowarabazar", "Jamalganj", "Jagannathpur", "Madhyanagar", "Shalla", "South Sunamganj", "Sunamganj Sadar", "Tahirpur"},
                    {"Balaganj", "Beanibazar", "Bishwanath", "Companiganj", "Dakshinsurma", "Fenchuganj", "Golapganj", "Gowainghat", "Jaintiapur", "Kanaighat", "Osmaninagar", "Sylhet Sadar", "Zakiganj"}
            }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPowerPlantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Change notification color
        getWindow().setStatusBarColor(getResources().getColor(R.color.glitter_lake));

        // Set the title of the activity
        setTitle("Add Power Plant");

        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Power Plant...");
        progressDialog.setCancelable(false);

        // Initialize AutoCompleteTextViews
        divisionAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewDivision);
        districtAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewDistrict);
        upazillaAutoCompleteTextView = findViewById(R.id.autoCompleteTextViewUpazilla);
        autoCompleteTextViewOperator = findViewById(R.id.autoCompleteTextViewOperator);
        autoCompleteTextViewOwnership = findViewById(R.id.autoCompleteTextViewOwnership);
        autoCompleteTextViewFuelType = findViewById(R.id.autoCompleteTextViewFuelType);
        autoCompleteTextViewMethod = findViewById(R.id.autoCompleteTextViewMethod);

        // Initialize Adapters
        divisionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, divisions);
        divisionAutoCompleteTextView.setAdapter(divisionAdapter);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        districtAutoCompleteTextView.setAdapter(districtAdapter);

        upazillaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        upazillaAutoCompleteTextView.setAdapter(upazillaAdapter);

        // Set listeners for AutoCompleteTextViews
        divisionAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                updateDistrictAutoCompleteTextView(position);
                // Clear the text of the district AutoCompleteTextView when division changes
                districtAutoCompleteTextView.setText("");
                // Clear the text of the upazilla AutoCompleteTextView when division changes
                upazillaAutoCompleteTextView.setText("");
                // Clear the upazilla adapter when the division changes
                upazillaAdapter.clear();
                upazillaAdapter.notifyDataSetChanged();
            }
        });

        districtAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                int divisionPosition = divisionAdapter.getPosition(divisionAutoCompleteTextView.getText().toString());
                updateUpazillaAutoCompleteTextView(divisionPosition, position);
                // Clear the text of the upazilla AutoCompleteTextView when district changes
                upazillaAutoCompleteTextView.setText("");
            }
        });

        ArrayAdapter<String> operatorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.operators_array));
        autoCompleteTextViewOperator.setAdapter(operatorAdapter);

        ArrayAdapter<String> ownershipAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.ownership_array));
        autoCompleteTextViewOwnership.setAdapter(ownershipAdapter);

        ArrayAdapter<String> fuelTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.fuel_types_array));
        autoCompleteTextViewFuelType.setAdapter(fuelTypeAdapter);

        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.methods_array));
        autoCompleteTextViewMethod.setAdapter(methodAdapter);

        // Handle button click
        binding.addPowerPlantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String division = divisionAutoCompleteTextView.getText().toString().trim();
                String district = districtAutoCompleteTextView.getText().toString().trim();
                String upazilla = upazillaAutoCompleteTextView.getText().toString().trim();
                String operator = autoCompleteTextViewOperator.getText().toString().trim();
                String ownership = autoCompleteTextViewOwnership.getText().toString().trim();
                String fuelType = autoCompleteTextViewFuelType.getText().toString().trim();
                String method = autoCompleteTextViewMethod.getText().toString().trim();
                String output = binding.outputEditText.getText().toString().trim();
                String name = binding.addPowerPlantEditText.getText().toString().trim();

                if (!division.isEmpty() && !district.isEmpty() && !upazilla.isEmpty() && !operator.isEmpty() && !ownership.isEmpty() && !fuelType.isEmpty() && !method.isEmpty() && !output.isEmpty() && !name.isEmpty()) {
//                    Toast.makeText(AddPowerPlantActivity.this, "Hello "+division+district+upazilla+operator+ownership+fuelType+method+output+name, Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                    addPowerPlantToDatabase(division, district, upazilla, operator, ownership, fuelType, method, output, name);
                    uploadPPDataToFirebase();
                } else {
                    Toast.makeText(AddPowerPlantActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPowerPlantToDatabase(String division, String district, String upazilla, String operator,
                                         String ownership, String fuelType, String method, String output, String name) {

        // Reference to "SGM/PowerPlant"
        DatabaseReference powerPlantRef = mDatabase.child("SGM").child("PowerPlant").push();

        // Create a map to store the power plant data
        PowerPlant powerPlant = new PowerPlant(division, district, upazilla, operator, ownership, fuelType, method, output, name);

        // Run a transaction to push the power plant data to the database
        powerPlantRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Set the power plant data
                mutableData.setValue(powerPlant);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError == null) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(AddPowerPlantActivity.this, MainActivity.class);
                    // Start MainActivity
                    startActivity(intent);
                    Toast.makeText(AddPowerPlantActivity.this, "Power plant added successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields after successful addition
                    binding.addPowerPlantEditText.setText("");
                    binding.outputEditText.setText("");
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddPowerPlantActivity.this, "Failed to add power plant: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void uploadPPDataToFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("SGM");

        // Get the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Upload PowerPlant data
        DatabaseReference powerPlantRef = databaseRef.child("PowerPlant");

        powerPlantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String powerPlantKey = snapshot.getKey();
                    DatabaseReference powerPlantDateRef = snapshot.child("Date").child(currentDate).getRef();
                    powerPlantDateRef.child("capacity").child("ppcurrentCapacity").setValue(0);
                    powerPlantDateRef.child("capacity").child("pptargetCapacity").setValue(0);
                    powerPlantDateRef.child("total").child("pptotalCurrentCapacity").setValue(0);
                    powerPlantDateRef.child("alert").setValue("false");
                    powerPlantDateRef.child("history").child("pptotalCurrentCapacity").setValue(0);
                    powerPlantDateRef.child("history").child("last_update_time").setValue("11.59.59 PM");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UploadDataToFirebase", "Failed to upload power plant data: " + databaseError.getMessage());
            }
        });
    }

    private void updateDistrictAutoCompleteTextView(int divisionPosition) {
        // Update districts AutoCompleteTextView based on selected division
        String[] selectedDistricts = districts[divisionPosition];
        districtAdapter.clear();
        districtAdapter.addAll(selectedDistricts);
        districtAdapter.notifyDataSetChanged();
    }

    private void updateUpazillaAutoCompleteTextView(int divisionPosition, int districtPosition) {
        // Update upazillas AutoCompleteTextView based on selected district and division
        String[] selectedUpazillas = upazillas[divisionPosition][districtPosition];
        upazillaAdapter.clear();
        upazillaAdapter.addAll(selectedUpazillas);
        upazillaAdapter.notifyDataSetChanged();
    }
}
