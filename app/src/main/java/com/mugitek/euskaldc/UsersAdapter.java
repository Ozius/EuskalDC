package com.mugitek.euskaldc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Arkaitz on 26/07/2014.
 */
public class UsersAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<User> users;

    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    public void addItem(User user){
        users.add(user);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /*View view;

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

        text1.setText(messages.get(position).getUser());
        text2.setText("" + messages.get(position).getMensaje());*/

        return null;
    }
}