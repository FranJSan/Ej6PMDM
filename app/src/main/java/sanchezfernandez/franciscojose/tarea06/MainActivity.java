package sanchezfernandez.franciscojose.tarea06;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * TAREA 6
 * Realizar una aplicación que permita alijar video alojado en un servidor web. Agregar segúndo botón
 * para reproducir un video alojado en un servidor local.
 * Usar ExoPlayer de manera opcional.
 *
 * Para la creación del servidor web he usado Apache, el que viene en Xampp. Una vez conseguí alojar
 * mi archivo la carpeta asignada del servidor, me encontré el problema de que no podía usar "localhost".
 * Entiendo que es normal, ya que el dispositivo es virtual. Tuve que buscar la ip local por donde se
 * exponía el servidor Apache, en mi caso: 192.168.56.1. Imagino que en otrás máquinas será otra ip,
 * e incluso el nombre del archivo (yo he usado video.mp4) será otro.
 *
 * Hice primero la aplicación con VideoView y posteriormente con ExoPlayer. Decidí hacer el proyecto
 * de manera que incluyese el uso de ambas posibilidades. La primera activy contiene el videoView con
 * botones para reproducir desde la Web o desde Local, además de sus semejantes para usar ExoPlayer.
 *
 * La app guarda los estados y está construida para poder usarse de manera vertical y horizontal.
 * NOTA: Para ExoPlayer no he diseñado ninguna UI. Solo es un ExoPlayer2.ui.StyledPlayerView que
 * ocupa toda la pantalla. Como la view se comporta bien a los cambios de orientación del dispositivo,
 * no he realizado ningún activity adicional.
 *
 * La app está diseñada para guardar los datos en los cambios de estado. En el dispositivo virtual
 * me funciona sin problema, pero en mi dispositivo físico me ha dado un problema: en los cambios de
 * orientación no seguía el video reproduciendose, es como si se crease la activity desde cero y no
 * cargase ninguna configuración. Tiene que ser un problema de versiones o del certificado de
 * seguridad y que mi dispositivo impida la escritura por lo que he estado mirando.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnReproducirWeb;
    private Button btnReproducirServerLocal;
    private Button btnExoPlayerWeb;
    private Button btnExoPlayerServerL;
    private MediaController mediaController;
    private VideoView videoView;
    private Uri uriVideoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarViews();
    }

    /**
     * Método para inicializar y configura todas los Buttons y el MediaController.
     */
    private void inicializarViews() {
        videoView = findViewById(R.id.videov);
        btnReproducirWeb = findViewById(R.id.btnReproducir);
        btnReproducirServerLocal = findViewById(R.id.btnReproducirServidor);
        btnExoPlayerWeb = findViewById(R.id.btnExoPlayerWeb);
        btnExoPlayerServerL = findViewById(R.id.btnExoPlayerServer);

        btnReproducirWeb.setOnClickListener(this);
        btnReproducirServerLocal.setOnClickListener(this);
        btnExoPlayerWeb.setOnClickListener(this);
        btnExoPlayerServerL.setOnClickListener(this);

        mediaController = new MediaController(this);
    }

    /**
     * Controlador del evento click de todos los botones de la activity.
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        Button btn = (Button) view;

        if (btn == btnReproducirWeb) {
            reproducirVideo("https://img-9gag-fun.9cache.com/photo/axorx4D_460svvp9.webm");

        } else if (btn == btnReproducirServerLocal) {
            reproducirVideo("http://192.168.56.1/video.mp4");

        } else if (btn == btnExoPlayerWeb) {
           //lanzarIntentExoPlayer("https://img-9gag-fun.9cache.com/photo/axorx4D_460svvp9.webm");
            lanzarIntentExoPlayer("https://img-9gag-fun.9cache.com/photo/aKEQvqW_460svvp9.webm");

        } else if (btn == btnExoPlayerServerL) {
            lanzarIntentExoPlayer("http://192.168.56.1/video.mp4");
        }
    }

    /**
     * Método que se lanza para reproducir un video.
     * @param uriS String video a reproducir
     */
    private void reproducirVideo(String uriS) {
        Uri uri = Uri.parse(uriS);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.start();

        // Guarda qué video se está reproduciendo por si la aplicación sufre un cambio
        // de estado.
        uriVideoActual = uri;
    }

    /**
     * Método para lanzar un intent de ExoPlayerActivity.class con la uri del video que se pase
     * como argumento.
     * @param uri del video que se va a reproducir
     */
    private void lanzarIntentExoPlayer(String uri) {
        Intent intent = new Intent(getApplicationContext(), ExoPlayerActivity.class);
        intent.putExtra("uri", uri);
        if (uriVideoActual != null && uriVideoActual.toString().equals(uri)) {
            intent.putExtra("msReproduccion", (long)videoView.getCurrentPosition());
        } else
            intent.putExtra("msReproduccion", 0);

        startActivity(intent);
    }

    /**
     * Método para guardar el estado de la aplicación. Se guarda la uri del video, si hay alguno,
     * reproduciendose y el instante que está reproduciendo.
     * @param outState Bundle in which to place your saved state.
     *
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (videoView.isPlaying()) {
            outState.putString("uri", uriVideoActual.toString());
            outState.putInt("msReproduccion", videoView.getCurrentPosition());
        }
    }

    /**
     * Método para cargar y restaurar el estado de la aplicación.
     * Carga la uri del video (si la hay) lo comienza a reproducir en milisegundo guardado.
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     *
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        inicializarViews();
        if (savedInstanceState != null) {
            String uriString = savedInstanceState.getString("uri");
            if (uriString != null) {
                Uri uri = Uri.parse(uriString);
                uriVideoActual = uri;
                videoView.setVideoURI(uri);
                videoView.setMediaController(mediaController);
                videoView.start();
                videoView.seekTo(savedInstanceState.getInt("msReproduccion"));
            }

        }

    }
}