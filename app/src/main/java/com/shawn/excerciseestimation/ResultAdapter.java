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
    List<ExerciseResult> mList;


    //Default COnstructor for Result adaptor

    public ResultAdapter(@NonNull Context context, int resource, List<ExerciseResult> List) {
        super(context, resource, List);
        mcontext = context;
        mResources = resource;
        mList = List;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String ResultID = getItem(position).getResultUID();
        String Result = getItem(position).getResult();
        String PersonUID = getItem(position).getPersonEmail();
        String Date = getItem(position).getDate();
        ArrayList<TextView> TextResultList = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mResources,parent,false);

        TextResultList.add(convertView.findViewById(R.id.txtEResult1));
        TextResultList.add(convertView.findViewById(R.id.txtEResult2));
        TextResultList.add(convertView.findViewById(R.id.txtEResult3));
        TextResultList.add(convertView.findViewById(R.id.txtEResult4));
        TextResultList.add(convertView.findViewById(R.id.txtEResult5));
        TextResultList.add(convertView.findViewById(R.id.txtEResult6));
        TextResultList.add(convertView.findViewById(R.id.txtEResult7));
        TextResultList.add(convertView.findViewById(R.id.txtEResult8));
        TextResultList.add(convertView.findViewById(R.id.txtEResult9));
        TextResultList.add(convertView.findViewById(R.id.txtEResult10));
        TextResultList.add(convertView.findViewById(R.id.txtEResult11));
        TextResultList.add(convertView.findViewById(R.id.txtEResult12));
        TextResultList.add(convertView.findViewById(R.id.txtEResult13));
        TextResultList.add(convertView.findViewById(R.id.txtEResult14));
        TextResultList.add(convertView.findViewById(R.id.txtEResult15));
        TextResultList.add(convertView.findViewById(R.id.txtEResult16));
        TextResultList.add(convertView.findViewById(R.id.txtEResult17));

        ExerciseResult exerciseResult = mList.get(position);
        String result = exerciseResult.getResult();
        String[] companies = result.split("\\s+");
        int index =0;

        for (TextView text : TextResultList)
        {
            String s = companies[index];
            text.setText(s);
            index++;
        }

        return convertView;
    }
}
