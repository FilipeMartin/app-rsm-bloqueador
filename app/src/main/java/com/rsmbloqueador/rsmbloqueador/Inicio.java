package com.rsmbloqueador.rsmbloqueador;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
import android.view.*;
import android.widget.*;
import java.util.*;
import funcoes.*;
import functions.*;
import models.*;

import static android.app.Activity.RESULT_OK;

public class Inicio extends Fragment{

    // Veículo
    private AppDB appDB;
    private Veiculo veiculo;
    private Config config;
    private Functions functions;
    //------------------------------------

    // Painel Principal
    private TextView txtNome;
    private ImageView imgIgnicao;
    private TextView txtVelocidade;
    private Button btnLocalizacao;
    //------------------------------------

    // Botão Reproduzir Áudio
    private Switch btnReproduzirAudio;
    private TextToSpeech speak;
    private int resultSpeak;
    //------------------------------------

    // Data e Hora
    private TextView txtDataHora;
    private TextView dataHora;
    //------------------------------------

    // Botões Principais
    private Button btnBloquear;
    private Button btnLocalizar;
    private Button btnDesbloquear;
    private Button btnEscuta;
    private Button btnPlataforma;
    private Button btnAtualizar;
    private Button btnAlertaMovimento;
    private Button btnMonitorarVelocidade;
    private Button btnConsultarSaldo;
    //------------------------------------

    // Status das funções
    private boolean statusBotoes = true;
    private boolean statusBloquear = false;
    private boolean statusLocalizar = false;
    private boolean statusDesbloquear = false;
    private boolean statusAtualizar = false;
    private boolean statusConsultarSaldo = false;
    private boolean[] statusEscuta = {false, false, false};
    private boolean[] statusAlertaMovimento = {false, false, false};
    private boolean[] statusMonitorarVelocidade = {false, false, false};
    //------------------------------------------------------------------

    // Controle
    private boolean statusSmsErro = false;
    private boolean desbloquearMapa = false;
    private boolean desbloquearAtualizar = false;
    //-----------------------------------------------

    // AlertDialog Alerta de Movimento
    private Boolean statusAlertDialogMove = false;
    private AlertDialog alertDialogMove;
    //-----------------------------------------------

    // AlertDialog Monitorar Velocidade
    private Boolean statusAlertDialogSpeed = false;
    private AlertDialog alertDialogSpeed;
    //-----------------------------------------------

    // Editar Senha
    private boolean statusEditarSenha = false;
    private Veiculo usuarioSenha = new Veiculo();
    //-----------------------------------------------

    // SMS
    private IntentFilter intentFilter;
    private String SENT = "SMS_SENT";
    private String DELIVERED = "SMS_DELIVERED";
    private PendingIntent sentPI, deliveredPI;
    private BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    private AlertDialog dialogProgressSms;
    //--------------------------------------------------------------

    // FragmentManager
    private FragmentManager fm;
    //-------------------------

    // Check App Update
    private CheckAppUpdate checkAppUpdate;
    //------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);

        // FragmentManager
        fm = getActivity().getSupportFragmentManager();
        //---------------------------------------------

        // Veículo
        appDB = new AppDB(getActivity());
        veiculo = appDB.getVeiculo(appDB.getConfig("VEICULO_ATUAL"));
        config = appDB.getConfig();
        functions = new Functions(getActivity());
        //-----------------------------------------------------------------

        // Intent to filter for SMS messagens received
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        sentPI = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(getActivity(), 0, new Intent(DELIVERED), 0);
        //-----------------------------------------------------------------------------------------------------

        // Painel Principal
        txtNome = (TextView) view.findViewById(R.id.txtNome);
        imgIgnicao = (ImageView) view.findViewById(R.id.imgIgnicao);
        txtVelocidade = (TextView) view.findViewById(R.id.txtVelocidade);
        btnLocalizacao = (Button) view.findViewById(R.id.btnLocalizacao);
        //----------------------------------------------------------------

        // Data e Hora
        txtDataHora = (TextView) view.findViewById(R.id.txtDataHora);
        dataHora = (TextView) view.findViewById(R.id.dataHora);
        //----------------------------------------------------------------

        // Botão Reproduzir Áudio
        btnReproduzirAudio = (Switch) view.findViewById(R.id.btnReproduzirAudio);
        //-----------------------------------------------------------------------

        // Botões Principais
        btnBloquear = (Button) view.findViewById(R.id.btnBloquear);
        btnLocalizar = (Button) view.findViewById(R.id.btnLocalizar);
        btnDesbloquear = (Button) view.findViewById(R.id.btnDesbloquear);
        btnEscuta = (Button) view.findViewById(R.id.btnEscuta);
        btnPlataforma = (Button) view.findViewById(R.id.btnPlataforma);
        btnAtualizar = (Button) view.findViewById(R.id.btnAtualizar);
        btnAlertaMovimento = (Button) view.findViewById(R.id.btnAlertaMovimento);
        btnMonitorarVelocidade = (Button) view.findViewById(R.id.btnMonitorarVelocidade);
        btnConsultarSaldo = (Button) view.findViewById(R.id.btnConsultarSaldo);
        //-------------------------------------------------------------------------------

        // Inicializar Sistema
        startPainel();
        startAudio();
        speak();
        startMonitorarVelocidade();
        //-------------------------

        // Check App Update
        checkAppUpdate = new CheckAppUpdate(getActivity(), false);
        checkAppUpdate.check();
        //--------------------------------------------------------------------------

