package functions;

import android.app.*;
import android.content.*;
import android.net.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.rsmbloqueador.rsmbloqueador.R;

public class Functions {

    private Context context;

    public Functions(Context context){
        this.context = context;
    }

    public String formatCpf(String cpf){
        if(cpf.length() == 11){
            cpf = cpf.substring(0,3)+"."+cpf.substring(3,6)+"."+cpf.substring(6,9)+"-"+cpf.substring(9,11);
        }
        return cpf;
    }

    public String clearCpf(String cpf){
        cpf = cpf.replace("-","");
        cpf = cpf.replace(".","");
        return cpf;
    }

    public String formatPhone(String phone){
        int size = phone.length();
        phone = "(" + phone.substring(0, size - 9) + ") " + phone.substring(size - 9, size-4) +"-"+ phone.substring(size - 4, size);
        return phone;
    }

    public void closeKeyboard(View view){
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public boolean statusInternet(){
        ConnectivityManager conexao = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conexao.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        alertDialog("Atenção", "Sem conexão com a Internet !", R.mipmap.ic_atencao, "Voltar");
        return false;
    }

    public void alertDialog(String title, String message, int icone, String txtButton){
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg
                .setTitle(title)
                .setIcon(icone)
                .setMessage(message)
                .setNeutralButton(txtButton, null)
                .show();
    }

    public void toast(String texto){
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }
}