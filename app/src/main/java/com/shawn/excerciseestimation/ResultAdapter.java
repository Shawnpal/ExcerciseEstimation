package com.shawn.excerciseestimation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shawn.excerciseestimation.Retrofit.ExerciseResult;


import java.util.ArrayList;
import java.util.List;

public class ResultAdapter  extends ArrayAdapter<ExerciseResult> {

    private static final String TAG = "ExerciseResultAdapter";
    private Context mcontext;
    int mResources;


    //Default COnstructor for Result adaptor

    public ResultAdapter(@NonNull Context context, int resource, List<ExerciseResult> textViewResourceId) {
        super(context, resource, textViewResourceId);
        mcontext = context;
        mResources = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String ResultID = getItem(position).getResultUID();
        String Result = getItem(position).getResult();
        String PersonUID = getItem(position).getPersonEmail();
        String Date = getItem(position).getDate();
        ArrayList<TextView> TextResultList = new ArrayList<>();
        ExerciseResult exerciseResult = new ExerciseResult(Result,ResultID,PersonUID,Date);

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mResources,parent,false);

        TextResultList.add(convertView.findViewById(R.id.txtResult1));
        TextResultList.add(convertView.findViewById(R.id.txtResult2));
        TextResultList.add(convertView.findViewById(R.id.txtResult3));
        TextResultList.add(convertView.findViewById(R.id.txtResult4));
        TextResultList.add(convertView.findViewById(R.id.txtResult5));
        TextResultList.add(convertView.findViewById(R.id.txtResult6));
        TextResultList.add(convertView.findViewById(R.id.txtResult7));
        TextResultList.add(convertView.findViewById(R.id.txtResult8));
        TextResultList.add(convertView.findViewById(R.id.txtResult9));
        TextResultList.add(convertView.findViewById(R.id.txtResult10));
        TextResultList.add(convertView.findViewById(R.id.txtResult11));
        TextResultList.add(convertView.findViewById(R.id.txtResult12));
        TextResultList.add(convertView.findViewById(R.id.txtResult13));
        TextResultList.add(convertView.findViewById(R.id.txtResult14));
        TextResultList.add(convertView.findViewById(R.id.txtResult15));
        TextResultList.add(convertView.findViewById(R.id.txtResult16));
        TextResultList.add(convertView.findViewById(R.id.txtResult17));

        return convertView;
    }
}