        // Painel Principal
        imgIgnicao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(veiculo.getPainelStatus() == 1){
                    if(veiculo.getPainelIgnicao() == 1) {
                        functions.toast("Ignição Ligada");
                        reproduzirAudio(14);
                    } else{
                        functions.toast("Ignição Desligada");
                        reproduzirAudio(15);
                    }
                } else{
                    functions.toast("Atualize o Sistema !");
                    reproduzirAudio(10);
                }
            }
        });

        txtVelocidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(veiculo.getPainelStatus() == 1) {
                    reproduzirAudio(16);
                } else{
                    functions.toast("Atualize o Sistema !");
                    reproduzirAudio(10);
                }
            }
        });

        btnLocalizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(veiculo.getPainelStatus() == 1) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(veiculo.getPainelUrlMapa()));
                    startActivity(intent);
                } else{
                    functions.toast("Atualize o Sistema !");
                    reproduzirAudio(10);
                }
            }
        });

        btnReproduzirAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    appDB.editConfig("STATUS_AUDIO", 1);
                    config.setStatusAudio(1);
                } else{
                    appDB.editConfig("STATUS_AUDIO", 0);
                    config.setStatusAudio(0);
                    if(speak != null){
                        speak.stop();
                    }
                }
            }
        });

        // Funções do app
        btnBloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBloquear();
            }
        });

        btnLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLocalizar();
            }
        });

        btnDesbloquear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDesbloquear();
            }
        });

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAtualizar();
            }
        });

        btnEscuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusBotoes()){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    View view = getActivity().getLayoutInflater().inflate(R.layout.opcoes_escuta, null);
                    final Button btnLigar = (Button) view.findViewById(R.id.btnLigar);
                    final Button btnDesligar = (Button) view.findViewById(R.id.btnDesligar);
                    final ImageButton btnDuvida = (ImageButton) view.findViewById(R.id.btnDuvida);
                    final Button btnOkDuvida = (Button) view.findViewById(R.id.btnOkDuvida);
                    final LinearLayout layoutDuvida = (LinearLayout) view.findViewById(R.id.layoutDuvida);

                    dlg.setView(view);
                    final AlertDialog dialog = dlg.create();
                    dialog.show();

                    btnDuvida.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutDuvida.setVisibility(View.VISIBLE);
                        }
                    });

                    btnOkDuvida.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutDuvida.setVisibility(View.GONE);
                        }
                    });

                    if(statusEscuta[2]){
                        btnLigar.setText("Escuta Ligada");
                    } else{
                        btnLigar.setText("Ligar Escuta");
                    }

                    btnLigar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            if(statusEscuta[2]){
                                ligarEscuta();
                            } else{
                                functions.toast("Ligando Escuta...");
                                enviarSms("monitor"+veiculo.getSenha());
                                btnEscuta.setTextColor(getResources().getColor(R.color.colorRed));
                                statusEscuta[0] = true;
                            }
                        }
                    });

                    btnDesligar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            enviarSms("tracker"+veiculo.getSenha());
                            functions.toast("Desligando Escuta...");
                            btnEscuta.setTextColor(getResources().getColor(R.color.colorAccent));
                            statusEscuta[1] = true;
                        }
                    });
                }
            }
        });

        btnPlataforma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(config.getConta() == 1){
                    if(functions.statusInternet()){

                        ((MainActivity)getActivity()).setVisibleFragments(false, true);

                        Plataforma plataforma = (Plataforma) fm.findFragmentByTag("Plataforma");

                        if(plataforma == null){
                            ((MainActivity)getActivity()).setActionBarTitle("Plataforma");
                            ((MainActivity)getActivity()).iconeAtualizarPlataforma(true);
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.add(R.id.container_plataforma, new Plataforma(), "Plataforma");
                            ft.commit();
                        }
                    }
                } else{
                    Intent intent = new Intent(getActivity(), AnuncioPlataforma.class);
                    startActivity(intent);
                }
            }
        });

        btnAlertaMovimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusBotoes()){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    View view = getActivity().getLayoutInflater().inflate(R.layout.opcoes_move, null);
                    final Button btnLigar = (Button) view.findViewById(R.id.btnLigar);
                    final Button btnDesligar = (Button) view.findViewById(R.id.btnDesligar);
                    final ImageButton btnDuvida = (ImageButton) view.findViewById(R.id.btnDuvida);
                    final Button btnOkDuvida = (Button) view.findViewById(R.id.btnOkDuvida);
                    final LinearLayout layoutDuvida = (LinearLayout) view.findViewById(R.id.layoutDuvida);

                    dlg.setView(view);
                    final AlertDialog dialog = dlg.create();
                    dialog.show();

                    btnDuvida.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutDuvida.setVisibility(View.VISIBLE);
                        }
                    });

                    btnOkDuvida.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutDuvida.setVisibility(View.GONE);
                        }
                    });

                    if(statusAlertaMovimento[2]){
                        btnLigar.setText("Ligado");
                    } else{
                        btnLigar.setText("Ligar");
                    }

                    btnLigar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            enviarSms("move"+veiculo.getSenha());
                            functions.toast("Ligando Alerta de Movimento...");
                            btnAlertaMovimento.setTextColor(getResources().getColor(R.color.colorRed));
                            statusAlertaMovimento[0] = true;
                        }
                    });

                    btnDesligar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            enviarSms("nomove"+veiculo.getSenha());
                            functions.toast("Desligando Alerta de Movimento...");
                            btnAlertaMovimento.setTextColor(getResources().getColor(R.color.colorAccent));
                            statusAlertaMovimento[1] = true;
                        }
                    });
                }
            }
        });

        btnMonitorarVelocidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusBotoes()){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    View view = getActivity().getLayoutInflater().inflate(R.layout.opcoes_velocidade, null);
                    final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                    final Button btnLigar = (Button) view.findViewById(R.id.btnLigar);
                    final Button btnDesligar = (Button) view.findViewById(R.id.btnDesligar);
                    final ImageButton btnDuvida = (ImageButton) view.findViewById(R.id.btnDuvida);
                    final Button btnOkDuvida = (Button) view.findViewById(R.id.btnOkDuvida);
                    final LinearLayout layoutDuvida = (LinearLayout) view.findViewById(R.id.layoutDuvida);
                    ArrayAdapter<String> opcoes;

                    dlg.setView(view);
                    final AlertDialog dialog = dlg.create();
                    dialog.show();

                    btnDuvida.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutDuvida.setVisibility(View.VISIBLE);
                        }
                    });

                    btnOkDuvida.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layoutDuvida.setVisibility(View.GONE );
                        }
                    });

                    opcoes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
                    opcoes.setDropDownViewResource(android.R.layout.simple_list_item_checked);
                    spinner.setAdapter(opcoes);

                    for(int i=20; i<=300; i+=20 ){
                        opcoes.add(Integer.toString(i)+" Km");
                    }

                    if(veiculo.getVelocidadeLimite() < 15) {
                        spinner.setSelection(veiculo.getVelocidadeLimite());
                    } else{
                        spinner.setSelection(0);
                    }

                    btnLigar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            veiculo.setVelocidadeLimite(spinner.getSelectedItemPosition());
                            int velocidade = 20 * (veiculo.getVelocidadeLimite() + 1);

                            enviarSms("speed"+veiculo.getSenha()+" "+velocidade);
                            functions.toast("Definindo Velocidade...");
                            btnMonitorarVelocidade.setTextColor(getResources().getColor(R.color.colorRed));
                            statusMonitorarVelocidade[0] = true;
                        }
                    });

                    btnDesligar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            veiculo.setVelocidadeLimite(0);

                            enviarSms("nospeed"+veiculo.getSenha());
                            functions.toast("Desligando monitoramento de Velocidade...");
                            btnMonitorarVelocidade.setTextColor(getResources().getColor(R.color.colorAccent));
                            statusMonitorarVelocidade[1] = true;
                        }
                    });
                }
            }
        });

        btnConsultarSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(config.getConta() == 1){
                    if(statusBotoes()){
                        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                        View view = getActivity().getLayoutInflater().inflate(R.layout.opcoes_consultar_saldo, null);
                        final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
                        final Button btnConsultar = (Button) view.findViewById(R.id.btnConsultar);
                        final ImageButton btnDuvida = (ImageButton) view.findViewById(R.id.btnDuvida);
                        final Button btnOkDuvida = (Button) view.findViewById(R.id.btnOkDuvida);
                        final LinearLayout layoutDuvida = (LinearLayout) view.findViewById(R.id.layoutDuvida);

                        ArrayAdapter<String> operadoras = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
                        operadoras.setDropDownViewResource(android.R.layout.simple_list_item_checked);
                        spinner.setAdapter(operadoras);

                        operadoras.add("Vivo");
                        //operadoras.add("Tim");
                        //operadoras.add("Claro");
                        //operadoras.add("Oi");

                        dlg.setView(view);
                        final AlertDialog dialog = dlg.create();
                        dialog.show();

                        btnDuvida.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutDuvida.setVisibility(View.VISIBLE);
                            }
                        });

                        btnOkDuvida.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutDuvida.setVisibility(View.GONE );
                            }
                        });

                        btnConsultar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                String codigo = null;
                                switch(spinner.getSelectedItemPosition()){
                                    case 0:
                                        codigo = "8000 saldo";
                                        break;
                                    case 1:
                                        codigo = "8000 saldo";
                                        break;
                                    case 2:
                                        codigo = "8000 saldo";
                                        break;
                                    case 3:
                                        codigo = "8000 saldo";
                                }

                                enviarSms("balance"+veiculo.getSenha()+" "+codigo);
                                functions.toast("Consultando Saldo...");
                                btnConsultarSaldo.setTextColor(getResources().getColor(R.color.colorRed));
                                statusConsultarSaldo = true;
                            }
                        });
                    }
                } else{
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    View view = getLayoutInflater().inflate(R.layout.dialog_contato, null);
                    final TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                    final TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
                    final ImageView btnWhatsApp = (ImageView) view.findViewById(R.id.btnWhatsApp);
                    final TextView txtPhone1 = (TextView) view.findViewById(R.id.txtPhone1);
                    final TextView txtPhone2 = (TextView) view.findViewById(R.id.txtPhone2);
                    final Button btnOk = (Button) view.findViewById(R.id.btnOk);

                    txtTitle.setText("PLATAFORMA ONLINE");
                    txtMessage.setText("Função disponível apenas para usuários da Plataforma Online.\n\nPara contratar o serviço:");

                    dlg.setView(view);
                    final AlertDialog dialog = dlg.create();
                    dialog.show();

                    btnWhatsApp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.whatsapp.com/send?1=pt_BR&phone=5521998259550"));
                            startActivity(intent);
                        }
                    });

                    txtPhone1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.whatsapp.com/send?1=pt_BR&phone=5521998259550"));
                            startActivity(intent);
                        }
                    });

                    txtPhone2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.whatsapp.com/send?1=pt_BR&phone=5521996396999"));
                            startActivity(intent);
                        }
                    });

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        return view;
    }

    private void btnBloquear(){
        if(statusBotoes()){
            enviarSms("stop"+veiculo.getSenha());
            functions.toast("Aguarde, Veículo sendo Bloqueado...");
            statusBloquear = true;

            btnBloquear.setTextColor(getResources().getColor(R.color.colorRed));
            btnBloquear.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_cadeadro_fechado_bloqueado, 0, 0);
            btnDesbloquear.setTextColor(getResources().getColor(R.color.colorAccent));
            btnDesbloquear.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_cadeadro_aberto, 0, 0);
        }
    }

    private void btnLocalizar(){
        if(statusBotoes()){
            enviarSms("fix010s001n"+veiculo.getSenha());
            functions.toast("Localizando Veículo...");
            btnLocalizar.setTextColor(getResources().getColor(R.color.colorRed));
            desbloquearMapa = true;
            statusLocalizar = true;
        }
    }

    private void btnDesbloquear(){
        if(statusBotoes()){
            enviarSms("resume"+veiculo.getSenha());
            functions.toast("Aguarde, Veículo sendo Desbloqueado...");
            statusDesbloquear = true;

            btnDesbloquear.setTextColor(getResources().getColor(R.color.colorRed));
            btnDesbloquear.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_cadeadro_aberto_desbloqueado, 0, 0);
            btnBloquear.setTextColor(getResources().getColor(R.color.colorAccent));
            btnBloquear.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_cadeadro_fechado, 0, 0);
        }
    }

    private void btnAtualizar(){
        if(statusBotoes()){
            enviarSms("fix010s001n"+veiculo.getSenha());
            functions.toast("Atualizando Sistema...");
            btnAtualizar.setTextColor(getResources().getColor(R.color.colorRed));
            desbloquearAtualizar = true;
            statusAtualizar = true;
        }
    }

    private boolean statusBotoes(){
        if(statusBotoes){
            return true;
        }
        snackbar("AGUARDE - Comando em Execução !");
        reproduzirAudio(11);
        return false;
    }

    private void startPainel(){
        String telefone = veiculo.getTelefone();
        int length = telefone.length();
        final String telefonePainel = "(" + telefone.substring(0, length - 9) + ") " + telefone.substring(length - 9, length-4) +"-"+ telefone.substring(length - 4, length);

        // Nome do Veículo no Painel
        txtNome.setText(veiculo.getNome());
        //---------------------------------

        // Telefone Snackbar
        txtNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar(telefonePainel);
            }
        });
        //------------------------------------------------------

        if(veiculo.getPainelStatus() == 1){
            txtVelocidade.setText(veiculo.getPainelVelocidade());
            txtDataHora.setText("Atualizado às ");
            dataHora.setText(veiculo.getPainelData());

            // Painel
            btnLocalizacao.setBackgroundDrawable(getResources().getDrawable(R.drawable.icone_localizar));

            if(veiculo.getPainelIgnicao() == 1){
                imgIgnicao.setImageResource(R.mipmap.ic_verde);
            } else{
                imgIgnicao.setImageResource(R.mipmap.ic_vermelho);
            }
        }
    }

    private void editarPainel(){
        // Editar a data
        veiculo.setPainelData(dataHora());
        //--------------------------------

        // Armazenar no BD
        veiculo.setPainelStatus(appDB.editPainel(veiculo));
        //-------------------------------------------------

        // Editar Painel
        if(veiculo.getPainelIgnicao() == 1){
            imgIgnicao.setImageResource(R.mipmap.ic_verde);
        } else{
            imgIgnicao.setImageResource(R.mipmap.ic_vermelho);
        }
        btnLocalizacao.setBackgroundDrawable(getResources().getDrawable(R.drawable.icone_localizar));
        dataHora.setText(veiculo.getPainelData());
        //-------------------------------------------------------------------------------------------
    }

    @Override
    public void onResume(){
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        erroSms();
                }
            }
        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        if(!statusBotoes){
                            progressSms(false);
                            statusBotoes = true;

                            if(statusBloquear){
                                functions.alertDialog("Informações", "Veículo Bloqueado com Sucesso !", R.mipmap.ic_informacao, "OK");
                                reproduzirAudio(2);
                                statusBloquear = false;
                                break;
                            }

                            if(statusLocalizar){
                                snackbar("SMS Recebido, aguardando abertura do Mapa !");
                                reproduzirAudio(13);
                                btnLocalizar.setTextColor(getResources().getColor(R.color.colorAccent));
                                statusLocalizar = false;
                                break;
                            }

                            if(statusDesbloquear){
                                functions.alertDialog("Informações", "Veículo Desbloqueado com Sucesso !", R.mipmap.ic_informacao, "OK");
                                reproduzirAudio(1);
                                statusDesbloquear = false;
                                break;
                            }

                            if(statusEscuta[0]){
                                functions.toast("Escuta Ligada !");
                                ligarEscuta();
                                statusEscuta[2] = true;
                                statusEscuta[0] = false;
                                break;
                            }

                            if(statusEscuta[1]){
                                functions.alertDialog("Informações", "Escuta Desligada !", R.mipmap.ic_informacao, "OK");
                                reproduzirAudio(7);
                                statusEscuta[2] = false;
                                statusEscuta[1] = false;
                                break;
                            }

                            if(statusAtualizar){
                                snackbar("SMS Recebido, aguardando Atualização !");
                                reproduzirAudio(12);
                                btnAtualizar.setTextColor(getResources().getColor(R.color.colorAccent));
                                statusAtualizar = false;
                                break;
                            }

                            if(statusAlertaMovimento[0]){
                                functions.alertDialog("Informações", "Alerta de Movimento Ligado !", R.mipmap.ic_informacao, "OK");
                                reproduzirAudio(18);
                                statusAlertaMovimento[2] = true;
                                statusAlertaMovimento[0] = false;
                                break;
                            }

                            if(statusAlertaMovimento[1]){
                                functions.alertDialog("Informações", "Alerta de Movimento Desligado !", R.mipmap.ic_informacao, "OK");
                                reproduzirAudio(19);
                                statusAlertaMovimento[2] = false;
                                statusAlertaMovimento[1] = false;
                                break;
                            }

                            if(statusMonitorarVelocidade[0]){
                                appDB.editVelocidadeLimite(veiculo.getId(), veiculo.getVelocidadeLimite());

                                functions.alertDialog("Informações", "Monitoramento de Velocidade Ligado !", R.mipmap.ic_informacao, "OK");
                                reproduzirAudio(8);
                                statusMonitorarVelocidade[0] = false;
                                break;
                            }

                            if(statusMonitorarVelocidade[1]){
                                appDB.editVelocidadeLimite(veiculo.getId(), 15);

                                functions.alertDialog("Informações", "Monitoramento de Velocidade Desligado !", R.mipmap.ic_informacao, "OK");
                                reproduzirAudio(9);
                                statusMonitorarVelocidade[1] = false;
                                break;
                            }

                            if(statusConsultarSaldo){
                                functions.alertDialog("Consultar Saldo", "O saldo será enviado para a sua caixa de mensagens, entretanto, esse processo poderá demorar um pouco.\n\nAguarde o envio do saldo !", R.mipmap.ic_sifrao, "OK");
                                reproduzirAudio(22);
                                btnConsultarSaldo.setTextColor(getResources().getColor(R.color.colorAccent));
                                statusConsultarSaldo = false;
                                break;
                            }

                            if(statusEditarSenha){
                                appDB.editSenha(usuarioSenha);
                                ((MainActivity) getActivity()).liberarAlterarSenha(true);
                                statusEditarSenha = false;
                                break;
                            }
                        }
                        break;

                    case Activity.RESULT_CANCELED:
                        erroSms();
                }
            }
        };

        getActivity().registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        getActivity().registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
        getActivity().registerReceiver(intentReceiver, intentFilter);
        ((MainActivity)getActivity()).btnAtalhoCadastrar(false);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        checkAppUpdate.setStatusAlertDialog(false);
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String numeroSms = intent.getExtras().getString("numero");
            String smsR = intent.getExtras().getString("sms");

            int maxNumero = veiculo.getTelefone().length();
            String numeroUsuario = veiculo.getTelefone().substring(maxNumero - 8, maxNumero);

            if (numeroSms.contains(numeroUsuario)) {

                if ((smsR.contains("speed")) && (smsR.contains("acc") || smsR.contains("ACC"))) {

                    // Velocidade
                    int vel_1, vel_2;

                    vel_1 = smsR.indexOf("speed:");
                    vel_2 = smsR.lastIndexOf("T:");
                    String velocidade = smsR.substring(vel_1 + 6, vel_2);
                    int posicaoVelocidade = velocidade.lastIndexOf(".");
                    veiculo.setPainelVelocidade(velocidade.substring(0, posicaoVelocidade));
                    txtVelocidade.setText(veiculo.getPainelVelocidade());
                    //----------------------------------------------------------------------

                    // Localização no Mapa
                    int urlMapa_1, urlMapa_2;

                    urlMapa_1 = smsR.indexOf("http");
                    urlMapa_2 = smsR.lastIndexOf("16");
                    veiculo.setPainelUrlMapa(smsR.substring(urlMapa_1, urlMapa_2 + 2));

                    if(desbloquearMapa){
                        Intent intentt = new Intent(Intent.ACTION_VIEW, Uri.parse(veiculo.getPainelUrlMapa()));
                        startActivity(intentt);
                        reproduzirAudio(3);
                        functions.toast(veiculo.getNome()+" Localizado com Sucesso");
                        functions.toast("Sistema Atualizado com Sucesso");
                        desbloquearMapa = false;

                    } else if(desbloquearAtualizar){
                        functions.alertDialog("Informações", "Sistema Atualizado com Sucesso !", R.mipmap.ic_informacao, "OK");
                        reproduzirAudio(4);
                        desbloquearAtualizar = false;
                    }
                    //--------------------------------------------------------------------------------------------------------------------------------

                    // Ignição
                    int ligado, ligado_1, ligado_2;

                    ligado = smsR.indexOf("ACC: ");

                    String statusLigar = smsR.substring(ligado + 5);

                    String statusLigar_1 = statusLigar;

                    if (smsR.contains("acc on!") || smsR.contains("acc off!")) {
                        ligado_1 = smsR.indexOf("acc ");
                        ligado_2 = smsR.lastIndexOf("!");
                        statusLigar_1 = smsR.substring(ligado_1 + 4, ligado_2);
                    }

                    if (statusLigar.equals("ON") || statusLigar_1.equals("on")) {
                        veiculo.setPainelIgnicao(1);
                    } else{
                        veiculo.setPainelIgnicao(0);
                    }
                    //------------------------------------------------------------

                    // Editar Painel
                    editarPainel();
                    //--------------

                } else if (smsR.contains("Lac")) {

                    // Ao apertar no botão localizar e a resposta do SMS for um LAC,
                    // apenas o mapa será aberto e não armazenará às informações no banco de dados.
                    if(desbloquearMapa){
                        // Localização Mapa
                        int urlMapa_1, urlMapa_2;
                        String url_localizacao;

                        urlMapa_1 = smsR.indexOf("http");
                        urlMapa_2 = smsR.lastIndexOf("16");
                        url_localizacao = smsR.substring(urlMapa_1, urlMapa_2 + 2);

                        Intent intentUrlMapa = new Intent(Intent.ACTION_VIEW, Uri.parse(url_localizacao));
                        startActivity(intentUrlMapa);
                        reproduzirAudio(3);
                        functions.toast(veiculo.getNome()+" Localizado com Sucesso");
                        desbloquearMapa = false;

                    }
                    if(desbloquearAtualizar){
                        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                        dlg
                                .setTitle("GPS SEM SINAL")
                                .setIcon(R.mipmap.ic_atencao)
                                .setMessage("Aguarde um minuto, e tente atualizar o sistema novamente !")
                                .setNeutralButton("OK", null)
                                .setCancelable(false)
                                .show();

                        reproduzirAudio(21);
                        desbloquearAtualizar = false;
                    }

                } else if (smsR.contains("monitor ok")) {
                    statusEscuta[2] = true;
                    btnEscuta.setTextColor(getResources().getColor(R.color.colorRed));

                } else if (smsR.contains("tracker ok")) {
                    statusEscuta[2] = false;
                    btnEscuta.setTextColor(getResources().getColor(R.color.colorAccent));

                } else if (smsR.contains("move!")) {
                    // Redroduzir Áudio
                    reproduzirAudio(20);
                    //---------------------------

                    // Config AlertDialog
                    if(statusAlertDialogMove){
                        alertDialogMove.dismiss();
                    }
                    statusAlertDialogMove = true;
                    //-----------------------------

                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    dlg.setCancelable(false);
                    View mView = getActivity().getLayoutInflater().inflate(R.layout.alerta_movimento, null);
                    final TextView txtVelocidadeAtual = (TextView) mView.findViewById(R.id.txtVelocidadeAtual);
                    final ImageButton btn_mapa = (ImageButton) mView.findViewById(R.id.btn_mapa);
                    final Button btnFechar = (Button) mView.findViewById(R.id.btnFechar);

                    dlg.setView(mView);
                    alertDialogMove = dlg.create();
                    alertDialogMove.show();

                    // Velocidade
                    int vel_1, vel_2;

                    vel_1 = smsR.indexOf("speed:");
                    vel_2 = smsR.lastIndexOf("T:");
                    String velocidade = smsR.substring(vel_1 + 6, vel_2);
                    int posicaoVelocidade = velocidade.lastIndexOf(".");
                    //------------------------------------------------------

                    // TXT Velocidade
                    txtVelocidadeAtual.setText(velocidade.substring(0, posicaoVelocidade)+" Km/h");
                    //-----------------------------------------------------------------------------

                    // Mensagem Toast
                    functions.toast("ATENÇÃO - Veículo em Movimento");
                    //------------------------------------------------------

                    // Localização Mapa
                    int urlMapa_1, urlMapa_2;

                    urlMapa_1 = smsR.indexOf("http");
                    urlMapa_2 = smsR.lastIndexOf("16");
                    final String url_mapa = smsR.substring(urlMapa_1, urlMapa_2 + 2);
                    //---------------------------------------------------------------

                    btn_mapa.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentt = new Intent(Intent.ACTION_VIEW, Uri.parse(url_mapa));
                            startActivity(intentt);
                            functions.toast(veiculo.getNome()+" Localizado com Sucesso");
                        }
                    });

                    btnFechar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialogMove.dismiss();
                            statusAlertDialogMove = false;
                        }
                    });
                } else if (smsR.contains("speed!")) {
                    // Redroduzir Áudio
                    reproduzirAudio(17);
                    //---------------------------

                    // Config AlertDialog
                    if(statusAlertDialogSpeed){
                        alertDialogSpeed.dismiss();
                    }
                    statusAlertDialogSpeed = true;
                    //-----------------------------

                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    dlg.setCancelable(false);
                    View mView = getActivity().getLayoutInflater().inflate(R.layout.aviso_velocidade, null);
                    final ImageView placa_velocidade = (ImageView) mView.findViewById(R.id.placa_velocidade);
                    final TextView txtVelocidadeAtual = (TextView) mView.findViewById(R.id.txtVelocidadeAtual);
                    final ImageButton btn_mapa_velocidade = (ImageButton) mView.findViewById(R.id.btn_mapa_velocidade);
                    final Button btnFechar = (Button) mView.findViewById(R.id.btnFechar);

                    dlg.setView(mView);
                    alertDialogSpeed = dlg.create();
                    alertDialogSpeed.show();

                    switch (veiculo.getVelocidadeLimite()){
                        case 0: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_20);break;
                        case 1: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_40);break;
                        case 2: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_60);break;
                        case 3: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_80);break;
                        case 4: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_100);break;
                        case 5: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_120);break;
                        case 6: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_140);break;
                        case 7: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_160);break;
                        case 8: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_180);break;
                        case 9: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_200);break;
                        case 10: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_220);break;
                        case 11: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_240);break;
                        case 12: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_260);break;
                        case 13: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_280);break;
                        case 14: placa_velocidade.setImageResource(R.mipmap.ic_placa_velocidade_300);break;
                    }

                    int vel_1, vel_2, urlMapa_1, urlMapa_2;

                    vel_1 = smsR.indexOf("speed:");
                    vel_2 = smsR.lastIndexOf("T:");
                    String velocidade = smsR.substring(vel_1 + 6, vel_2);
                    int posicaoVelocidade = velocidade.lastIndexOf(".");
                    veiculo.setPainelVelocidade(velocidade.substring(0, posicaoVelocidade));

                    txtVelocidadeAtual.setText(veiculo.getPainelVelocidade()+" Km/h");

                    imgIgnicao.setImageResource(R.mipmap.ic_verde);
                    txtVelocidade.setText(veiculo.getPainelVelocidade());
                    btnLocalizacao.setBackgroundDrawable(getResources().getDrawable(R.drawable.icone_localizar));
                    btnMonitorarVelocidade.setTextColor(getResources().getColor(R.color.colorRed));
                    veiculo.setPainelIgnicao(1);

                    urlMapa_1 = smsR.indexOf("http");
                    urlMapa_2 = smsR.lastIndexOf("16");
                    veiculo.setPainelUrlMapa(smsR.substring(urlMapa_1, urlMapa_2 + 2));

                    functions.toast("Velocidade atual do veículo "+veiculo.getPainelVelocidade()+" Km/h");

                    btn_mapa_velocidade.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentt = new Intent(Intent.ACTION_VIEW, Uri.parse(veiculo.getPainelUrlMapa()));
                            startActivity(intentt);
                            functions.toast(veiculo.getNome()+" Localizado com Sucesso");
                        }
                    });

                    btnFechar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialogSpeed.dismiss();
                            statusAlertDialogSpeed = false;
                        }
                    });

                    // Editar Painel
                    editarPainel();
                    //--------------

                } else if (smsR.contains("Credito de Recarga")) {
                    smsR = smsR.replace("@","");
                    functions.alertDialog("Consultar Saldo", smsR, R.mipmap.ic_sifrao, "OK");
                }
            }

            if (smsR.contains("pwd fail") || smsR.contains("password fail")) {
                // Resetar Botões
                resetarBotoes();
                btnMonitorarVelocidade.setTextColor(getResources().getColor(R.color.colorAccent));
                //--------------------------------------------------------------------------------

                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg
                        .setTitle("Atenção")
                        .setIcon(R.mipmap.ic_atencao)
                        .setMessage("Senha Inválida !\nTelefone: "+numeroSms)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                ListarVeiculos listarVeiculos = (ListarVeiculos) fm.findFragmentByTag("ListarVeiculos");

                                if(listarVeiculos != null){
                                    fm.popBackStack("ListarVeiculos", 0);
                                } else{
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.container_padrao, new ListarVeiculos(), "ListarVeiculos");
                                    ft.addToBackStack("ListarVeiculos");
                                    ft.commit();
                                }
                            }
                        })
                        .show();
            } else if(smsR.contains("password ok")){
                functions.alertDialog("Atenção", "Senha alterada com sucesso !", R.mipmap.ic_informacao, "OK");
            }
        }
    };

    private void reproduzirAudio(int comando){
        if(config.getStatusAudio() == 1){
            String frase = null;

            switch(comando){
                case 1:
                    frase = veiculo.getNome()+" DESBLOQUEADO COM SUCESSO";
                    break;

                case 2:
                    frase = veiculo.getNome()+" BLOQUEADO COM SUCESSO";
                    break;

                case 3:
                    frase = veiculo.getNome()+" LOCALIZADO COM SUCESSO";
                    break;

                case 4:
                    frase = "SISTEMA ATUALIZADO COM SUCESSO";
                    break;

                case 7:
                    frase = "ESCUTA DESLIGADA";
                    break;

                case 8:
                    int velocidade = 20 * (veiculo.getVelocidadeLimite() + 1);
                    frase = "MONITORAMENTO DE VELOCIDADE LIGADO. VELOCIDADE DEFINIDA. "+velocidade+" QUILÔMETROS POR HORA";
                    break;

                case 9:
                    frase = "MONITORAMENTO DE VELOCIDADE DESLIGADO";
                    break;

                case 10:
                    frase = "ATUALIZE O SISTEMA POR FAVOR";
                    break;

                case 11:
                    frase = "AGUARDE. ENVIANDO SMS";
                    break;

                case 12:
                    frase = "SMS RECEBIDO. AGUARDANDO ATUALIZAÇÃO";
                    break;

                case 13:
                    frase = "SMS RECEBIDO. AGUARDANDO ABERTURA DO MAPA";
                    break;

                case 14:
                    frase = "IGNIÇÃO LIGADA";
                    break;

                case 15:
                    frase = "IGNIÇÃO DESLIGADA";
                    break;

                case 16:
                    if(veiculo.getPainelVelocidade().equals("0")){
                        frase = veiculo.getNome()+" ESTÁ PARADO";

                    } else if(veiculo.getPainelVelocidade().equals("1")){
                        frase = "VELOCIDADE ATUAL DO VEÍCULO. "+veiculo.getPainelVelocidade()+" QUILÔMETRO POR HORA";

                    } else {
                        frase = "VELOCIDADE ATUAL DO VEÍCULO. "+veiculo.getPainelVelocidade()+" QUILÔMETROS POR HORA";
                    }
                    break;

                case 17:
                    frase = veiculo.getNome()+" ESTÁ ACIMA DA VELOCIDADE MÁXIMA PERMITIDA";
                    break;

                case 18:
                    frase = "ALERTA DE MOVIMENTO LIGADO";
                    break;

                case 19:
                    frase = "ALERTA DE MOVIMENTO DESLIGADO";
                    break;

                case 20:
                    frase = "ATENÇÃO. VEÍCULO EM MOVIMENTO";
                    break;

                case 21:
                    frase = "GPS SEM SINAL. AGUARDE UM MINUTO. E TENTE ATUALIZAR O SISTEMA NOVAMENTE";
                    break;

                case 22:
                    frase = "AGUARDE O ENVIO DO SALDO";
                    break;
            }

            if(resultSpeak == TextToSpeech.LANG_MISSING_DATA || resultSpeak == TextToSpeech.LANG_NOT_SUPPORTED){
                functions.toast("Recurso não suportado no seu dispositivo !");

            } else{
                speak.speak(frase, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case 10:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String mensagem = result.get(0).toLowerCase();

                    switch(mensagem){
                        case "bloquear":
                        case "stop":
                        case "desligar":
                            btnBloquear();
                            break;

                        case "desbloquear":
                        case "resume":
                        case "ligar":
                            btnDesbloquear();
                            break;

                        case "localizar":
                        case "abrir mapa":
                            btnLocalizar();
                            break;

                        case "atualizar":
                        case "atualizar painel":
                            btnAtualizar();
                            break;

                        case "sair":
                            getActivity().finish();
                            functions.toast("Tenha um Bom Dia !");
                            break;

                        case "editar":
                        case "senha":
                        case "editar veículo":
                        case "editar senha":

                            appDB.editConfig("VEICULO_EDITAR", appDB.getConfig("VEICULO_ATUAL"));

                            ((MainActivity)getActivity()).navigationView.setCheckedItem(R.id.nav_listar_usuarios);
                            EditarVeiculo editarVeiculo = (EditarVeiculo) fm.findFragmentByTag("EditarVeiculo");

                            if(editarVeiculo != null){
                                fm.popBackStack("EditarVeiculo", 0);
                            } else{
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.container_padrao, new EditarVeiculo(), "EditarVeiculo");
                                ft.addToBackStack("EditarVeiculo");
                                ft.commit();
                            }
                            break;

                        case "trocar":
                        case "trocar veículo":
                        case "veiculos cadastrados":
                        case "cadastrados":

                            ((MainActivity)getActivity()).navigationView.setCheckedItem(R.id.nav_listar_usuarios);
                            ListarVeiculos listarVeiculos = (ListarVeiculos) fm.findFragmentByTag("ListarVeiculos");

                            if(listarVeiculos != null){
                                fm.popBackStack("ListarVeiculos", 0);
                            } else{
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.container_padrao, new ListarVeiculos(), "ListarVeiculos");
                                ft.addToBackStack("ListarVeiculos");
                                ft.commit();
                            }
                            break;

                        case "cadastrar":
                        case "cadastrar veículo":

                            ((MainActivity)getActivity()).navigationView.setCheckedItem(R.id.nav_cadastrar_usuarios);
                            Cadastrar cadastrar = (Cadastrar) fm.findFragmentByTag("Cadastrar");

                            if(cadastrar != null){
                                fm.popBackStack("Cadastrar", 0);
                            } else{
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.replace(R.id.container_padrao, new Cadastrar(), "Cadastrar");
                                ft.addToBackStack("Cadastrar");
                                ft.commit();
                            }
                            break;

                        case "cancelar":
                        case "cancelar comandos":
                        case "cancelar comando":

                            cancelarComandoSms();
                            break;

                        default:
                            functions.alertDialog("Informações", "Comando Inválido !\nComando: " + mensagem, R.mipmap.ic_informacao, "OK");
                    }
                }
        }
    }

    private void enviarSms(String comando){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(veiculo.getTelefone(), null, comando, sentPI, deliveredPI);

        progressSms(true);
        statusBotoes = false;
    }

    private void erroSms(){
        if(statusSmsErro){
            if(statusEditarSenha){
                functions.alertDialog("Atenção", "SMS NÃO ENVIADO - SENHA NÃO ALTERADA !", R.mipmap.ic_atencao, "OK");
                functions.toast("SMS NÃO ENVIADO - SENHA NÃO ALTERADA !");
                ((MainActivity) getActivity()).liberarAlterarSenha(false);
            } else{
                functions.alertDialog("Atenção", "SMS NÃO ENVIADO !", R.mipmap.ic_atencao, "OK");
                functions.toast("SMS NÃO ENVIADO !");
            }
            cancelarComandoSms();
            statusSmsErro = false;
        }
    }

    private void cancelarComandoSms(){
        progressSms(false);

        // Status das funções
        statusBotoes = true;
        statusBloquear = false;
        statusLocalizar = false;
        statusDesbloquear = false;
        statusAtualizar = false;
        statusConsultarSaldo = false;

        for(int i=0; i<3; i++){
            statusEscuta[i] = false;
            statusAlertaMovimento[i] = false;
            statusMonitorarVelocidade[i] = false;
        }
        //---------------------------------------

        // Controle
        statusSmsErro = false;
        desbloquearMapa = false;
        desbloquearAtualizar = false;
        //---------------------------------------

        // AlertDialog Alerta de Movimento
        statusAlertDialogMove = false;
        //---------------------------------------

        // AlertDialog Monitorar Velocidade
        statusAlertDialogSpeed = false;
        //---------------------------------------

        // Editar Senha
        statusEditarSenha = false;
        //---------------------------------------

        // Resetar Botões
        resetarBotoes();
        //---------------------------------------
    }

    private void resetarBotoes(){
        btnDesbloquear.setTextColor(getResources().getColor(R.color.colorAccent));
        btnDesbloquear.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_cadeadro_aberto, 0, 0);
        btnBloquear.setTextColor(getResources().getColor(R.color.colorAccent));
        btnBloquear.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_cadeadro_fechado, 0, 0);
        btnLocalizar.setTextColor(getResources().getColor(R.color.colorAccent));
        btnEscuta.setTextColor(getResources().getColor(R.color.colorAccent));
        btnAtualizar.setTextColor(getResources().getColor(R.color.colorAccent));
        btnAlertaMovimento.setTextColor(getResources().getColor(R.color.colorAccent));
        btnConsultarSaldo.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void progressSms(boolean status){
        if(status){
            AnimationDrawable animation;
            AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
            View view = getActivity().getLayoutInflater().inflate(R.layout.progress_sms, null);
            final ImageButton btnCancelar = (ImageButton) view.findViewById(R.id.btnCancelar);
            final ImageView gif = (ImageView) view.findViewById(R.id.gif);

            dlg.setCancelable(false);
            dlg.setView(view);
            dialogProgressSms = dlg.create();
            dialogProgressSms.show();

            gif.setBackgroundResource(R.drawable.gif_progress_sms);
            animation = (AnimationDrawable) gif.getBackground();
            animation.start();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar("AGUARDE - Enviando SMS...");
                    reproduzirAudio(11);
                }
            });

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    dlg     .setIcon(R.mipmap.ic_interrogacao)
                            .setTitle("Cancelar comando ?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cancelarComandoSms();
                                }
                            })
                            .setNegativeButton("Não", null)
                            .show();
                }
            });
        } else{
            if(dialogProgressSms != null){ dialogProgressSms.dismiss(); }
        }
    }

    private void ligarEscuta(){
        Uri uri = Uri.parse("tel:"+veiculo.getTelefone());
        Intent intent = new Intent(Intent.ACTION_CALL,uri);
        startActivity(intent);
    }

    private void startMonitorarVelocidade(){
        if(veiculo.getVelocidadeLimite() < 15){
            btnMonitorarVelocidade.setTextColor(getResources().getColor(R.color.colorRed));
        }
    }

    private void startAudio(){
        if(config.getStatusAudio() == 1){
            btnReproduzirAudio.setChecked(true);
        } else{
            btnReproduzirAudio.setChecked(false);
        }
    }

    private void speak(){
        speak = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {
                    resultSpeak = speak.setLanguage(Locale.getDefault());

                } else{
                    functions.toast("Recurso não suportado no seu dispositivo !");
                }
            }

        });
    }

    private String dataHora(){
        txtDataHora.setText("Atualizado às ");

        Date date = new Date();
        CharSequence dataHora = android.text.format.DateFormat.format("HH:mm - dd/MM/yyyy",date.getTime());
        String data = dataHora.toString();

        return data;
    }

    private void snackbar(String mensagem){
        Snackbar snackbar = Snackbar.make(getView(), mensagem, Snackbar.LENGTH_SHORT);
        TextView tv = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.GREEN);
        snackbar.show();
    }

    public void setVisibleFragment(Boolean controle){
        if(controle) {
            getView().setVisibility(View.VISIBLE);
        } else{
            getView().setVisibility(View.GONE);
        }
    }

    public void comandoVoz(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(intent, 10);
        } else{
            functions.toast("Comando de voz desativado.");
        }
    }

    public void editarSenha(Veiculo veiculo){
        if(statusBotoes()){
            String telefone = veiculo.getTelefone();
            String senhaAntiga = veiculo.getSenha();
            String novaSenha = veiculo.getNovaSenha();
            String comando = "password"+senhaAntiga+" "+novaSenha;

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, comando, sentPI, deliveredPI);

            usuarioSenha.setId(veiculo.getId());
            usuarioSenha.setNome(veiculo.getNome());
            usuarioSenha.setTelefone(veiculo.getTelefone());
            usuarioSenha.setSenha(veiculo.getNovaSenha());
            statusEditarSenha = true;

        } else{
            ((MainActivity) getActivity()).liberarAlterarSenha(false);
        }
    }
}