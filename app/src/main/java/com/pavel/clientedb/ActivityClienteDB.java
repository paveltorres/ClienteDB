package com.pavel.clientedb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class ActivityClienteDB extends AppCompatActivity {

   private TextView txtCodigo;
   private TextView txtTipo;
    private Button btnRojo;
   private Button btnScan ;
    Connection conexion;
    ResultSet resultado;

    String ls_precio;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_cliente_db);

        final EditText et_servidor  = (EditText) findViewById(R.id.et_Servidor);
        Button btnVerde = (Button) findViewById(R.id.buttonVerde);
        btnRojo = (Button) findViewById(R.id.buttonRojo);
        btnScan = (Button) findViewById(R.id.buttonScan);
        final RadioButton rbtnMysql = (RadioButton) findViewById(R.id.radioButtonMySql);
        final RadioButton rbtnOracle = (RadioButton) findViewById(R.id.radioButtonOracle);
        final TextView tvResultado = (TextView)findViewById(R.id.textViewCodigo);
        final TextView tvPrecio = (TextView)findViewById(R.id.textViewPvp);

        String ls_usuario="pavel";
        String ls_clave="pavelito";
        final String ls_server;

        rbtnMysql.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("**************************RESULTADO: " + rbtnMysql.getText());
                rbtnMysql.setChecked(true);
                rbtnOracle.setChecked(false);

            }
        });

        rbtnOracle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("**************************RESULTADO: " + rbtnOracle.getText());
                rbtnMysql.setChecked(false);
                rbtnOracle.setChecked(true);

            }
        });

        btnVerde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                conectarBD lcBD = new conectarBD();
                lcBD.execute(et_servidor.getText().toString(), "verde");
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(500);


            }
        });

        btnRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                conectarBD lcBD = new conectarBD();
                lcBD.execute(et_servidor.getText().toString(), "rojo");
                Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);


            }
        });
          btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ls_pvp = "";
                IntentIntegrator scanIntegrator;
                scanIntegrator = new IntentIntegrator(ActivityClienteDB.this);
                scanIntegrator.initiateScan();

               /* conectarBDORA lcBD = new conectarBDORA();
                ls_pvp = lcBD.ConectarBD(et_servidor.getText().toString(), tvResultado.getText().toString());
                tvPrecio.setText(ls_pvp);
                System.out.println("PRECIO:  " + ls_pvp);
                */

            }
        });

    }
    public String  ConectarBD (String... strings) {

        String ls_usuario = "COMERCIAL";
        String ls_clave = "COMERCIAL";
        String ls_sql = "select pvp from INV_ARTICULO where cod_barras ='"+strings[1]+"'";
       /* String ls_sql = "select pvp from INV_ARTICULO where cod_barras ='9788431681111'";*/


        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conexion = DriverManager.getConnection(strings[0], ls_usuario, ls_clave);
            Statement estado = conexion.createStatement();
            resultado = (ResultSet) estado.executeQuery(ls_sql);
            while (resultado.next()) {
                ls_precio = "Precio: " + resultado.getString(1);
            }

            System.out.println("Conexion realizada " + ls_precio);


            resultado.close();
            estado.close();
            conexion.close();


        } catch (ClassNotFoundException e) {
                    /*Toast.makeText(getApplicationContext(), "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();*/
            e.printStackTrace();
        } catch (SQLException e) {
                    /*Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();*/
            e.printStackTrace();
        }

        return ls_precio;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        System.out.println("*****************************RESULTADO");
        handleResult(scanningResult);
       /* if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            txtCodigo.setText("CODIGO: "+ scanContent );
            txtTipo.setText("TIPO: "+ scanFormat);
        } else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }*/
    }
    private void handleResult(IntentResult scanResult) {
        boolean lb_salida = true;
        if (scanResult != null) {

            updateUITextViews(scanResult.getContents(), scanResult.getFormatName());


        } else {
            Toast.makeText(this, "No se ha leído nada :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUITextViews(String scan_result, String scan_result_format) {
        ((TextView)findViewById(R.id.textViewTipo)).setText(scan_result_format);
        final TextView tvResult = (TextView)findViewById(R.id.textViewCodigo);
        tvResult.setText(scan_result);
        final TextView tvPvp = (TextView)findViewById(R.id.textViewPvp);

        String ls_pvp;
        /*conectarBDORA lcBD = new conectarBDORA();*/
        ls_pvp = ConectarBD("jdbc:oracle:thin:COMERCIAL/COMERCIAL@studium.ec:1521:XE", scan_result);
        tvPvp.setText(ls_pvp);
        System.out.println("PRECIO:  " + ls_pvp);


       // Linkify.addLinks(tvResult, Linkify.ALL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_cliente_db, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


class conectarBD extends AsyncTask<String, Void,ResultSet> {
    Connection conexionMysql;
     @Override
        protected ResultSet  doInBackground(String...strings){

        String ls_usuario = "pavel";
        String ls_clave = "pavelito";
        String ls_sql = "insert into punto (color,cantidad) values('"+strings[1]+"',1)";

                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    conexionMysql = DriverManager.getConnection(strings[0], ls_usuario, ls_clave);
                    Statement estado = conexionMysql.createStatement();
                    estado.execute(ls_sql);
                    System.out.println("Conexion realizada");
                    conexionMysql.commit();
                    conexionMysql.close();


                } catch (ClassNotFoundException e) {
                    /*Toast.makeText(getApplicationContext(), "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();*/
                    e.printStackTrace();
                }   catch (SQLException e) {
                    /*Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();*/
                    e.printStackTrace();
                }

            return null;
        }


    }

class conectarBDOracle extends AsyncTask<String, Void,ResultSet> {
    Connection conexion;
    ResultSet resultado;
    public String ls_precio;


    @Override
    protected ResultSet doInBackground(String... strings) {

        String ls_usuario = "COMERCIAL";
        String ls_clave = "COMERCIAL";
        String ls_sql = "select pvp from INV_ARTICULO where cod_barras ='"+strings[1]+"'";
       /* String ls_sql = "select pvp from INV_ARTICULO where cod_barras ='9788431681111'";*/

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conexion = DriverManager.getConnection(strings[0], ls_usuario, ls_clave);
            Statement estado = conexion.createStatement();
            resultado = (ResultSet) estado.executeQuery(ls_sql);
            while (resultado.next()) {
                ls_precio = "Precio: " + resultado.getString(1);
            }

            System.out.println("Conexion realizada " + ls_precio);


           /* resultado.close();*/
            estado.close();
            conexion.close();


        } catch (ClassNotFoundException e) {
                    /*Toast.makeText(getApplicationContext(), "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();*/
            e.printStackTrace();
        } catch (SQLException e) {
                    /*Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();*/
            e.printStackTrace();
        }

        return null;
    }

    protected String onPostExecute(String result){

        return ls_precio;
    }
}


class conectarBDORA  {
    Connection conexion;
    ResultSet resultado;
    public String ls_precio;


    public String  ConectarBD (String... strings) {

        String ls_usuario = "COMERCIAL";
        String ls_clave = "COMERCIAL";
        String ls_sql = "select pvp from INV_ARTICULO where cod_barras ='"+strings[1]+"'";
       /* String ls_sql = "select pvp from INV_ARTICULO where cod_barras ='9788431681111'";*/

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conexion = DriverManager.getConnection(strings[0], ls_usuario, ls_clave);
            Statement estado = conexion.createStatement();
            resultado = (ResultSet) estado.executeQuery(ls_sql);
            while (resultado.next()) {
                ls_precio = "Precio: " + resultado.getString(1);
            }

            System.out.println("Conexion realizada " + ls_precio);


           resultado.close();
            estado.close();
            conexion.close();


        } catch (ClassNotFoundException e) {
                    /*Toast.makeText(getApplicationContext(), "ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();*/
            e.printStackTrace();
        } catch (SQLException e) {
                    /*Toast.makeText(getApplicationContext(), "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();*/
            e.printStackTrace();
        }

        return ls_precio;
    }

}
