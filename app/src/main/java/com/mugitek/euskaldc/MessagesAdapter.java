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
public class MessagesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mensaje> messages;

    public MessagesAdapter(Context context, ArrayList<Mensaje> messages) {
        this.context = context;
        this.messages = messages;
    }

    public void addItem(Mensaje mensaje){
        messages.add(mensaje);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
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

        text1.setText(messages.get(position).getUser());
        text2.setText("" + messages.get(position).getMensaje());

        return view;
    }
}