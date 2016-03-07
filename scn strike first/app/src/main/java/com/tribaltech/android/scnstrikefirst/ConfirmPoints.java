package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tribaltech.android.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import rmn.androidscreenlibrary.ASSL;


public class ConfirmPoints extends Fragment implements View.OnClickListener {

    EditText passcode;
    WalletTransaction activity;
    private List<Button> buttons;
    private static final int[] BUTTON_IDS = {
            R.id.numericZero,
            R.id.numericOne,
            R.id.numericTwo,
            R.id.numericThree,
            R.id.numericFour,
            R.id.numericFive,
            R.id.numericSix,
            R.id.numericSeven,
            R.id.numericEight,
            R.id.numericNine,
            R.id.clearButton,
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.activity_confirm_points, container,
                false);
        new ASSL(getActivity(), (ViewGroup) view.findViewById(R.id.root), 1134,
                720, false);
        passcode = (EditText) view.findViewById(R.id.passcode);
        passcode.setClickable(false);
        view.findViewById(R.id.submit).setOnClickListener(this);
        if (activity.caseAdd) {
            view.findViewById(R.id.addTextParent).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.addText)).setText("Add " + activity.points + " points to account ?");
        }
        TextView dummyView=(TextView)view.findViewById(R.id.dummyView);
        dummyView.setEnabled(false);
        buttons = new ArrayList<Button>();

        // buttons = new ArrayList<Button>(BUTTON_IDS.length);
        for (int id : BUTTON_IDS) {
            Button button = (Button) view.findViewById(id);
            button.setOnClickListener(this); // maybe
            buttons.add(button);
        }
       Button deleteButton=(Button)view.findViewById(R.id.clearButton);
        deleteButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               passcode.getText().clear();
            return false;
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (WalletTransaction) activity;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit: {
                if (passcode.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please enter a passcode", Toast.LENGTH_SHORT).show();
                } else {
                    if (activity.caseAdd) {
                        activity.addRedeemPoints(passcode.getText().toString(), true);
                    } else {
//                        activity.redeemImage(passcode.getText().toString());
                        CommonUtil.hideSoftKeyboard(getActivity(), passcode);
                        activity.addRedeemPoints(passcode.getText().toString(), false);
                    }
                }
                break;
            }
            case R.id.numericOne: {
                passcode.append("1");
                break;
            }
            case R.id.numericTwo: {
                passcode.append("2");
                break;
            }
            case R.id.numericThree: {
                passcode.append("3");
                break;
            }
            case R.id.numericFour: {
                passcode.append("4");
                break;
            }
            case R.id.numericFive: {
                passcode.append("5");
                break;
            }
            case R.id.numericSix: {
                passcode.append("6");
                break;
            }
            case R.id.numericSeven: {
                passcode.append("7");
                break;
            }
            case R.id.numericEight: {
                passcode.append("8");
                break;
            }
            case R.id.numericNine: {
                passcode.append("9");
                break;
            }
            case R.id.numericZero: {
                passcode.append("0");
                break;
            }
            case R.id.clearButton: {
                String textString = passcode.getText().toString();
                if (textString.length() > 0) {
                    passcode.setText(textString.substring(0, textString.length() - 1));
                    passcode.setSelection(passcode.getText().length());//position cursor at the end of the line
                }
                break;
            }


//                int length = editText.getText().length();
//                if (length > 0) {
//                    editText.getText().delete(length - 1, length);
        }
    }

}


