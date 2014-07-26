package com.mugitek.euskaldc;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.mugitek.euskaldc.eventos.NewMessageEvent;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the {@link GeneralChatCallbacks}
 * interface.
 */
public class GeneralChatFragment extends ListFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MessagesAdapter mAdapter;
    private EditText mNewMessage;
    private ImageButton mNewMessageSend;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GeneralChatCallbacks mListener;

    // TODO: Rename and change types of parameters
    public static GeneralChatFragment newInstance(String param1, String param2) {
        GeneralChatFragment fragment = new GeneralChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GeneralChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAdapter = new MessagesAdapter(getActivity(), new ArrayList<Mensaje>());

        setListAdapter(mAdapter);

        //mAdapter.addItem(new Mensaje("Ozius", "mensaje de prueba"));
        //mAdapter.addItem(new Mensaje("Ozius", "mensaje de prueba más largo."));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_general_chat, container, false);

        mNewMessage = (EditText) v.findViewById(R.id.newmsg);
        mNewMessageSend = (ImageButton) v.findViewById(R.id.newmsgsend);

        mNewMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        return v;
    }

    private void sendMessage() {
        if(!TextUtils.isEmpty(mNewMessage.getText().toString())){
            BusProvider.getInstance().post(new NewMessageEvent(mNewMessage.getText().toString()));
            mNewMessage.setText("");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (GeneralChatCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement GeneralChatCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        /*if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }*/
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface GeneralChatCallbacks {
        // TODO: Update argument type and name
        public void onEnviarMensaje(String mensaje);
    }

    public void escribirNuevoMensaje(Mensaje m){
        mAdapter.addItem(m);
    }
}
