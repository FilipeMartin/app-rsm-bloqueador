package funcoes;

import android.content.Context;
import android.net.*;

public class StatusInternet {

    Context context;

    public StatusInternet(Context context){
        this.context = context;
    }

    public boolean status(){
        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conexao.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        return false;
    }
}