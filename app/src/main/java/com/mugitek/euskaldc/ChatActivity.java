package com.mugitek.euskaldc;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mugitek.euskaldc.eventos.NewMessageEvent;
import com.mugitek.euskaldc.eventos.SendMessageEvent;
import com.mugitek.euskaldc.eventos.UserLoginEvent;
import com.mugitek.euskaldc.eventos.UserLogoutEvent;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


public class ChatActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, GeneralChatFragment.GeneralChatCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private GeneralChatFragment mChatFragment;

    public static final String LOGTAG = "ChatActivity";
    public static final ArrayList<User> mUsuarios = new ArrayList<User>();

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        Bundle extras = getIntent().getExtras();

        if(extras != null)
            mTitle = extras.getString("Titulo");
        else if(savedInstanceState != null){
            mTitle = savedInstanceState.getString("Titulo");
        } else
            mTitle = getTitle();

        setTitle(mTitle);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mChatFragment = (GeneralChatFragment) getFragmentManager().findFragmentByTag("chat-general");

        if(mChatFragment == null) {
            mChatFragment = GeneralChatFragment.newInstance("", "");

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, mChatFragment, "chat-general")
                    .commit();
        }

        BusProvider.getInstance().register(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        /*FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Titulo", mTitle.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.chat, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEnviarMensaje(String mensaje) {
        //BusProvider.getInstance().post(new NewMessageEvent(mensaje));
    }


    @Subscribe
    public void userLogged(UserLoginEvent event) {
        //Log.d(LOGTAG, "nuevo usuario: " + event.getNick());
        //mAdapter.addItem(new User(event.getSid(), event.getCid(), event.getNick(), event.getDescription()));
        mUsuarios.add(new User(event.getSid(), event.getCid(), event.getNick(), event.getDescription()));
        mNavigationDrawerFragment.actualizarListView();
    }

    @Subscribe
    public void userLogout(UserLogoutEvent event) {
        //Buscamos el usuario con el id que se ha ido
        for(User user : mUsuarios){
            if(user.getSid().equalsIgnoreCase(event.getSid())){
                mUsuarios.remove(user);
                mNavigationDrawerFragment.actualizarListView();
                break;
            }
        }
    }

    @Subscribe
    public void newMessage(SendMessageEvent event) {
        //Log.d(LOGTAG, "nuevo mensaje recibido");
        String nick = "";

        //Buscamos el nick del usuario que ha escrito
        for(int i = 0; i < mUsuarios.size(); i++){
            User u = mUsuarios.get(i);
            if(u.getSid().equalsIgnoreCase(event.getUserSid())) {
                nick = u.getNick();
                break;
            }
        }

        Log.d(LOGTAG, "nuevo mensaje recibido de " + nick);
        mChatFragment.escribirNuevoMensaje(new Mensaje(nick, event.getMessage()));
    }

}
