package com.mugitek.euskaldc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class MessagesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Person> persons;

    public MessagesAdapter(Context context, ArrayList<Person> persons) {
        this.context = context;
        this.persons = persons;
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(
                    R.layout.message_row_layout, null);
        } else {
            view = convertView;
        }

        TextView text1 = (TextView) view.findViewById(R.id.textUser);
        TextView text2 = (TextView) view.findViewById(R.id.textMessage);

        text1.setText(persons.get(position).getName());
        text2.setText("" + persons.get(position).getAge());

        return twoLineListItem;
    }
}