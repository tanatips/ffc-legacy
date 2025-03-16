package th.in.ffc.person;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.nhso.model.NHSOHospitalInfo;
import th.in.ffc.app.form.nhso.model.NHSOPatientInfo;
import th.in.ffc.app.form.nhso.model.NHSOPractitionerInfo;
import th.in.ffc.app.form.nhso.service.NHSOHospitalService;
import th.in.ffc.app.form.nhso.service.NHSOPatientService;
import th.in.ffc.app.form.nhso.service.NHSOPractitionerService;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.dao.VisitDao;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.Log;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    private List<PersonInfo> personList;
    private OnItemClickListener listener;
    boolean isButtonClicked = false;

    public PersonAdapter(List<PersonInfo> personList) {
        this.personList = personList;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.person_item, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        PersonInfo person = personList.get(position);
        holder.tvName.setText(person.getFname() + " " + person.getLname());
        holder.tvIdcard.setText("เลขบัตรประชาชน: " + person.getIdcard());
        holder.tvBirthday.setText("วันเกิด: " + person.getBirthday());
        holder.tvPhone.setText("โทรศัพท์: " + person.getPhone());
        holder.tvPersonId.setText(person.getId());
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvIdcard, tvBirthday, tvPhone, tvPersonId;
        Button btnSubmitClaim;
        boolean isButtonClicked = false;

        PersonViewHolder(View itemView) {
            super(itemView);
            tvPersonId = itemView.findViewById(R.id.tvPersonId);
            tvName = itemView.findViewById(R.id.tvName);
            tvIdcard = itemView.findViewById(R.id.tvIdcard);
            tvBirthday = itemView.findViewById(R.id.tvBirthday);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            btnSubmitClaim = itemView.findViewById(R.id.btnSubmitClaim);
            btnSubmitClaim.setOnClickListener(v -> {
                if (listener != null && getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    isButtonClicked = true;
                    listener.onItemClick(personList.get(getBindingAdapterPosition()));
                    personList.remove(getBindingAdapterPosition());
                    notifyItemRemoved(getBindingAdapterPosition());
                    notifyItemRangeChanged(getBindingAdapterPosition(), personList.size());
//                    Toast.makeText(itemView.getContext(), tvIdcard.getText().toString(), Toast.LENGTH_LONG ).show();
                    String[] names = tvName.getText().toString().split(" ");
                    List<PersonInfo>  personInfos =  SfPersonInfoDao.getSfPersonInfoById(Integer.valueOf(tvPersonId.getText().toString()));
                    PersonInfo personInfo = new PersonInfo();
                    if(!personInfos.isEmpty()) {
                        personInfo = personInfos.get(0);
                    }
                    UserSessionManager userSessionManager = new UserSessionManager(itemView.getContext());
                    String user = userSessionManager.getUser();
//                    Toast.makeText(itemView.getContext(), user,Toast.LENGTH_LONG).show();

                    NHSOPatientService nhsoPatientService = new NHSOPatientService(itemView.getContext());
                    NHSOHospitalService nhsoHospitalService = new NHSOHospitalService(itemView.getContext());
                    NHSOPractitionerService nhsoPractitionerService = new NHSOPractitionerService(itemView.getContext());
                    NHSOPatientInfo patient = new NHSOPatientInfo();
                    NHSOHospitalInfo hospital = new NHSOHospitalInfo();
                    NHSOPractitionerInfo practitioner = new NHSOPractitionerInfo();

                    VisitDao visitDao = new VisitDao(itemView.getContext().getContentResolver());

                    long visitId = visitDao.saveNewVisitWithVitalSigns(
                            userSessionManager.getPcuCode(),                     // pcucode
                            userSessionManager.getPcuCode(),                     // pcucodePerson
                            personInfo.getIdcard(),                              // pid
                            personInfo.getCreated_date(),                        // visitDate
                            (float)personInfo.getWeight(),                       // weight
                            (float)personInfo.getHeight(),                       // height
                            personInfo.getBp(),                                  // pressure
                            (float)personInfo.getTemperature(),                  // temperature
                            Integer.valueOf(personInfo.getBp()),                               // pluse
                            (float)personInfo.getWaist_size(),                   // waist
                            String.valueOf(personInfo.getSystolic_pressure()),                 // systolic
                            String.valueOf(personInfo.getDiastolic_pressure()),                // diastolic                               // diagnote
                            userSessionManager.getUsername()                     // username
                    );
                    // แฟ้ม 1
                    patient.setType("CID");
                    patient.setCid(personInfo.getIdcard());
                    patient.setNameGiven(names[0]);
                    patient.setNameFamily(names[1]);
                    patient.setSeq(String.valueOf(visitId));
                    patient.setBirthDate(personInfo.getBirthday());
                    patient.setGender(personInfo.getGender().equals("M")?"1":"2");
                    patient.setAddressLine(personInfo.getHomeNo()+" หมู่ที่ "+personInfo.getVillageNo());
                    patient.setAddressCity(personInfo.getSubDistCode());
                    patient.setAddressDistrict(personInfo.getDistCode());
                    patient.setAddressState(personInfo.getProvCode());
                    patient.setAddressPostalCode(personInfo.getPostCode());
                    patient.setHn(personInfo.getHn());
                    nhsoPatientService.createPatient(patient, userSessionManager.getUser());

                    // แฟ้ม 2
                    hospital.setSeq(String.valueOf(visitId));
                    hospital.setHcode(personInfo.getHcode());
                    nhsoHospitalService.createHospital(hospital, userSessionManager.getUser());

                    // แฟ้ม 3

                    practitioner.setSeq(String.valueOf(visitId));
                    practitioner.setHcode(personInfo.getHcode());
                    practitioner.setCid(personInfo.getIdcard());

                    nhsoPractitionerService.createPractitioner(practitioner,userSessionManager.getUser());

                }
            });
            itemView.setOnClickListener(v -> {
                if (listener != null && getBindingAdapterPosition() != RecyclerView.NO_POSITION && !isButtonClicked) {
                    listener.onItemClick(personList.get(getBindingAdapterPosition()));
                }
                isButtonClicked = false;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PersonInfo person);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
