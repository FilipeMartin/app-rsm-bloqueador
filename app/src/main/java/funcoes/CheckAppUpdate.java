package funcoes;

import android.app.*;
import android.content.*;
import android.net.Uri;
import com.android.volley.*;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rsmbloqueador.rsmbloqueador.R;
import org.json.JSONObject;
import api.*;

public class CheckAppUpdate {

    private String URL_API = Api.BASE_URL+"api/app_version/";
    private String URL_PLAY_STORE = "https://play.google.com/store/apps/details?id=rsmbloqueador.com.br";
    private Context context;
    private RequestQueue requestQueue;
    private boolean btnCheckAppUpdate;
    private ProgressDialog progressDialog;
    private boolean statusAlertDialog;

    public CheckAppUpdate(Context context, boolean btnCheckAppUpdate){
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.btnCheckAppUpdate = btnCheckAppUpdate;
        this.statusAlertDialog = true;
    }

    public boolean check(){
        StatusInternet statusInternet = new StatusInternet(context);
        if(statusInternet.status()){
            startProgressDialog();
            URL_API = URL_API.replace(" ","%20");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_API, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(statusAlertDialog){
                                String currentVersion = context.getString(R.string.versao_app_number);
                                String newVersion = response.optString("version");

                                if(!currentVersion.equals(newVersion)){

                                    AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                                    dlg
                                            .setTitle("Nova atualização")
                                            .setIcon(R.mipmap.ic_app_update)
                                            .setMessage(" Atualize o app para a versão "+response.optString("version"))
                                            .setCancelable(false)
                                            .setPositiveButton("ATUALIZAR AGORA", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intentUrlMapa = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_PLAY_STORE));
                                                    context.startActivity(intentUrlMapa);
                                                }
                                            })
                                            .setNegativeButton("AGORA NÃO", null)
                                            .show();

                                }else if(btnCheckAppUpdate){
                                    caixaDialogo("Informações", "Voçê já tem a versão mais recente do RSM Bloqueador.", R.mipmap.ic_informacao);
                                }
                                stopProgressDialog();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(statusAlertDialog){
                                if(btnCheckAppUpdate) {
                                    caixaDialogo("Atenção", "Serviço indisponível !", R.mipmap.ic_atencao);
                                    stopProgressDialog();
                                }
                            }
                        }
                    }
            );

            request.setRetryPolicy(new DefaultRetryPolicy(8000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(request);

            return true;
        }
        return false;
    }

    public void startProgressDialog(){
        if(btnCheckAppUpdate) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Carregando...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void stopProgressDialog(){
        if(btnCheckAppUpdate){
            progressDialog.hide();
        }
    }

    public void caixaDialogo(String title, String message, int icone){
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);
        dlg
                .setTitle(title)
                .setIcon(icone)
                .setMessage(message)
                .setNeutralButton("OK", null)
                .show();
    }

    public void setStatusAlertDialog(boolean statusAlertDialog){
        this.statusAlertDialog = statusAlertDialog;
    }
}