package com.tribaltech.android.scnstrikefirst;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import rmn.androidscreenlibrary.ASSL;


public class EmailList extends Activity {

    ListView listView1;
    emailAdapter adepter;
//    EditText emailAddress;

    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.select_email_popup);
        new ASSL(this, (ViewGroup) findViewById(R.id.root), 1134, 720,
                false);
        listView1 = (ListView) findViewById(R.id.listView1);
        listView1.setDivider(null);
        listView1.setDividerHeight(0);
        getNameEmailDetails();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    public ArrayList<String> getNameEmailDetails() {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> emlRecs = new ArrayList<String>();
        HashSet<String> emlRecsHS = new HashSet<String>();
        ;
        ContentResolver cr = getContentResolver();
        String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
        String order = "CASE WHEN " + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA
                + " NOT LIKE ''";
        Cursor cur = cr.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION,
                filter, null, order);
        if (cur.moveToFirst()) {
            do {
                // names comes in hand sometimes
                String name = cur.getString(1);
                String emlAddr = cur.getString(3);

                // keep unique only
                if (emlRecsHS.add(emlAddr.toLowerCase())) {
                    names.add(name);
                    emlRecs.add(emlAddr);
                    Log.v(name, emlAddr);
                }
            } while (cur.moveToNext());
        }

        cur.close();

        emlRecs.add("dbdjd");
        emlRecs.add("dbdjd");
        emlRecs.add("dbdjd");
        emlRecs.add("dbdjd");
        emlRecs.add("dbdjd");

        names.add("3");
        names.add("1");
        names.add("d");
        names.add("d");
        names.add("c");

        adepter = new emailAdapter(names, emlRecs);
        listView1.setAdapter(adepter);

        return emlRecs;
    }

    public class emailAdapter extends BaseAdapter {

        ArrayList<String> ranks = new ArrayList<String>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> email = new ArrayList<String>();
        private LayoutInflater inflater;

        public emailAdapter(ArrayList<String> name, ArrayList<String> emailId) {

            names = name;
            email = emailId;

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return names.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return names.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        class ViewHolder {
            TextView email;
            RelativeLayout rlt;
        }

        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {

            final ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.email_listitem, null);
                holder = new ViewHolder();
                // if (arg1 == null)

                holder.rlt = (RelativeLayout) convertView
                        .findViewById(R.id.root);

                holder.email = (TextView) convertView.findViewById(R.id.name);

                holder.rlt
                        .setLayoutParams(new AbsListView.LayoutParams(900, 80));

                ASSL.DoMagic(holder.rlt);

                holder.rlt.setTag(holder);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.rlt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    Intent intent = new Intent();
                    intent.putExtra("email",holder.email.getTag().toString());
                    setResult(RESULT_OK,intent);
                    finish();

                }
            });
            holder.email
                    .setText(names.get(arg0) + " (" + email.get(arg0) + ")");
            holder.email.setTag(email.get(arg0));

            return convertView;
        }
    }

}
