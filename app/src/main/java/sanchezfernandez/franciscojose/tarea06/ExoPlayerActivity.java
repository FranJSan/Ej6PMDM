package sanchezfernandez.franciscojose.tarea06;

import androidx.appcompat.app.AppCompatActivity;


import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.util.Objects;

/**
 * Clase que maneja el ExoPlayer
 */
public class ExoPlayerActivity extends AppCompatActivity {

    ExoPlayer player;
    StyledPlayerView styledPlayer;
    String uri = null;
    MediaItem mediaItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);

        long ms = getIntent().getExtras().getLong("msReproduccion");
        String uriS = getIntent().getExtras().getString("uri");
        uri = uriS;
        inicializarComponentes(uriS, ms);
    }

    /**
     * Método para inicializar los componentes de la activity. Recibe por parámetros la uri y el
     * milisegundo por el cual se debe reproducir el video.
     * @param uri
     * @param ms
     */
    private void inicializarComponentes(String uri, long ms) {

        styledPlayer = findViewById(R.id.spView);
        if (player == null) { // Compruebo si ya existe una instancia de ExoPlayer para crearla o no.
            player = new ExoPlayer.Builder(this).build();
        }
        styledPlayer.setPlayer(player);
        mediaItem = MediaItem.fromUri(Uri.parse(uri));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
        player.seekTo(ms);
    }

    /**
     * Método para guardar la información en los cambios de estado de la app.
     * @param outState Bundle in which to place your saved state.
     *
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("uri", uri);
        outState.putLong("msReproduccion", player.getCurrentPosition());
    }

    /**
     * Método para restaurar la información de la app. Se restaura la uri del video que se estaba
     * reproducciendo y el milisegundo de reproducción.
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     *
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            String uriS = savedInstanceState.getString("uri");
            long ms = savedInstanceState.getLong("msReproduccion");
            if (uri != null) {
                uri = uriS;
                inicializarComponentes(uriS, ms);
            }
        }
    }

    /**
     * Método para liberar los recursos de ExoPlayer al detenerse la aplicación.
     */
    @Override
    protected void onStop() {
        super.onStop();
        player.setPlayWhenReady(false);
        player.release();
        player = null;
    }
}