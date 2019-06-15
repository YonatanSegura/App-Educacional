package sv.edu.unab.educacional;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    ProgressDialog progressDialog;
    SwipeRefreshLayout refreshLayout;
    String error = "<HTML>\n" +
            "<HEAD>\n" +
            "    <META HTTP-EQUIV=\"CONTENT-TYPE\" CONTENT=\"text/html; charset=utf-8\">\n" +
            "</HEAD>\n" +
            "<BODY LANG=\"en-US\" BGCOLOR=\"#ffffff\" DIR=\"LTR\">\n" +
            "<H3><center>Se requiere conexión a internet.</center></H3>\n" +
            "</BODY>\n" +
            "</HTML>";
    private static ConnectivityManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();
        webView = findViewById(R.id.webView);
        refreshLayout = findViewById(R.id.refresh);
        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        }else {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Advertencia")
                    .setMessage("Su versión de Android no es compatible con protocolos mixtos en la web, algunos elementos no se podrán mostrar correctamente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }

        loadWebView();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadWebView();
                refreshLayout.setRefreshing(false);
            }
        });





    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("¿Salir?")
                .setMessage("¿Está completamente seguro que desea salir?")

                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();

        //super.onBackPressed();
    }

    private void loadWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (isOnline(getApplicationContext())){
            webView.loadUrl("https://education-website-unab.000webhostapp.com/index.html");
        }else {
            new AlertDialog.Builder(MainActivity.this)
            .setTitle("Error de conexión")
            .setMessage("No se pudo establecer la conexión con el servidor, verifica tu conexión a internet")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
            webView.loadData(error,"text/html",null);
        }
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }

        });
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
