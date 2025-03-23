package th.in.ffc.person;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.model.PersonInfo;

public class SearchPersonActivity extends AppCompatActivity {
    private TextInputEditText edtIdcard, edtFirstName, edtLastName;
    private RecyclerView recyclerView;
    private PersonAdapter adapter;
    private List<PersonInfo> personList;
    private SfPersonInfoDao personInfoDao;
    private List<PersonInfo> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_person);

        // Initialize views
        edtIdcard = findViewById(R.id.edtIdcard);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        recyclerView = findViewById(R.id.recyclerViewResults);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        personList = new ArrayList<>();
        adapter = new PersonAdapter(personList);
        recyclerView.setAdapter(adapter);

        // Initialize DAO
        personInfoDao = new SfPersonInfoDao(this);

        // Setup search button
        findViewById(R.id.btnSearch).setOnClickListener(v -> performSearch());

        // Setup item click
        adapter.setOnItemClickListener((person, isButtonClicked)-> {
            if(!isButtonClicked) {
                Intent intent = new Intent(this, PersonScreeningForm15Activity.class);
                intent.putExtra("person_id", person.getId());
                startActivity(intent);
            }
            else {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String idcard = edtIdcard.getText().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();

        // ทำการค้นหาข้อมูลจาก DAO
        results = personInfoDao.searchPerson(idcard, firstName, lastName);

        // อัพเดทข้อมูลใน RecyclerView
        personList.clear();
        if (results != null) {
            personList.addAll(results);
        }
        adapter.notifyDataSetChanged();
    }

}